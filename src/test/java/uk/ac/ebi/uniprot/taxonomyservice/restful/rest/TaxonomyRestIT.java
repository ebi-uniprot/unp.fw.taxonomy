package uk.ac.ebi.uniprot.taxonomyservice.restful.rest;

import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ExtractableResponse;
import com.jayway.restassured.response.Response;
import org.junit.ClassRule;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.when;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
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

    @Test
    public void lookupTaxonomyIdWithEmptyIdReturnsNotFoundStatusWithErrorMessage() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/")
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();
        assertResourceNotFoundResponseInTheCorrectContentType(response, ContentType.JSON);
    }

    @Test
    public void lookupTaxonomyIdThatDoesNotExistReturnsNotFoundStatusWithErrorMessage() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/99999")
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();
        assertResourceNotFoundResponseInTheCorrectContentType(response, ContentType.JSON);
    }

    @Test
    public void lookupTaxonomyIdWithInvalidIdReturnsBadRequestStatusInDefaultJsonFormat() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/INVALID")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();
        assertBadRequestResponseInTheCorrectContentType(response, ContentType.JSON);
    }

    @Test
    public void lookupTaxonomyIdWithInvalidIdReturnsBadRequestStatusInJsonFormat() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/INVALID.json")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();
        assertBadRequestResponseInTheCorrectContentType(response, ContentType.JSON);
    }

    @Test
    public void lookupTaxonomyIdWithInvalidIdReturnsBadRequestStatusInXmlFormat() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/INVALID.xml")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();
        assertBadRequestResponseInTheCorrectContentType(response, ContentType.XML);
    }

    @Test
    public void lookupTaxonomyIdWithValidIdReturnsOKStatusInDefaultJsonFormatAndTheCorrectId() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response, ContentType.JSON,12345,true);
    }

    @Test
    public void lookupTaxonomyIdWithValidIdReturnsOKStatusInJsonFormatAndTheCorrectId() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345.json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response,ContentType.JSON,12345,true);
    }

    @Test
    public void lookupTaxonomyIdWithResourceXmlPathReturnsOKStatusXmlFormatAndTheCorrectId() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345.xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response, ContentType.XML,12345,true);
    }

    @Test
    public void lookupTaxonomyIChildrendWithInvalidChildPathReturnsNotFoundStatus() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/invalid")
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();
        assertResourceNotFoundResponseInTheCorrectContentType(response, ContentType.JSON);
    }

    @Test
    public void lookupTaxonomyIChildrendWithInvalidTaxonomyIdReturnsNotFoundStatus() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/99999/children")
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();
        assertResourceNotFoundResponseInTheCorrectContentType(response, ContentType.JSON);
    }

    @Test
    public void lookupTaxonomyChildrenWithDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/children")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response,ContentType.JSON,false);
    }

    @Test
    public void lookupTaxonomyChildrenWithJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/children.json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response,ContentType.JSON,false);
    }

    @Test
    public void lookupTaxonomyChildrenWithXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/children.xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response,ContentType.XML,false);
    }

    @Test
    public void lookupTaxonomySiblingsWithDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/siblings")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.JSON,false);
    }

    @Test
    public void lookupTaxonomySiblingsWithJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/siblings.json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response,ContentType.JSON,false);
    }

    @Test
    public void lookupTaxonomySiblingsWithXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/siblings.xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response,ContentType.XML,false);
    }

    @Test
    public void lookupTaxonomyParentWithDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/parent")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response, ContentType.JSON, 999,false);
    }

    @Test
    public void lookupTaxonomyParentWithJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/parent.json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(response, ContentType.JSON, 999,false);
    }

    @Test
    public void lookupTaxonomyParentWithXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList() {
        ExtractableResponse<Response> xmlResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/parent.xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent(xmlResponse, ContentType.XML, 999,false);
    }

    @Test
    public void lookupTaxonomyNameWithEmptyIdReturnsNotFoundStatusWithErrorMessage() {
        ExtractableResponse<Response> jsonResponse = when()
                .get(TAXONOMY_BASE_PATH + "/name/")
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();
        assertResourceNotFoundResponseInTheCorrectContentType(jsonResponse, ContentType.JSON);
    }

    @Test
    public void lookupTaxonomyNameThatDoesNotExistReturnsNotFoundStatusWithErrorMessage() {
        ExtractableResponse<Response> jsonResponse = when()
                .get(TAXONOMY_BASE_PATH + "/name/INVALID")
                .then()
                .statusCode(NOT_FOUND.getStatusCode())
                .extract();
        assertResourceNotFoundResponseInTheCorrectContentType(jsonResponse, ContentType.JSON);
    }

    @Test
    public void lookupTaxonomyNameValidReturnsOkWithDefaultContentTypeAndCorrectTaxonomies() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/name/HUMAN")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.JSON,true);
    }

    @Test
    public void lookupTaxonomyNameCaseInsensitiveValidReturnsOkWithDefaultContentTypeAndCorrectTaxonomies() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/name/human")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.JSON,true);
    }

    @Test
    public void lookupTaxonomyNameValidReturnsOkWithXmlContentTypeAndCorrectTaxonomies() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/name/human.xml")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.XML,true);
    }

    @Test
    public void lookupTaxonomyNameValidReturnsOkWithJsonContentTypeAndCorrectTaxonomies() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/name/human.json")
                .then()
                .statusCode(OK.getStatusCode())
                .extract();
        assertValidTaxonomiesResponseWithCorrectContentTypeNotEmptyListAndValidContent(response, ContentType.JSON,true);
    }

    @Test
    public void lookupTaxonomyPathWithoutParametersReturnsBadRequest() {
        ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/path")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())
                .extract();
        assertBadRequestResponseInTheCorrectContentType(response,ContentType.JSON);
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


    private void assertResourceNotFoundResponseInTheCorrectContentType(ExtractableResponse<Response> response,
            ContentType contentType) {
        assertThat(response, is(not(equalTo(null))));
        assertThat(response.contentType(), equalTo(contentType.toString()));
        ErrorMessage error = response.as(ErrorMessage.class);
        //assertThat(error.getErrorMessage(), equalTo(SwaggerConstant.API_RESPONSE_404)); TODO Fix It
    }

    private void assertBadRequestResponseInTheCorrectContentType(ExtractableResponse<Response> response,
            ContentType contentType) {
        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(contentType.toString()));
        ErrorMessage error = response.as(ErrorMessage.class);
        //assertThat(error.getErrorMessage(), equalTo(SwaggerConstant.API_RESPONSE_400)); TODO Fix It
    }

    private void assertValidTaxonomyNodeResponseWithCorrectContentTypeAndValidContent
            (ExtractableResponse<Response> response, ContentType contentType, long taxonomyId, boolean checkLinks) {
        assertThat(response, notNullValue());
        assertThat(response.contentType(), equalTo(contentType.toString()));

        TaxonomyNode node = response.as(TaxonomyNode.class);
        assertThat(node, notNullValue());
        assertTaxonomyNodeAttributesHasValues(node,taxonomyId,checkLinks);
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
        assertTaxonomyNodeAttributesHasValues(node,node.getTaxonomyId(),checkLinks);
    }

    private void assertTaxonomyNodeAttributesHasValues(TaxonomyNode node, long taxonomyId, boolean checkLinks) {
        assertThat(node.getTaxonomyId(), equalTo(taxonomyId));
        assertThat(node.getCommonName(), not(isEmptyOrNullString()));
        assertThat(node.getMnemonic(), not(isEmptyOrNullString()));
        assertThat(node.getRank(), not(isEmptyOrNullString()));
        assertThat(node.getScientificName(), not(isEmptyOrNullString()));
        assertThat(node.getSynonym(), not(isEmptyOrNullString()));
        if(checkLinks){
            assertThat(node.getParentLink(), not(isEmptyOrNullString()));

            assertThat(node.getChildrenLinks(), notNullValue());
            assertThat(node.getChildrenLinks(), not(emptyIterable()));

            assertThat(node.getSiblingsLinks(), notNullValue());
            assertThat(node.getSiblingsLinks(), not(emptyIterable()));
        }
    }

}