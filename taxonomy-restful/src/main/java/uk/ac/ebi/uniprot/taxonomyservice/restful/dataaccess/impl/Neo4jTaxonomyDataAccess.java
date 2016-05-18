package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathDirections;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.File;
import java.util.*;
import javax.annotation.PostConstruct;
import org.neo4j.graphalgo.impl.util.PathImpl;
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

    private static final String GET_TAXONOMY_DETAIL_MATCH_BASE = "OPTIONAL MATCH (c:Node)-[rc:CHILD_OF]->(n)" +
            "OPTIONAL MATCH (n)-[rp:CHILD_OF]->(p:Node) <-[rs:CHILD_OF]-(s:Node) " +
            "RETURN distinct n as node,p.taxonomyId as parentId,c.taxonomyId as childId,s.taxonomyId as " +
            "siblingId";

    private static final String GET_TAXONOMY_DETAILS_BY_ID_ONE_CYPHER_QUERY =
            "MATCH (n:Node) WHERE n.taxonomyId = {id} "+GET_TAXONOMY_DETAIL_MATCH_BASE;

    private static final String GET_TAXONOMY_DETAILS_BY_NAME_CYPHER_QUERY =
            "MATCH (n:Node) WHERE n.scientificName =~ {name} "+GET_TAXONOMY_DETAIL_MATCH_BASE;

    private static final String GET_TAXONOMY_RELATIONSHIP_CYPHER_QUERY =
            "MATCH (n1:Node),(n2:Node), path = shortestpath((n1)-[r:CHILD_OF*]-(n2)) where n1" +
                    ".taxonomyId = {from} and n2.taxonomyId = {to} return path";

    private static final String GET_TAXONOMY_PATH_DOWN_CYPHER_QUERY =
            "MATCH (n1:Node)<-[r:CHILD_OF*1..{depth}]-(n2:Node) where n1.taxonomyId = {id} return r";

    private static final String GET_TAXONOMY_PATH_TOP_CYPHER_QUERY =
            "MATCH (n1:Node)-[r:CHILD_OF*1..{depth}]->(n2:Node) where n1.taxonomyId = {id} return r";

    private static final String CHECK_HISTORICAL_CHANGE_CYPHER_QUERY =
            "MATCH (n1:Node)-[r:MERGED_TO]->(n2:Node) where n1.taxonomyId = {id} RETURN n2.taxonomyId as taxonomyId";

    @Inject
    public Neo4jTaxonomyDataAccess(@Named("neo4j.database.path") String filePath){
        this.filePath = filePath;
    }

    @PostConstruct
    public void start() {
        logger.debug("Starting up Neo4jTaxonomyDataAccess service");

        if (this.neo4jDb == null) {
            logger.debug("Creating an instance for Neo4jTaxonomyDataAccess and using neo4jDb filePath: "+filePath);
            neo4jDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File(filePath));
            //GraphDatabaseSettings
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
    public TaxonomyNode getTaxonomyDetailsById(long taxonomyId, String basePath) {
        TaxonomyNode result = null;
        long startTime = System.currentTimeMillis();
        Map<String, Object> params = new HashMap<>();
        params.put("id", "" + taxonomyId);
        try (Transaction ignored = neo4jDb.beginTx();
                Result queryResult = neo4jDb.execute(GET_TAXONOMY_DETAILS_BY_ID_ONE_CYPHER_QUERY, params)) {
            result = getTaxonomyFromQueryResult(basePath, queryResult).getOrDefault(taxonomyId,null);
        }
        long elapsed = System.currentTimeMillis() - startTime;
        logger.debug("NeoQuery Time: "+elapsed);
        return result;
    }

    @Override
    public Taxonomies getTaxonomySiblingsById(long taxonomyId) {
        Taxonomies result = getNodeBaseList(GET_TAXONOMY_SIBLINGS_BY_ID_CYPHER_QUERY,taxonomyId);
        if(result != null && result.getTaxonomies().size() > 1){
            TaxonomyNode itSelf = new TaxonomyNode();
            itSelf.setTaxonomyId(taxonomyId);
            result.getTaxonomies().remove(itSelf);
            return result;
        }else{
            return null;
        }
    }

    @Override
    public TaxonomyNode getTaxonomyParentById(long taxonomyId) {
        TaxonomyNode result = null;
        Map<String, Object> params = new HashMap<>();
        params.put( "id", ""+taxonomyId );

        try ( Transaction ignored = neo4jDb.beginTx();
                Result queryResult = neo4jDb.execute(GET_TAXONOMY_PARENT_BY_ID_CYPHER_QUERY,params ) )
        {
            if ( queryResult.hasNext() )
            {
                Map<String,Object> row = queryResult.next();
                if(row.containsKey("node")) {
                    result = new TaxonomyNode();
                    Node node = (Node) row.get("node");
                    result = getTaxonomyBaseNodeFromQueryResult(node);
                }
            }
        }
        return result;
    }

    @Override
    public Taxonomies getTaxonomyChildrenById(long taxonomyId) {
        return getNodeBaseList(GET_TAXONOMY_CHILDREN_BY_ID_CYPHER_QUERY,taxonomyId);
    }

    @Override
    public Taxonomies getTaxonomyDetailsByName(String taxonomyName, String basePath) {
        Collection<TaxonomyNode> result = null;
        Map<String, Object> params = new HashMap<>();
        params.put( "name", "(?i).*"+taxonomyName+".*" );

        try ( Transaction ignored = neo4jDb.beginTx();
                Result queryResult = neo4jDb.execute(GET_TAXONOMY_DETAILS_BY_NAME_CYPHER_QUERY,params ) )
        {
            result =  getTaxonomyFromQueryResult(basePath, queryResult).values();
        }
        Taxonomies taxonomies = null;
        if(result != null && !result.isEmpty()){
            taxonomies = new Taxonomies();
            taxonomies.setTaxonomies(new ArrayList<>(result));
        }
        return taxonomies;
    }

    @Override
    public TaxonomyNode checkRelationshipBetweenTaxonomies(long taxonomyId1, long taxonomyId2) {
        TaxonomyNode result = null;
        Map<String, Object> params = new HashMap<>();
        params.put( "from", ""+taxonomyId1);
        params.put( "to", ""+taxonomyId2);

        try ( Transaction ignored = neo4jDb.beginTx();
                Result queryResult = neo4jDb.execute(GET_TAXONOMY_RELATIONSHIP_CYPHER_QUERY,params ) )
        {
            if ( queryResult.hasNext() ) {
                Optional<Object> value = getProperty(queryResult.next(),"path");
                if (value.isPresent()) {
                    PathImpl path = (PathImpl) value.get();
                    result = getTaxonomyNodePath(taxonomyId1,path);
                }

            }
        }
        return result;
    }

    @Override
    public TaxonomyNode getTaxonomyPath(PathRequestParams nodePathParams) {
        TaxonomyNode result = null;
        String cypherQuery;
        if(nodePathParams.getPathDirection().equals(PathDirections.TOP)){
            cypherQuery = GET_TAXONOMY_PATH_TOP_CYPHER_QUERY.replace("{depth}",""+nodePathParams.getDepth());
        }else{
            cypherQuery = GET_TAXONOMY_PATH_DOWN_CYPHER_QUERY.replace("{depth}",""+nodePathParams.getDepth());
        }
        Map<String, Object> params = new HashMap<>();
        params.put( "id", nodePathParams.getId());

        try ( Transaction ignored = neo4jDb.beginTx();
                Result queryResult = neo4jDb.execute(cypherQuery,params ) )
        {
            while ( queryResult.hasNext() ) {
                Optional<Object> value = getProperty(queryResult.next(),"r");
                if (value.isPresent()) {
                    List<Relationship> relationshipList = (List<Relationship>) value.get();
                    result = getTaxonomyNodePath(Long.parseLong(nodePathParams.getId()),relationshipList,result);
                }
            }
        }
        return result;
    }

    @Override
    public long checkTaxonomyIdHistoricalChange(long id) {
        long result = -1;
        Map<String, Object> params = new HashMap<>();
        params.put( "id", ""+id );

        try ( Transaction ignored = neo4jDb.beginTx();
                Result queryResult = neo4jDb.execute(CHECK_HISTORICAL_CHANGE_CYPHER_QUERY,params ) )
        {
            if ( queryResult.hasNext() )
            {
                Optional<Object> value = getProperty(queryResult.next(),"taxonomyId");
                if (value.isPresent()) {
                    result = Long.parseLong("" + value.get());
                }
            }
        }
        return result;
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

    private Map<Long,TaxonomyNode> getTaxonomyFromQueryResult(String basePath, Result queryResult) {
        Map<Long,TaxonomyNode> result = new HashMap<>();
        Set<String> siblings = new HashSet<>();
        Set<String> children = new HashSet<>();
        Long currentTaxonomy = -1L;
        TaxonomyNode currentNode = null;
        while (queryResult.hasNext()) {
            Map<String, Object> row = queryResult.next();

            Optional<Object> value = getProperty(row, "node");
            if (value.isPresent()) {
                Node node = (Node) value.get();
                Long rowTaxonomy = Long.parseLong(""+node.getProperty("taxonomyId"));
                if(currentNode == null){// first iteration
                    currentNode = getTaxonomyNodeWithParentLink(basePath, row, node);
                }else if(!currentTaxonomy.equals(rowTaxonomy)){ // if result has more than one result
                    updateSiblingsAndChildrenLinksForNode(basePath,siblings, children, currentTaxonomy,currentNode);
                    result.put(currentTaxonomy, currentNode);

                    siblings = new HashSet<>();
                    children = new HashSet<>();
                    currentNode = getTaxonomyNodeWithParentLink(basePath, row, node);
                }
                currentTaxonomy = rowTaxonomy;
            }
            value = getProperty(row, "childId");
            if (value.isPresent()) {
                children.add(basePath + value.get());
            }
            value = getProperty(row, "siblingId");
            if (value.isPresent()) {
                siblings.add(basePath + value.get());
            }
        }
        if(currentNode != null) {
            updateSiblingsAndChildrenLinksForNode(basePath,siblings, children, currentTaxonomy,currentNode);
            result.put(currentTaxonomy, currentNode);
        }
        return result;
    }

    private void updateSiblingsAndChildrenLinksForNode(String basePath, Set<String> siblings, Set<String> children,
            Long currentTaxonomy, TaxonomyNode currentNode) {
        if (!siblings.isEmpty()) {
            siblings.remove(basePath + currentTaxonomy);
            currentNode.setSiblingsLinks(new ArrayList<>(siblings));
        }
        if (!children.isEmpty()) {
            currentNode.setChildrenLinks(new ArrayList<>(children));
        }
    }

    private TaxonomyNode getTaxonomyNodeWithParentLink(String basePath, Map<String, Object> row, Node node) {
        TaxonomyNode currentNode = getTaxonomyBaseNodeFromQueryResult(node);
        Optional<Object> parentIdValue = getProperty(row, "parentId");
        if (parentIdValue.isPresent()) {
            currentNode.setParentLink(basePath + parentIdValue.get());
        }
        return currentNode;
    }

    private Optional<Object> getProperty(Map<String, Object> row, String propertyName){
        return Optional.ofNullable(row.getOrDefault(propertyName,null));
    }

    private Taxonomies getNodeBaseList(String cypherSQL,long taxonomyId){
        Map<String, Object> params = new HashMap<>();
        params.put( "id", ""+taxonomyId );
        Taxonomies taxonomies = null;
        List<TaxonomyNode> queryResultList = new ArrayList<>();
        try ( Transaction ignored = neo4jDb.beginTx();
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
        }
        if(queryResultList != null && !queryResultList.isEmpty()){
            taxonomies = new Taxonomies();
            taxonomies.setTaxonomies(queryResultList);
        }
        return taxonomies;
    }

    private TaxonomyNode getTaxonomyNodePath(long initialTaxonomyId, PathImpl path) {
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
