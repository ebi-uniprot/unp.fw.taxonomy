package uk.ac.ebi.uniprot.taxonomyservice.restful.rest;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.RelationshipRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;

import io.swagger.annotations.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.SwaggerConstant.*;

/**
 * This class is responsible to provide services about taxonomy tree and return Taxonomy details
 *
 * Created by lgonzales on 19/02/16.
 *
 */
@Path("/taxonomy")
@Api(value = "/taxonomy", description = TAXONOMY_API_VALUE)
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class TaxonomyRest {
    public static final Logger logger = LoggerFactory.getLogger(TaxonomyRest.class);

    public TaxonomyRest() {
        logger.debug("created instance of TaxonomyRest");
    }

    @Inject
    private TaxonomyDataAccess dataAccess;

    @Context
    private HttpServletRequest request;

    @GET
    @ApiOperation(value = API_OPERATION_TAXONOMY_DETAIL_BY_ID,
            notes = NOTE_TAXONOMY_ID,
            response = TaxonomyNode.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = ID_PARAMETER_IS_REQUIRED,response = ErrorMessage.class),
            @ApiResponse(code = 400, message = API_RESPONSE_400 , response = ErrorMessage.class),
            @ApiResponse(code = 400, message = REQUEST_PARAMETER_INVALID_VALUE, response = ErrorMessage.class),
            @ApiResponse(code = 404, message = API_RESPONSE_404, response = ErrorMessage.class),
            @ApiResponse(code = 500, message = API_RESPONSE_500, response = ErrorMessage.class)})
    @Path("/id/{id}")
    public Response getTaxonomyDetailsById(@ApiParam(value = "id", required = true)
    @NotNull(message = ID_PARAMETER_IS_REQUIRED) @PathParam("id") Long taxonomyId) {
        logger.debug(">>TaxonomyRest.getTaxonomyDetailsById");

        TaxonomyNode response = dataAccess.getTaxonomyDetailsById(taxonomyId);

        return buildTaxonomyNodeResponse(response,taxonomyId);
    }

    @GET
    @ApiOperation(value = API_OPERATION_TAXONOMY_SIBLINGS_BY_ID,
            notes = NOTE_TAXONOMY_ID,
            response = Taxonomies.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = ID_PARAMETER_IS_REQUIRED,response = ErrorMessage.class),
            @ApiResponse(code = 400, message = API_RESPONSE_400 , response = ErrorMessage.class),
            @ApiResponse(code = 400, message = REQUEST_PARAMETER_INVALID_VALUE, response = ErrorMessage.class),
            @ApiResponse(code = 404, message = API_RESPONSE_404, response = ErrorMessage.class),
            @ApiResponse(code = 500, message = API_RESPONSE_500, response = ErrorMessage.class)})
    @Path("/id/{id}/siblings")
    public Response getTaxonomyNodesSiblingsById(@ApiParam(value = "id", required = true)
    @NotNull(message = ID_PARAMETER_IS_REQUIRED) @PathParam("id") Long taxonomyId) {
        logger.debug(">>TaxonomyRest.getTaxonomyDetailsByIdXml");

        Taxonomies response = dataAccess.getTaxonomySiblingsById(taxonomyId);

        return buildTaxonomiesResponse(response,taxonomyId);
    }

    @GET
    @ApiOperation(value = API_OPERATION_TAXONOMY_CHILDREN_BY_ID,
            notes = NOTE_TAXONOMY_ID,
            response = Taxonomies.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = ID_PARAMETER_IS_REQUIRED,response = ErrorMessage.class),
            @ApiResponse(code = 400, message = API_RESPONSE_400 , response = ErrorMessage.class),
            @ApiResponse(code = 400, message = REQUEST_PARAMETER_INVALID_VALUE, response = ErrorMessage.class),
            @ApiResponse(code = 404, message = API_RESPONSE_404, response = ErrorMessage.class),
            @ApiResponse(code = 500, message = API_RESPONSE_500, response = ErrorMessage.class)})
    @Path("/id/{id}/children")
    public Response getTaxonomyNodesChildrenById(@ApiParam(value = "id", required = true)
    @NotNull(message = ID_PARAMETER_IS_REQUIRED) @PathParam("id") Long taxonomyId) {
        logger.debug(">>TaxonomyRest.getTaxonomyDetailsByIdXml");

        Taxonomies response = dataAccess.getTaxonomyChildrenById(taxonomyId);

        return buildTaxonomiesResponse(response,taxonomyId);
    }

    @GET
    @ApiOperation(value = API_OPERATION_TAXONOMY_PARENT_BY_ID,
            notes = NOTE_TAXONOMY_ID,
            response = TaxonomyNode.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = ID_PARAMETER_IS_REQUIRED)})
    @Path("/id/{id}/parent")
    public Response getTaxonomyNodeParentById(@ApiParam(value = "id", required = true)
    @NotNull(message = ID_PARAMETER_IS_REQUIRED) @PathParam("id") Long taxonomyId) {
        logger.debug(">>TaxonomyRest.getTaxonomyDetailsByIdXml");

        TaxonomyNode response = dataAccess.getTaxonomyParentById(taxonomyId);

        return buildTaxonomyNodeResponse(response,taxonomyId);
    }

    @GET
    @ApiOperation(value = API_OPERATION_TAXONOMY_DETAIL_BY_NAME,
            notes = NOTE_TAXONOMY_DETAIL_BY_NAME,
            response = Taxonomies.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = NAME_PARAMETER_IS_REQUIRED,response = ErrorMessage.class),
            @ApiResponse(code = 400, message = API_RESPONSE_400 , response = ErrorMessage.class),
            @ApiResponse(code = 400, message = REQUEST_PARAMETER_INVALID_VALUE, response = ErrorMessage.class),
            @ApiResponse(code = 404, message = API_RESPONSE_404, response = ErrorMessage.class),
            @ApiResponse(code = 500, message = API_RESPONSE_500, response = ErrorMessage.class)})
    @Path("/name/{name}")
    public Response getTaxonomiesDetailsByName(@ApiParam(value = "name", required = true)
    @NotNull(message = NAME_PARAMETER_IS_REQUIRED) @PathParam("name") String taxonomyName) {

        Taxonomies response = dataAccess.getTaxonomyDetailsByName(taxonomyName);
        if (response != null) {
            return Response.ok(response).build();
        } else {
            return buildNotFoundResponse();
        }
    }

    @GET
    @ApiOperation(value = API_OPERATION_TAXONOMY_RELATIONSHIP,
            notes = NOTE_TAXONOMY_RELATIONSHIP,
            response = TaxonomyNode.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = API_RESPONSE_400 , response = ErrorMessage.class),
            @ApiResponse(code = 400, message = FROM_PARAMETER_IS_REQUIRED, response = ErrorMessage.class),
            @ApiResponse(code = 400, message = TO_PARAMETER_IS_REQUIRED, response = ErrorMessage.class),
            @ApiResponse(code = 400, message = REQUEST_PARAMETER_INVALID_VALUE, response = ErrorMessage.class),
            @ApiResponse(code = 404, message = API_RESPONSE_404, response = ErrorMessage.class),
            @ApiResponse(code = 500, message = API_RESPONSE_500, response = ErrorMessage.class)})
    @Path("/relationship")
    public Response checkRelationshipBetweenTaxonomies(@Valid @BeanParam RelationshipRequestParams params) {

        TaxonomyNode response = dataAccess.checkRelationshipBetweenTaxonomies(params.getFrom(), params.getTo());
        if (response != null) {
            return Response.ok(response).build();
        } else {
            String newURL = request.getRequestURL().toString();
            long newId = 0;
            long newFromTaxonomyId = dataAccess.checkTaxonomyIdHistoricalChange(params.getFrom());
            if(newFromTaxonomyId > 0){
                newURL = getNewRedirectHeaderLocationURL(newURL,params.getFrom(), newFromTaxonomyId);
                newId = newFromTaxonomyId;
            }
            long newToTaxonomyId = dataAccess.checkTaxonomyIdHistoricalChange(params.getTo());
            if(newToTaxonomyId > 0){
                newURL = getNewRedirectHeaderLocationURL(newURL,params.getFrom(), newToTaxonomyId);
                newId = newToTaxonomyId;
            }
            if(newFromTaxonomyId > 0 || newToTaxonomyId > 0){
                return buildRedirectResponse(newURL,newId);
            }else {
                return buildNotFoundResponse();
            }
        }
    }

    @GET
    @ApiOperation(value = API_OPERATION_TAXONOMY_PATH,
            notes = NOTE_TAXONOMY_PATH,
            response = TaxonomyNode.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = ID_PARAMETER_IS_REQUIRED,response = ErrorMessage.class),
            @ApiResponse(code = 400, message = DEPTH_PARAMETER_IS_REQUIRED , response = ErrorMessage.class),
            @ApiResponse(code = 400, message = DEPTH_PARAM_MIN_MAX , response = ErrorMessage.class),
            @ApiResponse(code = 400, message = DIRECTION_VALID_VALUES , response = ErrorMessage.class),
            @ApiResponse(code = 400, message = DIRECTION_PARAMETER_IS_REQUIRED , response = ErrorMessage.class),
            @ApiResponse(code = 400, message = REQUEST_PARAMETER_INVALID_VALUE, response = ErrorMessage.class),
            @ApiResponse(code = 404, message = API_RESPONSE_404, response = ErrorMessage.class),
            @ApiResponse(code = 500, message = API_RESPONSE_500, response = ErrorMessage.class)})
    @Path("/path")
    public Response getTaxonomyPath(
    @Valid @BeanParam PathRequestParams pathRequestParam) {

        TaxonomyNode response = dataAccess.getTaxonomyPath(pathRequestParam);

        return buildTaxonomyNodeResponse(response,pathRequestParam.getId());
    }

    private Response buildTaxonomyNodeResponse(TaxonomyNode response,long taxonomyId) {
        if (response != null) {
            return Response.ok(response).build();
        } else {
            return buildResponseWithHistoricalCheck(taxonomyId);
        }
    }

    private Response buildTaxonomiesResponse(Taxonomies response,long taxonomyId) {
        if (response != null) {
            return Response.ok(response).build();
        } else {
            return buildResponseWithHistoricalCheck(taxonomyId);
        }
    }

    private Response buildResponseWithHistoricalCheck(long taxonomyId) {
        long newTaxonomyId = dataAccess.checkTaxonomyIdHistoricalChange(taxonomyId);
        if(newTaxonomyId > 0){
            String newURL = getNewRedirectHeaderLocationURL(request.getRequestURL().toString(),taxonomyId, newTaxonomyId);
            return buildRedirectResponse(newURL,newTaxonomyId);
        }else {
            return buildNotFoundResponse();
        }
    }

    private Response buildNotFoundResponse() {
        ErrorMessage error = new ErrorMessage();
        error.setRequestedURL(request.getRequestURL().toString(),request.getQueryString());
        error.addErrorMessage(API_RESPONSE_404);
        return Response.status(Response.Status.NOT_FOUND).entity(error).build();
    }

    private Response buildRedirectResponse(String newURL,long newId) {
        ErrorMessage error = new ErrorMessage();
        error.setRequestedURL(request.getRequestURL().toString(),request.getQueryString());
        error.addErrorMessage(API_RESPONSE_303.replace("{newId}",""+newId));
        return Response.status(Response.Status.SEE_OTHER).entity(error).header(HttpHeaders.LOCATION,newURL).build();
    }

    private String getNewRedirectHeaderLocationURL(String currentURL,long oldId, long newId) {
        if (request.getQueryString() != null && !request.getQueryString().isEmpty()) {
            try {
                currentURL+= "?"+(URLDecoder.decode(request.getQueryString(),"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                logger.error("Error encoding ErrorMessage.requestedURL: ",e);
            }
        }
        return currentURL.replace(""+oldId,""+newId);
    }

}