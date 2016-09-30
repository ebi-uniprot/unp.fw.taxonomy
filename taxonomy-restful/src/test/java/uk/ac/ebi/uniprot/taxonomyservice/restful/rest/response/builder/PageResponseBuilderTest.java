package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.builder;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl.FakeTaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;
import uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants;
import uk.ac.ebi.uniprot.taxonomyservice.restful.util.BeanCreatorUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class is responsible to test build {@link Response} object for search requests
 *
 * Created by lgonzales on 27/09/16.
 */
public class PageResponseBuilderTest {

    private static final String BASE_URL = "http://localhost:9090/uniprot/services/restful/taxonomy/id/";

    private static FakeTaxonomyDataAccess neo4jDataAccess;

    @BeforeClass
    public static void setUpAndLoadMockDataFromCSVFile() {
        neo4jDataAccess = new FakeTaxonomyDataAccess("");
    }

    @AfterClass
    public static void tearDown() {
        neo4jDataAccess.getNeo4jDb().shutdown();
    }

    @Test(expected=IllegalStateException.class)
    public void buildResponseWithoutRequiredAttributes() {
        new PageResponseBuilder().buildResponse();
    }

    @Test(expected=IllegalStateException.class)
    public void buildResponseWithoutRequiredAttributesForErrorOrRedirectValidation() {
        Map<String,Long> requestId = new HashMap<>();
        HttpServletRequest request = mock(HttpServletRequest.class);

        new PageResponseBuilder()
                .setIdsForHistoricalCheck(requestId)
                .setEntity(Optional.empty())
                .setRequest(request)
                .setNotFoundErrorMessage(TaxonomyConstants.API_RESPONSE_404_ENTRY)
                .buildResponse();
    }

    @Test
    public void buildResponseThatReturnTaxonomyNodeSuccess() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Optional<TaxonomyNode> responseBody = BeanCreatorUtil.getOptionalTaxonomyNode(11);

        Response response = new PageResponseBuilder()
                .setEntity(responseBody)
                .setRequest(request)
                .setNotFoundErrorMessage(TaxonomyConstants.API_RESPONSE_404_ENTRY)
                .buildResponse();

        assertThat(response.getEntity(),instanceOf(TaxonomyNode.class));
        TaxonomyNode builtBody = (TaxonomyNode) response.getEntity();
        assertThat(builtBody.getTaxonomyId(),is(11L));
    }

    @Test
    public void buildResponseThatReturnTaxonomiesSuccess() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Optional<Taxonomies> responseBody = BeanCreatorUtil.getOptionalTaxonomies(11L,12L);

        Response response = new PageResponseBuilder()
                .setEntity(responseBody)
                .setRequest(request)
                .setNotFoundErrorMessage(TaxonomyConstants.API_RESPONSE_404_ENTRY)
                .buildResponse();

        assertThat(response.getEntity(),instanceOf(Taxonomies.class));
        Taxonomies builtBody = (Taxonomies) response.getEntity();
        assertThat(builtBody.getTaxonomies(),notNullValue());
        assertThat(builtBody.getTaxonomies().isEmpty(),is(false));
        assertThat(builtBody.getTaxonomies().size(),is(2));
    }

    @Test
    public void buildResponseThatReturnError() {
        Map<String,Long> requestId = new HashMap<>();
        requestId.put(null,14L);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer(BASE_URL));

        Response response = new PageResponseBuilder()
                .setIdsForHistoricalCheck(requestId)
                .setEntity(Optional.empty())
                .setDataAccess(neo4jDataAccess)
                .setRequest(request)
                .setNotFoundErrorMessage(TaxonomyConstants.API_RESPONSE_404_ENTRY)
                .buildResponse();

        assertThat(response.getEntity(),instanceOf(ErrorMessage.class));
        ErrorMessage builtBody = (ErrorMessage) response.getEntity();
        assertThat(builtBody.getRequestedURL(),is(BASE_URL));
        assertThat(builtBody.getErrorMessages(),is(Arrays.asList(TaxonomyConstants.API_RESPONSE_404_ENTRY)));
    }

    @Test
    public void buildResponseThatReturnRedirect() {
        Map<String,Long> requestId = new HashMap<>();
        requestId.put(null,9L);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer(BASE_URL+"9"));

        Response response = new PageResponseBuilder()
                .setIdsForHistoricalCheck(requestId)
                .setEntity(Optional.empty())
                .setDataAccess(neo4jDataAccess)
                .setRequest(request)
                .setNotFoundErrorMessage(TaxonomyConstants.API_RESPONSE_404_ENTRY)
                .buildResponse();

        assertThat(response.getEntity(),instanceOf(ErrorMessage.class));
        ErrorMessage builtBody = (ErrorMessage) response.getEntity();
        assertThat(builtBody.getRequestedURL(),is(BASE_URL+"9"));
        assertThat(builtBody.getErrorMessages().get(0),is(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10")));
        assertThat(response.getHeaderString(HttpHeaders.LOCATION),is(BASE_URL+"10"));
    }

    @Test
    public void buildResponseThatReturnRedirectForRelationshipEndpoint() {
        Map<String,Long> requestId = new HashMap<>();
        requestId.put("from",9L);
        requestId.put("to",99L);
        String requestedURL = "http://localhost:9090/uniprot/services/restful/taxonomy/relationship?from=9&to=99";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer(requestedURL));

        Response response = new PageResponseBuilder()
                .setIdsForHistoricalCheck(requestId)
                .setEntity(Optional.empty())
                .setDataAccess(neo4jDataAccess)
                .setRequest(request)
                .setNotFoundErrorMessage(TaxonomyConstants.API_RESPONSE_404_ENTRY)
                .buildResponse();

        assertThat(response.getEntity(),instanceOf(ErrorMessage.class));
        ErrorMessage builtBody = (ErrorMessage) response.getEntity();
        assertThat(builtBody.getRequestedURL(),is(requestedURL));
        assertThat(builtBody.getErrorMessages().get(0),is(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10")));
        assertThat(builtBody.getErrorMessages().get(1),is(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","100")));
        String expectedURL = "http://localhost:9090/uniprot/services/restful/taxonomy/relationship?from=10&to=100";
        assertThat(response.getHeaderString(HttpHeaders.LOCATION),is(expectedURL));
    }

}