package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.builder;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl.FakeTaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;
import uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants;
import uk.ac.ebi.uniprot.taxonomyservice.restful.util.BeanCreatorUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class is responsible to test response {@link Response} object for search by id list requests
 *
 * Created by lgonzales on 27/09/16.
 */
public class ListResponseBuilderTest {

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
        new ListResponseBuilder().buildResponse();
    }

    @Test(expected=IllegalStateException.class)
    public void buildResponseWithoutRequiredAttributesForErrorOrRedirectValidation() {
        List<String> requestedIds = Arrays.asList("9","11");
        Optional<Taxonomies> responseBody = BeanCreatorUtil.getOptionalTaxonomies(11L);

        new ListResponseBuilder()
                .setRequestedIds(requestedIds)
                .setEntity(responseBody)
                .buildResponse();
    }

    @Test
    public void buildResponseWithOnlyValidTaxonomyNodeValues() {
        List<String> requestedIds = Arrays.asList("11","12");
        Optional<Taxonomies> responseBody = BeanCreatorUtil.getOptionalTaxonomies(11L,12L);

        Response response = new ListResponseBuilder()
                .setRequestedIds(requestedIds)
                .setEntity(responseBody)
                .setDataAccess(neo4jDataAccess)
                .buildResponse();

        assertThat(response.getEntity(),instanceOf(Taxonomies.class));
        Taxonomies builtBody = (Taxonomies) response.getEntity();
        assertThat(builtBody.getTaxonomies(),notNullValue());
        assertThat(builtBody.getTaxonomies().isEmpty(),is(false));
        assertThat(builtBody.getTaxonomies().size(),is(2));
        assertThat(builtBody.getErrors(),nullValue());
        assertThat(builtBody.getRedirects(),nullValue());
    }

    @Test
    public void buildResponseWithOnlyRedirectValues() {
        List<String> requestedIds = Arrays.asList("9","99");
        Optional<Taxonomies> responseBody = BeanCreatorUtil.getOptionalTaxonomies();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer(BASE_URL));

        Response response = new ListResponseBuilder()
                .setRequestedIds(requestedIds)
                .setEntity(responseBody)
                .setRequest(request)
                .setNotFoundErrorMessage(TaxonomyConstants.API_RESPONSE_404_ENTRY)
                .setDataAccess(neo4jDataAccess)
                .buildResponse();

        assertThat(response.getEntity(),instanceOf(Taxonomies.class));
        Taxonomies builtBody = (Taxonomies) response.getEntity();
        assertThat(builtBody.getTaxonomies(),nullValue());
        assertThat(builtBody.getErrors(),nullValue());
        assertThat(builtBody.getRedirects(),notNullValue());
        assertThat(builtBody.getRedirects().isEmpty(),is(false));
        assertThat(builtBody.getRedirects().size(),is(2));
    }

    @Test
    public void buildResponseWithOnlyErrorsValues() {
        List<String> requestedIds = Arrays.asList("14","15");
        Optional<Taxonomies> responseBody = BeanCreatorUtil.getOptionalTaxonomies();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer(BASE_URL));

        Response response = new ListResponseBuilder()
                .setRequestedIds(requestedIds)
                .setEntity(responseBody)
                .setRequest(request)
                .setNotFoundErrorMessage(TaxonomyConstants.API_RESPONSE_404_ENTRY)
                .setDataAccess(neo4jDataAccess)
                .buildResponse();

        assertThat(response.getEntity(),instanceOf(Taxonomies.class));
        Taxonomies builtBody = (Taxonomies) response.getEntity();
        assertThat(builtBody.getTaxonomies(),nullValue());
        assertThat(builtBody.getRedirects(),nullValue());
        assertThat(builtBody.getErrors(),notNullValue());
        assertThat(builtBody.getErrors().isEmpty(),is(false));
        assertThat(builtBody.getErrors().size(),is(2));
    }

    @Test
    public void buildResponseWithValidTaxonomyWithErrorsAndWithRedirectValues() {
        List<String> requestedIds = Arrays.asList("14","9","11");
        Optional<Taxonomies> responseBody = BeanCreatorUtil.getOptionalTaxonomies(11);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer(BASE_URL));

        Response response = new ListResponseBuilder()
                .setRequestedIds(requestedIds)
                .setEntity(responseBody)
                .setRequest(request)
                .setNotFoundErrorMessage(TaxonomyConstants.API_RESPONSE_404_ENTRY)
                .setDataAccess(neo4jDataAccess)
                .buildResponse();

        assertThat(response.getEntity(),instanceOf(Taxonomies.class));
        Taxonomies builtBody = (Taxonomies) response.getEntity();
        assertThat(builtBody.getTaxonomies(),notNullValue());
        assertThat(builtBody.getTaxonomies().isEmpty(),is(false));
        assertThat(builtBody.getTaxonomies().size(),is(1));
        assertThat(builtBody.getRedirects(),notNullValue());
        assertThat(builtBody.getRedirects().isEmpty(),is(false));
        assertThat(builtBody.getRedirects().size(),is(1));
        assertThat(builtBody.getErrors(),notNullValue());
        assertThat(builtBody.getErrors().isEmpty(),is(false));
        assertThat(builtBody.getErrors().size(),is(1));
    }

}