package uk.ac.ebi.uniprot.taxonomyservice.restful.rest;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyDetailResponse;

import io.swagger.annotations.*;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible to provide services about taxonomy tree and return Taxonomy details
 *
 * Created by lgonzales on 19/02/16.
 *
 */
@Path("taxonomy")
@Api(value = "/taxonomy", description = "Rest services to return information about protein taxonomy tree. Basically " +
        "there are services that returns details about a taxonomy element and also details about it parent, siblings " +
        "and children. There are also services that return the relationship between 2 taxonomy elements, and the path" +
        " between them. Services can return JSON or XML format)")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class TaxonomyRest {
    public static final Logger logger = LoggerFactory.getLogger(TaxonomyRest.class);

    @Inject
    private TaxonomyDataAccess dataAccess;

    @GET
    @ApiOperation(value = "This service return details about a taxonomy element in json format",
            notes = "with taxonomy identification",
            response = TaxonomyDetailResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 404,
            message = "Taxonomy entry is not found"),
            @ApiResponse(code = 400,
                    message = "Invalid taxonomy id")})
    @Path("/{taxonomyId}.json")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getTaxonomyDetailsByIdJson(@ApiParam(value = "taxonomyId", required = true)
    @PathParam("taxonomyId")
    long taxonomyId) {
        logger.debug(">>TaxonomyRest.getTaxonomyDetailsByIdJson");

        TaxonomyDetailResponse response = dataAccess.getTaxonomyDetailsById(taxonomyId);

        return Response.ok(response).type(MediaType.APPLICATION_JSON_TYPE).status(Response.Status.OK)
                .build();
    }

    @GET
    @ApiOperation(value = "This service return details about a taxonomy element in xml format",
            notes = "with taxonomy identification",
            response = TaxonomyDetailResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 404,
            message = "Taxonomy entry is not found"),
            @ApiResponse(code = 400,
                    message = "Invalid taxonomy id")})
    @Path("/{taxonomyId}.xml")
    @Produces({MediaType.APPLICATION_XML})
    public Response getTaxonomyDetailsByIdXml(@ApiParam(value = "taxonomyId", required = true)
    @PathParam("taxonomyId")
    long taxonomyId) {
        logger.debug(">>TaxonomyRest.getTaxonomyDetailsByIdXml");

        TaxonomyDetailResponse response = dataAccess.getTaxonomyDetailsById(taxonomyId);

        return Response.ok(response).type(MediaType.APPLICATION_XML_TYPE).status(Response.Status.OK)
                .build();
    }

}
