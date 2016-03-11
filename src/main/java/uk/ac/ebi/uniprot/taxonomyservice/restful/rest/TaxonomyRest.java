package uk.ac.ebi.uniprot.taxonomyservice.restful.rest;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;
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
@Path("/taxonomy")
@Api(value = "/taxonomy", description = SwaggerConstant.TAXONOMY_API_VALUE)
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class TaxonomyRest {
    public static final Logger logger = LoggerFactory.getLogger(TaxonomyRest.class);

    public TaxonomyRest(){
        logger.debug("created instance of TaxonomyRest");
    }

    @Inject
    private TaxonomyDataAccess dataAccess;

    @GET
    @ApiOperation(value = SwaggerConstant.API_OPERATION_TAXONOMY_DETAIL_BY_ID,
            notes = SwaggerConstant.NOTE_TAXONOMY_ID,
            response = TaxonomyNode.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = SwaggerConstant.API_RESPONSE_404),
            @ApiResponse(code = 400, message = SwaggerConstant.API_RESPONSE_400)})
    @Path("/id/{taxonomyId}")
    public Response getTaxonomyDetailsById(@ApiParam(value = "taxonomyId", required = true)
    @PathParam("taxonomyId") long taxonomyId) {
        logger.debug(">>TaxonomyRest.getTaxonomyDetailsByIdXml");

        TaxonomyNode response = dataAccess.getTaxonomyDetailsById(taxonomyId);

        return Response.ok(response).build();
    }

    @GET
    @ApiOperation(value = SwaggerConstant.API_OPERATION_TAXONOMY_SIBLINGS_BY_ID,
            notes = SwaggerConstant.NOTE_TAXONOMY_ID,
            response = Taxonomies.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = SwaggerConstant.API_RESPONSE_404),
            @ApiResponse(code = 400, message = SwaggerConstant.API_RESPONSE_400)})
    @Path("/id/{taxonomyId}/siblings")
    public Response getTaxonomyNodesSiblingsById(@ApiParam(value = "taxonomyId", required = true)
    @PathParam("taxonomyId") long taxonomyId) {
        logger.debug(">>TaxonomyRest.getTaxonomyDetailsByIdXml");

        Taxonomies response = dataAccess.getTaxonomySiblingsById(taxonomyId);

        return Response.ok(response).build();
    }

    @GET
    @ApiOperation(value = SwaggerConstant.API_OPERATION_TAXONOMY_CHILDREN_BY_ID,
            notes = SwaggerConstant.NOTE_TAXONOMY_ID,
            response = Taxonomies.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = SwaggerConstant.API_RESPONSE_404),
            @ApiResponse(code = 400, message = SwaggerConstant.API_RESPONSE_400)})
    @Path("/id/{taxonomyId}/children")
    public Response getTaxonomyNodesChildrenById(@ApiParam(value = "taxonomyId", required = true)
    @PathParam("taxonomyId") long taxonomyId) {
        logger.debug(">>TaxonomyRest.getTaxonomyDetailsByIdXml");

        Taxonomies response = dataAccess.getTaxonomyChildrenById(taxonomyId);

        return Response.ok(response).build();
    }

    @GET
    @ApiOperation(value = SwaggerConstant.API_OPERATION_TAXONOMY_PARENT_BY_ID,
            notes = SwaggerConstant.NOTE_TAXONOMY_ID,
            response = TaxonomyNode.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = SwaggerConstant.API_RESPONSE_404),
            @ApiResponse(code = 400, message = SwaggerConstant.API_RESPONSE_400)})
    @Path("/id/{taxonomyId}/parent")
    public Response getTaxonomyNodeParentById(@ApiParam(value = "taxonomyId", required = true)
    @PathParam("taxonomyId") long taxonomyId) {
        logger.debug(">>TaxonomyRest.getTaxonomyDetailsByIdXml");

        TaxonomyNode response = dataAccess.getTaxonomyParentById(taxonomyId);

        return Response.ok(response).build();
    }

    @GET
    @ApiOperation(value = SwaggerConstant.API_OPERATION_TAXONOMY_DETAIL_BY_NAME,
            notes = SwaggerConstant.NOTE_TAXONOMY_DETAIL_BY_NAME,
            response = Taxonomies.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = SwaggerConstant.API_RESPONSE_404),
            @ApiResponse(code = 400, message = SwaggerConstant.API_RESPONSE_400)})
    @Path("/name/{taxonomyName}")
    public Response getTaxonomiesDetailsByName(@ApiParam(value = "taxonomyName", required = true)
    @PathParam("taxonomyName") String taxonomyName) {

        Taxonomies response = dataAccess.getTaxonomyDetailsByName(taxonomyName);

        return Response.ok(response).build();
    }

    @GET
    @ApiOperation(value = SwaggerConstant.API_OPERATION_TAXONOMY_RELATIONSHIP,
            notes = SwaggerConstant.NOTE_TAXONOMY_RELATIONSHIP,
            response = TaxonomyNode.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = SwaggerConstant.API_RESPONSE_404),
            @ApiResponse(code = 400, message = SwaggerConstant.API_RESPONSE_400)})
    @Path("/relationship")
    public Response checkRelationshipBetweenTaxonomies(@ApiParam(value = "taxonomyId1", required = true)
    @PathParam("taxonomyId1") long taxonomyId1, @ApiParam(value = "taxonomyId2", required = true)
    @PathParam("taxonomyId2") long taxonomyId2) {

        TaxonomyNode response = dataAccess.checkRelationshipBetweenTaxonomies(taxonomyId1, taxonomyId2);

        return Response.ok(response).build();
    }

    @GET
    @ApiOperation(value = SwaggerConstant.API_OPERATION_TAXONOMY_PATH,
            notes = SwaggerConstant.NOTE_TAXONOMY_PATH,
            response = TaxonomyNode.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = SwaggerConstant.API_RESPONSE_404),
            @ApiResponse(code = 400, message = SwaggerConstant.API_RESPONSE_400)})
    @Path("/path")
    public Response getTaxonomyPath(@ApiParam(value = "taxonomyId", required = true)
    @PathParam("taxonomyId") long taxonomyId, @ApiParam(value = "direction", required = true)
    @PathParam("direction") String direction, @ApiParam(value = "depth", required = true)
    @PathParam("depth") int depth){

        TaxonomyNode response = null;//dataAccess.getTaxonomyPath(taxonomyId, direction, depth);

        return Response.ok(response).build();
    }


}
