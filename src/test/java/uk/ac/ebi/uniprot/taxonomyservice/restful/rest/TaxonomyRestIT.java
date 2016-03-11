package uk.ac.ebi.uniprot.taxonomyservice.restful.rest;

import org.junit.ClassRule;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.when;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;

/**
 * Class used to test Taxonomy service use case
 *
 * Created by lgonzales on 24/02/16.
 */
public class TaxonomyRestIT {

    private static final String TAXONOMY_BASE_PATH = "/taxonomy";


    @ClassRule
    public static RestContainer restContainer = new RestContainer();


    @Test
    public void lookupTaxonomyIdWithValidId(){
        when()
                .get(TAXONOMY_BASE_PATH + "/id/12345")
                .then()
                .statusCode(OK.getStatusCode());
    }


    @Test
    public void lookupTaxonomyIdWithResourceXmlPath(){
        when()
                .get(TAXONOMY_BASE_PATH + "/id/12345.xml")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    public void lookupTaxonomyIdWithResourceJsonPath(){
        when()
                .get(TAXONOMY_BASE_PATH + "/id/12345.json")
                .then()
                .statusCode(OK.getStatusCode());
    }

    @Test
    public void lookupTaxonomyIdWithInvalidId(){
        when()
                .get(TAXONOMY_BASE_PATH + "/id/UNKNOW")
                .then()
                .statusCode(BAD_REQUEST.getStatusCode());
    }

    @Test
    public void lookupTaxonomyIdWithEmptyId(){
        when()
                .get(TAXONOMY_BASE_PATH + "/id/")
                .then()
                .statusCode(OK.getStatusCode());
    }


}