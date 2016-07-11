package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl;

import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.NameRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;

import java.util.ArrayList;
import java.util.Arrays;
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
    public static void setUpAndLoadMockDataFromCSVFile() {
        neo4jDataAccess = new FakeTaxonomyDataAccess("");
    }

    @AfterClass
    public static void tearDown() {
        neo4jDataAccess.getNeo4jDb().shutdown();
    }

    @Test
    public void getTaxonomyDetailsByIdWithARootNodeAndReturnWithoutParentAndSiblingsLink() {
        boolean hasChildrenLink = true;
        boolean hasSiblingsLink = false;
        boolean hasParentLink = false;
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyDetailsById(1,baseURL);
        assertThat(node.isPresent(),is(true));
        assertNodeDetail(1L,node.get(),hasChildrenLink,hasSiblingsLink,hasParentLink);
    }

    @Test
    public void getTaxonomyDetailsByIdWithACompleteNodeReturnChildrenSiblingsAndParentLink() {
        boolean hasChildrenLink = true;
        boolean hasSiblingsLink = true;
        boolean hasParentLink = true;
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyDetailsById(10,baseURL);
        assertThat(node.isPresent(),is(true));
        assertNodeDetail(10L,node.get(),hasChildrenLink,hasSiblingsLink,hasParentLink);
    }

    @Test
    public void getTaxonomyDetailsByIdWithABottomNodeReturnWithoutChildrenLink() {
        boolean hasChildrenLink = false;
        boolean hasSiblingsLink = true;
        boolean hasParentLink = true;
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyDetailsById(1222,baseURL);
        assertThat(node.isPresent(),is(true));
        assertNodeDetail(1222L,node.get(),hasChildrenLink,hasSiblingsLink,hasParentLink);
    }

    @Test
    public void getTaxonomyDetailsByIdWithAnInvalidIdNodeReturnOptionalEmpty() {
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyDetailsById(5,baseURL);
        assertThat(node.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyBaseNodeByIdWithAnInvalidIdNodeReturnOptionalEmpty() {
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyBaseNodeById(5);
        assertThat(node.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyBaseNodeByIdWithValidIdNodeReturnValidBaseNode() {
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyBaseNodeById(10L);
        assertThat(node.isPresent(),is(true));
        assertBaseNode(10L,node.get());
    }

    @Test
    public void getTaxonomyParentByIdWithIdThatDoesNoteReturnParent() {
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyParentById(1L);
        assertThat(node.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyParentByIdWithIdThatReturnValidParent() {
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyParentById(10L);
        assertThat(node.isPresent(),is(true));
        assertBaseNode(1L,node.get());
    }

    @Test
    public void getTaxonomySiblingsByIdWithIdThatReturnTwoSiblings() {
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomySiblingsById(10L);
        assertTaxonomiesResult(nodeOptional,2,2,null, 11);
    }

    @Test
    public void getTaxonomySiblingsByIdWithIdThatDoesNoteReturnSiblings() {
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomySiblingsById(1L);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyChildrenByIdThatReturnThreeChildren() {
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyChildrenById(1L);
        assertTaxonomiesResult(nodeOptional,3,3,null, 10);
    }

    @Test
    public void getTaxonomyChildrenByIdWithBottomIdThatDoesNoteReturnChildren() {
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomyChildrenById(1233L);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyDetailsByNameWithEqualsToValidExactName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("EQUALSTO");
        nameParams.setTaxonomyName("equals to only");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertTaxonomiesResult(nodeOptional,1,1,nameParams, 10000);
    }

    @Test
    public void getTaxonomyDetailsByNameWithEqualsToInvalidSimilarNames() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("EQUALSTO");
        nameParams.setTaxonomyName("equalsto only");
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyDetailsByNameWithEqualsToSmallNamesReturnValidTaxonomies() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("EQUALSTO");
        nameParams.setTaxonomyName("sn");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertTaxonomiesResult(nodeOptional,1,1,nameParams, 10000005);
    }

    @Test
    public void getTaxonomyDetailsByNameEqualsToInValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("EQUALSTO");
        nameParams.setTaxonomyName("INVALID");
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyDetailsByNameContainsValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("CONTAINS");
        nameParams.setTaxonomyName("name");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertTaxonomiesResult(nodeOptional,4,4,nameParams, 100000);
    }

    @Test
    public void getTaxonomyDetailsByNameContainsInValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("CONTAINS");
        nameParams.setTaxonomyName("invalid");
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyDetailsByNameEndsWithValidName()  {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("ENDSWITH");
        nameParams.setTaxonomyName("ended");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertTaxonomiesResult(nodeOptional,4,4,nameParams, 1000000);
    }

    @Test
    public void getTaxonomyDetailsByNameEndsWithInValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("ENDSWITH");
        nameParams.setTaxonomyName("INVALID");
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyDetailsByNameStartsWithValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("STARTSWITH");
        nameParams.setTaxonomyName("start");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertTaxonomiesResult(nodeOptional,4,4,nameParams, 1000000);
    }

    @Test
    public void getTaxonomyDetailsByNameSecondPageWithValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("CONTAINS");
        nameParams.setTaxonomyName("common");
        nameParams.setFieldName("commonName");
        nameParams.setPageNumber("2");
        nameParams.setPageSize("10");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertTaxonomiesResult(nodeOptional,10,50,nameParams, 120);
    }

    @Test
    public void getTaxonomyDetailsByNameLastPageWithValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("CONTAINS");
        nameParams.setTaxonomyName("common");
        nameParams.setFieldName("commonName");
        nameParams.setPageNumber("5");
        nameParams.setPageSize("10");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertTaxonomiesResult(nodeOptional,10,50,nameParams, 10000);
    }

    @Test
    public void getTaxonomyDetailsByNameInvalidPageWithValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("CONTAINS");
        nameParams.setTaxonomyName("common");
        nameParams.setFieldName("commonName");
        nameParams.setPageNumber("6");
        nameParams.setPageSize("10");
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyDetailsByCommonNameContainsWithValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("CONTAINS");
        nameParams.setTaxonomyName("common");
        nameParams.setFieldName("commonName");
        nameParams.setPageNumber("2");
        nameParams.setPageSize("10");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertTaxonomiesResult(nodeOptional,10,50,nameParams, 120);
    }

    @Test
    public void getTaxonomyDetailsByNameStartsWithInValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("STARTSWITH");
        nameParams.setTaxonomyName("invalid");
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomyDetailsByName(nameParams,baseURL);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyNodesByNameWithEqualsToValidExactName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("EQUALSTO");
        nameParams.setTaxonomyName("equals to only");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyNodesByName(nameParams,baseURL);
        assertTaxonomiesResult(nodeOptional,1,1,nameParams, 10000);
    }

    @Test
    public void getTaxonomyNodesByNameWithEqualsToInvalidSimilarNames() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("EQUALSTO");
        nameParams.setTaxonomyName("equalsto only");
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomyNodesByName(nameParams,baseURL);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyNodesByNameWithEqualsToSmallNamesReturnValidTaxonomies() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("EQUALSTO");
        nameParams.setTaxonomyName("sn");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyNodesByName(nameParams,baseURL);
        assertTaxonomiesResult(nodeOptional,1,1,nameParams, 10000005);
    }

    @Test
    public void getTaxonomyNodesByNameEqualsToInValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("EQUALSTO");
        nameParams.setTaxonomyName("INVALID");
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomyNodesByName(nameParams,baseURL);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyNodesByNameContainsValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("CONTAINS");
        nameParams.setTaxonomyName("name");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyNodesByName(nameParams,baseURL);
        assertTaxonomiesResult(nodeOptional,4,4,nameParams, 100000);
    }

    @Test
    public void getTaxonomyNodesByNameContainsInValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("CONTAINS");
        nameParams.setTaxonomyName("invalid");
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomyNodesByName(nameParams,baseURL);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyNodesByNameEndsWithValidName()  {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("ENDSWITH");
        nameParams.setTaxonomyName("ended");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyNodesByName(nameParams,baseURL);
        assertTaxonomiesResult(nodeOptional,4,4,nameParams, 1000000);
    }

    @Test
    public void getTaxonomyNodesByNameEndsWithInValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("ENDSWITH");
        nameParams.setTaxonomyName("INVALID");
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomyNodesByName(nameParams,baseURL);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyNodesByNameStartsWithValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("STARTSWITH");
        nameParams.setTaxonomyName("start");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyNodesByName(nameParams,baseURL);
        assertTaxonomiesResult(nodeOptional,4,4,nameParams, 1000000);
    }

    @Test
    public void getTaxonomyNodesByNameSecondPageWithValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("CONTAINS");
        nameParams.setTaxonomyName("common");
        nameParams.setFieldName("commonName");
        nameParams.setPageNumber("2");
        nameParams.setPageSize("10");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyNodesByName(nameParams,baseURL);
        assertTaxonomiesResult(nodeOptional,10,50,nameParams, 120);
    }

    @Test
    public void getTaxonomyNodesByNameLastPageWithValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("CONTAINS");
        nameParams.setTaxonomyName("common");
        nameParams.setFieldName("commonName");
        nameParams.setPageNumber("5");
        nameParams.setPageSize("10");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyNodesByName(nameParams,baseURL);
        assertTaxonomiesResult(nodeOptional,10,50,nameParams, 10000);
    }

    @Test
    public void getTaxonomyNodesByNameInvalidPageWithValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("CONTAINS");
        nameParams.setTaxonomyName("common");
        nameParams.setFieldName("commonName");
        nameParams.setPageNumber("6");
        nameParams.setPageSize("10");
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomyNodesByName(nameParams,baseURL);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyNodesByCommonNameContainsWithValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("CONTAINS");
        nameParams.setTaxonomyName("common");
        nameParams.setFieldName("commonName");
        nameParams.setPageNumber("2");
        nameParams.setPageSize("10");
        Optional<Taxonomies> nodeOptional = neo4jDataAccess.getTaxonomyNodesByName(nameParams,baseURL);
        assertTaxonomiesResult(nodeOptional,10,50,nameParams, 120);
    }

    @Test
    public void getTaxonomyNodesByNameStartsWithInValidName() {
        NameRequestParams nameParams = new NameRequestParams();
        nameParams.setSearchType("STARTSWITH");
        nameParams.setTaxonomyName("invalid");
        Optional<Taxonomies> nodes = neo4jDataAccess.getTaxonomyNodesByName(nameParams,baseURL);
        assertThat(nodes.isPresent(),is(false));
    }

    @Test
    public void checkRelationshipBetweenTaxonomiesInvalidFromIdReturnNull() {
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomiesRelationship(5L,10L);
        assertThat(node.isPresent(),is(false));
    }

    @Test
    public void checkRelationshipBetweenTaxonomiesInvalidToIdReturnNull() {
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomiesRelationship(1L,6L);
        assertThat(node.isPresent(),is(false));
    }

    @Test
    public void checkRelationshipBetweenTaxonomiesReturnOnlyParentRelationShip() {
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomiesRelationship(10000000L,1L);
        assertThat(node.isPresent(),is(true));
        assertNodePath(10000000L,node.get(),0,7);
    }

    @Test
    public void checkRelationshipBetweenTaxonomiesReturnOnlyChildrenRelationShip() {
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomiesRelationship(1L,10000000L);
        assertThat(node.isPresent(),is(true));
        assertNodePath(1L,node.get(),7,0);
    }

    @Test
    public void checkRelationshipBetweenTaxonomiesReturnParentAndChildrenRelationShip() {
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomiesRelationship(10000000,1222L);
        assertThat(node.isPresent(),is(true));
        assertNodePath(10000000L,node.get(),3,7);
    }

    @Test
    public void getTaxonomyPathWithInvalidIdReturnNull() {
        PathRequestParams params = new PathRequestParams();
        params.setDepth(1);
        params.setDirection("bottom");
        params.setId("5");
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyPath(params);
        assertThat(node.isPresent(),is(false));
    }

    @Test
    public void getTaxonomyPathWithBottomDirectionOneDepth() {
        PathRequestParams params = new PathRequestParams();
        params.setDepth(1);
        params.setDirection("bottom");
        params.setId("1");
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyPath(params);
        assertThat(node.isPresent(),is(true));
        assertNodePath(1L,node.get(),1,0);
    }

    @Test
    public void getTaxonomyPathWithBottomDirectionFiveDepth() {
        PathRequestParams params = new PathRequestParams();
        params.setDepth(5);
        params.setDirection("bottom");
        params.setId("1");
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyPath(params);
        assertThat(node.isPresent(),is(true));
        assertNodePath(1L,node.get(),5,0);
    }

    @Test
    public void getTaxonomyPathWithBottomDirectionFiveDepthThatThatHasBottominThreeDepth() {
        PathRequestParams params = new PathRequestParams();
        params.setDepth(5);
        params.setDirection("bottom");
        params.setId("10000");
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyPath(params);
        assertThat(node.isPresent(),is(true));
        assertNodePath(10000L,node.get(),3,0);
    }

    @Test
    public void getTaxonomyPathWithTopDirectionUntilRootDepthThatReturnOneDepth() {
        PathRequestParams params = new PathRequestParams();
        params.setDepth(3);
        params.setDirection("top");
        params.setId("10");
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyPath(params);
        assertThat(node.isPresent(),is(true));
        assertNodePath(10L,node.get(),0,1);
    }

    @Test
    public void getTaxonomyPathWithTopDirection()  {
        PathRequestParams params = new PathRequestParams();
        params.setDirection("top");
        params.setId("10000000");
        Optional<TaxonomyNode> node = neo4jDataAccess.getTaxonomyPath(params);
        assertThat(node.isPresent(),is(true));
        assertNodePath(10000000L,node.get(),0,7);
    }

    @Test
    public void getTaxonomyHistoricalChangeThatReturnNewId() {
        Optional<Long> newId = neo4jDataAccess.getTaxonomyHistoricalChange(9L);
        assertThat(newId.isPresent(),is(true));
        assertThat(newId.get(), is(10L));
    }

    @Test
    public void getTaxonomyHistoricalChangeThatDoesNotReturnNewId() {
        Optional<Long> newId = neo4jDataAccess.getTaxonomyHistoricalChange(5L);
        assertThat(newId.isPresent(), is(false));
    }

    @Test
    public void getTaxonomyLineageWithRootNodeThatReturnOnlyRootLineage() {
        Optional<Taxonomies> taxonomies = neo4jDataAccess.getTaxonomyLineageById(1L);
        assertThat(taxonomies.isPresent(), is(true));
        Taxonomies nodes = taxonomies.get();
        assertThat(nodes.getTaxonomies(),notNullValue());
        assertThat(nodes.getTaxonomies().size(),is(1));
        assertThat(nodes.getTaxonomies().get(0).getTaxonomyId(),is(1L));
    }

    @Test
    public void getTaxonomyLineageWithInvalidIdThatDoesNotReturnLineage() {
        Optional<Taxonomies> taxonomies = neo4jDataAccess.getTaxonomyLineageById(5L);
        assertThat(taxonomies.isPresent(), is(false));
    }

    @Test
    public void getTaxonomyLineageWithValidIdThatReturnLineage() {
        Optional<Taxonomies> taxonomies = neo4jDataAccess.getTaxonomyLineageById(10000000L);
        assertThat(taxonomies.isPresent(), is(true));

    }

    @Test
    public void getTaxonomyAncestorWithValidIdsThatReturnCorrectAncestor() {
        Long[] ids = {1000L,1001L,1002L,1010L,1011L};
        ArrayList<Long> idsArray = new ArrayList<>(Arrays.asList(ids));
        Optional<TaxonomyNode> taxonomy = neo4jDataAccess.getTaxonomyAncestorFromTaxonomyIds(idsArray);

        assertThat(taxonomy.isPresent(), is(true));
        assertBaseNode(10L,taxonomy.get());
    }

    @Test
    public void getTaxonomyAncestorWithValidIdsAndIsPartOfIdsThatReturnCorrectAncestor() {
        Long[] ids = {10L,1000L,1001L,1002L,1010L,1011L};
        ArrayList<Long> idsArray = new ArrayList<>(Arrays.asList(ids));
        Optional<TaxonomyNode> taxonomy = neo4jDataAccess.getTaxonomyAncestorFromTaxonomyIds(idsArray);

        assertThat(taxonomy.isPresent(), is(true));
        assertBaseNode(10L,taxonomy.get());
    }

    @Test
    public void getTaxonomyAncestorWithInvalidIdsReturnEmptyResult() {
        Long[] ids = {10L,8L};
        ArrayList<Long> idsArray = new ArrayList<>(Arrays.asList(ids));
        Optional<TaxonomyNode> taxonomy = neo4jDataAccess.getTaxonomyAncestorFromTaxonomyIds(idsArray);

        assertThat(taxonomy.isPresent(), is(false));
    }

    private void assertTaxonomiesResult(Optional<Taxonomies> nodeOptional,int size,int totalRecords, NameRequestParams
            nameParams, long
     firstTaxonomyId) {
        assertThat(nodeOptional.isPresent(),is(true));
        Taxonomies nodes = nodeOptional.get();
        assertThat(nodes.getTaxonomies(),notNullValue());
        assertThat(nodes.getTaxonomies().size(),is(size));
        Collections.sort(nodes.getTaxonomies());
        assertBaseNode(firstTaxonomyId,nodes.getTaxonomies().get(0));
        if(nameParams != null) {
            assertThat(nodes.getPageInfo(), notNullValue());
            assertThat(nodes.getPageInfo().getCurrentPage(), is(Integer.parseInt(nameParams.getPageNumber())));
            assertThat(nodes.getPageInfo().getResultsPerPage(), is(nameParams.getPageSizeInt()));
            assertThat(nodes.getPageInfo().getTotalRecords(), is(totalRecords));
        }
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