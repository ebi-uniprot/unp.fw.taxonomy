package uk.ac.ebi.uniprot.taxonomyservice.restful.rest.filter;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.RestContainer;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.SwaggerConstant;
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


    private static final String TAXONOMY_BASE_PATH = "/taxonomy";

    @ClassRule
    public static RestContainer restContainer = new RestContainer();


    @Test
    public void jsonFormatQueryParameterReturnJsonAcceptHeader() throws Exception{
        String requestedURL = TAXONOMY_BASE_PATH + "/id/12345?format=json";

        ExtractableResponse<com.jayway.restassured.response.Response> response = RestAssured.when()
                .get(requestedURL)
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(MediaType.APPLICATION_JSON));
    }


    @Test
    public void xmlFormatQueryParameterReturnXmlAcceptHeader() throws Exception{
        String requestedURL = TAXONOMY_BASE_PATH + "/id/12345?format=xml";

        ExtractableResponse<com.jayway.restassured.response.Response> response = RestAssured.when()
                .get(requestedURL)
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(MediaType.APPLICATION_XML));
    }

    @Test
    public void withoutFormatAndAcceptHeaderParametersReturnDefaultJsonAcceptHeader() throws Exception{
        String requestedURL = TAXONOMY_BASE_PATH + "/id/12345";

        ExtractableResponse<com.jayway.restassured.response.Response> response = RestAssured.when()
                .get(requestedURL)
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(MediaType.APPLICATION_JSON));
    }

    @Test
    public void validJsonAcceptHeaderHasPriorityOverXmlFormatParamReturnJsonAcceptHeader() throws Exception{
        String requestedURL = TAXONOMY_BASE_PATH + "/id/12345?format=xml";

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
    public void validJsonAcceptHeaderHasPriorityOverJsonFormatParamReturnJsonAcceptHeader() throws Exception{
        String requestedURL = TAXONOMY_BASE_PATH + "/id/12345?format=json";

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
    public void xmlFormatParamHasPriorityOverInvalidAtomAcceptHeaderAddXmlAcceptHeader() throws Exception{
        String requestedURL = TAXONOMY_BASE_PATH + "/id/12345?format=xml";

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
    public void jsonFormatParamHasPriorityOverInvalidAtomAcceptHeaderAddXmlAcceptHeader() throws Exception{
        String requestedURL = TAXONOMY_BASE_PATH + "/id/12345?format=json";

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
    public void invalidFormatParamReturnErrorMessageWithdefaultJsonContentType() throws Exception{
        String requestedURL = TAXONOMY_BASE_PATH + "/id/12345?format=INVALID";

        ExtractableResponse<com.jayway.restassured.response.Response> response = RestAssured.when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        assertThat(response, notNullValue());

        ErrorMessage expectedErrorMessage = new ErrorMessage();
        expectedErrorMessage.setRequestedURL(restContainer.baseURL+requestedURL);
        expectedErrorMessage.addErrorMessage(SwaggerConstant.REQUEST_PARAMETER_INVALID_VALUE.replace
                ("{parameterName}","format"));

        ResponseAssert.assertResponseErrorMessage(expectedErrorMessage,response);
    }

}
