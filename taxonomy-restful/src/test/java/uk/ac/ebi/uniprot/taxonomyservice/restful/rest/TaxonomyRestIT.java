package uk.ac.ebi.uniprot.taxonomyservice.restful.rest;

import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.PageInformation;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;
import uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants;
import uk.ac.ebi.uniprot.taxonomyservice.restful.util.ResponseAssert;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ExtractableResponse;
import com.jayway.restassured.response.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;

/**
 * Class used to test Taxonomy service use cases
 *
 * Created by lgonzales on 24/02/16.
 */
public class TaxonomyRestIT {

    private static final String TAXONOMY_BASE_PATH = "";

    @ClassRule
    public static final RestContainer restContainer = new RestContainer();

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
        errorMessages.add(TaxonomyConstants.API_RESPONSE_404_GENERAL);
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
        errorMessages.add(TaxonomyConstants.API_RESPONSE_404_ENTRY);
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
        errorMessages.add(TaxonomyConstants.ID_PARAMETER_VALID_NUMBER);
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
        errorMessages.add(TaxonomyConstants.ID_PARAMETER_VALID_NUMBER);
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
        errorMessages.add(TaxonomyConstants.ID_PARAMETER_VALID_NUMBER);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.XML,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyIdWithValidIdReturnsOKStatusInDefaultJsonFormatAndTheCorrectId() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response, ContentType.JSON, 10, true);
    }

    @Test
    public void lookupTaxonomyIdWithValidIdReturnsOKStatusInJsonFormatAndTheCorrectId() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10?format=json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response, ContentType.JSON, 10, true);
    }

    @Test
    public void lookupTaxonomyIdWithResourceXmlPathReturnsOKStatusXmlFormatAndTheCorrectId() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10?format=xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response, ContentType.XML, 10, true);
    }

    @Test
    public void lookupTaxonomyIdWithHistoricalChangeReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10") //redirect Location
                // header
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyIdWithHistoricalChangeAndXmlPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9?format=xml";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10?format=xml")
                //redirect Location header
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.XML,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyIdWithHistoricalChangeAndJsonPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9?format=json";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10?format=json")
                //redirect Location header
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

     /*
        END: Test with /taxonomy/id

        START: Test with /taxonomy/id/{subresources}
     */

    @Test
    public void lookupTaxonomyIdWithInvalidSubResourcePathReturnsNotFoundStatus() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/10/invalid";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_404_GENERAL);
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
        errorMessages.add(TaxonomyConstants.API_RESPONSE_404_ENTRY);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyChildrenWithHistoricalChangeReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/children";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/children") //redirect Location header
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyChildrenWithDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/children")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.JSON,
                false);
    }

    @Test
    public void lookupTaxonomyChildrenWithJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/children?format=json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.JSON,
                false);
    }

    @Test
    public void lookupTaxonomyChildrenWithHistoricalChangeAndJsonPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/children?format=json";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/children?format=json")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyChildrenWithXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/children?format=xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.XML,
                false);
    }

    @Test
    public void lookupTaxonomyChildrenWithHistoricalChangeAndXmlPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/children?format=xml";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/children?format=xml")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.XML,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomySiblingsWithDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/siblings")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.JSON,
                false);
    }

    @Test
    public void lookupTaxonomySiblingsWithHistoricalChangeReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/siblings";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/siblings") //redirect Location header
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomySiblingsWithJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/siblings?format=json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.JSON,
                false);
    }

    @Test
    public void lookupTaxonomySiblingsWithHistoricalChangeAndJsonPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/siblings?format=json";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/siblings?format=json")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomySiblingsWithXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/siblings?format=xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.XML,
                false);
    }

    @Test
    public void lookupTaxonomySiblingsWithHistoricalChangeAndXmlPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/siblings?format=xml";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/siblings?format=xml")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.XML,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyParentWithDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/parent")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response, ContentType.JSON, 1, false);
    }

    @Test
    public void lookupTaxonomyParentWithHistoricalChangeAndDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/parent";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/parent")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyParentWithJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/parent?format=json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response, ContentType.JSON, 1, false);
    }

    @Test
    public void lookupTaxonomyParentWithHistoricalChangeAndJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/parent?format=json";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/parent?format=json")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyParentWithXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> xmlResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/parent?format=xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(xmlResponse, ContentType.XML, 1, false);
    }

    @Test
    public void lookupTaxonomyParentWithHistoricalChangeAndXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/parent?format=xml";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/parent?format=xml")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.XML,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    /*
        END: Test with /taxonomy/id/{subresources}

        START: Test with /taxonomy/ids/{ids}
    */

    @Test
    public void lookupTaxonomyIdsWithInvalidPathReturnsNotFoundStatus() {
        String requestedURL = TAXONOMY_BASE_PATH + "/ids/";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_404_GENERAL);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyIdsWithInvalidTaxonomyIdReturnsNotFoundStatus() throws UnsupportedEncodingException {
        String requestedURL = TAXONOMY_BASE_PATH + "/ids/1,INVALID,2";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.IDS_PARAMETER_VALID_NUMBER);
        requestedURL = TAXONOMY_BASE_PATH + "/ids/"+URLEncoder.encode("1,INVALID,2", StandardCharsets.UTF_8.toString());
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyIdsWithBigListReturnMaxSizeErrorWithJsonContentType() throws
                                                                                         UnsupportedEncodingException{
        String requestedURL = TAXONOMY_BASE_PATH + "/ids/1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7," +
                "8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.IDS_PARAMETER_MIN_MAX_SIZE.replace("{minSize}","2").replace("{maxSize}",
                "50"));
        requestedURL = TAXONOMY_BASE_PATH + "/ids/"+URLEncoder.encode("1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0," +
                "1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1", StandardCharsets.UTF_8.toString());
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyIdsWithSmallListReturnMinSizeErrorWithJsonContentType()throws UnsupportedEncodingException{
        String requestedURL = TAXONOMY_BASE_PATH + "/ids/1";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.IDS_PARAMETER_MIN_MAX_SIZE.replace("{minSize}","2").replace("{maxSize}",
                "50"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyIdsWithHistoricalChangeOnlyReturnsOkStatusJsonFormatAndTheCorrectBody() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/ids/9,99")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        ResponseAssert.assertTaxonomiesListResponse(response,ContentType.JSON,0,null,2,9L,0,null,false);
    }


    @Test
    public void lookupTaxonomyIdsWithInvalidIdsOnlyReturnsOkStatusJsonFormatAndTheCorrectBody() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/ids/6,7,8")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        ResponseAssert.assertTaxonomiesListResponse(response,ContentType.JSON,0,null,0,null,3,6L,false);
    }

    @Test
    public void lookupTaxonomyIdsWithValidIdsOnlyReturnsOkStatusJsonFormatAndTheCorrectBody() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/ids/11,12,101,102")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        ResponseAssert.assertTaxonomiesListResponse(response,ContentType.JSON,4,11L,0,null,0,null,true);
    }

    @Test
    public void lookupTaxonomyIdsWithValidInvalidAndRedirectIdsThatReturnsOkStatusJsonFormatAndTheCorrectBody() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/ids/11,9,5?format=JSON")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        ResponseAssert.assertTaxonomiesListResponse(response,ContentType.JSON,1,11L,1,9L,1,5L,true);
    }

    @Test
    public void lookupTaxonomyIdsWithValidInvalidAndRedirectIdsThatReturnsOkStatusXmlFormatAndTheCorrectBody() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/ids/11,9,5?format=XML")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        ResponseAssert.assertTaxonomiesListResponse(response,ContentType.XML,1,11L,1,9L,1,5L,true);
    }

    /*
        END: Test with /taxonomy/ids/{ids}

        START: Test with /taxonomy/ids/{ids}/node
    */


    @Test
    public void lookupTaxonomyBaseNodeIdsWithInvalidPathReturnsNotFoundStatus() {
        String requestedURL = TAXONOMY_BASE_PATH + "/ids/1/INVALID";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_404_GENERAL);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyBaseNodeIdsWithInvalidTaxonomyIdReturnsNotFoundStatus() throws UnsupportedEncodingException {
        String requestedURL = TAXONOMY_BASE_PATH + "/ids/1,INVALID,2/node";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.IDS_PARAMETER_VALID_NUMBER);
        requestedURL = TAXONOMY_BASE_PATH + "/ids/"+URLEncoder.encode("1,INVALID,2", StandardCharsets.UTF_8.toString())+ "/node";
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyBaseNodeIdsWithBigListReturnMaxSizeErrorWithJsonContentType() throws
                                                                                    UnsupportedEncodingException{
        String requestedURL = TAXONOMY_BASE_PATH + "/ids/1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7," +
                "8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1/node";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.IDS_PARAMETER_MIN_MAX_SIZE.replace("{minSize}","2").replace("{maxSize}",
                "50"));
        requestedURL = TAXONOMY_BASE_PATH + "/ids/"+URLEncoder.encode("1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0," +
                "1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1", StandardCharsets.UTF_8.toString())
                +"/node";
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyBaseNodeIdsWithSmallListReturnMinSizeErrorWithJsonContentType()throws UnsupportedEncodingException{
        String requestedURL = TAXONOMY_BASE_PATH + "/ids/1/node";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.IDS_PARAMETER_MIN_MAX_SIZE.replace("{minSize}","2").replace("{maxSize}",
                "50"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyBaseNodeIdsWithHistoricalChangeOnlyReturnsOkStatusJsonFormatAndTheCorrectBody() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/ids/9,99/node")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        ResponseAssert.assertTaxonomiesListResponse(response,ContentType.JSON,0,null,2,9L,0,null,false);
    }


    @Test
    public void lookupTaxonomyBaseNodeIdsWithInvalidIdsOnlyReturnsOkStatusJsonFormatAndTheCorrectBody() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/ids/6,7,8/node")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        ResponseAssert.assertTaxonomiesListResponse(response,ContentType.JSON,0,null,0,null,3,6L,false);
    }

    @Test
    public void lookupTaxonomyBaseNodeIdsWithValidIdsOnlyReturnsOkStatusJsonFormatAndTheCorrectBody() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/ids/11,12,101,102/node")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        ResponseAssert.assertTaxonomiesListResponse(response,ContentType.JSON,4,11L,0,null,0,null,false);
    }

    @Test
    public void lookupTaxonomyBaseNodeIdsWithValidInvalidAndRedirectIdsThatReturnsOkStatusJsonFormatAndTheCorrectBody() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/ids/11,9,5/node?format=JSON")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        ResponseAssert.assertTaxonomiesListResponse(response,ContentType.JSON,1,11L,1,9L,1,5L,false);
    }

    @Test
    public void lookupTaxonomyBaseNodeIdsWithValidInvalidAndRedirectIdsThatReturnsOkStatusXmlFormatAndTheCorrectBody() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/ids/11,9,5/node?format=XML")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        ResponseAssert.assertTaxonomiesListResponse(response,ContentType.XML,1,11L,1,9L,1,5L,false);
    }

    /*
        END: Test with /taxonomy/ids/{ids}/node

        START: Test with /taxonomy/name
    */

    @Test
    public void lookupTaxonomyNameWithEmptyIdReturnsNotFoundStatusWithErrorMessage() {
        String requestedURL = TAXONOMY_BASE_PATH + "/name/";

        ExtractableResponse<Response> jsonResponse = when()
                .get(requestedURL)
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_404_GENERAL);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(jsonResponse, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyNameWithSmallNameContainsReturnsBadRequestStatusWithErrorMessage() {
        String requestedURL = TAXONOMY_BASE_PATH + "/name/sn?searchType=contains";

        ExtractableResponse<Response> jsonResponse = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.NAME_PARAMETER_MIN_SIZE_FOR_PARTIAL_SEARCHES.replace("{min}","4"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(jsonResponse, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyNameWithSmallNameStartsWithReturnsBadRequestStatusWithErrorMessage() {
        String requestedURL = TAXONOMY_BASE_PATH + "/name/sn?searchType=startswith";

        ExtractableResponse<Response> jsonResponse = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.NAME_PARAMETER_MIN_SIZE_FOR_PARTIAL_SEARCHES.replace("{min}","4"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(jsonResponse, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyNameWithSmallNameEqualsToReturnsCorrectTaxonomies() {
        String requestedURL = TAXONOMY_BASE_PATH + "/name/sn?searchType=equalsto";

        ExtractableResponse<Response> jsonResponse = when()
                .get(requestedURL)
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(jsonResponse, ContentType.JSON,
                false);
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
        errorMessages.add(TaxonomyConstants.API_RESPONSE_404_NAME);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(jsonResponse, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyNameValidReturnsOkWithDefaultContentTypeAndCorrectTaxonomies() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/name/Equals to Only")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.JSON,
                false);
    }

    @Test
    public void lookupTaxonomyNameCaseInsensitiveValidReturnsOkWithDefaultContentTypeAndCorrectTaxonomies() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/name/EQUALS tO OnLY")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.JSON,
                false);
    }

    @Test
    public void lookupTaxonomyNameValidReturnsOkWithXmlContentTypeAndCorrectTaxonomies() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/name/Equals to Only?format=xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.XML, true);
    }

    @Test
    public void lookupTaxonomyNameValidReturnsOkWithJsonContentTypeAndCorrectTaxonomies() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/name/Equals to Only?format=json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.JSON,
                true);
    }

    @Test
    public void lookupTaxonomyNameInvalidSearchTypeReturnsBadRequestStatusWithErrorMessage() {
        String requestedURL = TAXONOMY_BASE_PATH + "/name/sn?searchType=INVALID";

        ExtractableResponse<Response> jsonResponse = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.SEARCH_TYPE_VALID_VALUES);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(jsonResponse, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyNameInvalidFieldNameReturnsBadRequestStatusWithErrorMessage() {
        String requestedURL = TAXONOMY_BASE_PATH + "/name/validName?fieldName=INVALID";

        ExtractableResponse<Response> jsonResponse = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.FIELD_NAME_VALID_VALUES);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(jsonResponse, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyNameInvalidPageNumberReturnsBadRequestStatusWithErrorMessage() {
        String requestedURL = TAXONOMY_BASE_PATH + "/name/validName?pageNumber=0";

        ExtractableResponse<Response> jsonResponse = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.PAGE_NUMBER_PARAMETER_MIN_VALUE.replace("{min}","1"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(jsonResponse, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyNameInvalidPageSizeReturnsBadRequestStatusWithErrorMessage() {
        String requestedURL = TAXONOMY_BASE_PATH + "/name/validName?pageSize=201";

        ExtractableResponse<Response> jsonResponse = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.PAGE_SIZE_PARAMETER_MAX_VALUE.replace("{max}","200"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(jsonResponse, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyNameInvalidPageSizeAndNameReturnsBadRequestStatusWithErrorMessage() {
        String requestedURL = TAXONOMY_BASE_PATH + "/name/validName?pageSize=INVALID&pageNumber=INVALID";

        ExtractableResponse<Response> jsonResponse = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.PAGE_NUMBER_PARAMETER_VALID_NUMBER);
        errorMessages.add(TaxonomyConstants.PAGE_SIZE_PARAMETER_VALID_NUMBER);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(jsonResponse, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyNameValidPageMetadataPage2ReturnsOkWithValidPageMetadata() {
        PageInformation expectedPageInfo = new PageInformation();
        expectedPageInfo.setTotalRecords(50);
        expectedPageInfo.setResultsPerPage(10);
        expectedPageInfo.setCurrentPage(2);
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/name/common?searchType=startswith&fieldName=commonName&pageNumber=2" +
                        "&pageSize=10&format=xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidPageMetadataAndContent(response,
                ContentType.XML,true,expectedPageInfo);
    }

    @Test
    public void lookupTaxonomyNameValidPageMetadataLastPageReturnsOkWithValidPageMetadata() {
        PageInformation expectedPageInfo = new PageInformation();
        expectedPageInfo.setTotalRecords(50);
        expectedPageInfo.setResultsPerPage(10);
        expectedPageInfo.setCurrentPage(5);
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/name/common?searchType=startswith&fieldName=commonName&pageNumber=5" +
                        "&pageSize=10")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidPageMetadataAndContent(response,
                ContentType.JSON,true,expectedPageInfo);
    }

    @Test
    public void lookupTaxonomyNameInvalidPageNumberReturnsNotFoundStatusWithErrorMessage() {
        String requestedURL =
                TAXONOMY_BASE_PATH + "/name/common?searchType=startswith&fieldName=commonName&pageNumber=3";
        ExtractableResponse<Response> jsonResponse = when()
                .get(requestedURL)
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_404_NAME);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(jsonResponse, ContentType.JSON, errorMessages,
                restContainer.baseURL + requestedURL);
    }

    @Test
    public void lookupTaxonomyNameFullValidParametersReturnsOkWithJsonContentTypeAndCorrectTaxonomies() {
        PageInformation expectedPageInfo = new PageInformation();
        expectedPageInfo.setTotalRecords(50);
        expectedPageInfo.setResultsPerPage(5);
        expectedPageInfo.setCurrentPage(3);
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/name/common?searchType=startswith&fieldName=commonName&pageNumber=3" +
                        "&pageSize=5&format=xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidPageMetadataAndContent(response,
                ContentType.XML,true,expectedPageInfo);
    }

    @Test
    public void lookupTaxonomyNodeNameWithSmallNameEqualsToReturnsCorrectTaxonomies() {
        String requestedURL = TAXONOMY_BASE_PATH + "/name/sn/node?searchType=equalsto";

        ExtractableResponse<Response> jsonResponse = when()
                .get(requestedURL)
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(jsonResponse, ContentType.JSON,
                false);
    }

    @Test
    public void lookupTaxonomyNodeNameThatDoesNotExistReturnsNotFoundStatusWithErrorMessage() {
        String requestedURL = TAXONOMY_BASE_PATH + "/name/INVALID/node";

        ExtractableResponse<Response> jsonResponse = when()
                .get(requestedURL)
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_404_NAME);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(jsonResponse, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyNodeNameValidReturnsOkWithDefaultContentTypeAndCorrectTaxonomies() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/name/Equals to Only/node")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.JSON,
                false);
    }
    /*
        END: Test with /taxonomy/name

        START: Test with /taxonomy/path
    */

    @Test
    public void lookupTaxonomyPathWithoutParametersReturnsBadRequest() {
        String requestedURL = TAXONOMY_BASE_PATH + "/path";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.ID_PARAMETER_IS_REQUIRED);
        errorMessages.add(TaxonomyConstants.DIRECTION_PARAMETER_IS_REQUIRED);

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
        errorMessages.add(TaxonomyConstants.ID_PARAMETER_IS_REQUIRED);

        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyPathWithoutDirectionParametersReturnsBadRequest() {
        String requestedURL = TAXONOMY_BASE_PATH + "/path?id=10&depth=3";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.DIRECTION_PARAMETER_IS_REQUIRED);

        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyPathWithoutDepthParametersReturnsBadRequest() {
        String requestedURL = TAXONOMY_BASE_PATH + "/path?id=100000&direction=BOTTOM";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.DEPTH_PARAMETER_IS_REQUIRED);

        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyPathMaxDepthParametersReturnsBadRequest() {
        String requestedURL = TAXONOMY_BASE_PATH + "/path?id=10&direction=BOTTOM&depth=6";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.DEPTH_PARAM_MIN_MAX.replace("{max}","5").replace("{min}","1"));

        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyPathMinDepthParametersReturnsBadRequest() {
        String requestedURL = TAXONOMY_BASE_PATH + "/path?id=10&direction=BOTTOM&depth=0";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.DEPTH_PARAM_MIN_MAX.replace("{max}","5").replace("{min}","1"));

        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyPathValidReturnsOkWithJsonContentTypeAndCorrectTaxonomyNode() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/path?id=100000&direction=TOP")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response,ContentType.JSON,100000,false);
    }

    @Test
    public void lookupTaxonomyPathBottomDirectionParametersReturnsBadRequest() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/path?id=10&direction=BOTTOM&depth=3")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response,ContentType.JSON,10,false);
    }

    @Test
    public void lookupTaxonomyPathWithHistoricalChangesReturnsSeeOtherWithJsonContentType() {
        String requestedURL = TAXONOMY_BASE_PATH + "/path?id=9&direction=TOP";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH +
                        "/path?id=10&direction=TOP")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    /*
        END: Test with /taxonomy/path

        START: Test with /taxonomy/relationship
    */

    @Test
    public void lookupTaxonomyRelationshipWithTwoHistoricalChangesReturnsSeeOtherWithJsonContentType() {
        String requestedURL = TAXONOMY_BASE_PATH + "/relationship?from=9&to=99";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH +
                        "/relationship?from=10&to=100")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","100"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyRelationshipWithFromHistoricalChangesReturnsSeeOtherWithJsonContentType() {
        String requestedURL = TAXONOMY_BASE_PATH + "/relationship?from=9&to=10";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH +
                        "/relationship?from=10&to=10")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyRelationshipWithToHistoricalChangesReturnsSeeOtherWithJsonContentType() {
        String requestedURL = TAXONOMY_BASE_PATH + "/relationship?from=10&to=9";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH +
                        "/relationship?from=10&to=10")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
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
        errorMessages.add(TaxonomyConstants.API_RESPONSE_404_RELATIONSHIP);
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
        errorMessages.add(TaxonomyConstants.FROM_PARAMETER_VALID_NUMBER);
        errorMessages.add(TaxonomyConstants.TO_PARAMETER_VALID_NUMBER);
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
        errorMessages.add(TaxonomyConstants.FROM_PARAMETER_IS_REQUIRED);
        errorMessages.add(TaxonomyConstants.TO_PARAMETER_IS_REQUIRED);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(jsonResponse, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyRelationshipWithValidIdsReturnsOkWithJsonContentType() {
        String requestedURL = TAXONOMY_BASE_PATH + "/relationship?from=10&to=100000";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response,ContentType.JSON,10,false);
    }

    /*
        END: Test with /taxonomy/relationship

        START: Test with /taxonomy/lineage
    */

    @Test
    public void lookupTaxonomyLineageWithInvalidIdsReturnsNotFoundWithJsonContentType() {
        String requestedURL = TAXONOMY_BASE_PATH + "/lineage/3";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_404_LINEAGE);
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyLineageWithFromHistoricalChangesReturnsSeeOtherWithJsonContentType(){
        String requestedURL = TAXONOMY_BASE_PATH + "/lineage/999";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/lineage/1000")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","1000"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyLineageWithValidIdsReturnsOkWithJsonContentType() {
        String requestedURL = TAXONOMY_BASE_PATH + "/lineage/100000";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        assertValidTaxonomiesResponseForLineageWithCorrectContentTypeNotEmptyListAndValidContent(response,ContentType
                .JSON,100000,1);
    }

    @Test
    public void lookupTaxonomyLineageWithRootIdsReturnsOkWithJsonContentType() {
        String requestedURL = TAXONOMY_BASE_PATH + "/lineage/1";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        assertValidTaxonomiesResponseForLineageWithCorrectContentTypeNotEmptyListAndValidContent(response,ContentType
                .JSON,1,1);
    }

   /*
        END: Test with /taxonomy/lineage

        START: Test with /taxonomy/ancestor
    */
    @Test
    public void lookupTaxonomyAncestorWithInvalidIdsReturnNotFoundWithJsonContentType()throws
                                                                                       UnsupportedEncodingException{
        String requestedURL = TAXONOMY_BASE_PATH + "/ancestor/5,10";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_404_ANCESTOR);
        requestedURL = TAXONOMY_BASE_PATH + "/ancestor/"+URLEncoder.encode("5,10", StandardCharsets.UTF_8.toString());
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);

    }

    @Test
    public void lookupTaxonomyAncestorWithValidIdsReturnOkWithJsonContentType(){
        String requestedURL = TAXONOMY_BASE_PATH + "/ancestor/1000,1001,1002,1010,1011";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response,ContentType.JSON,10L,false);
    }

    @Test
    public void lookupTaxonomyAncestorWithAncestorAsPartOfListParamIdsReturnOkWithJsonContentType(){
        String requestedURL = TAXONOMY_BASE_PATH + "/ancestor/1000,1001,1002,1010,1011,10";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(OK.getStatusCode())
                .extract();

        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response,ContentType.JSON,10L,false);
    }

    @Test
    public void lookupTaxonomyAncestorWithBigListReturnMaxSizeErrorWithJsonContentType() throws
                                                                                         UnsupportedEncodingException{
        String requestedURL = TAXONOMY_BASE_PATH + "/ancestor/1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7," +
                "8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.IDS_PARAMETER_MIN_MAX_SIZE.replace("{minSize}","2").replace("{maxSize}",
                "50"));
        requestedURL = TAXONOMY_BASE_PATH + "/ancestor/"+URLEncoder.encode("1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0," +
                "1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1", StandardCharsets.UTF_8.toString());
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyAncestorWithSmallListReturnMinSizeErrorWithJsonContentType()throws
                                                                                          UnsupportedEncodingException{
        String requestedURL = TAXONOMY_BASE_PATH + "/ancestor/1";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.IDS_PARAMETER_MIN_MAX_SIZE.replace("{minSize}","2").replace("{maxSize}",
                "50"));
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyAncestorWithListInvalidTypeReturnInvalidTypeErrorWithJsonContentType() throws
                                                                                                     UnsupportedEncodingException{
        String requestedURL = TAXONOMY_BASE_PATH + "/ancestor/1,INVALID,2";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.IDS_PARAMETER_VALID_NUMBER);
        requestedURL = TAXONOMY_BASE_PATH + "/ancestor/"+URLEncoder.encode("1,INVALID,2", StandardCharsets.UTF_8
                .toString());
        assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);

    }
     /*
        END: Test with /taxonomy/ancestor
     */


    private void assertErrorResponseReturnCorrectContentTypeAndResponseBody(ExtractableResponse<Response>
            response, ContentType contentType, List<String> errorMessages, String requestedURL) {
        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(contentType.toString()));

        ErrorMessage expectedErrorMessage = new ErrorMessage();
        expectedErrorMessage.setErrorMessages(errorMessages);
        expectedErrorMessage.setRequestedURL(requestedURL);

        ResponseAssert.assertResponseErrorMessage(expectedErrorMessage,response);
    }

    private void assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent
            (ExtractableResponse<Response> response, ContentType contentType, long taxonomyId, boolean checkLinks) {
        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(contentType.toString()));

        TaxonomyNode node = response.as(TaxonomyNode.class);
        assertThat(node, notNullValue());
        ResponseAssert.assertTaxonomyNodeAttributesHasValues(node, taxonomyId, checkLinks);
    }

    private void assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidPageMetadataAndContent(
            ExtractableResponse<Response> response, ContentType contentType, boolean checkLinks,PageInformation
            expectedPageInfo
    ){
        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(contentType.toString()));

        Taxonomies taxonomies = response.as(Taxonomies.class);
        assertThat(taxonomies.getTaxonomies(), notNullValue());
        assertThat(taxonomies.getTaxonomies(), not(emptyIterable()));

        TaxonomyNode node = taxonomies.getTaxonomies().get(0);
        assertThat(node, notNullValue());
        ResponseAssert.assertTaxonomyNodeAttributesHasValues(node, node.getTaxonomyId(), checkLinks);

        if(expectedPageInfo != null){
            assertThat(taxonomies.getPageInfo(), notNullValue());
            assertThat(taxonomies.getPageInfo(), equalTo(expectedPageInfo));
        }
    }

    private void assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(
            ExtractableResponse<Response> response, ContentType contentType, boolean checkLinks) {
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidPageMetadataAndContent(response,
                contentType,checkLinks,null);
    }

    private void assertValidTaxonomiesResponseForLineageWithCorrectContentTypeNotEmptyListAndValidContent(
            ExtractableResponse<Response> response, ContentType contentType, long first, long last) {
        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(contentType.toString()));

        Taxonomies taxonomies = response.as(Taxonomies.class);
        assertThat(taxonomies.getTaxonomies(), notNullValue());
        assertThat(taxonomies.getTaxonomies(), not(emptyIterable()));

        TaxonomyNode node = taxonomies.getTaxonomies().get(0);
        assertThat(node, notNullValue());
        assertThat(node.getTaxonomyId(), equalTo(first));

        node = taxonomies.getTaxonomies().get(taxonomies.getTaxonomies().size()-1);
        assertThat(node, notNullValue());
        assertThat(node.getTaxonomyId(), equalTo(last));
    }


}