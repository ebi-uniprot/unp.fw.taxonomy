package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl.converter.PropertyConverter;
import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl.converter.TaxonomyNodeConverter;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.NameRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PageRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.TaxonomyIdWithPageRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.param.values.PathDirections;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.PageInformation;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;

import static uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl.CypherQueryConstants.*;
import static uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode.TAXONOMY_NODE_FIELDS.taxonomyId;

/**
 * Neo4J taxonomy data access class is responsible to query information from Neo4J Taxonomy database and build the
 * correct response TaxonomyNode or Taxonomies object
 *
 * Created by lgonzales on 08/04/16.
 */
public class Neo4jTaxonomyDataAccess implements TaxonomyDataAccess{

    private static final Logger logger = LoggerFactory.getLogger(Neo4jTaxonomyDataAccess.class);
    private static final String FOR_LOGGER = " for ";

    protected Neo4JQueryExecutor neo4jDb;

    protected String filePath;

    @Inject
    public Neo4jTaxonomyDataAccess(@Named("NEO4J_DATABASE_PATH") String filePath){
        this.filePath = filePath;
    }

    @PostConstruct
    public void start() {
        logger.debug("Starting up Neo4jTaxonomyDataAccess service");

        if (this.neo4jDb == null) {
            logger.debug("Creating an instance for Neo4jTaxonomyDataAccess and using neo4jDb filePath: "+filePath);
            GraphDatabaseService graphDatabase = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(new File(filePath))
                    .setConfig("dbms.threads.worker_count", "20" )
                    .setConfig(GraphDatabaseSettings.read_only,"true")
                    .newGraphDatabase();
            neo4jDb = new Neo4JQueryExecutor(graphDatabase);

            registerStop(neo4jDb);
        }

    }

    /**
     * TODO: Currently I am registering the stop manually
     *       There is an automatic way, like uniprot restfull service does
     **/
    public void registerStop(final Neo4JQueryExecutor graphDb) {
        logger.debug("Shutting down Neo4jTaxonomyDataAccess service");
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                logger.debug("Shutting down Hook Neo4jTaxonomyDataAccess service");
                graphDb.shutdown();
            }
        } );
    }

    @Override
    public Optional<TaxonomyNode> getTaxonomyDetailsById(long taxonomyId, String basePath) {
        return getTaxonomyDetailsById(GET_TAXONOMY_DETAILS_BY_ID_CYPHER_QUERY, taxonomyId,basePath);
    }

    @Override
    public Optional<Taxonomies> getTaxonomyDetailsByIdList(List<String> taxonomyIds, String basePath) {
        Optional<Taxonomies> result;
        long startTime = System.currentTimeMillis();

        Map<String, Object> params = new HashMap<>();
        params.put("ids", taxonomyIds);

        Optional<List<TaxonomyNode>> node = neo4jDb.executeQueryList(GET_TAXONOMY_DETAILS_BY_ID_LIST_CYPHER_QUERY,
                params, new TaxonomyNodeConverter(basePath,true));
        result = buildTaxonomies(node,null,-1);

        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomyDetailsByIdList: "+elapsed+ FOR_LOGGER+taxonomyId);
        return result;
    }

    @Override
    public Optional<TaxonomyNode> getTaxonomyBaseNodeById(long taxonomyId){
        long startTime = System.currentTimeMillis();
        Optional<TaxonomyNode> result = getTaxonomyBaseNodeById(taxonomyId,GET_TAXONOMY_BASE_NODE_BY_ID_CYPHER_QUERY);
        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomyBaseNodeById: "+elapsed+ FOR_LOGGER+taxonomyId);
        return result;
    }


    @Override
    public Optional<Taxonomies> getTaxonomyBaseNodeByIdList(List<String> taxonomyIds){
        long startTime = System.currentTimeMillis();
        Map<String, Object> params = new HashMap<>();
        params.put("ids", taxonomyIds);
        Optional<Taxonomies> result = getTaxonomiesBaseNodeList(params, GET_TAXONOMY_BASE_NODE_LIST_BY_IDS_CYPHER_QUERY,null,-1);
        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomyBaseNodeByIdList: "+elapsed+ FOR_LOGGER+taxonomyIds);
        return result;
    }

    @Override
    public Optional<Taxonomies> getTaxonomySiblingsById(TaxonomyIdWithPageRequestParams params) {
        return getNodeBaseList(GET_TAXONOMY_SIBLINGS_BY_ID_CYPHER_QUERY,
                GET_TAXONOMY_SIBLINGS_BY_ID_CYPHER_QUERY_TOTAL, params);
    }

    @Override
    public Optional<Taxonomies> getTaxonomySiblingsByIdWithDetail(TaxonomyIdWithPageRequestParams params, String basePath) {
        return getTaxonomiesListWithDetail(GET_TAXONOMY_SIBLINGS_BY_ID_CYPHER_QUERY_WITH_DETAIL,
                GET_TAXONOMY_SIBLINGS_BY_ID_CYPHER_QUERY_TOTAL, basePath, params);
    }

    @Override
    public Optional<TaxonomyNode> getTaxonomyParentById(long taxonomyId) {
        long startTime = System.currentTimeMillis();
        Optional<TaxonomyNode> result = getTaxonomyBaseNodeById(taxonomyId,GET_TAXONOMY_PARENT_BY_ID_CYPHER_QUERY);
        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomyParentById: "+elapsed+ FOR_LOGGER+taxonomyId);
        return result;
    }

    @Override
    public Optional<TaxonomyNode> getTaxonomyParentByIdWithDetail(long taxonomyId, String basePath) {
        return getTaxonomyDetailsById(GET_TAXONOMY_PARENT_BY_ID_CYPHER_QUERY_WITH_DETAIL, taxonomyId,basePath);
    }

    @Override
    public Optional<Taxonomies> getTaxonomyChildrenById(TaxonomyIdWithPageRequestParams params) {
        return getNodeBaseList(GET_TAXONOMY_CHILDREN_BY_ID_CYPHER_QUERY,
                GET_TAXONOMY_CHILDREN_BY_ID_CYPHER_QUERY_TOTAL,params);
    }

    @Override
    public Optional<Taxonomies> getTaxonomyChildrenByIdWithDetail(TaxonomyIdWithPageRequestParams params, String basePath) {
        return getTaxonomiesListWithDetail(GET_TAXONOMY_CHILDREN_BY_ID_CYPHER_QUERY_WITH_DETAIL,
                GET_TAXONOMY_CHILDREN_BY_ID_CYPHER_QUERY_TOTAL,basePath, params);
    }

    @Override
    public Optional<Taxonomies> getTaxonomyNodesByName(NameRequestParams nameParams, String basePath) {
        long startTime = System.currentTimeMillis();

        String query = GET_TAXONOMY_NODES_BY_NAME_CYPHER_QUERY.replace("{searchType}", nameParams
                .getSearchTypeQueryKeyword()).replace("{fieldName}", nameParams.getFieldNameQueryKeyword());
        Optional<Taxonomies> result = getTaxonomyDetailsByName(nameParams,basePath,query);

        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomyNodesByName: " + elapsed + FOR_LOGGER + nameParams);
        return result;
    }

    @Override
    public Optional<Taxonomies> getTaxonomyDetailsByName(NameRequestParams nameParams, String basePath) {
        long startTime = System.currentTimeMillis();

        String query = GET_TAXONOMY_DETAILS_BY_NAME_CYPHER_QUERY.replace("{searchType}", nameParams
                .getSearchTypeQueryKeyword()).replace("{fieldName}", nameParams.getFieldNameQueryKeyword());
        Optional<Taxonomies> result =  getTaxonomyDetailsByName(nameParams,basePath,query);

        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomyDetailsByName: " + elapsed + FOR_LOGGER + nameParams);
        return result;
    }

    @Override
    public Optional<TaxonomyNode> getTaxonomiesRelationship(long taxonomyId1, long to) {
        Optional<TaxonomyNode> result = Optional.empty();
        long startTime = System.currentTimeMillis();
        Map<String, Object> params = new HashMap<>();
        params.put( "from", ""+taxonomyId1);
        params.put( "to", ""+ to);

        result = neo4jDb.executeQueryForPath(GET_TAXONOMY_RELATIONSHIP_CYPHER_QUERY, params, taxonomyId1);

        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomiesRelationship: "+elapsed+ " from "+taxonomyId1+ " to "+to);
        return result;
    }

    @Override
    public Optional<TaxonomyNode> getTaxonomyPath(PathRequestParams nodePathParams) {
        Optional<TaxonomyNode> result = Optional.empty();
        long startTime = System.currentTimeMillis();
        String cypherQuery;
        if(nodePathParams.getPathDirection().equals(PathDirections.TOP)){
            cypherQuery = GET_TAXONOMY_PATH_TOP_CYPHER_QUERY;
        }else{
            cypherQuery = GET_TAXONOMY_PATH_DOWN_CYPHER_QUERY.replace("{depth}",""+nodePathParams.getDepth());
        }
        Map<String, Object> params = new HashMap<>();
        params.put( "id", nodePathParams.getId());

        result = neo4jDb.executeQueryForPath(cypherQuery, params, Long.parseLong(nodePathParams.getId()));

        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomyPath: "+elapsed+  FOR_LOGGER +nodePathParams);
        return result;
    }

    @Override
    public Optional<Taxonomies> getTaxonomyPathNodes(PathRequestParams requestParams,PageRequestParams pageRequestParams) {
        long startTime = System.currentTimeMillis();
        Optional<Taxonomies> result = Optional.empty();
        String depth = "";
        if(requestParams.getDepth() != null){
            depth = String.valueOf(requestParams.getDepth());
        }
        int totalRecords = getTaxonomyPathNodesTotalRecords(requestParams.getId(),requestParams.getPathDirection(),depth);
        if(pageRequestParams.getSkip() < totalRecords) {
            String cypherQuery;
            if(requestParams.getPathDirection().equals(PathDirections.TOP)){
                cypherQuery = GET_TAXONOMY_PATH_TOP_NODE_LIST_CYPHER_QUERY_PAGINATED;
            }else{
                cypherQuery = GET_TAXONOMY_PATH_DOWN_NODE_LIST_CYPHER_QUERY_PAGINATED.replace("{depth}",depth);
            }
            Map<String, Object> params = new HashMap<>();
            params.put("id", requestParams.getId());
            params.put("skip", pageRequestParams.getSkip());
            params.put("limit", pageRequestParams.getPageSizeInt());

            result = getTaxonomiesBaseNodeList(params, cypherQuery, pageRequestParams, totalRecords);
        }
        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomyPathNodes: " + elapsed + FOR_LOGGER + requestParams);
        return result;
    }

    @Override
    public Optional<Long> getTaxonomyHistoricalChange(long id) {
        Long result = null;
        Map<String, Object> params = new HashMap<>();
        params.put( "id", ""+id );
        Optional<Object> queryResult = neo4jDb.executeQuery(CHECK_HISTORICAL_CHANGE_CYPHER_QUERY, params, new PropertyConverter("taxonomyId"));
        if(queryResult.isPresent()){
            result = Long.parseLong(queryResult.get().toString());
        }
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Taxonomies> getTaxonomyLineageById(long taxonomyId) {
        Optional<Taxonomies> taxonomies = Optional.empty();
        long startTime = System.currentTimeMillis();
        Map<String, Object> params = new HashMap<>();
        params.put( "id", ""+taxonomyId );

        Optional<List<TaxonomyNode>> value = neo4jDb.executeQueryList(GET_TAXONOMY_LIENAGE_CYPHER_QUERY, params, new TaxonomyNodeConverter(null,false));
        if (value.isPresent()) {
            List<TaxonomyNode> result = (List<TaxonomyNode>) value.get();
            result.forEach(this::updateLineageNode);
            taxonomies = buildTaxonomies(Optional.of(result),null,-1);
        }

        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomyLineageById: "+elapsed+ FOR_LOGGER +params);
        return taxonomies;
    }

    @Override
    public Optional<TaxonomyNode> getTaxonomyAncestorFromTaxonomyIds(List<Long> ids) {
        Optional<TaxonomyNode> result = Optional.empty();
        long startTime = System.currentTimeMillis();
        Map<String, Object> params = new java.util.HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            params.put("id" + i, "" + ids.get(i));
        }
        String ancestorCypherQuery = buildAncestorCypherQuery(ids);
        Optional<Object> queryResult = neo4jDb.executeQuery(ancestorCypherQuery, params, new PropertyConverter(null));
        if (queryResult.isPresent()){
            Optional<Long> ancestor = getAncestorIdFromResult((Map<String, Object>) queryResult.get());
            if (ancestor.isPresent()) {
                result = getTaxonomyBaseNodeById(ancestor.get());
            }
        }

        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomyAncestorFromTaxonomyIds: "+elapsed+ FOR_LOGGER +params);
        return result;
    }

    private Optional<TaxonomyNode> getTaxonomyDetailsById(String cypherQuery, long taxonomyId, String basePath) {
        Optional<TaxonomyNode> result;
        long startTime = System.currentTimeMillis();

        Map<String, Object> params = new HashMap<>();
        params.put("id", "" + taxonomyId);
        result = neo4jDb.executeQuery(cypherQuery, params, new TaxonomyNodeConverter(basePath,true));

        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomyDetailsById: "+elapsed+ FOR_LOGGER+taxonomyId);
        return result;
    }

    private Optional<TaxonomyNode> getTaxonomyBaseNodeById(long taxonomyId,String cypherQuery){
        Map<String, Object> params = new HashMap<>();
        params.put("id", "" + taxonomyId);
        return neo4jDb.executeQuery(cypherQuery, params, new TaxonomyNodeConverter(null,false));
    }

    private Optional<Taxonomies> getTaxonomyDetailsByName(NameRequestParams nameParams, String basePath, String query) {
        Optional<Taxonomies> taxonomies = Optional.empty();
        int totalRecords = getTaxonomyDetailsByNameTotalRecords(nameParams);
        if(nameParams.getSkip() < totalRecords) {
            Optional<List<TaxonomyNode>> result = null;
            Map<String, Object> params = new HashMap<>();
            params.put("name", nameParams.getTaxonomyName().toLowerCase());
            params.put("skip", nameParams.getSkip());
            params.put("limit", nameParams.getPageSizeInt());

            result = neo4jDb.executeQueryList(query, params, new TaxonomyNodeConverter(basePath,true));
            taxonomies = buildTaxonomies(result,nameParams,totalRecords);
        }
        return taxonomies;
    }

    private Optional<Taxonomies> getTaxonomiesBaseNodeList(Map<String, Object> params, String query,PageRequestParams pageParams,int totalRecords) {
        Optional<List<TaxonomyNode>>  queryResult = neo4jDb.executeQueryList(query, params, new TaxonomyNodeConverter(null,false));
        return buildTaxonomies(queryResult,pageParams,totalRecords);
    }

    private Optional<Taxonomies> getTaxonomiesListWithDetail(String cypherQuery, String cypherTotal, String basePath, TaxonomyIdWithPageRequestParams idParam) {
        Optional<Taxonomies> result = Optional.empty();
        int totalRecords = getTotalRecordsForTaxonomyQuery(cypherTotal,idParam);

        if(idParam.getSkip() < totalRecords) {
            Map<String, Object> params = new HashMap<>();
            params.put("id", idParam.getId());
            params.put("skip", idParam.getSkip());
            params.put("limit", idParam.getPageSizeInt());

            Optional<List<TaxonomyNode>> node =  neo4jDb.executeQueryList(cypherQuery, params, new TaxonomyNodeConverter(basePath,true));
            result = buildTaxonomies(node,idParam,totalRecords);
        }
        return result;
    }

    private PageInformation buildPageInfo(int pageNumber,int pageSize, int totalRecords) {
        PageInformation pageInfo = new PageInformation();
        pageInfo.setCurrentPage(pageNumber);
        pageInfo.setResultsPerPage(pageSize);
        pageInfo.setTotalRecords(totalRecords);
        return pageInfo;
    }

    private Optional<Taxonomies> buildTaxonomies(Optional<List<TaxonomyNode>> node,PageRequestParams pageParam, int totalRecords) {
        Optional<Taxonomies> result = Optional.empty();
        if (node.isPresent()) {
            Taxonomies taxonomies = new Taxonomies();
            taxonomies.setTaxonomies(node.get());
            if(pageParam != null) {
                PageInformation pageInfo;
                pageInfo = buildPageInfo(pageParam.getPageNumberInt(), pageParam.getPageSizeInt(), totalRecords);
                taxonomies.setPageInfo(pageInfo);
            }
            result = Optional.of(taxonomies);
        }
        return result;
    }

    private Optional<Long> getAncestorIdFromResult(Map<String, Object> row) {
        Optional<Long> result = Optional.empty();
        ArrayList<Long> commonIds = null;
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            Iterable<String> idList = (Iterable<String>) entry.getValue();
            if(commonIds == null){
                commonIds = getLineageIds(idList);
            }else{
                commonIds.retainAll(getLineageIds(idList));
            }
        }
        if(commonIds != null && !commonIds.isEmpty()) {
            result = Optional.of(commonIds.get(0));
        }
        return result;
    }

    private ArrayList<Long> getLineageIds(Iterable<String> idList) {
        ArrayList<Long> commonIds = new ArrayList<>();
        for (String id: idList) {
            if(!id.isEmpty()){
                commonIds.add(Long.parseLong(id));
            }
        }
        return commonIds;
    }

    private String buildAncestorCypherQuery(List<Long> ids){
        String cypherMatchQuery = "MATCH (n0:Node) ";
        String cypherWhereQuery = " WHERE n0.taxonomyId = {id0} ";
        String cypherReturnQuery = " RETURN extract(item in nodes(last((n0:Node)-[:CHILD_OF*]->(:Node))) | item.taxonomyId)";
        for(int i=1;i<ids.size();i++) {
            cypherMatchQuery += ",(n"+i+":Node)";
            cypherWhereQuery += " AND n"+i+".taxonomyId = {id"+i+"} ";
            cypherReturnQuery += ", extract(item in nodes(last((n"+i+":Node)-[:CHILD_OF*]->(:Node))) | item.taxonomyId)";
        }
        return cypherMatchQuery+cypherWhereQuery+cypherReturnQuery;
    }

    private void updateLineageNode(TaxonomyNode node) {
        node.setMnemonic(null);
        node.setCommonName(null);
        node.setRank(null);
        node.setSynonym(null);
    }

    private int getTaxonomyDetailsByNameTotalRecords(NameRequestParams nameParams) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> params = new HashMap<>();
        params.put( "name", nameParams.getTaxonomyName().toLowerCase() );

        String query = GET_TAXONOMY_DETAILS_BY_NAME_TOTAL_RECORDS_CYPHER_QUERY.replace("{searchType}", nameParams
                .getSearchTypeQueryKeyword()).replace("{fieldName}",nameParams.getFieldNameQueryKeyword());
        int result = getTotalRecords(query, params);

        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomyDetailsByNameTotalRecords: "+elapsed+ FOR_LOGGER +nameParams);
        return result;
    }

    private int getTaxonomyPathNodesTotalRecords(String id,PathDirections direction,String depth) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> params = new HashMap<>();
        params.put( "id", id);
        String cypherQuery;
        if(direction.equals(PathDirections.TOP)){
            cypherQuery = GET_TAXONOMY_PATH_TOP_NODE_LIST_CYPHER_QUERY_QUERY_TOTAL;
        }else{
            cypherQuery = GET_TAXONOMY_PATH_DOWN_NODE_LIST_CYPHER_QUERY_QUERY_TOTAL.replace("{depth}",""+depth);
        }
        int result = getTotalRecords(cypherQuery, params);

        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomyPathNodesTotalRecords: "+elapsed+ FOR_LOGGER +id+" depth "+depth);
        return result;
    }

    private Optional<Taxonomies> getNodeBaseList(String cypherQuery,String cypherTotal,TaxonomyIdWithPageRequestParams
            idParam){
        Optional<Taxonomies> taxonomies = Optional.empty();
        int totalRecords = getTotalRecordsForTaxonomyQuery(cypherTotal,idParam);
        if(idParam.getSkip() < totalRecords) {
            long startTime = System.currentTimeMillis();
            Map<String, Object> params = new HashMap<>();
            params.put("id", idParam.getId());
            params.put("skip", idParam.getSkip());
            params.put("limit", idParam.getPageSizeInt());

            Optional<List<TaxonomyNode>> node =  neo4jDb.executeQueryList(cypherQuery, params, new TaxonomyNodeConverter(null,false));
            taxonomies = buildTaxonomies(node,idParam,totalRecords);

            long elapsed = System.currentTimeMillis() - startTime;
            logger.debug("NeoQuery Time for getNodeBaseList: "+elapsed+FOR_LOGGER+idParam);
        }
        return taxonomies;
    }

    private int getTotalRecordsForTaxonomyQuery(String query, TaxonomyIdWithPageRequestParams idParams){
        long startTime = System.currentTimeMillis();
        Map<String, Object> params = new HashMap<>();
        params.put( "id", idParams.getId() );

        int result = getTotalRecords(query, params);

        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTotalRecordsForTaxonomyQuery: "+elapsed+ FOR_LOGGER +idParams);
        return result;
    }

    private int getTotalRecords(String query, Map<String, Object> params) {
        int result = 0;
        Optional<Object> totalRecords = neo4jDb.executeQuery(query, params, new PropertyConverter("totalRecords"));
        if(totalRecords.isPresent()){
            result = Integer.parseInt(totalRecords.get().toString());
        }
        return result;
    }

}