package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.filter;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.RestContainer;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants;
import uk.ac.ebi.uniprot.taxonomyservice.restful.util.ResponseAssert;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.ExtractableResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.junit.ClassRule;
import org.junit.Test;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Class that implement integration tests for FilterResourceURL use cases
 *
 * Created by lgonzales on 05/04/16.
 */
public class FilterResourceURLIT {


    private static final String TAXONOMY_BASE_PATH = "";

    @ClassRule
    public static final RestContainer restContainer = new RestContainer();


    @Test
    public void assertJsonFormatQueryParameterReturnJsonAcceptHeader() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/10?format=json";

        ExtractableResponse<com.jayway.restassured.response.Response> response = RestAssured.when()
                .get(requestedURL)
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(MediaType.APPLICATION_JSON));
    }


    @Test
    public void assertXmlFormatQueryParameterReturnXmlAcceptHeader() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/10?format=xml";

        ExtractableResponse<com.jayway.restassured.response.Response> response = RestAssured.when()
                .get(requestedURL)
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(MediaType.APPLICATION_XML));
    }

    @Test
    public void assertWithoutFormatAndAcceptHeaderParametersReturnDefaultJsonAcceptHeader() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/10";

        ExtractableResponse<com.jayway.restassured.response.Response> response = RestAssured.when()
                .get(requestedURL)
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(MediaType.APPLICATION_JSON));
    }

    @Test
    public void assertValidJsonAcceptHeaderHasPriorityOverXmlFormatParamReturnJsonAcceptHeader() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/10?format=xml";

        ExtractableResponse<com.jayway.restassured.response.Response> response = RestAssured.given()
                .header(HttpHeaders.ACCEPT,MediaType.APPLICATION_JSON)
                .when()
                .get(requestedURL)
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(MediaType.APPLICATION_JSON));
    }

    @Test
    public void assertValidJsonAcceptHeaderHasPriorityOverJsonFormatParamReturnJsonAcceptHeader() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/10?format=json";

        ExtractableResponse<com.jayway.restassured.response.Response> response = RestAssured.given()
                .header(HttpHeaders.ACCEPT,MediaType.APPLICATION_XML)
                .when()
                .get(requestedURL)
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(MediaType.APPLICATION_XML));
    }

    @Test
    public void assertXmlFormatParamHasPriorityOverInvalidAtomAcceptHeaderAddXmlAcceptHeader() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/10?format=xml";

        ExtractableResponse<com.jayway.restassured.response.Response> response = RestAssured.given()
                .header(HttpHeaders.ACCEPT,MediaType.APPLICATION_ATOM_XML)
                .when()
                .get(requestedURL)
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(MediaType.APPLICATION_XML));
    }

    @Test
    public void assertJsonFormatParamHasPriorityOverInvalidAtomAcceptHeaderAddXmlAcceptHeader() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/10?format=json";

        ExtractableResponse<com.jayway.restassured.response.Response> response = RestAssured.given()
                .header(HttpHeaders.ACCEPT,MediaType.APPLICATION_ATOM_XML)
                .when()
                .get(requestedURL)
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(MediaType.APPLICATION_JSON));
    }

    @Test
    public void assertInvalidFormatParamReturnErrorMessageWithdefaultJsonContentType() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/10?format=INVALID";

        ExtractableResponse<com.jayway.restassured.response.Response> response = RestAssured.when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        assertThat(response, notNullValue());

        ErrorMessage expectedErrorMessage = new ErrorMessage();
        expectedErrorMessage.setRequestedURL(restContainer.baseURL+requestedURL);
        expectedErrorMessage.addErrorMessage(TaxonomyConstants.REQUEST_PARAMETER_INVALID_VALUE.replace
                ("{parameterName}","format"));

        ResponseAssert.assertResponseErrorMessage(expectedErrorMessage,response);
    }

}
