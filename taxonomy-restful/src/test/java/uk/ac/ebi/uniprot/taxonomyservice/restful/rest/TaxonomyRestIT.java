package uk.ac.ebi.uniprot.taxonomyservice.restful.rest;

import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;
import uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.SwaggerConstant;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ExtractableResponse;
import com.jayway.restassured.response.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.HttpHeaders;
import org.junit.ClassRule;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.SEE_OTHER;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;

/**
 * Class used to test Taxonomy service use cases
 *
 * Created by lgonzales on 24/02/16.
 */
public class TaxonomyRestIT {

    private static final String TAXONOMY_BASE_PATH = "/taxonomy";

    @ClassRule
    public static RestContainer restContainer = new RestContainer();

    /*
        START: Test with /taxonomy/id
     */

    @Test
    public void lookupTaxonomyIdWithEmptyIdReturnsNotFoundStatusWithErrorMessage() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_404);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyIdThatDoesNotExistReturnsNotFoundStatusWithErrorMessage() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/99999";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_404);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyIdWithInvalidIdReturnsBadRequestStatusInDefaultJsonFormat() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/INVALID";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.ID_PARAMETER_VALID_NUMBER);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyIdWithInvalidIdReturnsBadRequestStatusInJsonFormat() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/INVALID?format=json";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.ID_PARAMETER_VALID_NUMBER);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyIdWithInvalidIdReturnsBadRequestStatusInXmlFormat() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/INVALID?format=xml";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.ID_PARAMETER_VALID_NUMBER);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.XML,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyIdWithValidIdReturnsOKStatusInDefaultJsonFormatAndTheCorrectId() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response, ContentType.JSON, 12345, true);
    }

    @Test
    public void lookupTaxonomyIdWithValidIdReturnsOKStatusInJsonFormatAndTheCorrectId() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345?format=json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response, ContentType.JSON, 12345, true);
    }

    @Test
    public void lookupTaxonomyIdWithResourceXmlPathReturnsOKStatusXmlFormatAndTheCorrectId() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345?format=xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response, ContentType.XML, 12345, true);
    }

    @Test
    public void lookupTaxonomyIdWithHistoricalChangeReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/33333";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/55555") //redirect Location header
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_303.replace("{newId}","55555"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyIdWithHistoricalChangeAndXmlPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/33333?format=xml";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/55555?format=xml") //redirect Location header
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_303.replace("{newId}","55555"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.XML,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyIdWithHistoricalChangeAndJsonPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/33333?format=json";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/55555?format=json") //redirect Location header
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_303.replace("{newId}","55555"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

     /*
        END: Test with /taxonomy/id

        START: Test with /taxonomy/id/{subresources}
     */

    @Test
    public void lookupTaxonomyIdWithInvalidSubResourcePathReturnsNotFoundStatus() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/12345/invalid";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_404);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyChildrenWithInvalidTaxonomyIdReturnsNotFoundStatus() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/99999/children";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_404);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }


    @Test
    public void lookupTaxonomyChildrenWithHistoricalChangeReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/33333/children";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/55555/children") //redirect Location header
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_303.replace("{newId}","55555"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyChildrenWithDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/children")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.JSON,
                false);
    }

    @Test
    public void lookupTaxonomyChildrenWithJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/children?format=json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.JSON,
                false);
    }

    @Test
    public void lookupTaxonomyChildrenWithHistoricalChangeAndJsonPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/33333/children?format=json";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/55555/children?format=json")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_303.replace("{newId}","55555"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyChildrenWithXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/children?format=xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.XML,
                false);
    }

    @Test
    public void lookupTaxonomyChildrenWithHistoricalChangeAndXmlPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/33333/children?format=xml";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/55555/children?format=xml")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_303.replace("{newId}","55555"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.XML,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomySiblingsWithDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/siblings")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.JSON,
                false);
    }

    @Test
    public void lookupTaxonomySiblingsWithHistoricalChangeReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/33333/siblings";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/55555/siblings") //redirect Location header
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_303.replace("{newId}","55555"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomySiblingsWithJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/siblings?format=json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.JSON,
                false);
    }

    @Test
    public void lookupTaxonomySiblingsWithHistoricalChangeAndJsonPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/33333/siblings?format=json";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/55555/siblings?format=json")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_303.replace("{newId}","55555"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomySiblingsWithXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/siblings?format=xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.XML,
                false);
    }

    @Test
    public void lookupTaxonomySiblingsWithHistoricalChangeAndXmlPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/33333/siblings?format=xml";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/55555/siblings?format=xml")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_303.replace("{newId}","55555"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.XML,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyParentWithDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/parent")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response, ContentType.JSON, 999, false);
    }

    @Test
    public void lookupTaxonomyParentWithHistoricalChangeAndDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/33333/parent";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/55555/parent")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_303.replace("{newId}","55555"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyParentWithJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/parent?format=json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response, ContentType.JSON, 999, false);
    }

    @Test
    public void lookupTaxonomyParentWithHistoricalChangeAndJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/33333/parent?format=json";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/55555/parent?format=json")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_303.replace("{newId}","55555"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyParentWithXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> xmlResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/parent?format=xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(xmlResponse, ContentType.XML, 999, false);
    }

    @Test
    public void lookupTaxonomyParentWithHistoricalChangeAndXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/33333/parent?format=xml";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/55555/parent?format=xml")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_303.replace("{newId}","55555"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.XML,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyNameWithEmptyIdReturnsNotFoundStatusWithErrorMessage() {
        String requestedURL = TAXONOMY_BASE_PATH + "/name/";

        ExtractableResponse<Response> jsonResponse = when()
                .get(requestedURL)
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_404);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(jsonResponse, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyNameWithSmallNameReturnsBadRequestStatusWithErrorMessage() {
        String requestedURL = TAXONOMY_BASE_PATH + "/name/a";

        ExtractableResponse<Response> jsonResponse = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.NAME_PARAMETER_MIN_SIZE);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(jsonResponse, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyNameThatDoesNotExistReturnsNotFoundStatusWithErrorMessage() {
        String requestedURL = TAXONOMY_BASE_PATH + "/name/INVALID";

        ExtractableResponse<Response> jsonResponse = when()
                .get(requestedURL)
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_404);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(jsonResponse, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyNameValidReturnsOkWithDefaultContentTypeAndCorrectTaxonomies() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/name/HUMAN")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.JSON,
                true);
    }

    @Test
    public void lookupTaxonomyNameCaseInsensitiveValidReturnsOkWithDefaultContentTypeAndCorrectTaxonomies() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/name/human")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.JSON,
                true);
    }

    @Test
    public void lookupTaxonomyNameValidReturnsOkWithXmlContentTypeAndCorrectTaxonomies() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/name/human?format=xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.XML, true);
    }

    @Test
    public void lookupTaxonomyNameValidReturnsOkWithJsonContentTypeAndCorrectTaxonomies() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/name/human?format=json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.JSON,
                true);
    }

    @Test
    public void lookupTaxonomyPathWithoutParametersReturnsBadRequest() {
        String requestedURL = TAXONOMY_BASE_PATH + "/path";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.ID_PARAMETER_IS_REQUIRED);
        errorMessages.add(SwaggerConstant.DIRECTION_PARAMETER_IS_REQUIRED);
        errorMessages.add(SwaggerConstant.DEPTH_PARAMETER_IS_REQUIRED);
        errorMessages.add(SwaggerConstant.DIRECTION_VALID_VALUES);

        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyPathWithoutIdParametersReturnsBadRequest() {
        String requestedURL = TAXONOMY_BASE_PATH + "/path?direction=TOP&depth=3";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.ID_PARAMETER_IS_REQUIRED);

        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyPathWithoutDirectionParametersReturnsBadRequest() {
        String requestedURL = TAXONOMY_BASE_PATH + "/path?id=12345&depth=3";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.DIRECTION_PARAMETER_IS_REQUIRED);
        errorMessages.add(SwaggerConstant.DIRECTION_VALID_VALUES);

        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyPathWithoutDepthParametersReturnsBadRequest() {
        String requestedURL = TAXONOMY_BASE_PATH + "/path?id=12345&direction=TOP";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.DEPTH_PARAMETER_IS_REQUIRED);

        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyPathMaxDepthParametersReturnsBadRequest() {
        String requestedURL = TAXONOMY_BASE_PATH + "/path?id=12345&direction=TOP&depth=6";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.DEPTH_PARAM_MIN_MAX);

        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyPathMinDepthParametersReturnsBadRequest() {
        String requestedURL = TAXONOMY_BASE_PATH + "/path?id=12345&direction=TOP&depth=0";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.DEPTH_PARAM_MIN_MAX);

        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyPathValidReturnsOkWithJsonContentTypeAndCorrectTaxonomyNode() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/path?id=12345&direction=TOP&depth=3")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response,ContentType.JSON,12345,false);
    }


    @Test
    public void lookupTaxonomyPathBottomDirectionParametersReturnsBadRequest() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/path?id=12345&direction=BOTTOM&depth=3")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response,ContentType.JSON,12345,false);
    }

    @Test
    public void lookupTaxonomyPathWithHistoricalChangesReturnsSeeOtherWithJsonContentType() {
        String requestedURL = TAXONOMY_BASE_PATH + "/path?id=33333&direction=TOP&depth=3";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH +
                        "/path?id=55555&direction=TOP&depth=3")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_303.replace("{newId}","55555"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyRelationshipWithTwoHistoricalChangesReturnsSeeOtherWithJsonContentType() {
        String requestedURL = TAXONOMY_BASE_PATH + "/relationship?from=33333&to=34343";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH +
                        "/relationship?from=55555&to=55555")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_303.replace("{newId}","55555"));
        errorMessages.add(SwaggerConstant.API_RESPONSE_303.replace("{newId}","55555"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyRelationshipWithFromHistoricalChangesReturnsSeeOtherWithJsonContentType() {
        String requestedURL = TAXONOMY_BASE_PATH + "/relationship?from=33333&to=12345";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH +
                        "/relationship?from=55555&to=12345")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_303.replace("{newId}","55555"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyRelationshipWithToHistoricalChangesReturnsSeeOtherWithJsonContentType() {
        String requestedURL = TAXONOMY_BASE_PATH + "/relationship?from=12345&to=33333";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH +
                        "/relationship?from=12345&to=55555")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_303.replace("{newId}","55555"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyRelationshipWithInvalidIdsReturnsNotFoundWithJsonContentType() {
        String requestedURL = TAXONOMY_BASE_PATH + "/relationship?from=98989&to=99999";

        ExtractableResponse<Response> jsonResponse = when()
                .get(requestedURL)
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.API_RESPONSE_404);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(jsonResponse, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyRelationshipWithInvalidIdTypesReturnsBadRequestWithJsonContentType() {
        String requestedURL = TAXONOMY_BASE_PATH + "/relationship?from=INVALIDFrom&to=INVALIDTo";

        ExtractableResponse<Response> jsonResponse = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.FROM_PARAMETER_VALID_NUMBER);
        errorMessages.add(SwaggerConstant.TO_PARAMETER_VALID_NUMBER);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(jsonResponse, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyRelationshipWithoutParametersReturnsBadRequestWithJsonContentType() {
        String requestedURL = TAXONOMY_BASE_PATH + "/relationship";

        ExtractableResponse<Response> jsonResponse = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(SwaggerConstant.FROM_PARAMETER_IS_REQUIRED);
        errorMessages.add(SwaggerConstant.TO_PARAMETER_IS_REQUIRED);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(jsonResponse, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }


    @Test
    public void lookupTaxonomyRelationshipWithValidIdsReturnsOkWithJsonContentType() {
        String requestedURL = TAXONOMY_BASE_PATH + "/relationship?from=12345&to=22222";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response,ContentType.JSON,12345,false);
    }

    private void assertErrorResponseReturnCorrectContentTypeAndResponseBody(ExtractableResponse<Response>
            response, ContentType contentType, List<String> errorMessages, String requestedURL) {
        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(contentType.toString()));
        ErrorMessage error = response.as(ErrorMessage.class);
        assertThat(error.getErrorMessages(), notNullValue());
        assertThat(error.getErrorMessages(), not(emptyIterable()));
        assertThat(error.getErrorMessages().size(),is(errorMessages.size()));

        Collections.sort(error.getErrorMessages());
        Collections.sort(errorMessages);
        assertThat(error.getErrorMessages(),equalTo(errorMessages));
        assertThat(error.getRequestedURL(), notNullValue());
        assertThat(error.getRequestedURL(), is(requestedURL));

    }

    private void assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent
            (ExtractableResponse<Response> response, ContentType contentType, long taxonomyId, boolean checkLinks) {
        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(contentType.toString()));

        TaxonomyNode node = response.as(TaxonomyNode.class);
        assertThat(node, notNullValue());
        assertTaxonomyNodeAttributesHasValues(node, taxonomyId, checkLinks);
    }

    private void assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(
            ExtractableResponse<Response> response, ContentType contentType, boolean checkLinks) {
        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(contentType.toString()));

        Taxonomies taxonomies = response.as(Taxonomies.class);
        assertThat(taxonomies.getTaxonomies(), notNullValue());
        assertThat(taxonomies.getTaxonomies(), not(emptyIterable()));

        TaxonomyNode node = taxonomies.getTaxonomies().get(0);
        assertThat(node, notNullValue());
        assertTaxonomyNodeAttributesHasValues(node, node.getTaxonomyId(), checkLinks);
    }

    private void assertTaxonomyNodeAttributesHasValues(TaxonomyNode node, long taxonomyId, boolean checkLinks) {
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
}