package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl;

import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.NameRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;

import java.util.Collections;
import java.util.Optional;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This class is responsible to verify if Neo4jTaxonomyDataAccess is returning the correct TaxonomyNode and
 * Taxonomies object for different scenarios
 *
 * Created by lgonzales on 20/04/16.
 */
public class Neo4jTaxonomyDataAccessTest {

    private static final String baseURL = "http://localhost:9090/uniprot/services/restful/taxonomy/id/";

    private static FakeTaxonomyDataAccess neo4jDataAccess;

    @BeforeClass
    public static void setUpAndLoadMockDataFromCSVFile() throws Exception {
        neo4jDataAccess = new FakeTaxonomyDataAccess("");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        neo4jDataAccess.getNeo4jDb().shutdown();
    }

    @Test
    public void getTaxonomyDetailsByIdWithARootNodeAndReturnWithoutParentAndSiblingsLink() throws Exception {
        boolean hasChildrenLink = true;
        boolean hasSiblingsLink = false;
        boolean hasParentLink = false;
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyDetailsById(1,baseURL);
        assertThat(node.isPresent(),is(true));
        assertNodeDetail(1L,node.get(),hasChildrenLink,hasSiblingsLink,hasParentLink);
    }

    @Test
    public void getTaxonomyDetailsByIdWithACompleteNodeReturnChildrenSiblingsAndParentLink() throws Exception {
        boolean hasChildrenLink = true;
        boolean hasSiblingsLink = true;
        boolean hasParentLink = true;
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyDetailsById(10,baseURL);
        assertThat(node.isPresent(),is(true));
        assertNodeDetail(10L,node.get(),hasChildrenLink,hasSiblingsLink,hasParentLink);
    }

    @Test
    public void getTaxonomyDetailsByIdWithABottomNodeReturnWithoutChildrenLink() throws Exception {
        boolean hasChildrenLink = false;
        boolean hasSiblingsLink = true;
        boolean hasParentLink = true;
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyDetailsById(1222,baseURL);
        assertThat(node.isPresent(),is(true));
        assertNodeDetail(1222L,node.get(),hasChildrenLink,hasSiblingsLink,hasParentLink);
    }

    @Test
    public void getTaxonomyParentByIdWithIdThatDoesNoteReturnParent() throws Exception {
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyParentById(1L);
        assertThat(node.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyParentByIdWithIdThatReturnValidParent() throws Exception {
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyParentById(10L);
        assertThat(node.isPresent(),is(true));
        assertBaseNode(1L,node.get());
    }

    @Test
    public void getTaxonomySiblingsByIdWithIdThatReturnTwoSiblings() throws Exception {
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomySiblingsById(10L);
        assertThat(nodeOptional.isPresent(),is(true));
        Taxonomies nodes = nodeOptional.get();
        assertThat(nodes.getTaxonomies(),notNullValue());
        assertThat(nodes.getTaxonomies().size(),is(2));
        Collections.sort(nodes.getTaxonomies());
        assertBaseNode(11L,nodes.getTaxonomies().get(0));
    }

    @Test
    public void getTaxonomySiblingsByIdWithIdThatDoesNoteReturnSiblings() throws Exception {
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomySiblingsById(1L);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyChildrenByIdThatReturnThreeChildren() throws Exception {
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyChildrenById(1L);
        assertThat(nodeOptional.isPresent(),is(true));
        Taxonomies nodes = nodeOptional.get();
        assertThat(nodes.getTaxonomies(),notNullValue());
        assertThat(nodes.getTaxonomies().size(),is(3));
        Collections.sort(nodes.getTaxonomies());
        assertBaseNode(10L,nodes.getTaxonomies().get(0));
    }

    @Test
    public void getTaxonomyChildrenByIdWithBottomIdThatDoesNoteReturnChildren() throws Exception {
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomyChildrenById(1233L);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyDetailsByNameWithEqualsToValidExactName() throws Exception {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("EQUALSTO");
        nameParams.setTaxonomyName("equals to only");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertThat(nodeOptional.isPresent(),is(true));
        Taxonomies nodes = nodeOptional.get();
        assertThat(nodes.getTaxonomies(),notNullValue());
        assertThat(nodes.getTaxonomies().size(),is(1));
        assertBaseNode(10000,nodes.getTaxonomies().get(0));
    }

    @Test
    public void getTaxonomyDetailsByNameWithEqualsToInvalidSimilarNames() throws Exception {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("EQUALSTO");
        nameParams.setTaxonomyName("equalsto only");
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyDetailsByNameWithEqualsToSmallNamesReturnValidTaxonomies() throws Exception {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("EQUALSTO");
        nameParams.setTaxonomyName("sn");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertThat(nodeOptional.isPresent(),is(true));
        Taxonomies nodes = nodeOptional.get();
        assertThat(nodes.getTaxonomies(),notNullValue());
        assertThat(nodes.getTaxonomies().size(),is(1));
        assertBaseNode(10000005,nodes.getTaxonomies().get(0));
    }

    @Test
    public void getTaxonomyDetailsByNameEqualsToInValidName() throws Exception {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("EQUALSTO");
        nameParams.setTaxonomyName("INVALID");
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyDetailsByNameContainsValidName() throws Exception {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("CONTAINS");
        nameParams.setTaxonomyName("name");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        Taxonomies nodes = nodeOptional.get();
        assertThat(nodes.getTaxonomies(),notNullValue());
        assertThat(nodes.getTaxonomies().size(),is(4));
    }

    @Test
    public void getTaxonomyDetailsByNameContainsInValidName() throws Exception {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("CONTAINS");
        nameParams.setTaxonomyName("invalid");
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyDetailsByNameEndsWithValidName() throws Exception {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("ENDSWITH");
        nameParams.setTaxonomyName("ended");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        Taxonomies nodes = nodeOptional.get();
        assertThat(nodes.getTaxonomies(),notNullValue());
        assertThat(nodes.getTaxonomies().size(),is(4));
    }

    @Test
    public void getTaxonomyDetailsByNameEndsWithInValidName() throws Exception {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("ENDSWITH");
        nameParams.setTaxonomyName("INVALID");
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyDetailsByNameStartsWithValidName() throws Exception {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("STARTSWITH");
        nameParams.setTaxonomyName("start");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        Taxonomies nodes = nodeOptional.get();
        assertThat(nodes.getTaxonomies(),notNullValue());
        assertThat(nodes.getTaxonomies().size(),is(4));
    }

    @Test
    public void getTaxonomyDetailsByNameStartsWithInValidName() throws Exception {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("STARTSWITH");
        nameParams.setTaxonomyName("invalid");
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void checkRelationshipBetweenTaxonomiesInvalidFromIdReturnNull() throws Exception {
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomiesRelationship(5L,10L);
        assertThat(node.isPresent(),is(false));
    }

    @Test
    public void checkRelationshipBetweenTaxonomiesInvalidToIdReturnNull() throws Exception {
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomiesRelationship(1L,6L);
        assertThat(node.isPresent(),is(false));
    }

    @Test
    public void checkRelationshipBetweenTaxonomiesReturnOnlyParentRelationShip() throws Exception {
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomiesRelationship(10000000L,1L);
        assertThat(node.isPresent(),is(true));
        assertNodePath(10000000L,node.get(),0,7);
    }

    @Test
    public void checkRelationshipBetweenTaxonomiesReturnOnlyChildrenRelationShip() throws Exception {
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomiesRelationship(1L,10000000L);
        assertThat(node.isPresent(),is(true));
        assertNodePath(1L,node.get(),7,0);
    }

    @Test
    public void checkRelationshipBetweenTaxonomiesReturnParentAndChildrenRelationShip() throws Exception {
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomiesRelationship(10000000,1222L);
        assertThat(node.isPresent(),is(true));
        assertNodePath(10000000L,node.get(),3,7);
    }

    @Test
    public void getTaxonomyPathWithInvalidIdReturnNull() throws Exception {
        PathRequestParams params = new PathRequestParams();
        params.setDepth(1);
        params.setDirection("bottom");
        params.setId("5");
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyPath(params);
        assertThat(node.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyPathWithBottomDirectionOneDepth() throws Exception {
        PathRequestParams params = new PathRequestParams();
        params.setDepth(1);
        params.setDirection("bottom");
        params.setId("1");
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyPath(params);
        assertThat(node.isPresent(),is(true));
        assertNodePath(1L,node.get(),1,0);
    }

    @Test
    public void getTaxonomyPathWithBottomDirectionFiveDepth() throws Exception {
        PathRequestParams params = new PathRequestParams();
        params.setDepth(5);
        params.setDirection("bottom");
        params.setId("1");
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyPath(params);
        assertThat(node.isPresent(),is(true));
        assertNodePath(1L,node.get(),5,0);
    }

    @Test
    public void getTaxonomyPathWithBottomDirectionFiveDepthThatThatHasBottominThreeDepth() throws Exception {
        PathRequestParams params = new PathRequestParams();
        params.setDepth(5);
        params.setDirection("bottom");
        params.setId("10000");
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyPath(params);
        assertThat(node.isPresent(),is(true));
        assertNodePath(10000L,node.get(),3,0);
    }

    @Test
    public void getTaxonomyPathWithTopDirectionUntilRootDepthThatReturnOneDepth() throws Exception {
        PathRequestParams params = new PathRequestParams();
        params.setDepth(3);
        params.setDirection("top");
        params.setId("10");
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyPath(params);
        assertThat(node.isPresent(),is(true));
        assertNodePath(10L,node.get(),0,1);
    }

    @Test
    public void getTaxonomyPathWithTopDirectionThreeDepth() throws Exception {
        PathRequestParams params = new PathRequestParams();
        params.setDepth(3);
        params.setDirection("top");
        params.setId("10000000");
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyPath(params);
        assertThat(node.isPresent(),is(true));
        assertNodePath(10000000L,node.get(),0,3);
    }

    @Test
    public void getTaxonomyPathWithTopDirectionFiveDepth() throws Exception {
        PathRequestParams params = new PathRequestParams();
        params.setDepth(5);
        params.setDirection("top");
        params.setId("10000000");
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyPath(params);
        assertThat(node.isPresent(),is(true));
        assertNodePath(10000000L,node.get(),0,5);
    }

    @Test
    public void getTaxonomyHistoricalChangeThatReturnNewId() throws Exception {
        Optional<Long> newId = neo4jDataAccess.getTaxonomyHistoricalChange(9L);
        assertThat(newId.isPresent(),is(true));
        assertThat(newId.get(), is(10L));
    }

    @Test
    public void getTaxonomyHistoricalChangeThatDoesNotReturnNewId() throws Exception {
        Optional<Long> newId = neo4jDataAccess.getTaxonomyHistoricalChange(5L);
        assertThat(newId.isPresent(), is(false));
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
}