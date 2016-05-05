package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl;

import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

/**
 * This class is responsible to verify if Neo4jTaxonomyDataAccess is returning the correct TaxonomyNode and
 * Taxonomies object for different scenarios
 *
 * Created by lgonzales on 20/04/16.
 */
public class Neo4jTaxonomyDataAccessTest {

    private static final String baseURL = "http://localhost:9090/uniprot/services/restful/taxonomy/id/";

    private static final String LOAD_CSV = "LOAD CSV WITH HEADERS FROM {csvPath} AS row FIELDTERMINATOR ',' ";

    private static final String IMPORT_CYPHER_NODE_QUERY = LOAD_CSV +
            "MERGE (node:Node { taxonomyId : row.TAX_ID }) " +
                "SET node += {taxonomyId : row.TAX_ID, mnemonic : row.SPTR_CODE, scientificName : row.SPTR_SCIENTIFIC, " +
                    "commonName : row.SPTR_COMMON, synonym : row .SPTR_SYNONYM, rank : row.RANK} " +
            "MERGE (parent:Node {taxonomyId:row.PARENT_ID}) " +
            "MERGE (node)-[:CHILD_OF]-(parent)";

    private static final String IMPORT_CYPHER_MERGED_QUERY  = LOAD_CSV +
            "MERGE (node:Node { taxonomyId : row.OLD_TAX_ID })-[:MERGED_TO]-(node1:Node { taxonomyId : row.NEW_TAX_ID})";

    private static final String IMPORT_CYPHER_DELETED_QUERY  = LOAD_CSV +
            "MERGE(n:Node {taxonomyId:row.TAX_ID}) SET n:Deleted REMOVE n:Node";

    private static MockedNeo4jTaxonomyDataAccess neo4jDataAccess;

    @BeforeClass
    public static void setUpAndLoadMockDataFromCSVFile() throws Exception {
        GraphDatabaseService neo4jDb = new TestGraphDatabaseFactory().newImpermanentDatabase();

        importNeo4JData(neo4jDb,"/neo4JMockNodeData.csv",IMPORT_CYPHER_NODE_QUERY);
        importNeo4JData(neo4jDb,"/neo4JMockMergedData.csv",IMPORT_CYPHER_MERGED_QUERY);
        importNeo4JData(neo4jDb,"/neo4JMockDeletedData.csv",IMPORT_CYPHER_DELETED_QUERY);

        neo4jDataAccess = new MockedNeo4jTaxonomyDataAccess("");
        neo4jDataAccess.setNeo4jDb(neo4jDb);
    }

    private static void importNeo4JData(GraphDatabaseService neo4jDb,String resourcePath, String query) {
        URL csvFilePath = Neo4jTaxonomyDataAccessTest.class.getResource(resourcePath);
        Map<String, Object> params = new HashMap<>();
        params.put( "csvPath",csvFilePath.toString());

        try ( Transaction tx = neo4jDb.beginTx();
                Result queryResult = neo4jDb.execute(query,params ) )
        {
            tx.success();
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        neo4jDataAccess.getNeo4jDb().shutdown();
    }

    @Test
    public void assertgetTaxonomyDetailsByIdWithARootNodeAndReturnWithoutParentAndSiblingsLink() throws Exception {
        boolean hasChildrenLink = true;
        boolean hasSiblingsLink = false;
        boolean hasParentLink = false;
        TaxonomyNode node = neo4jDataAccess.getTaxonomyDetailsById(1,baseURL);
        assertNodeDetail(1L,node,hasChildrenLink,hasSiblingsLink,hasParentLink);
    }

    @Test
    public void assertgetTaxonomyDetailsByIdWithACompleteNodeReturnChildrenSiblingsAndParentLink() throws Exception {
        boolean hasChildrenLink = true;
        boolean hasSiblingsLink = true;
        boolean hasParentLink = true;
        TaxonomyNode node = neo4jDataAccess.getTaxonomyDetailsById(10,baseURL);
        assertNodeDetail(10L,node,hasChildrenLink,hasSiblingsLink,hasParentLink);
    }

    @Test
    public void assertgetTaxonomyDetailsByIdWithABottomNodeReturnWithoutChildrenLink() throws Exception {
        boolean hasChildrenLink = false;
        boolean hasSiblingsLink = true;
        boolean hasParentLink = true;
        TaxonomyNode node = neo4jDataAccess.getTaxonomyDetailsById(1222,baseURL);
        assertNodeDetail(1222L,node,hasChildrenLink,hasSiblingsLink,hasParentLink);
    }

    @Test
    public void assertgetTaxonomySiblingsByIdWithIdThatReturnTwoSiblings() throws Exception {
        Taxonomies nodes = neo4jDataAccess.getTaxonomySiblingsById(10L);
        assertThat(nodes,notNullValue());
        assertThat(nodes.getTaxonomies(),notNullValue());
        assertThat(nodes.getTaxonomies().size(),is(2));
        Collections.sort(nodes.getTaxonomies());
        assertBaseNode(11L,nodes.getTaxonomies().get(0));
    }

    @Test
    public void assertgetTaxonomySiblingsByIdWithIdThatDoesNoteReturnSiblings() throws Exception {
        Taxonomies nodes = neo4jDataAccess.getTaxonomySiblingsById(1L);
        assertThat(nodes,nullValue());
    }

    @Test
    public void assertgetTaxonomyChildrenByIdThatReturnThreeChildren() throws Exception {
        Taxonomies nodes = neo4jDataAccess.getTaxonomyChildrenById(1L);
        assertThat(nodes,notNullValue());
        assertThat(nodes.getTaxonomies(),notNullValue());
        assertThat(nodes.getTaxonomies().size(),is(3));
        Collections.sort(nodes.getTaxonomies());
        assertBaseNode(10L,nodes.getTaxonomies().get(0));
    }

    @Test
    public void assertgetTaxonomyChildrenByIdWithBottomIdThatDoesNoteReturnChildren() throws Exception {
        Taxonomies nodes = neo4jDataAccess.getTaxonomyChildrenById(1233L);
        assertThat(nodes,nullValue());
    }

    @Test
    public void assertGetTaxonomyDetailsByNameWithValidSimilarNames() throws Exception {
        Taxonomies nodes = neo4jDataAccess.getTaxonomyDetailsByName("Name",baseURL);
        assertThat(nodes,notNullValue());
        assertThat(nodes.getTaxonomies(),notNullValue());
        assertThat(nodes.getTaxonomies().size(),is(4));
    }

    @Test
    public void assertGetTaxonomyDetailsByNameWithValidExactName() throws Exception {
        Taxonomies nodes = neo4jDataAccess.getTaxonomyDetailsByName("MyCompleteName",baseURL);
        assertThat(nodes,notNullValue());
        assertThat(nodes.getTaxonomies(),notNullValue());
        assertThat(nodes.getTaxonomies().size(),is(1));
        assertBaseNode(100000L,nodes.getTaxonomies().get(0));
    }

    @Test
    public void assertGetTaxonomyDetailsByNameWithInValidName() throws Exception {
        Taxonomies nodes = neo4jDataAccess.getTaxonomyDetailsByName("INVALID",baseURL);
        assertThat(nodes,nullValue());
    }

    @Test
    public void assertCheckRelationshipBetweenTaxonomiesInvalidFromIdReturnNull() throws Exception {
        TaxonomyNode node = neo4jDataAccess.checkRelationshipBetweenTaxonomies(5L,10L);
        assertThat(node,nullValue());
    }

    @Test
    public void assertCheckRelationshipBetweenTaxonomiesInvalidToIdReturnNull() throws Exception {
        TaxonomyNode node = neo4jDataAccess.checkRelationshipBetweenTaxonomies(1L,6L);
        assertThat(node,nullValue());
    }

    @Test
    public void assertCheckRelationshipBetweenTaxonomiesReturnOnlyParentRelationShip() throws Exception {
        TaxonomyNode node = neo4jDataAccess.checkRelationshipBetweenTaxonomies(10000000L,1L);
        assertNodePath(10000000L,node,0,7);
    }

    @Test
    public void assertCheckRelationshipBetweenTaxonomiesReturnOnlyChildrenRelationShip() throws Exception {
        TaxonomyNode node = neo4jDataAccess.checkRelationshipBetweenTaxonomies(1L,10000000L);
        assertNodePath(1L,node,7,0);
    }

    @Test
    public void assertCheckRelationshipBetweenTaxonomiesReturnParentAndChildrenRelationShip() throws Exception {
        TaxonomyNode node = neo4jDataAccess.checkRelationshipBetweenTaxonomies(10000000,1222L);
        assertNodePath(10000000L,node,3,7);
    }

    @Test
    public void assertGetTaxonomyPathWithInvalidIdReturnNull() throws Exception {
        PathRequestParams params = new PathRequestParams();
        params.setDepth(1);
        params.setDirection("bottom");
        params.setId("5");
        TaxonomyNode node = neo4jDataAccess.getTaxonomyPath(params);
        assertThat(node,nullValue());
    }

    @Test
    public void assertGetTaxonomyPathWithBottomDirectionOneDepth() throws Exception {
        PathRequestParams params = new PathRequestParams();
        params.setDepth(1);
        params.setDirection("bottom");
        params.setId("1");
        TaxonomyNode node = neo4jDataAccess.getTaxonomyPath(params);
        assertNodePath(1L,node,1,0);
    }

    @Test
    public void assertGetTaxonomyPathWithBottomDirectionFiveDepth() throws Exception {
        PathRequestParams params = new PathRequestParams();
        params.setDepth(5);
        params.setDirection("bottom");
        params.setId("1");
        TaxonomyNode node = neo4jDataAccess.getTaxonomyPath(params);
        assertNodePath(1L,node,5,0);
    }

    @Test
    public void assertGetTaxonomyPathWithBottomDirectionFiveDepthThatThatHasBottominThreeDepth() throws Exception {
        PathRequestParams params = new PathRequestParams();
        params.setDepth(5);
        params.setDirection("bottom");
        params.setId("10000");
        TaxonomyNode node = neo4jDataAccess.getTaxonomyPath(params);
        assertNodePath(10000L,node,3,0);
    }

    @Test
    public void assertGetTaxonomyPathWithTopDirectionUntilRootDepthThatReturnOneDepth() throws Exception {
        PathRequestParams params = new PathRequestParams();
        params.setDepth(3);
        params.setDirection("top");
        params.setId("10");
        TaxonomyNode node = neo4jDataAccess.getTaxonomyPath(params);
        assertNodePath(10L,node,0,1);
    }

    @Test
    public void assertGetTaxonomyPathWithTopDirectionThreeDepth() throws Exception {
        PathRequestParams params = new PathRequestParams();
        params.setDepth(3);
        params.setDirection("top");
        params.setId("10000000");
        TaxonomyNode node = neo4jDataAccess.getTaxonomyPath(params);
        assertNodePath(10000000L,node,0,3);
    }

    @Test
    public void assertGetTaxonomyPathWithTopDirectionFiveDepth() throws Exception {
        PathRequestParams params = new PathRequestParams();
        params.setDepth(5);
        params.setDirection("top");
        params.setId("10000000");
        TaxonomyNode node = neo4jDataAccess.getTaxonomyPath(params);
        assertNodePath(10000000L,node,0,5);
    }

    @Test
    public void assertCheckTaxonomyIdHistoricalChangeThatReturnNewId() throws Exception {
        Long newId = neo4jDataAccess.checkTaxonomyIdHistoricalChange(9L);
        assertThat(newId, is(10L));
    }

    @Test
    public void assertCheckTaxonomyIdHistoricalChangeThatDoesNotReturnNewId() throws Exception {
        long newId = neo4jDataAccess.checkTaxonomyIdHistoricalChange(5L);
        assertThat(newId, lessThan(0L));
    }

    private void assertNodeDetail(long expectedTaxonomyId, TaxonomyNode node,boolean hasChildrenLink,boolean
            hasSiblingsLink, boolean hasParentLink) {

        assertBaseNode(expectedTaxonomyId, node);
        assertThat(node.getParent(), nullValue());
        if(hasParentLink) {
            assertThat(node.getParentLink(), notNullValue());
        }else{
            assertThat(node.getParentLink(),nullValue());
        }
        assertThat(node.getChildren(),nullValue());
        if(hasChildrenLink){
            assertThat(node.getChildrenLinks(),notNullValue());
            assertThat(node.getChildrenLinks().size(),is(3));
        }else{
            assertThat(node.getChildrenLinks(),nullValue());
        }
        assertThat(node.getSiblings(),nullValue());
        if(hasSiblingsLink){
            assertThat(node.getSiblingsLinks(),notNullValue());
            assertThat(node.getSiblingsLinks().size(),is(2));
        }else{
            assertThat(node.getSiblingsLinks(),nullValue());
        }
    }

    private void assertBaseNode(long expectedTaxonomyId, TaxonomyNode node) {
        assertThat(node,notNullValue());
        if(expectedTaxonomyId > 0) {
            assertThat(node.getTaxonomyId(), is(expectedTaxonomyId));
        }else{
            assertThat(node.getTaxonomyId(),notNullValue());
        }
        assertThat(node.getCommonName(),notNullValue());
        assertThat(node.getMnemonic(),notNullValue());
        assertThat(node.getRank(),notNullValue());
        assertThat(node.getSynonym(),notNullValue());
        assertThat(node.getScientificName(),notNullValue());
    }

    private void assertNodePath(Long expectedRootId, TaxonomyNode node, int expectedChildrenLevels, int
            expectedParentLevel){
        int chidrenLevels = 0;
        int parentLevels = 0;
        assertThat(node, notNullValue());
        assertBaseNode(expectedRootId,node);

        boolean hasParent = true;
        do {
            if (node.getParent() != null) {
                node = node.getParent();
                assertThat(node, notNullValue());
                assertBaseNode(0,node);
                parentLevels++;
            }else{
                hasParent = false;
            }
        }while(hasParent);

        boolean hasChildren = true;
        do {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                Collections.sort(node.getChildren());
                node = node.getChildren().get(0);
                assertThat(node, notNullValue());
                assertBaseNode(0,node);
                chidrenLevels++;
            }else{
                hasChildren = false;
            }
        }while (hasChildren);

        assertThat(chidrenLevels, is(expectedChildrenLevels));
        assertThat(parentLevels, is(expectedParentLevel));
    }

    private static class MockedNeo4jTaxonomyDataAccess extends Neo4jTaxonomyDataAccess {

        public MockedNeo4jTaxonomyDataAccess(String filePath) {
            super(filePath);
        }

        public void setNeo4jDb(GraphDatabaseService neo4jDb){
            this.neo4jDb = neo4jDb;
        }

        public GraphDatabaseService getNeo4jDb(){
            return this.neo4jDb;
        }
    }
}