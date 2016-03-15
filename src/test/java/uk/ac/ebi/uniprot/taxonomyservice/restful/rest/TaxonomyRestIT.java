package uk.ac.ebi.uniprot.taxonomyservice.restful.rest;

import org.junit.ClassRule;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ExtractableResponse;
import com.jayway.restassured.response.Response;

import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;
import uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.SwaggerConstant;

import static com.jayway.restassured.RestAssured.when;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.List;

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
    public void lookupTaxonomyIdWithEmptyIdReturnsNotFoundStatusWithErrorMessage(){
    	ExtractableResponse<Response> jsonResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/")
                .then()
                .statusCode(NOT_FOUND.getStatusCode())                        
                .extract();
    	assertResourceNotFoundResponseInTheCorrectContentType(jsonResponse,ContentType.JSON);
    }
    
    @Test
    public void lookupTaxonomyIdThatDoesNotExistReturnsNotFoundStatusWithErrorMessage(){
    	ExtractableResponse<Response> jsonResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/99999")
                .then()
                .statusCode(NOT_FOUND.getStatusCode())                        
                .extract();
    	assertResourceNotFoundResponseInTheCorrectContentType(jsonResponse,ContentType.JSON);
    }

    @Test
    public void lookupTaxonomyIdWithInvalidIdReturnsBadRequestStatusInDefaultJsonFormat(){
    	ExtractableResponse<Response> jsonResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/INVALID")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())                        
                .extract();
    	assertBadRequestResponseInTheCorrectContentType(jsonResponse, ContentType.JSON);
    }
    
    @Test
    public void lookupTaxonomyIdWithInvalidIdReturnsBadRequestStatusInJsonFormat(){
    	ExtractableResponse<Response> jsonResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/INVALID.json")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())                        
                .extract();
    	assertBadRequestResponseInTheCorrectContentType(jsonResponse, ContentType.JSON);
    }
    
    @Test
    public void lookupTaxonomyIdWithInvalidIdReturnsBadRequestStatusInXmlFormat(){
    	ExtractableResponse<Response> jsonResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/INVALID.xml")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode())                        
                .extract();
    	assertBadRequestResponseInTheCorrectContentType(jsonResponse, ContentType.XML);
    }

    @Test
    public void lookupTaxonomyIdWithValidIdReturnsOKStatusInDefaultJsonFormatAndTheCorrectId(){
    	ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345")
                .then()
                .statusCode(OK.getStatusCode())          
                .extract();
        assertValidtTaxonomyNodeResponseInTheCorrectContentType(response, ContentType.JSON, 12345);
    }

    @Test
    public void lookupTaxonomyIdWithValidIdReturnsOKStatusInJsonFormatAndTheCorrectId(){
    	ExtractableResponse<Response> response = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345.json")
                .then()
                .statusCode(OK.getStatusCode())          
                .extract();
        assertValidtTaxonomyNodeResponseInTheCorrectContentType(response, ContentType.JSON, 12345);
    }    

    @Test
    public void lookupTaxonomyIdWithResourceXmlPathReturnsOKStatusXmlFormatAndTheCorrectId(){
        ExtractableResponse<Response> xmlResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345.xml")
                .then()
                .statusCode(OK.getStatusCode())                        
                .extract();
        assertValidtTaxonomyNodeResponseInTheCorrectContentType(xmlResponse, ContentType.XML, 12345);
    }
    
    @Test
    public void lookupTaxonomyIChildrendWithInvalidChildPathReturnsNotFoundStatus(){
        ExtractableResponse<Response> jsonResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/invalid")
                .then()
                .statusCode(NOT_FOUND.getStatusCode())                        
                .extract();
        assertResourceNotFoundResponseInTheCorrectContentType(jsonResponse, ContentType.JSON);
    }

    @Test
    public void lookupTaxonomyIChildrendWithInvalidTaxonomyIdReturnsNotFoundStatus(){
        ExtractableResponse<Response> jsonResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/99999/children")
                .then()
                .statusCode(NOT_FOUND.getStatusCode())                        
                .extract();
        assertResourceNotFoundResponseInTheCorrectContentType(jsonResponse, ContentType.JSON);
    }
    
    @Test
    public void lookupTaxonomyChildrenWithDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList(){
        ExtractableResponse<Response> jsonResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/children")
                .then()
                .statusCode(OK.getStatusCode())                        
                .extract();
        assertValidTaxonomiesResponseInTheCorrectContentTypeAndNotEmptyList(jsonResponse, ContentType.JSON);
    }

    @Test
    public void lookupTaxonomyChildrenWithJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList(){
        ExtractableResponse<Response> jsonResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/children.json")
                .then()
                .statusCode(OK.getStatusCode())                        
                .extract();
        assertValidTaxonomiesResponseInTheCorrectContentTypeAndNotEmptyList(jsonResponse, ContentType.JSON);
    }    

    @Test
    public void lookupTaxonomyChildrenWithXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList(){
        ExtractableResponse<Response> jsonResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/children.xml")
                .then()
                .statusCode(OK.getStatusCode())                        
                .extract();
        assertValidTaxonomiesResponseInTheCorrectContentTypeAndNotEmptyList(jsonResponse, ContentType.XML);
    }     
    
    @Test
    public void lookupTaxonomySiblingsWithDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList(){
        ExtractableResponse<Response> jsonResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/siblings")
                .then()
                .statusCode(OK.getStatusCode())                        
                .extract();
        assertValidTaxonomiesResponseInTheCorrectContentTypeAndNotEmptyList(jsonResponse, ContentType.JSON);
    }
    
    @Test
    public void lookupTaxonomySiblingsWithJsonResourcePathReturnsOKStatusJsonFormatAndTheCorrectList(){
        ExtractableResponse<Response> jsonResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/siblings.json")
                .then()
                .statusCode(OK.getStatusCode())                        
                .extract();
        assertValidTaxonomiesResponseInTheCorrectContentTypeAndNotEmptyList(jsonResponse, ContentType.JSON);
    }
    
    @Test
    public void lookupTaxonomySiblingsWithXmlResourcePathReturnsOKStatusJsonFormatAndTheCorrectList(){
        ExtractableResponse<Response> jsonResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/siblings.xml")
                .then()
                .statusCode(OK.getStatusCode())                        
                .extract();
        assertValidTaxonomiesResponseInTheCorrectContentTypeAndNotEmptyList(jsonResponse, ContentType.XML);
    }
    
    @Test
    public void lookupTaxonomyParentWithDefaultResourcePathReturnsOKStatusJsonFormatAndTheCorrectList(){
        ExtractableResponse<Response> jsonResponse = when()
                .get(TAXONOMY_BASE_PATH + "/id/12345/parent")
                .then()
                .statusCode(OK.getStatusCode())                        
                .extract();
        
    }        
    
	private void assertResourceNotFoundResponseInTheCorrectContentType(ExtractableResponse<Response> response,ContentType contentType) {
		assertThat(response, is(not(equalTo(null))));
    	assertThat(response.contentType(), equalTo(contentType.toString()));
    	ErrorMessage error = response.as(ErrorMessage.class);
    	assertThat(error.getErrorMessage(), equalTo(SwaggerConstant.API_RESPONSE_404));
	}
	
	private void assertBadRequestResponseInTheCorrectContentType(ExtractableResponse<Response> response,ContentType contentType) {
		assertThat(response, notNullValue());
    	assertThat(response.contentType(), equalTo(contentType.toString()));
    	ErrorMessage error = response.as(ErrorMessage.class);
    	assertThat(error.getErrorMessage(), equalTo(SwaggerConstant.API_RESPONSE_400));
	}
	
	private void assertValidtTaxonomyNodeResponseInTheCorrectContentType(ExtractableResponse<Response> response,ContentType contentType, long taxonomyId) {
		assertThat(response, notNullValue());
    	assertThat(response.contentType(), equalTo(contentType.toString()));
    	TaxonomyNode node = response.as(TaxonomyNode.class);
    	assertThat(node.getTaxonomyId(), equalTo(taxonomyId));
	}
	
	
	private void assertValidTaxonomiesResponseInTheCorrectContentTypeAndNotEmptyList(ExtractableResponse<Response> response,ContentType contentType) {
		assertThat(response, notNullValue());
    	assertThat(response.contentType(), equalTo(contentType.toString()));
    	
    	Taxonomies taxonomies = response.as(Taxonomies.class);
    	assertThat(taxonomies.getTaxonomies(), is(not(equalTo(null))));
    	
    	List<TaxonomyNode> items = taxonomies.getTaxonomies();
    	assertThat(items.isEmpty(), is(false));
	}

	/*private void assertThatTaxonomies(ExtractableResponse<Response> response,ContentType contentType, long taxonomyId) {
		TaxonomyNode firstItem = assertValidTaxonomiesResponseInTheCorrectContentTypeAndNotEmptyList(response, contentType);
		assertThat(firstItem.getParent(), notNullValue());    	
    	assertThat(firstItem.getParent().getTaxonomyId(), is(equalTo(taxonomyId)));
	}*/
}