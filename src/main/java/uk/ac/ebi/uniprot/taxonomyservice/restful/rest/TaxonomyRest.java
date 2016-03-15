package uk.ac.ebi.uniprot.taxonomyservice.restful.rest;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.RelationshipRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;
import uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.SwaggerConstant;

import io.swagger.annotations.*;
import javax.inject.Inject;
import javax.ws.rs.*;
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

    public TaxonomyRest() {
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
        logger.debug(">>TaxonomyRest.getTaxonomyDetailsById");

        TaxonomyNode response = dataAccess.getTaxonomyDetailsById(taxonomyId);

        return returnTaxonomyNodeResponse(response);
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

        return returnTaxonomiesResponse(response);
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

        return returnTaxonomiesResponse(response);
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

        return returnTaxonomyNodeResponse(response);
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

        return returnTaxonomiesResponse(response);
    }

    @GET
    @ApiOperation(value = SwaggerConstant.API_OPERATION_TAXONOMY_RELATIONSHIP,
            notes = SwaggerConstant.NOTE_TAXONOMY_RELATIONSHIP,
            response = TaxonomyNode.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = SwaggerConstant.API_RESPONSE_404),
            @ApiResponse(code = 400, message = SwaggerConstant.API_RESPONSE_400)})
    @Path("/relationship")
    public Response checkRelationshipBetweenTaxonomies(@BeanParam RelationshipRequestParams params) {

        TaxonomyNode response = dataAccess.checkRelationshipBetweenTaxonomies(params.getTo(), params.getTo());

        return returnTaxonomyNodeResponse(response);
    }

    @GET
    @ApiOperation(value = SwaggerConstant.API_OPERATION_TAXONOMY_PATH,
            notes = SwaggerConstant.NOTE_TAXONOMY_PATH,
            response = TaxonomyNode.class)
    @ApiResponses(value = {@ApiResponse(code = 404, message = SwaggerConstant.API_RESPONSE_404),
            @ApiResponse(code = 400, message = SwaggerConstant.API_RESPONSE_400)})
    @Path("/path")
    public Response getTaxonomyPath(@BeanParam PathRequestParams pathRequestParam) {

        TaxonomyNode response = dataAccess.getTaxonomyPath(pathRequestParam);

        return returnTaxonomyNodeResponse(response);
    }

    private Response returnTaxonomyNodeResponse(TaxonomyNode response) {
        if (response != null) {
            return Response.ok(response).build();
        } else {
            return returnNotFoundResponse();
        }
    }

    private Response returnTaxonomiesResponse(Taxonomies response) {
        if (response != null) {
            return Response.ok(response).build();
        } else {
            return returnNotFoundResponse();
        }
    }

    private Response returnNotFoundResponse() {
        ErrorMessage error = new ErrorMessage();
        error.setErrorMessage(SwaggerConstant.API_RESPONSE_404);
        return Response.status(Response.Status.NOT_FOUND).entity(error).build();
    }

}