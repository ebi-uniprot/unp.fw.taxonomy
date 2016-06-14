package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.NameRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathDirections;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.File;
import java.util.*;
import javax.annotation.PostConstruct;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Neo4J taxonomy data access class is responsible to query information from Neo4J Taxonomy database and build the
 * correct response TaxonomyNode or Taxonomies object
 *
 * Created by lgonzales on 08/04/16.
 */
public class Neo4jTaxonomyDataAccess implements TaxonomyDataAccess{

    public static final Logger logger = LoggerFactory.getLogger(Neo4jTaxonomyDataAccess.class);

    protected GraphDatabaseService neo4jDb;

    protected String filePath;

    private static final String GET_TAXONOMY_SIBLINGS_BY_ID_CYPHER_QUERY =
            "MATCH (n:Node)-[r:CHILD_OF]->(p:Node) WHERE n.taxonomyId = {id} " +
                    "with p MATCH (s:Node)-[r:CHILD_OF]->(p) RETURN s as node";

    private static final String GET_TAXONOMY_PARENT_BY_ID_CYPHER_QUERY =
            "MATCH (n:Node)-[r:CHILD_OF]->(p:Node) WHERE n.taxonomyId = {id} RETURN p as node";

    private static final String GET_TAXONOMY_CHILDREN_BY_ID_CYPHER_QUERY =
            "MATCH (n:Node)-[r:CHILD_OF]->(p:Node) WHERE p.taxonomyId = {id} RETURN n as node";

    private static final String GET_TAXONOMY_DETAIL_MATCH_BASE = "OPTIONAL MATCH (n)-[rp:CHILD_OF]->(p:Node) " +
            "with n,p return n as node,p.taxonomyId as parentId," +
            "extract(path in (:Node)-[:CHILD_OF]->(n) | extract( r in relationships(path) | startNode(r).taxonomyId ))"+
            " as children," +
            "extract(path in (p)<-[:CHILD_OF]-(:Node) | extract( r in relationships(path) | startNode(r).taxonomyId ))"+
            " as siblings";

    private static final String GET_TAXONOMY_DETAILS_BY_ID_ONE_CYPHER_QUERY =
            "MATCH (n:Node) WHERE n.taxonomyId = {id} "+GET_TAXONOMY_DETAIL_MATCH_BASE;

    private static final String GET_TAXONOMY_DETAILS_BY_NAME_CYPHER_QUERY =
            "MATCH (n:Node) WHERE n.scientificNameLowerCase {searchType} {name} " +GET_TAXONOMY_DETAIL_MATCH_BASE +
            " UNION MATCH (n:Node) WHERE n.commonNameLowerCase {searchType} {name} " +GET_TAXONOMY_DETAIL_MATCH_BASE +
            " UNION MATCH (n:Node) WHERE n.mnemonicLowerCase {searchType} {name} "+GET_TAXONOMY_DETAIL_MATCH_BASE;

    private static final String GET_TAXONOMY_RELATIONSHIP_CYPHER_QUERY =
            "MATCH (n1:Node),(n2:Node) where n1.taxonomyId = {from} and n2.taxonomyId = {to} " +
                    "return shortestpath((n1)-[:CHILD_OF*]-(n2)) as path";

    private static final String GET_TAXONOMY_PATH_DOWN_CYPHER_QUERY =
            "MATCH (n1:Node)<-[r:CHILD_OF*1..{depth}]-(n2:Node) where n1.taxonomyId = {id} return r";

    private static final String GET_TAXONOMY_PATH_TOP_CYPHER_QUERY =
            "MATCH (n1:Node)-[r:CHILD_OF*1..{depth}]->(n2:Node) where n1.taxonomyId = {id} return r";

    private static final String CHECK_HISTORICAL_CHANGE_CYPHER_QUERY =
            "MATCH (m:Merged)-[r:MERGED_TO]->(n:Node) where m.taxonomyId = {id} RETURN n.taxonomyId as taxonomyId";

    @Inject
    public Neo4jTaxonomyDataAccess(@Named("NEO4J_DATABASE_PATH") String filePath){
        this.filePath = filePath;
    }

    @PostConstruct
    public void start() {
        logger.debug("Starting up Neo4jTaxonomyDataAccess service");

        if (this.neo4jDb == null) {
            logger.debug("Creating an instance for Neo4jTaxonomyDataAccess and using neo4jDb filePath: "+filePath);
            neo4jDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(new File(filePath))
                    .setConfig("dbms.threads.worker_count", "20" )
                    .setConfig(GraphDatabaseSettings.read_only,"true")
                    //.setConfig( GraphDatabaseSettings.pagecache_memory, "1g" )
                    //.setConfig( GraphDatabaseSettings.string_block_size, "120" )
                    //.setConfig( GraphDatabaseSettings.array_block_size, "600" )
                    .newGraphDatabase();
            registerStop(neo4jDb);
        }

    }

    /**
     * TODO: Currently I am registering the stop manually
     *       There is an automatic way, like uniprot restfull service does
     **/
    public void registerStop(final GraphDatabaseService graphDb) {
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
        Optional<TaxonomyNode> result = Optional.empty();
        long startTime = System.currentTimeMillis();
        Map<String, Object> params = new HashMap<>();
        params.put("id", "" + taxonomyId);
        try (Transaction tx = neo4jDb.beginTx();
                Result queryResult = neo4jDb.execute(GET_TAXONOMY_DETAILS_BY_ID_ONE_CYPHER_QUERY, params)) {
            Optional<ArrayList<TaxonomyNode>> node = getTaxonomyFromQueryResult(basePath, queryResult);
            if(node.isPresent()){
                result = Optional.of(node.get().get(0));
            }
            queryResult.close();
            tx.success();
        }
        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomyDetailsById: "+elapsed+ " for id "+taxonomyId);
        return result;
    }

    @Override
    public Optional<Taxonomies> getTaxonomySiblingsById(long taxonomyId) {
        Taxonomies result = getNodeBaseList(GET_TAXONOMY_SIBLINGS_BY_ID_CYPHER_QUERY,taxonomyId);
        if(result != null && result.getTaxonomies().size() > 1){
            TaxonomyNode itSelf = new TaxonomyNode();
            itSelf.setTaxonomyId(taxonomyId);
            result.getTaxonomies().remove(itSelf);
            return Optional.of(result);
        }else{
            return Optional.empty();
        }
    }

    @Override
    public Optional<TaxonomyNode> getTaxonomyParentById(long taxonomyId) {
        Optional<TaxonomyNode> result = Optional.empty();
        long startTime = System.currentTimeMillis();
        Map<String, Object> params = new HashMap<>();
        params.put( "id", ""+taxonomyId );

        try ( Transaction tx = neo4jDb.beginTx();
                Result queryResult = neo4jDb.execute(GET_TAXONOMY_PARENT_BY_ID_CYPHER_QUERY,params ) )
        {
            if ( queryResult.hasNext() )
            {
                Map<String,Object> row = queryResult.next();
                if(row.containsKey("node")) {
                    Node node = (Node) row.get("node");
                    result = Optional.ofNullable(getTaxonomyBaseNodeFromQueryResult(node));
                }
            }
            queryResult.close();
            tx.success();
            tx.close();
        }
        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomyParentById: "+elapsed+ " for id "+taxonomyId);
        return result;
    }

    @Override
    public Optional<Taxonomies> getTaxonomyChildrenById(long taxonomyId) {
        return Optional.ofNullable(getNodeBaseList(GET_TAXONOMY_CHILDREN_BY_ID_CYPHER_QUERY,taxonomyId));
    }

    @Override
    public Optional<Taxonomies> getTaxonomyDetailsByName(NameRequestParams nameParams, String basePath) {
        Optional<ArrayList<TaxonomyNode>> result = null;
        long startTime = System.currentTimeMillis();
        Map<String, Object> params = new HashMap<>();
        params.put( "name", nameParams.getTaxonomyName().toLowerCase() );

        String query = GET_TAXONOMY_DETAILS_BY_NAME_CYPHER_QUERY.replaceAll("\\{searchType\\}", nameParams
                .getSearchTypeQueryKeyword());
        logger.debug(query +" for "+nameParams.getTaxonomyName().toLowerCase());
        try ( Transaction tx = neo4jDb.beginTx();
                Result queryResult = neo4jDb.execute(query,params ) )
        {
            result =  getTaxonomyFromQueryResult(basePath, queryResult);
            queryResult.close();
            tx.success();
            tx.close();
        }
        Optional<Taxonomies> taxonomies = Optional.empty();
        if(result.isPresent()){
            Taxonomies nodeList = new Taxonomies();
            nodeList.setTaxonomies(result.get());
            taxonomies = Optional.of(nodeList);
        }
        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomyDetailsByName: "+elapsed+ " for "+params);
        return taxonomies;
    }

    @Override
    public Optional<TaxonomyNode> getTaxonomiesRelationship(long taxonomyId1, long to) {
        Optional<TaxonomyNode> result = Optional.empty();
        long startTime = System.currentTimeMillis();
        Map<String, Object> params = new HashMap<>();
        params.put( "from", ""+taxonomyId1);
        params.put( "to", ""+ to);

        try ( Transaction tx = neo4jDb.beginTx();
                Result queryResult = neo4jDb.execute(GET_TAXONOMY_RELATIONSHIP_CYPHER_QUERY,params ) )
        {
            if ( queryResult.hasNext() ) {
                Optional<Object> value = getProperty(queryResult.next(),"path");
                if (value.isPresent()) {
                    org.neo4j.graphalgo.impl.util.PathImpl path = (org.neo4j.graphalgo.impl.util.PathImpl) value.get();
                    result = Optional.ofNullable(getTaxonomyNodePath(taxonomyId1,path));
                }

            }
            queryResult.close();
            tx.success();
            tx.close();
        }
        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomiesRelationship: "+elapsed+ " from "+taxonomyId1+ " to "+to);
        return result;
    }

    @Override
    public Optional<TaxonomyNode> getTaxonomyPath(PathRequestParams nodePathParams) {
        TaxonomyNode result = null;
        long startTime = System.currentTimeMillis();
        String cypherQuery;
        if(nodePathParams.getPathDirection().equals(PathDirections.TOP)){
            cypherQuery = GET_TAXONOMY_PATH_TOP_CYPHER_QUERY.replace("{depth}",""+nodePathParams.getDepth());
        }else{
            cypherQuery = GET_TAXONOMY_PATH_DOWN_CYPHER_QUERY.replace("{depth}",""+nodePathParams.getDepth());
        }
        Map<String, Object> params = new HashMap<>();
        params.put( "id", nodePathParams.getId());

        try ( Transaction tx = neo4jDb.beginTx();
                Result queryResult = neo4jDb.execute(cypherQuery,params ) )
        {
            while ( queryResult.hasNext() ) {
                Optional<Object> value = getProperty(queryResult.next(),"r");
                if (value.isPresent()) {
                    List<Relationship> relationshipList = (List<Relationship>) value.get();
                    result = getTaxonomyNodePath(Long.parseLong(nodePathParams.getId()),relationshipList,result);
                }
            }
            queryResult.close();
            tx.success();
            tx.close();
        }
        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomyPath: "+elapsed+ " for path param "+nodePathParams);
        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Long> getTaxonomyHistoricalChange(long id) {
        Long result = null;
        long startTime = System.currentTimeMillis();
        Map<String, Object> params = new HashMap<>();
        params.put( "id", ""+id );

        try ( Transaction tx = neo4jDb.beginTx();
                Result queryResult = neo4jDb.execute(CHECK_HISTORICAL_CHANGE_CYPHER_QUERY,params ) )
        {
            if ( queryResult.hasNext() )
            {
                Optional<Object> value = getProperty(queryResult.next(),"taxonomyId");
                if (value.isPresent()) {
                    result = Long.parseLong("" + value.get());
                }
            }
            queryResult.close();
            tx.success();
            tx.close();
        }
        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getTaxonomyHistoricalChange: "+elapsed+ " for id "+id);
        return Optional.ofNullable(result);
    }

    private TaxonomyNode getTaxonomyBaseNodeFromQueryResult(Node node) {
        TaxonomyNode result = null;

        if(node.hasProperty("taxonomyId")) {
            String taxonomyId = ""+node.getProperty("taxonomyId");
            if(!taxonomyId.isEmpty()) {
                result = new TaxonomyNode();
                result.setTaxonomyId(Long.parseLong(taxonomyId));

                if(node.hasProperty("commonName")) {
                    result.setCommonName("" + node.getProperty("commonName"));
                }
                if(node.hasProperty("mnemonic")) {
                    result.setMnemonic("" + node.getProperty("mnemonic"));
                }
                if(node.hasProperty("rank")) {
                    result.setRank("" + node.getProperty("rank"));
                }
                if(node.hasProperty("scientificName")) {
                    result.setScientificName("" + node.getProperty("scientificName"));
                }
                if(node.hasProperty("synonym")) {
                    result.setSynonym("" + node.getProperty("synonym"));
                }
            }
        }

        return result;
    }

    private Optional<ArrayList<TaxonomyNode>> getTaxonomyFromQueryResult(String basePath, Result queryResult) {
        ArrayList<TaxonomyNode> result = new ArrayList<>();
        while (queryResult.hasNext()) {
            Map<String, Object> row = queryResult.next();
            TaxonomyNode taxonomyNode = null;

            Optional<Object> value = getProperty(row, "node");
            if (value.isPresent()) {
                Node node = (Node) value.get();
                taxonomyNode = getTaxonomyBaseNodeFromQueryResult(node);
                Long taxId = taxonomyNode.getTaxonomyId();
                taxonomyNode.setParentLink(getTaxonomyParentLink(basePath, row));
                taxonomyNode.setChildrenLinks(getLinkList("children",row,taxId,basePath));
                taxonomyNode.setSiblingsLinks(getLinkList("siblings",row,taxId,basePath));
                result.add(taxonomyNode);
            }
        }
        if(result.isEmpty()){
            return Optional.empty();
        }else{
            return Optional.of(result);
        }
    }

    private ArrayList<String> getLinkList(String propertyName,Map<String,Object> row,long id,String basePath) {
        ArrayList<String> result = null;
        Optional<Object> value = getProperty(row, propertyName);
        if (value.isPresent()) {
            Set<String> list = new HashSet<>();
            Iterable<Iterable<String>> pathList = (Iterable<Iterable<String>>) value.get();
            pathList.forEach(wrapper -> wrapper.forEach(item -> list.add(basePath + item)));
            list.remove(basePath + id);
            if (!list.isEmpty()){
                result = new ArrayList<>(list);
            }
        }
        return result;
    }

    private String getTaxonomyParentLink(String basePath, Map<String, Object> row) {
        String result = null;
        Optional<Object> parentIdValue = getProperty(row, "parentId");
        if (parentIdValue.isPresent()) {
            result = basePath + parentIdValue.get();
        }
        return result;
    }

    private Optional<Object> getProperty(Map<String, Object> row, String propertyName){
        Object propertyValue = row.getOrDefault(propertyName,null);
        if(propertyValue != null && !propertyValue.toString().isEmpty()){
            return Optional.of(propertyValue);
        }else{
            return Optional.empty();
        }
    }

    private Taxonomies getNodeBaseList(String cypherSQL,long taxonomyId){
        Map<String, Object> params = new HashMap<>();
        long startTime = System.currentTimeMillis();
        params.put( "id", ""+taxonomyId );
        Taxonomies taxonomies = null;
        List<TaxonomyNode> queryResultList = new ArrayList<>();
        try ( Transaction tx = neo4jDb.beginTx();
                Result queryResult = neo4jDb.execute(cypherSQL,params) ) {
            while ( queryResult.hasNext() ) {
                Map<String, Object> row = queryResult.next();
                Optional<Object> value = getProperty(row, "node");
                if (value.isPresent()) {
                    Node node = (Node) value.get();
                    TaxonomyNode taxonomyNode = getTaxonomyBaseNodeFromQueryResult(node);
                    queryResultList.add(taxonomyNode);
                }
            }
            queryResult.close();
            tx.success();
            tx.close();
        }
        if(!queryResultList.isEmpty()){
            taxonomies = new Taxonomies();
            taxonomies.setTaxonomies(queryResultList);
        }
        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time for getNodeBaseList: "+elapsed+" for id "+taxonomyId);
        return taxonomies;
    }

    private TaxonomyNode getTaxonomyNodePath(long initialTaxonomyId, org.neo4j.graphalgo.impl.util.PathImpl path) {
        return getTaxonomyNodePath(initialTaxonomyId,path.relationships(), null);
    }

    private TaxonomyNode getTaxonomyNodePath(long initialId, Iterable<Relationship> relationships,TaxonomyNode result) {
        TaxonomyNode lastEndNode = null;
        for (Relationship relationship :relationships) {
            TaxonomyNode startNode = getTaxonomyBaseNodeFromQueryResult(relationship.getStartNode());
            TaxonomyNode endNode = getTaxonomyBaseNodeFromQueryResult(relationship.getEndNode());
            if(result == null){
                if(startNode.getTaxonomyId() == initialId){
                    result = startNode;
                    lastEndNode = startNode.mergeParent(endNode);
                }else{
                    result = endNode;
                    lastEndNode = endNode.mergeChildren(startNode);
                }
            }else{
                if(lastEndNode == null){
                    if(endNode.getTaxonomyId() == initialId){
                        lastEndNode = result.mergeChildren(startNode);
                    }else{
                        lastEndNode = result.mergeParent(endNode);
                    }
                }else{
                    if (lastEndNode.getTaxonomyId() == startNode.getTaxonomyId()) {
                        lastEndNode = lastEndNode.mergeParent(endNode);
                    } else {
                        lastEndNode = lastEndNode.mergeChildren(startNode);
                    }
                }
            }
        }
        return result;
    }

}