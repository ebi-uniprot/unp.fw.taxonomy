package uk.ac.ebi.uniprot.taxonomyservice.restful.rest;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyDetailResponse;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNamesResponse;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyPathResponse;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyRelationshipResponse;
import uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.SwaggerConstant;

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
@Api(value = "/taxonomy", description = SwaggerConstant.TAXONOMY_API_VALUE)
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class TaxonomyRest {
    public static final Logger logger = LoggerFactory.getLogger(TaxonomyRest.class);

    @Inject
    private TaxonomyDataAccess dataAccess;

    @GET
    @ApiOperation(value = SwaggerConstant.API_OPERATION_TAXONOMY_DETAIL_BY_ID_XML,
            notes = SwaggerConstant.NOTE_TAXONOMY_DETAIL_BY_ID,
            response = TaxonomyDetailResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = SwaggerConstant.API_RESPONSE_404),
            @ApiResponse(code = 400, message = SwaggerConstant.API_RESPONSE_400)})
    @Path("/id/{taxonomyId}.xml")
    @Produces({MediaType.APPLICATION_XML})
    public Response getTaxonomyDetailsByIdXml(@ApiParam(value = "taxonomyId", required = true)
    @PathParam("taxonomyId") long taxonomyId) {
        logger.debug(">>TaxonomyRest.getTaxonomyDetailsByIdXml");

        TaxonomyDetailResponse response = dataAccess.getTaxonomyDetailsById(taxonomyId);

        return Response.ok(response).type(MediaType.APPLICATION_XML_TYPE).status(Response.Status.OK)
                .build();
    }

    @GET
    @ApiOperation(value = SwaggerConstant.API_OPERATION_TAXONOMY_DETAIL_BY_ID_JSON,
            notes = SwaggerConstant.NOTE_TAXONOMY_DETAIL_BY_ID,
            response = TaxonomyDetailResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = SwaggerConstant.API_RESPONSE_404),
            @ApiResponse(code = 400, message = SwaggerConstant.API_RESPONSE_400)})
    @Path("/id/{taxonomyId}.json")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getTaxonomyDetailsByIdJson(@ApiParam(value = "taxonomyId", required = true)
    @PathParam("taxonomyId") long taxonomyId) {
        logger.debug(">>TaxonomyRest.getTaxonomyDetailsByIdJson");

        TaxonomyDetailResponse response = dataAccess.getTaxonomyDetailsById(taxonomyId);

        return Response.ok(response).type(MediaType.APPLICATION_JSON_TYPE).status(Response.Status.OK)
                .build();
    }

    @GET
    @ApiOperation(value = SwaggerConstant.API_OPERATION_TAXONOMY_DETAIL_BY_NAME_XML,
            notes = SwaggerConstant.NOTE_TAXONOMY_DETAIL_BY_NAME,
            response = TaxonomyDetailResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = SwaggerConstant.API_RESPONSE_404),
            @ApiResponse(code = 400, message = SwaggerConstant.API_RESPONSE_400)})
    @Path("/name/{taxonomyName}.xml")
    @Produces({MediaType.APPLICATION_XML})
    public Response getTaxonomyDetailsByNameXml(@ApiParam(value = "taxonomyName", required = true)
    @PathParam("taxonomyName") String taxonomyName) {

        TaxonomyNamesResponse response = dataAccess.getTaxonomyDetailsByName(taxonomyName);

        return Response.ok(response).type(MediaType.APPLICATION_XML_TYPE).status(Response.Status.OK)
                .build();
    }

    @GET
    @ApiOperation(value = SwaggerConstant.API_OPERATION_TAXONOMY_DETAIL_BY_NAME_JSON,
            notes = SwaggerConstant.NOTE_TAXONOMY_DETAIL_BY_NAME,
            response = TaxonomyDetailResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = SwaggerConstant.API_RESPONSE_404),
            @ApiResponse(code = 400, message = SwaggerConstant.API_RESPONSE_400)})
    @Path("/name/{taxonomyName}.json")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getTaxonomyDetailsByNameJson(@ApiParam(value = "taxonomyName", required = true)
    @PathParam("taxonomyName") String taxonomyName) {

        TaxonomyNamesResponse response = dataAccess.getTaxonomyDetailsByName(taxonomyName);

        return Response.ok(response).type(MediaType.APPLICATION_JSON_TYPE).status(Response.Status.OK)
                .build();
    }

    @GET
    @ApiOperation(value = SwaggerConstant.API_OPERATION_TAXONOMY_RELATIONSHIP_XML,
            notes = SwaggerConstant.NOTE_TAXONOMY_RELATIONSHIP,
            response = TaxonomyDetailResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = SwaggerConstant.API_RESPONSE_404),
            @ApiResponse(code = 400, message = SwaggerConstant.API_RESPONSE_400)})
    @Path("/id/{taxonomyId1}/id/{taxonomyId2}.xml")
    @Produces({MediaType.APPLICATION_XML})
    public Response checkRelationshipBetweenTaxonomiesXml(@ApiParam(value = "taxonomyId1", required = true)
    @PathParam("taxonomyId1") long taxonomyId1, @ApiParam(value = "taxonomyId2", required = true)
    @PathParam("taxonomyId2") long taxonomyId2) {

        TaxonomyRelationshipResponse response = dataAccess.checkRelationshipBetweenTaxonomies(taxonomyId1, taxonomyId2);

        return Response.ok(response).type(MediaType.APPLICATION_XML_TYPE).status(Response.Status.OK)
                .build();
    }

    @GET
    @ApiOperation(value = SwaggerConstant.API_OPERATION_TAXONOMY_RELATIONSHIP_JSON,
            notes = SwaggerConstant.NOTE_TAXONOMY_RELATIONSHIP,
            response = TaxonomyDetailResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = SwaggerConstant.API_RESPONSE_404),
            @ApiResponse(code = 400, message = SwaggerConstant.API_RESPONSE_400)})
    @Path("/id/{taxonomyId1}/id/{taxonomyId2}.json")
    @Produces({MediaType.APPLICATION_JSON})
    public Response checkRelationshipBetweenTaxonomiesJson(@ApiParam(value = "taxonomyId1", required = true)
    @PathParam("taxonomyId1") long taxonomyId1, @ApiParam(value = "taxonomyId2", required = true)
    @PathParam("taxonomyId2") long taxonomyId2) {

        TaxonomyRelationshipResponse response = dataAccess.checkRelationshipBetweenTaxonomies(taxonomyId1, taxonomyId2);

        return Response.ok(response).type(MediaType.APPLICATION_JSON_TYPE).status(Response.Status.OK)
                .build();
    }


    @GET
    @ApiOperation(value = SwaggerConstant.API_OPERATION_TAXONOMY_PATH_XML,
            notes = SwaggerConstant.NOTE_TAXONOMY_PATH,
            response = TaxonomyDetailResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = SwaggerConstant.API_RESPONSE_404),
            @ApiResponse(code = 400, message = SwaggerConstant.API_RESPONSE_400)})
    @Path("/id/{taxonomyId}/direction/{direction}/depth/{depth}.xml")
    @Produces({MediaType.APPLICATION_XML})
    public Response getTaxonomyPathXml(@ApiParam(value = "taxonomyId", required = true)
    @PathParam("taxonomyId") long taxonomyId, @ApiParam(value = "direction", required = true)
    @PathParam("direction") String direction, @ApiParam(value = "depth", required = true)
    @PathParam("depth") int depth){

        TaxonomyPathResponse response = dataAccess.getTaxonomyPath(taxonomyId, direction, depth);

        return Response.ok(response).type(MediaType.APPLICATION_XML_TYPE).status(Response.Status.OK)
                .build();
    }


    @GET
    @ApiOperation(value = SwaggerConstant.API_OPERATION_TAXONOMY_PATH_JSON,
            notes = SwaggerConstant.NOTE_TAXONOMY_PATH,
            response = TaxonomyDetailResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = SwaggerConstant.API_RESPONSE_404),
            @ApiResponse(code = 400, message = SwaggerConstant.API_RESPONSE_400)})
    @Path("/id/{taxonomyId}/direction/{direction}/depth/{depth}.json")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getTaxonomyPathJson(@ApiParam(value = "taxonomyId", required = true)
    @PathParam("taxonomyId") long taxonomyId, @ApiParam(value = "direction", required = true)
    @PathParam("direction") String direction, @ApiParam(value = "depth", required = true)
    @PathParam("depth") int depth){

        TaxonomyPathResponse response = dataAccess.getTaxonomyPath(taxonomyId, direction, depth);

        return Response.ok(response).type(MediaType.APPLICATION_JSON_TYPE).status(Response.Status.OK)
                .build();
    }

}
