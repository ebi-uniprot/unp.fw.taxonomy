package uk.ac.ebi.uniprot.taxonomyservice.restful.rest;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.*;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.ErrorMessage;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.builder.ListResponseBuilder;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.builder.PageResponseBuilder;
import uk.ac.ebi.uniprot.taxonomyservice.restful.util.URLUtil;
import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.IsLongListParam;
import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.ListParamMinMaxSize;
import uk.ac.ebi.uniprot.taxonomyservice.restful.validation.constraint.MaxRequiredDepthForBottomPath;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.*;

import static uk.ac.ebi.uniprot.taxonomyservice.restful.swagger.TaxonomyConstants.*;

/**
 * This class is responsible to provide services about taxonomy tree and return Taxonomy details
 *
 * Created by lgonzales on 19/02/16.
 *
 */
@Path("/")
@Tag(name = "/taxonomy", description = TAXONOMY_API_VALUE)
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class TaxonomyRest {
    private static final Logger logger = LoggerFactory.getLogger(TaxonomyRest.class);

    @Inject
    private TaxonomyDataAccess dataAccess;

    @Context
    private HttpServletRequest request;

    @GET
    @Operation(summary = "",hidden = true)
    @Path("status")
    public Response getTaxonomyStatus(){
        Map<String,String> response = new HashMap<>();
        response.put("status","success");
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @GET
    @Operation(summary = API_OPERATION_TAXONOMY_DETAIL_BY_ID,
            description = NOTE_TAXONOMY_ID)
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = ID_PARAMETER_IS_REQUIRED),
            @ApiResponse(responseCode = "400", description =API_RESPONSE_400 ),
            @ApiResponse(responseCode = "400", description =REQUEST_PARAMETER_INVALID_VALUE),
            @ApiResponse(responseCode = "404", description =API_RESPONSE_404_ENTRY),
            @ApiResponse(responseCode = "500", description = API_RESPONSE_500)})
    @Path("id/{id}")
    public Response getTaxonomyDetailsById(
            @Parameter(name = TAXONOMY_ID_PARAM, required = true)
            @NotNull(message = ID_PARAMETER_IS_REQUIRED)
            @PathParam("id")
            @Pattern(regexp = "[0-9]+", message = ID_PARAMETER_VALID_NUMBER)
            String id) {

        long taxonomyId = Long.parseLong(id);
        Optional<TaxonomyNode> response = dataAccess.getTaxonomyDetailsById(taxonomyId,URLUtil.getTaxonomyIdBasePath(request));

        return buildResponse(taxonomyId, response,API_RESPONSE_404_ENTRY);
    }

    @GET
    @Operation(summary = API_OPERATION_TAXONOMY_DETAIL_BY_ID_LIST,
            description = NOTE_TAXONOMY_ID)
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description =IDS_PARAMETER_IS_REQUIRED),
            @ApiResponse(responseCode = "400", description =API_RESPONSE_400),
            @ApiResponse(responseCode = "400", description =REQUEST_PARAMETER_INVALID_VALUE),
            @ApiResponse(responseCode = "404", description =API_RESPONSE_404_ENTRY),
            @ApiResponse(responseCode = "500", description =API_RESPONSE_500)})
    @Path("ids/{ids}")
    public Response getTaxonomyDetailsByIdList(
            @Parameter(name = TAXONOMY_IDS_PARAM, required = true)
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
    @Operation(summary = API_OPERATION_TAXONOMY_NODE_BY_ID,
            description = NOTE_TAXONOMY_ID)
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description =ID_PARAMETER_IS_REQUIRED),
            @ApiResponse(responseCode = "400", description =API_RESPONSE_400 ),
            @ApiResponse(responseCode = "400", description =REQUEST_PARAMETER_INVALID_VALUE),
            @ApiResponse(responseCode = "404", description =API_RESPONSE_404_ENTRY),
            @ApiResponse(responseCode = "500", description =API_RESPONSE_500)})
    @Path("id/{id}/node")
    public Response getTaxonomyBaseNodeById(
            @Parameter(name = TAXONOMY_ID_PARAM, required = true)
            @NotNull(message = ID_PARAMETER_IS_REQUIRED)
            @PathParam("id")
            @Pattern(regexp = "[0-9]+", message = ID_PARAMETER_VALID_NUMBER)
                    String id) {

        long taxonomyId = Long.parseLong(id);
        Optional<TaxonomyNode> response = dataAccess.getTaxonomyBaseNodeById(taxonomyId);

        return buildResponse(taxonomyId, response,API_RESPONSE_404_ENTRY);
    }

    @GET
    @Operation(summary = API_OPERATION_TAXONOMY_NODE_BY_ID_LIST,
            description = NOTE_TAXONOMY_ID)
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description =IDS_PARAMETER_IS_REQUIRED),
            @ApiResponse(responseCode = "400", description =API_RESPONSE_400 ),
            @ApiResponse(responseCode = "400", description =REQUEST_PARAMETER_INVALID_VALUE),
            @ApiResponse(responseCode = "404", description =API_RESPONSE_404_ENTRY),
            @ApiResponse(responseCode = "500", description =API_RESPONSE_500)})
    @Path("ids/{ids}/node")
    public Response getTaxonomyBaseNodeByIds(
            @Parameter(name = TAXONOMY_IDS_PARAM, required = true)
            @NotNull(message = IDS_PARAMETER_IS_REQUIRED)
            @IsLongListParam(message = IDS_PARAMETER_VALID_NUMBER)
            @ListParamMinMaxSize(maxSize = 100, minSize = 1, message = IDS_PARAMETER_MIN_MAX_SIZE)
            @PathParam("ids")
                    String ids) {

        List<String> idStringList = Arrays.asList(ids.split(","));
        Optional<Taxonomies> response = dataAccess.getTaxonomyBaseNodeByIdList(idStringList);

        return buildListResponse(idStringList, response,API_RESPONSE_404_ENTRY);
    }

    @GET
    @Operation(summary = API_OPERATION_TAXONOMY_SIBLINGS_BY_ID,
            description = NOTE_TAXONOMY_ID)
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description =ID_PARAMETER_IS_REQUIRED),
            @ApiResponse(responseCode = "400", description =API_RESPONSE_400 ),
            @ApiResponse(responseCode = "400", description =REQUEST_PARAMETER_INVALID_VALUE),
            @ApiResponse(responseCode = "404", description =API_RESPONSE_404_ENTRY),
            @ApiResponse(responseCode = "500", description =API_RESPONSE_500)})
    @Path("id/{id}/siblings")
    public Response getTaxonomyNodesSiblingsByIdWithDetail(@Valid @BeanParam TaxonomyIdWithPageRequestParams param) {
        logger.debug(">>TaxonomyRest.getTaxonomyNodesSiblingsByIdWithDetail");

        Optional<Taxonomies> response = dataAccess.getTaxonomySiblingsByIdWithDetail(param,URLUtil
                .getTaxonomyIdBasePath(request));

        return buildResponse(param.getLongId(), response,API_RESPONSE_404_ENTRY);
    }

    @GET
    @Operation(summary = API_OPERATION_TAXONOMY_SIBLINGS_NODE_BY_ID,
            description = NOTE_TAXONOMY_ID)
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description =ID_PARAMETER_IS_REQUIRED),
            @ApiResponse(responseCode = "400", description =API_RESPONSE_400 ),
            @ApiResponse(responseCode = "400", description =REQUEST_PARAMETER_INVALID_VALUE),
            @ApiResponse(responseCode = "404", description =API_RESPONSE_404_ENTRY),
            @ApiResponse(responseCode = "500", description =API_RESPONSE_500)})
    @Path("id/{id}/siblings/node")
    public Response getTaxonomyNodesSiblingsById(@Valid @BeanParam TaxonomyIdWithPageRequestParams param) {
        logger.debug(">>TaxonomyRest.getTaxonomyNodesSiblingsById");

        Optional<Taxonomies> response = dataAccess.getTaxonomySiblingsById(param);

        return buildResponse(param.getLongId(), response,API_RESPONSE_404_ENTRY);
    }

    @GET
    @Operation(summary = API_OPERATION_TAXONOMY_CHILDREN_BY_ID,
            description = NOTE_TAXONOMY_ID)
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description =ID_PARAMETER_IS_REQUIRED),
            @ApiResponse(responseCode = "400", description =API_RESPONSE_400 ),
            @ApiResponse(responseCode = "400", description =REQUEST_PARAMETER_INVALID_VALUE),
            @ApiResponse(responseCode = "404", description =API_RESPONSE_404_ENTRY),
            @ApiResponse(responseCode = "500", description =API_RESPONSE_500)})
    @Path("id/{id}/children")
    public Response getTaxonomyNodesChildrenByIdWithDetail(@Valid @BeanParam TaxonomyIdWithPageRequestParams param) {
        logger.debug(">>TaxonomyRest.getTaxonomyNodesChildrenByIdWithDetail");

        Optional<Taxonomies> response = dataAccess.getTaxonomyChildrenByIdWithDetail(param,URLUtil
                .getTaxonomyIdBasePath(request));

        return buildResponse(param.getLongId(), response,API_RESPONSE_404_ENTRY);
    }

    @GET
    @Operation(summary = API_OPERATION_TAXONOMY_CHILDREN_NODE_BY_ID,
            description = NOTE_TAXONOMY_ID)
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description =ID_PARAMETER_IS_REQUIRED),
            @ApiResponse(responseCode = "400", description =API_RESPONSE_400 ),
            @ApiResponse(responseCode = "400", description =REQUEST_PARAMETER_INVALID_VALUE),
            @ApiResponse(responseCode = "404", description =API_RESPONSE_404_ENTRY),
            @ApiResponse(responseCode = "500", description =API_RESPONSE_500)})
    @Path("id/{id}/children/node")
    public Response getTaxonomyNodesChildrenById(@Valid @BeanParam TaxonomyIdWithPageRequestParams param) {
        logger.debug(">>TaxonomyRest.getTaxonomyNodesChildrenById");

        Optional<Taxonomies> response = dataAccess.getTaxonomyChildrenById(param);

        return buildResponse(param.getLongId(), response,API_RESPONSE_404_ENTRY);
    }

    @GET
    @Operation(summary = API_OPERATION_TAXONOMY_PARENT_BY_ID,
            description = NOTE_TAXONOMY_ID)
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description =ID_PARAMETER_IS_REQUIRED)})
    @Path("id/{id}/parent")
    public Response getTaxonomyNodeParentByIdWithDetail(
            @Parameter(name = TAXONOMY_ID_PARAM, required = true)
            @NotNull(message = ID_PARAMETER_IS_REQUIRED)
            @PathParam("id")
            @Pattern(regexp = "[0-9]+", message = ID_PARAMETER_VALID_NUMBER)
            String id) {
        logger.debug(">>TaxonomyRest.getTaxonomyNodeParentByIdWithDetail");

        long taxonomyId = Long.parseLong(id);
        Optional<TaxonomyNode> response = dataAccess.getTaxonomyParentByIdWithDetail(taxonomyId,URLUtil
                .getTaxonomyIdBasePath(request));

        return buildResponse(taxonomyId, response,API_RESPONSE_404_ENTRY);
    }

    @GET
    @Operation(summary = API_OPERATION_TAXONOMY_PARENT_NODE_BY_ID,
            description = NOTE_TAXONOMY_ID)
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description =ID_PARAMETER_IS_REQUIRED)})
    @Path("id/{id}/parent/node")
    public Response getTaxonomyNodeParentById(
            @Parameter(name = TAXONOMY_ID_PARAM, required = true)
            @NotNull(message = ID_PARAMETER_IS_REQUIRED)
            @PathParam("id")
            @Pattern(regexp = "[0-9]+", message = ID_PARAMETER_VALID_NUMBER)
                    String id) {
        logger.debug(">>TaxonomyRest.getTaxonomyDetailsById");

        long taxonomyId = Long.parseLong(id);
        Optional<TaxonomyNode> response = dataAccess.getTaxonomyParentById(taxonomyId);

        return buildResponse(taxonomyId, response,API_RESPONSE_404_ENTRY);
    }

    @GET
    @Operation(summary = API_OPERATION_TAXONOMY_DETAIL_BY_NAME,
            description = NOTE_TAXONOMY_DETAIL_BY_NAME)
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description =NAME_PARAMETER_IS_REQUIRED),
            @ApiResponse(responseCode = "400", description =API_RESPONSE_400 ),
            @ApiResponse(responseCode = "400", description =REQUEST_PARAMETER_INVALID_VALUE),
            @ApiResponse(responseCode = "404", description =API_RESPONSE_404_NAME),
            @ApiResponse(responseCode = "500", description =API_RESPONSE_500)})
    @Path("name/{name}")
    public Response getTaxonomiesDetailsByName(@Valid @BeanParam NameRequestParams params) {

        Optional<Taxonomies> response = dataAccess.getTaxonomyDetailsByName(params,URLUtil.getTaxonomyIdBasePath
                (request));
        return buildResponse(0, response,API_RESPONSE_404_NAME);
    }

    @GET
    @Operation(summary = API_OPERATION_TAXONOMY_NODES_BY_NAME,
            description = NOTE_TAXONOMY_DETAIL_BY_NAME)
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description =NAME_PARAMETER_IS_REQUIRED),
            @ApiResponse(responseCode = "400", description =API_RESPONSE_400 ),
            @ApiResponse(responseCode = "400", description =REQUEST_PARAMETER_INVALID_VALUE),
            @ApiResponse(responseCode = "404", description =API_RESPONSE_404_NAME),
            @ApiResponse(responseCode = "500", description =API_RESPONSE_500)})
    @Path("name/{name}/node")
    public Response getTaxonomiesNodeBaseByName(@Valid @BeanParam NameRequestParams params) {

        Optional<Taxonomies> response = dataAccess.getTaxonomyNodesByName(params,URLUtil.getTaxonomyIdBasePath
                (request));
        return buildResponse(0, response,API_RESPONSE_404_NAME);
    }

    @GET
    @Operation(summary = API_OPERATION_TAXONOMY_RELATIONSHIP,
            description = NOTE_TAXONOMY_RELATIONSHIP)
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description =API_RESPONSE_400 ),
            @ApiResponse(responseCode = "400", description =FROM_PARAMETER_IS_REQUIRED),
            @ApiResponse(responseCode = "400", description =TO_PARAMETER_IS_REQUIRED),
            @ApiResponse(responseCode = "400", description =REQUEST_PARAMETER_INVALID_VALUE),
            @ApiResponse(responseCode = "404", description =API_RESPONSE_404_RELATIONSHIP),
            @ApiResponse(responseCode = "500", description =API_RESPONSE_500)})
    @Path("relationship")
    public Response checkRelationshipBetweenTaxonomies(@Valid @BeanParam RelationshipRequestParams params) {

        long from = Long.parseLong(params.getFrom());
        long to = Long.parseLong(params.getTo());

        Optional<TaxonomyNode> response = dataAccess.getTaxonomiesRelationship(from,to);
        Map<String, Long> idsForHistoricalCheck = new HashMap<>();
        idsForHistoricalCheck.put("from", from);
        idsForHistoricalCheck.put("to", to);
        return buildResponse(idsForHistoricalCheck, response,API_RESPONSE_404_RELATIONSHIP);
    }

    @GET
    @Operation(summary = API_OPERATION_TAXONOMY_PATH,
            description = NOTE_TAXONOMY_PATH)
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description =ID_PARAMETER_IS_REQUIRED),
            @ApiResponse(responseCode = "400", description =DEPTH_PARAMETER_IS_REQUIRED ),
            @ApiResponse(responseCode = "400", description =DEPTH_PARAM_MAX ),
            @ApiResponse(responseCode = "400", description =DIRECTION_VALID_VALUES ),
            @ApiResponse(responseCode = "400", description =DIRECTION_PARAMETER_IS_REQUIRED ),
            @ApiResponse(responseCode = "400", description =REQUEST_PARAMETER_INVALID_VALUE),
            @ApiResponse(responseCode = "404", description = API_RESPONSE_404_PATH),
            @ApiResponse(responseCode = "500", description = API_RESPONSE_500)})
    @Path("path")
    public Response getTaxonomyPath(
    @MaxRequiredDepthForBottomPath(max = 5, message = DEPTH_PARAM_MAX, requiredMessage = DEPTH_PARAMETER_IS_REQUIRED)
    @Valid @BeanParam PathRequestParams pathRequestParam) {

        Optional<TaxonomyNode> response = dataAccess.getTaxonomyPath(pathRequestParam);

        Map<String, Long> idsForHistoricalCheck = new HashMap<>();
        idsForHistoricalCheck.put("id", Long.parseLong(pathRequestParam.getId()));

        return buildResponse(idsForHistoricalCheck,response,API_RESPONSE_404_PATH);
    }

    @GET
    @Operation(summary = API_OPERATION_TAXONOMY_PATH_NODES,
            description = NOTE_TAXONOMY_PATH_NODES)
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = ID_PARAMETER_IS_REQUIRED),
            @ApiResponse(responseCode = "400", description = DEPTH_PARAMETER_IS_REQUIRED ),
            @ApiResponse(responseCode = "400", description = DEPTH_PARAM_MAX ),
            @ApiResponse(responseCode = "400", description = DIRECTION_VALID_VALUES ),
            @ApiResponse(responseCode = "400", description = DIRECTION_PARAMETER_IS_REQUIRED ),
            @ApiResponse(responseCode = "400", description = REQUEST_PARAMETER_INVALID_VALUE),
            @ApiResponse(responseCode = "404", description = API_RESPONSE_404_PATH),
            @ApiResponse(responseCode = "500", description = API_RESPONSE_500)})
    @Path("path/nodes")
    public Response getTaxonomyPathNodes(
            @Valid @BeanParam PathRequestParams pathRequestParam,
            @Valid @BeanParam PageRequestParams pageRequestParams) {

        Optional<Taxonomies> response = dataAccess.getTaxonomyPathNodes(pathRequestParam,pageRequestParams);

        Map<String, Long> idsForHistoricalCheck = new HashMap<>();
        idsForHistoricalCheck.put("id", Long.parseLong(pathRequestParam.getId()));

        return buildResponse(idsForHistoricalCheck,response,API_RESPONSE_404_PATH);
    }


    @GET
    @Operation(summary = API_OPERATION_TAXONOMY_LINEAGE,
            description = NOTE_TAXONOMY_ID)
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = ID_PARAMETER_IS_REQUIRED),
            @ApiResponse(responseCode = "400", description = API_RESPONSE_400 ),
            @ApiResponse(responseCode = "400", description = REQUEST_PARAMETER_INVALID_VALUE),
            @ApiResponse(responseCode = "404", description = API_RESPONSE_404_LINEAGE),
            @ApiResponse(responseCode = "500", description = API_RESPONSE_500)})
    @Path("lineage/{id}")
    public Response getTaxonomyLineageById(
            @Parameter(name = TAXONOMY_ID_PARAM, required = true)
            @NotNull(message = ID_PARAMETER_IS_REQUIRED)
            @PathParam("id")
            @Pattern(regexp = "[0-9]+", message = ID_PARAMETER_VALID_NUMBER) String id) {

        long taxonomyId = Long.parseLong(id);
        Optional<Taxonomies> response = dataAccess.getTaxonomyLineageById(taxonomyId);
        return buildResponse(taxonomyId,response,API_RESPONSE_404_LINEAGE);
    }

    @GET
    @Operation(summary = API_OPERATION_TAXONOMY_ANCESTOR,
            description = NOTE_TAXONOMY_ANCESTOR_IDS)
    @ApiResponses(value = {@ApiResponse(responseCode = "400", description = IDS_PARAMETER_IS_REQUIRED),
            @ApiResponse(responseCode = "400", description = API_RESPONSE_400 ),
            @ApiResponse(responseCode = "400", description = REQUEST_PARAMETER_INVALID_VALUE),
            @ApiResponse(responseCode = "404", description = API_RESPONSE_404_ANCESTOR),
            @ApiResponse(responseCode = "500", description = API_RESPONSE_500)})
    @Path("ancestor/{ids}")
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