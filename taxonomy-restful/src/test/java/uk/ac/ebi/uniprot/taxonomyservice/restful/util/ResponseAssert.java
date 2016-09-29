package uk.ac.ebi.uniprot.taxonomyservice.restful.util;

import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.internal.mapper.ObjectMapperType;
import com.jayway.restassured.response.ExtractableResponse;
import java.util.Collections;
import javax.ws.rs.core.Response;
import org.junit.Assert;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertTrue;

/**
 * This class is responsible to assert Taxonomy response objects
 *
 * Created by lgonzales on 31/03/16.
 */
public class ResponseAssert {

    public static final String REQUEST_URL = "http://localhost:12345/rest/test";

    public static void assertResponseErrorMessage(ErrorMessage expectedError, Response response) {
        assertThat(response.getEntity(), notNullValue());
        assertTrue(response.getEntity() instanceof ErrorMessage);

        ErrorMessage errorResponse = (ErrorMessage) response.getEntity();
        assertResponseErrorMessage(expectedError, errorResponse);
    }

    public static void assertResponseErrorMessage(ErrorMessage expectedError, ExtractableResponse<com.jayway.restassured.response.Response> response) {
        assertThat(response, notNullValue());

        ErrorMessage errorResponse = null;
        if(response.contentType().isEmpty()){
            errorResponse = response.as(ErrorMessage.class,ObjectMapperType.JACKSON_2);
        }else{
            errorResponse = response.as(ErrorMessage.class);
        }

        assertResponseErrorMessage(expectedError, errorResponse);
    }

    public static void assertTaxonomyNodeAttributesHasValues(TaxonomyNode node, long taxonomyId, boolean checkLinks) {
        assertThat(node.getTaxonomyId(), equalTo(taxonomyId));
        assertThat(node.getCommonName(), not(isEmptyOrNullString()));
        assertThat(node.getMnemonic(), not(isEmptyOrNullString()));
        assertThat(node.getRank(), not(isEmptyOrNullString()));
        assertThat(node.getScientificName(), not(isEmptyOrNullString()));
        assertThat(node.getSynonym(), not(isEmptyOrNullString()));
        if (checkLinks) {
            assertThat(node.getParentLink(), not(isEmptyOrNullString()));

            assertThat(node.getChildrenLinks(), notNullValue());
            assertThat(node.getChildrenLinks(), not(emptyIterable()));

            assertThat(node.getSiblingsLinks(), notNullValue());
            assertThat(node.getSiblingsLinks(), not(emptyIterable()));
        }
    }

    public static void assertTaxonomiesListResponse(ExtractableResponse<com.jayway.restassured.response.Response>
            response, ContentType contentType,Integer taxonomiesSize,Long firstTaxonomiesId,Integer redirectSize, Long
            firstRedirectId,Integer errorSize, Long firstErrorId,boolean checkLinks){
        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(contentType.toString()));

        Taxonomies taxonomies = response.as(Taxonomies.class);

        if(taxonomiesSize != null && taxonomiesSize > 0) {
            assertThat(taxonomies.getTaxonomies(), notNullValue());
            assertThat(taxonomies.getTaxonomies().isEmpty(), is(false));
            assertThat(taxonomies.getTaxonomies().size(), is(taxonomiesSize));
            assertTaxonomyNodeAttributesHasValues(taxonomies.getTaxonomies().get(0),firstTaxonomiesId,checkLinks);
        }else{
            assertThat(taxonomies.getTaxonomies(),nullValue());
        }

        if(redirectSize != null && redirectSize > 0) {
            assertThat(taxonomies.getRedirects(), notNullValue());
            assertThat(taxonomies.getRedirects().isEmpty(), is(false));
            assertThat(taxonomies.getRedirects().size(), is(redirectSize));
            assertThat(taxonomies.getRedirects().get(0).getRequestedId(),notNullValue());
            assertThat(taxonomies.getRedirects().get(0).getRedirectLocation(),notNullValue());
            assertThat(taxonomies.getRedirects().get(0).getRequestedId(),is(firstRedirectId));

        }else{
            assertThat(taxonomies.getRedirects(),nullValue());
        }

        if(errorSize != null && errorSize > 0) {
            assertThat(taxonomies.getErrors(), notNullValue());
            assertThat(taxonomies.getErrors().isEmpty(), is(false));
            assertThat(taxonomies.getErrors().size(), is(errorSize));
            assertThat(taxonomies.getErrors().get(0).getRequestedId(),notNullValue());
            assertThat(taxonomies.getErrors().get(0).getErrorMessage(),notNullValue());
            assertThat(taxonomies.getErrors().get(0).getRequestedId(),is(firstErrorId));
        }else{
            assertThat(taxonomies.getErrors(),nullValue());
        }

    }


    private static void assertResponseErrorMessage(ErrorMessage expectedError, ErrorMessage errorResponse) {
        assertThat(errorResponse, notNullValue());
        Assert.assertThat(errorResponse.getErrorMessages(),notNullValue());
        assertThat(errorResponse.getErrorMessages().size(), is(expectedError.getErrorMessages().size()));
        Collections.sort(errorResponse.getErrorMessages());
        Collections.sort(expectedError.getErrorMessages());
        assertThat(errorResponse, is(expectedError));
    }

}
