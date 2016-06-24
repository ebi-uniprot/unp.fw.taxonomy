package uk.ac.ebi.uniprot.taxonomyservice.restful.rest;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.NameRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.RelationshipRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;
import uk.ac.ebi.uniprot.taxonomyservice.restful.util.URLUtil;

import io.swagger.annotations.*;
import java.util.Optional;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants.*;

/**
 * This class is responsible to provide services about taxonomy tree and return Taxonomy details
 *
 * Created by lgonzales on 19/02/16.
 *
 */
@Path("/")
@Api(value = "/taxonomy", description = TAXONOMY_API_VALUE)
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class TaxonomyRest {
    private static final Logger logger = LoggerFactory.getLogger(TaxonomyRest.class);

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
            @ApiResponse(code = 404, message = API_RESPONSE_404_ENTRY, response = ErrorMessage.class),
            @ApiResponse(code = 500, message = API_RESPONSE_500, response = ErrorMessage.class)})
    @Path("/id/{id}")
    public Response getTaxonomyDetailsById(
            @ApiParam(value = TAXONOMY_ID_PARAM, required = true)
            @NotNull(message = ID_PARAMETER_IS_REQUIRED)
            @PathParam("id")
            @Pattern(regexp = "[0-9]+", message = ID_PARAMETER_VALID_NUMBER)
            String id) {

        long taxonomyId = Long.valueOf(id);
        Optional<TaxonomyNode> response = dataAccess.getTaxonomyDetailsById(taxonomyId,URLUtil.getTaxonomyIdBasePath(request));

        return buildTaxonomyNodeResponse(response,taxonomyId,API_RESPONSE_404_ENTRY);
    }

    @GET
    @ApiOperation(value = API_OPERATION_TAXONOMY_SIBLINGS_BY_ID,
            notes = NOTE_TAXONOMY_ID,
            response = Taxonomies.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = ID_PARAMETER_IS_REQUIRED,response = ErrorMessage.class),
            @ApiResponse(code = 400, message = API_RESPONSE_400 , response = ErrorMessage.class),
            @ApiResponse(code = 400, message = REQUEST_PARAMETER_INVALID_VALUE, response = ErrorMessage.class),
            @ApiResponse(code = 404, message = API_RESPONSE_404_ENTRY, response = ErrorMessage.class),
            @ApiResponse(code = 500, message = API_RESPONSE_500, response = ErrorMessage.class)})
    @Path("/id/{id}/siblings")
    public Response getTaxonomyNodesSiblingsById(
            @ApiParam(value = TAXONOMY_ID_PARAM, required = true)
            @NotNull(message = ID_PARAMETER_IS_REQUIRED)
            @PathParam("id")
            @Pattern(regexp = "[0-9]+", message = ID_PARAMETER_VALID_NUMBER)
            String id) {
        logger.debug(">>TaxonomyRest.getTaxonomyDetailsById");

        long taxonomyId = Long.valueOf(id);
        Optional<Taxonomies> response = dataAccess.getTaxonomySiblingsById(taxonomyId);

        return buildTaxonomiesResponse(response,taxonomyId,API_RESPONSE_404_ENTRY);
    }

    @GET
    @ApiOperation(value = API_OPERATION_TAXONOMY_CHILDREN_BY_ID,
            notes = NOTE_TAXONOMY_ID,
            response = Taxonomies.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = ID_PARAMETER_IS_REQUIRED,response = ErrorMessage.class),
            @ApiResponse(code = 400, message = API_RESPONSE_400 , response = ErrorMessage.class),
            @ApiResponse(code = 400, message = REQUEST_PARAMETER_INVALID_VALUE, response = ErrorMessage.class),
            @ApiResponse(code = 404, message = API_RESPONSE_404_ENTRY, response = ErrorMessage.class),
            @ApiResponse(code = 500, message = API_RESPONSE_500, response = ErrorMessage.class)})
    @Path("/id/{id}/children")
    public Response getTaxonomyNodesChildrenById(
            @ApiParam(value = TAXONOMY_ID_PARAM, required = true)
            @NotNull(message = ID_PARAMETER_IS_REQUIRED)
            @PathParam("id")
            @Pattern(regexp = "[0-9]+", message = ID_PARAMETER_VALID_NUMBER)
            String id) {
        logger.debug(">>TaxonomyRest.getTaxonomyDetailsById");

        long taxonomyId = Long.valueOf(id);
        Optional<Taxonomies> response = dataAccess.getTaxonomyChildrenById(taxonomyId);

        return buildTaxonomiesResponse(response,taxonomyId,API_RESPONSE_404_ENTRY);
    }

    @GET
    @ApiOperation(value = API_OPERATION_TAXONOMY_PARENT_BY_ID,
            notes = NOTE_TAXONOMY_ID,
            response = TaxonomyNode.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = ID_PARAMETER_IS_REQUIRED)})
    @Path("/id/{id}/parent")
    public Response getTaxonomyNodeParentById(
            @ApiParam(value = TAXONOMY_ID_PARAM, required = true)
            @NotNull(message = ID_PARAMETER_IS_REQUIRED)
            @PathParam("id")
            @Pattern(regexp = "[0-9]+", message = ID_PARAMETER_VALID_NUMBER)
            String id) {
        logger.debug(">>TaxonomyRest.getTaxonomyDetailsById");

        long taxonomyId = Long.valueOf(id);
        Optional<TaxonomyNode> response = dataAccess.getTaxonomyParentById(taxonomyId);

        return buildTaxonomyNodeResponse(response,taxonomyId,API_RESPONSE_404_ENTRY);
    }

    @GET
    @ApiOperation(value = API_OPERATION_TAXONOMY_DETAIL_BY_NAME,
            notes = NOTE_TAXONOMY_DETAIL_BY_NAME,
            response = Taxonomies.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = NAME_PARAMETER_IS_REQUIRED,response = ErrorMessage.class),
            @ApiResponse(code = 400, message = API_RESPONSE_400 , response = ErrorMessage.class),
            @ApiResponse(code = 400, message = REQUEST_PARAMETER_INVALID_VALUE, response = ErrorMessage.class),
            @ApiResponse(code = 404, message = API_RESPONSE_404_NAME, response = ErrorMessage.class),
            @ApiResponse(code = 500, message = API_RESPONSE_500, response = ErrorMessage.class)})
    @Path("/name/{name}")
    public Response getTaxonomiesDetailsByName(@Valid @BeanParam NameRequestParams params) {

        Optional<Taxonomies> response = dataAccess.getTaxonomyDetailsByName(params,URLUtil.getTaxonomyIdBasePath
                (request));
        if (response.isPresent()) {
            return Response.ok(response.get()).build();
        } else {
            return buildNotFoundResponse(API_RESPONSE_404_NAME);
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
            @ApiResponse(code = 404, message = API_RESPONSE_404_RELATIONSHIP, response = ErrorMessage.class),
            @ApiResponse(code = 500, message = API_RESPONSE_500, response = ErrorMessage.class)})
    @Path("/relationship")
    public Response checkRelationshipBetweenTaxonomies(@Valid @BeanParam RelationshipRequestParams params) {

        long from = Long.valueOf(params.getFrom());
        long to = Long.valueOf(params.getTo());

        Optional<TaxonomyNode> response = dataAccess.getTaxonomiesRelationship(from,to);
        if (response.isPresent()) {
            return Response.ok(response.get()).build();
        } else {
            String newURL = URLUtil.getCurrentURL(request);
            Optional<Long> newFromTaxonomyId = dataAccess.getTaxonomyHistoricalChange(from);
            if(newFromTaxonomyId.isPresent()){
                newURL = URLUtil.getNewRedirectHeaderLocationURL(newURL,"from",from, newFromTaxonomyId.get());
                from = newFromTaxonomyId.get();
            }else{
                from = -1;
            }
            Optional<Long> newToTaxonomyId = dataAccess.getTaxonomyHistoricalChange(to);
            if(newToTaxonomyId.isPresent()){
                newURL = URLUtil.getNewRedirectHeaderLocationURL(newURL,"to",to, newToTaxonomyId.get());
                to = newToTaxonomyId.get();
            }else{
                to = -1;
            }
            if(newFromTaxonomyId.isPresent() || newToTaxonomyId.isPresent()){
                return buildRedirectResponse(newURL,from,to);
            }else {
                return buildNotFoundResponse(API_RESPONSE_404_RELATIONSHIP);
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
            @ApiResponse(code = 404, message = API_RESPONSE_404_PATH, response = ErrorMessage.class),
            @ApiResponse(code = 500, message = API_RESPONSE_500, response = ErrorMessage.class)})
    @Path("/path")
    public Response getTaxonomyPath(
    @Valid @BeanParam PathRequestParams pathRequestParam) {

        Optional<TaxonomyNode> response = dataAccess.getTaxonomyPath(pathRequestParam);

        return buildTaxonomyNodeResponse(response,Long.valueOf(pathRequestParam.getId()),API_RESPONSE_404_PATH);
    }


    @GET
    @ApiOperation(value = API_OPERATION_TAXONOMY_DETAIL_BY_ID,
            notes = NOTE_TAXONOMY_ID,
            response = TaxonomyNode.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = ID_PARAMETER_IS_REQUIRED,response = ErrorMessage.class),
            @ApiResponse(code = 400, message = API_RESPONSE_400 , response = ErrorMessage.class),
            @ApiResponse(code = 400, message = REQUEST_PARAMETER_INVALID_VALUE, response = ErrorMessage.class),
            @ApiResponse(code = 404, message = API_RESPONSE_404_LINEAGE, response = ErrorMessage.class),
            @ApiResponse(code = 500, message = API_RESPONSE_500, response = ErrorMessage.class)})
    @Path("/lineage/{id}")
    public Response getTaxonomyLineageById(
            @ApiParam(value = TAXONOMY_ID_PARAM, required = true)
            @NotNull(message = ID_PARAMETER_IS_REQUIRED)
            @PathParam("id")
            @Pattern(regexp = "[0-9]+", message = ID_PARAMETER_VALID_NUMBER) String id) {

        long taxonomyId = Long.valueOf(id);
        Optional<Taxonomies> response = dataAccess.getTaxonomyLineageById(taxonomyId);

        return buildTaxonomiesResponse(response,taxonomyId,API_RESPONSE_404_LINEAGE);
    }

    private Response buildTaxonomyNodeResponse(Optional<TaxonomyNode> response,long taxonomyId,String errorMessage) {
        if (response.isPresent()) {
            return Response.ok(response.get()).build();
        } else {
            return buildResponseWithHistoricalCheck(taxonomyId,errorMessage);
        }
    }

    private Response buildTaxonomiesResponse(Optional<Taxonomies> response,long taxonomyId,String errorMessage) {
        if (response.isPresent()) {
            return Response.ok(response.get()).build();
        } else {
            return buildResponseWithHistoricalCheck(taxonomyId,errorMessage);
        }
    }

    private Response buildResponseWithHistoricalCheck(long taxonomyId,String errorMessage) {
        Optional<Long> newTaxonomyId = dataAccess.getTaxonomyHistoricalChange(taxonomyId);
        if(newTaxonomyId.isPresent()){
            String currentURL = URLUtil.getCurrentURL(request);
            String newURL = URLUtil.getNewRedirectHeaderLocationURL(currentURL,null,taxonomyId,newTaxonomyId.get());
            return buildRedirectResponse(newURL,newTaxonomyId.get());
        }else {
            return buildNotFoundResponse(errorMessage);
        }
    }

    private Response buildNotFoundResponse(String errorMessage) {
        ErrorMessage error = new ErrorMessage();
        error.setRequestedURL(URLUtil.getCurrentURL(request));
        error.addErrorMessage(errorMessage);
        return Response.status(Response.Status.NOT_FOUND).entity(error).build();
    }

    private Response buildRedirectResponse(String newURL,long ... newId) {
        ErrorMessage error = new ErrorMessage();
        error.setRequestedURL(URLUtil.getCurrentURL(request));
        for (long id: newId) {
            if(id > 0){
                error.addErrorMessage(API_RESPONSE_303.replace("{newId}",""+ id));
            }
        }
        return Response.status(Response.Status.SEE_OTHER).entity(error).header(HttpHeaders.LOCATION,newURL).build();
    }

}