package uk.ac.ebi.uniprot.taxonomyservice.restful.rest;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.AncestorRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.NameRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.RelationshipRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.builder.ListResponseBuilder;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.builder.PageResponseBuilder;
import uk.ac.ebi.uniprot.taxonomyservice.restful.util.URLUtil;
import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.IsLongListParam;
import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.ListParamMinMaxSize;

import io.swagger.annotations.*;
import java.util.*;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
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

        return buildResponse(taxonomyId, response,API_RESPONSE_404_ENTRY);
    }

    @GET
    @ApiOperation(value = API_OPERATION_TAXONOMY_DETAIL_BY_ID_LIST,
            notes = NOTE_TAXONOMY_ID,
            response = Taxonomies.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = IDS_PARAMETER_IS_REQUIRED,response = ErrorMessage.class),
            @ApiResponse(code = 400, message = API_RESPONSE_400 , response = ErrorMessage.class),
            @ApiResponse(code = 400, message = REQUEST_PARAMETER_INVALID_VALUE, response = ErrorMessage.class),
            @ApiResponse(code = 404, message = API_RESPONSE_404_ENTRY, response = ErrorMessage.class),
            @ApiResponse(code = 500, message = API_RESPONSE_500, response = ErrorMessage.class)})
    @Path("/ids/{ids}")
    public Response getTaxonomyDetailsByIdList(
            @ApiParam(value = TAXONOMY_IDS_PARAM, required = true)
            @NotNull(message = IDS_PARAMETER_IS_REQUIRED)
            @IsLongListParam(message = IDS_PARAMETER_VALID_NUMBER)
            @ListParamMinMaxSize(maxSize = 50, minSize = 1, message = IDS_PARAMETER_MIN_MAX_SIZE)
            @PathParam("ids")
                    String ids) {

        List<String> idStringList = Arrays.asList(ids.split(","));
        Optional<Taxonomies> response = dataAccess.getTaxonomyDetailsByIdList(idStringList,URLUtil
                .getTaxonomyIdBasePath(request));

        return buildListResponse(idStringList, response,API_RESPONSE_404_ENTRY);
    }


    @GET
    @ApiOperation(value = API_OPERATION_TAXONOMY_NODE_BY_ID,
            notes = NOTE_TAXONOMY_ID,
            response = TaxonomyNode.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = ID_PARAMETER_IS_REQUIRED,response = ErrorMessage.class),
            @ApiResponse(code = 400, message = API_RESPONSE_400 , response = ErrorMessage.class),
            @ApiResponse(code = 400, message = REQUEST_PARAMETER_INVALID_VALUE, response = ErrorMessage.class),
            @ApiResponse(code = 404, message = API_RESPONSE_404_ENTRY, response = ErrorMessage.class),
            @ApiResponse(code = 500, message = API_RESPONSE_500, response = ErrorMessage.class)})
    @Path("/id/{id}/node")
    public Response getTaxonomyBaseNodeById(
            @ApiParam(value = TAXONOMY_ID_PARAM, required = true)
            @NotNull(message = ID_PARAMETER_IS_REQUIRED)
            @PathParam("id")
            @Pattern(regexp = "[0-9]+", message = ID_PARAMETER_VALID_NUMBER)
                    String id) {

        long taxonomyId = Long.valueOf(id);
        Optional<TaxonomyNode> response = dataAccess.getTaxonomyBaseNodeById(taxonomyId);

        return buildResponse(taxonomyId, response,API_RESPONSE_404_ENTRY);
    }

    @GET
    @ApiOperation(value = API_OPERATION_TAXONOMY_NODE_BY_ID_LIST,
            notes = NOTE_TAXONOMY_ID,
            response = Taxonomies.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = IDS_PARAMETER_IS_REQUIRED,response = ErrorMessage.class),
            @ApiResponse(code = 400, message = API_RESPONSE_400 , response = ErrorMessage.class),
            @ApiResponse(code = 400, message = REQUEST_PARAMETER_INVALID_VALUE, response = ErrorMessage.class),
            @ApiResponse(code = 404, message = API_RESPONSE_404_ENTRY, response = ErrorMessage.class),
            @ApiResponse(code = 500, message = API_RESPONSE_500, response = ErrorMessage.class)})
    @Path("/ids/{ids}/node")
    public Response getTaxonomyBaseNodeByIds(
            @ApiParam(value = TAXONOMY_IDS_PARAM, required = true)
            @NotNull(message = IDS_PARAMETER_IS_REQUIRED)
            @IsLongListParam(message = IDS_PARAMETER_VALID_NUMBER)
            @ListParamMinMaxSize(maxSize = 50, minSize = 1, message = IDS_PARAMETER_MIN_MAX_SIZE)
            @PathParam("ids")
                    String ids) {

        List<String> idStringList = Arrays.asList(ids.split(","));
        Optional<Taxonomies> response = dataAccess.getTaxonomyBaseNodeByIdList(idStringList);

        return buildListResponse(idStringList, response,API_RESPONSE_404_ENTRY);
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

        return buildResponse(taxonomyId, response,API_RESPONSE_404_ENTRY);
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

        return buildResponse(taxonomyId, response,API_RESPONSE_404_ENTRY);
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

        return buildResponse(taxonomyId, response,API_RESPONSE_404_ENTRY);
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
        return buildResponse(0, response,API_RESPONSE_404_NAME);
    }

    @GET
    @ApiOperation(value = API_OPERATION_TAXONOMY_NODES_BY_NAME,
            notes = NOTE_TAXONOMY_DETAIL_BY_NAME,
            response = Taxonomies.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = NAME_PARAMETER_IS_REQUIRED,response = ErrorMessage.class),
            @ApiResponse(code = 400, message = API_RESPONSE_400 , response = ErrorMessage.class),
            @ApiResponse(code = 400, message = REQUEST_PARAMETER_INVALID_VALUE, response = ErrorMessage.class),
            @ApiResponse(code = 404, message = API_RESPONSE_404_NAME, response = ErrorMessage.class),
            @ApiResponse(code = 500, message = API_RESPONSE_500, response = ErrorMessage.class)})
    @Path("/name/{name}/node")
    public Response getTaxonomiesNodeBaseByName(@Valid @BeanParam NameRequestParams params) {

        Optional<Taxonomies> response = dataAccess.getTaxonomyNodesByName(params,URLUtil.getTaxonomyIdBasePath
                (request));
        return buildResponse(0, response,API_RESPONSE_404_NAME);
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
        Map<String, Long> idsForHistoricalCheck = new HashMap<>();
        idsForHistoricalCheck.put("from", from);
        idsForHistoricalCheck.put("to", to);
        return buildResponse(idsForHistoricalCheck, response,API_RESPONSE_404_RELATIONSHIP);
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

        Map<String, Long> idsForHistoricalCheck = new HashMap<>();
        idsForHistoricalCheck.put("id", Long.valueOf(pathRequestParam.getId()));

        return buildResponse(idsForHistoricalCheck,response,API_RESPONSE_404_PATH);
    }


    @GET
    @ApiOperation(value = API_OPERATION_TAXONOMY_LINEAGE,
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
        return buildResponse(taxonomyId,response,API_RESPONSE_404_LINEAGE);
    }

    @GET
    @ApiOperation(value = API_OPERATION_TAXONOMY_ANCESTOR,
            notes = NOTE_TAXONOMY_ANCESTOR_IDS,
            response = TaxonomyNode.class)
    @ApiResponses(value = {@ApiResponse(code = 400, message = IDS_PARAMETER_IS_REQUIRED,response = ErrorMessage.class),
            @ApiResponse(code = 400, message = API_RESPONSE_400 , response = ErrorMessage.class),
            @ApiResponse(code = 400, message = REQUEST_PARAMETER_INVALID_VALUE, response = ErrorMessage.class),
            @ApiResponse(code = 404, message = API_RESPONSE_404_ANCESTOR, response = ErrorMessage.class),
            @ApiResponse(code = 500, message = API_RESPONSE_500, response = ErrorMessage.class)})
    @Path("/ancestor/{ids}")
    public Response getTaxonomyAncestor(@Valid @BeanParam AncestorRequestParams param) {
        Optional<TaxonomyNode> response = dataAccess.getTaxonomyAncestorFromTaxonomyIds(param.getIdList());
        return buildResponse(0,response,API_RESPONSE_404_ANCESTOR);
    }

    private Response buildResponse(long taxonomyId, Optional response,String notFoundErrorMessage) {
        Map<String, Long> idsForHistoricalCheck = null;
        if(taxonomyId > 0) {
            idsForHistoricalCheck = new HashMap<>();
            idsForHistoricalCheck.put(null, taxonomyId);
        }
        return buildResponse(idsForHistoricalCheck,response,notFoundErrorMessage);
    }

    private Response buildResponse(Map<String, Long> idsForHistoricalCheck, Optional response,String notFoundErrorMessage) {
        return new PageResponseBuilder()
                .setIdsForHistoricalCheck(idsForHistoricalCheck)
                .setEntity(response)
                .setRequest(request)
                .setNotFoundErrorMessage(notFoundErrorMessage)
                .setDataAccess(dataAccess)
                .buildResponse();
    }

    private Response buildListResponse(List<String> requestedIds, Optional response,String notFoundErrorMessage) {
        return new ListResponseBuilder()
                .setRequestedIds(requestedIds)
                .setEntity(response)
                .setRequest(request)
                .setNotFoundErrorMessage(notFoundErrorMessage)
                .setDataAccess(dataAccess)
                .buildResponse();
    }

}