package uk.ac.ebi.uniprot.taxonomyservice.restful.rest;

import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.PageInformation;
import uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants;
import uk.ac.ebi.uniprot.taxonomyservice.restful.util.ResponseAssert;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ExtractableResponse;
import com.jayway.restassured.response.Response;
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

/**
 * Class used to test Taxonomy service sub resource use cases
 *
 * Created by lgonzales on 29/11/16.
 */
public class TaxonomyRestSubResourcesIT{

    private static final String TAXONOMY_BASE_PATH = "";

    @ClassRule
    public static final RestContainer restContainer = new RestContainer();

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
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyChildrenDetailedWithInvalidFormatIdReturnsBadRequestStatusJsonFormatAndTheCorrectList() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/invalid/children";
        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.ID_PARAMETER_VALID_NUMBER);
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyChildrenDetailedWithInvalidTaxonomyIdReturnsNotFoundStatus() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/99999/children";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_404_ENTRY);
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyChildrenDetailedWithHistoricalChangeReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/children";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/children") //redirect Location header
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyChildrenDetailedWithDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        PageInformation expectedPageInfo = new PageInformation();
        expectedPageInfo.setTotalRecords(3);
        expectedPageInfo.setResultsPerPage(100);
        expectedPageInfo.setCurrentPage(1);

        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/children")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        ResponseAssert.assertValidTaxonomiesResponseWithCorrectContentTypeAndValidPageMetadataAndContent(response, ContentType.JSON,
                true,expectedPageInfo);
    }

    @Test
    public void lookupTaxonomyChildrenDetailedWithJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        PageInformation expectedPageInfo = new PageInformation();
        expectedPageInfo.setTotalRecords(3);
        expectedPageInfo.setResultsPerPage(100);
        expectedPageInfo.setCurrentPage(1);

        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/children?format=json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        ResponseAssert.assertValidTaxonomiesResponseWithCorrectContentTypeAndValidPageMetadataAndContent(response, ContentType.JSON,
                true,expectedPageInfo);
    }

    @Test
    public void lookupTaxonomyChildrenDetailedWithHistoricalChangeAndJsonPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/children?format=json";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/children?format=json")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyChildrenDetailedWithXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        PageInformation expectedPageInfo = new PageInformation();
        expectedPageInfo.setTotalRecords(3);
        expectedPageInfo.setResultsPerPage(100);
        expectedPageInfo.setCurrentPage(1);

        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/children?format=xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        ResponseAssert.assertValidTaxonomiesResponseWithCorrectContentTypeAndValidPageMetadataAndContent(response, ContentType.XML,
                true,expectedPageInfo);
    }

    @Test
    public void lookupTaxonomyChildrenDetailedWithHistoricalChangeAndXmlPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/children?format=xml";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/children?format=xml")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.XML,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyChildrenWithInvalidFormatIdReturnsBadRequestStatusJsonFormatAndTheCorrectList() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/invalid/children/node";
        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.ID_PARAMETER_VALID_NUMBER);
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyChildrenWithInvalidTaxonomyIdReturnsNotFoundStatus() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/99999/children/node";

        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_404_ENTRY);
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyChildrenWithHistoricalChangeReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/children/node";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/children/node") //redirect Location header
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyChildrenWithDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        PageInformation expectedPageInfo = new PageInformation();
        expectedPageInfo.setTotalRecords(3);
        expectedPageInfo.setResultsPerPage(100);
        expectedPageInfo.setCurrentPage(1);

        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/children/node")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        ResponseAssert.assertValidTaxonomiesResponseWithCorrectContentTypeAndValidPageMetadataAndContent(response, ContentType.JSON,
                false,expectedPageInfo);
    }

    @Test
    public void lookupTaxonomyChildrenWithJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        PageInformation expectedPageInfo = new PageInformation();
        expectedPageInfo.setTotalRecords(3);
        expectedPageInfo.setResultsPerPage(100);
        expectedPageInfo.setCurrentPage(1);

        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/children/node?format=json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        ResponseAssert.assertValidTaxonomiesResponseWithCorrectContentTypeAndValidPageMetadataAndContent(response, ContentType.JSON,
                false,expectedPageInfo);
    }

    @Test
    public void lookupTaxonomyChildrenWithHistoricalChangeAndJsonPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/children/node?format=json";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/children/node?format=json")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyChildrenWithXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        PageInformation expectedPageInfo = new PageInformation();
        expectedPageInfo.setTotalRecords(3);
        expectedPageInfo.setResultsPerPage(100);
        expectedPageInfo.setCurrentPage(1);

        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/children/node?format=xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        ResponseAssert.assertValidTaxonomiesResponseWithCorrectContentTypeAndValidPageMetadataAndContent(response, ContentType.XML,
                false,expectedPageInfo);
    }

    @Test
    public void lookupTaxonomyChildrenWithHistoricalChangeAndXmlPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/children/node?format=xml";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/children/node?format=xml")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.XML,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomySiblingsDetailedWithInvalidFormatIdReturnsBadRequestStatusJsonFormatAndTheCorrectList() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/invalid/siblings";
        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.ID_PARAMETER_VALID_NUMBER);
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomySiblingsDetailedWithDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        PageInformation expectedPageInfo = new PageInformation();
        expectedPageInfo.setTotalRecords(2);
        expectedPageInfo.setResultsPerPage(100);
        expectedPageInfo.setCurrentPage(1);

        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/siblings")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        ResponseAssert.assertValidTaxonomiesResponseWithCorrectContentTypeAndValidPageMetadataAndContent(response, ContentType.JSON,
                true,false,true,expectedPageInfo);
    }

    @Test
    public void lookupTaxonomySiblingsDetailedWithHistoricalChangeReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/siblings";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/siblings") //redirect Location header
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomySiblingsDetailedWithJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        PageInformation expectedPageInfo = new PageInformation();
        expectedPageInfo.setTotalRecords(2);
        expectedPageInfo.setResultsPerPage(100);
        expectedPageInfo.setCurrentPage(1);

        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/siblings?format=json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        ResponseAssert.assertValidTaxonomiesResponseWithCorrectContentTypeAndValidPageMetadataAndContent(response, ContentType.JSON,
                true,false,true,expectedPageInfo);
    }

    @Test
    public void lookupTaxonomySiblingsDetailedWithHistoricalChangeAndJsonPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/siblings?format=json";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/siblings?format=json")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomySiblingsDetailedWithXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        PageInformation expectedPageInfo = new PageInformation();
        expectedPageInfo.setTotalRecords(2);
        expectedPageInfo.setResultsPerPage(100);
        expectedPageInfo.setCurrentPage(1);

        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/siblings?format=xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        ResponseAssert.assertValidTaxonomiesResponseWithCorrectContentTypeAndValidPageMetadataAndContent(response, ContentType.XML,
                true,false,true,expectedPageInfo);
    }

    @Test
    public void lookupTaxonomySiblingsDetailedWithHistoricalChangeAndXmlPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/siblings?format=xml";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/siblings?format=xml")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.XML,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomySiblingsWithInvalidFormatIdReturnsBadRequestStatusJsonFormatAndTheCorrectList() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/invalid/siblings/node";
        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.ID_PARAMETER_VALID_NUMBER);
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomySiblingsWithDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        PageInformation expectedPageInfo = new PageInformation();
        expectedPageInfo.setTotalRecords(2);
        expectedPageInfo.setResultsPerPage(100);
        expectedPageInfo.setCurrentPage(1);

        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/siblings/node")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        ResponseAssert.assertValidTaxonomiesResponseWithCorrectContentTypeAndValidPageMetadataAndContent(response, ContentType.JSON,
                false,expectedPageInfo);
    }

    @Test
    public void lookupTaxonomySiblingsWithHistoricalChangeReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/siblings/node";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/siblings/node") //redirect Location header
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomySiblingsWithJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        PageInformation expectedPageInfo = new PageInformation();
        expectedPageInfo.setTotalRecords(2);
        expectedPageInfo.setResultsPerPage(100);
        expectedPageInfo.setCurrentPage(1);

        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/siblings/node?format=json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        ResponseAssert.assertValidTaxonomiesResponseWithCorrectContentTypeAndValidPageMetadataAndContent(response, ContentType.JSON,
                false,expectedPageInfo);
    }

    @Test
    public void lookupTaxonomySiblingsWithHistoricalChangeAndJsonPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/siblings/node?format=json";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/siblings/node?format=json")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomySiblingsWithXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        PageInformation expectedPageInfo = new PageInformation();
        expectedPageInfo.setTotalRecords(2);
        expectedPageInfo.setResultsPerPage(100);
        expectedPageInfo.setCurrentPage(1);

        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/siblings/node?format=xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        ResponseAssert.assertValidTaxonomiesResponseWithCorrectContentTypeAndValidPageMetadataAndContent(response, ContentType.XML,
                false,expectedPageInfo);
    }

    @Test
    public void lookupTaxonomySiblingsWithHistoricalChangeAndXmlPathReturnsSeeOtherStatusXmlFormatAndTheCorrectId() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/siblings/node?format=xml";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/siblings/node?format=xml")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.XML,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyParentWithInvalidIdReturnsBadRequestStatusJsonFormatAndTheCorrectList() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/invalid/parent/node";
        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.ID_PARAMETER_VALID_NUMBER);
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyParentWithDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/parent/node")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        ResponseAssert.assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response, ContentType.JSON, 1, false);
    }

    @Test
    public void lookupTaxonomyParentWithHistoricalChangeAndDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/parent/node";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/parent/node")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyParentWithJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/parent/node?format=json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        ResponseAssert.assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response, ContentType.JSON, 1, false);
    }

    @Test
    public void lookupTaxonomyParentWithHistoricalChangeAndJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/parent/node?format=json";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/parent/node?format=json")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyParentWithXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> xmlResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/10/parent/node?format=xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        ResponseAssert.assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(xmlResponse, ContentType.XML, 1, false);
    }

    @Test
    public void lookupTaxonomyParentWithHistoricalChangeAndXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/parent/node?format=xml";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/parent/node?format=xml")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.XML,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyParentDetailedWithInvalidIdReturnsBadRequestStatusJsonFormatAndTheCorrectList() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/invalid/parent";
        ExtractableResponse<Response> response = when()
                .get(requestedURL)
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();
        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.ID_PARAMETER_VALID_NUMBER);
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyParentDetailedWithDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/100/parent")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        ResponseAssert.assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response, ContentType.JSON, 10, true);
    }

    @Test
    public void lookupTaxonomyParentDetailedWithHistoricalChangeAndDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/parent";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/parent")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.JSON,errorMessages,
                restContainer.baseURL+requestedURL);
    }

    @Test
    public void lookupTaxonomyParentDetailedWithJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/100/parent?format=json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        ResponseAssert.assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response, ContentType.JSON, 10, true);
    }

    @Test
    public void lookupTaxonomyParentDetailedWithXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> xmlResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/100/parent?format=xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        ResponseAssert.assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(xmlResponse, ContentType.XML, 10, true);
    }

    @Test
    public void lookupTaxonomyParentDetailedWithHistoricalChangeAndXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        String requestedURL = TAXONOMY_BASE_PATH + "/id/9/parent?format=xml";

        ExtractableResponse<Response> response = given().redirects().follow(false)
                .when().get(requestedURL)
                .then()
                .statusCode(SEE_OTHER.getStatusCode())
                .header(HttpHeaders.LOCATION,restContainer.baseURL+TAXONOMY_BASE_PATH + "/id/10/parent?format=xml")
                .extract();

        List<String> errorMessages = new ArrayList<>();
        errorMessages.add(TaxonomyConstants.API_RESPONSE_303.replace("{newId}","10"));
        ResponseAssert.assertErrorResponseReturnCorrectContentTypeAndResponseBody(response, ContentType.XML,errorMessages,
                restContainer.baseURL+requestedURL);
    }

}
