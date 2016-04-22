package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl;

import uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.TaxonomyDataAccess;
import uk.ac.ebi.uniprot.taxonomyservice.restful.domain.TaxonomyNode;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathDirections;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.request.PathRequestParams;
import uk.ac.ebi.uniprot.taxonomyservice.restful.rest.response.Taxonomies;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.neo4j.graphalgo.impl.util.PathImpl;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
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

    private static final String GET_TAXONOMY_DETAILS_BY_ID_CYPHER_QUERY =
            "MATCH (n:Node)-[r:CHILD_OF]->(p:Node) WHERE n.taxonomyId = {id} RETURN n as node,p.taxonomyId as parentId";

    private static final String GET_TAXONOMY_DETAILS_BY_NAME_CYPHER_QUERY =
            "MATCH (n:Node)-[r:CHILD_OF]->(p:Node) WHERE n.scientificName =~ {name} " +
                    "RETURN n as node,p.taxonomyId as parentId";

    private static final String GET_LINK_LIST_CYPHER_QUERY =
            "MATCH (n:Node)-[r:CHILD_OF]->(p:Node) WHERE p.taxonomyId = {id} RETURN n.taxonomyId as taxonomyId";

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
        Map<String, Object> params = new HashMap<>();
        params.put( "id", ""+taxonomyId );

        try ( Transaction ignored = neo4jDb.beginTx();
                Result queryResult = neo4jDb.execute(GET_TAXONOMY_DETAILS_BY_ID_CYPHER_QUERY,params ) )
        {
            if ( queryResult.hasNext() )
            {
                result = getTaxonomyNodeDetail(queryResult, basePath);

            }
        }
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
        List<TaxonomyNode> result = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put( "name", "(?i).*"+taxonomyName+".*" );

        try ( Transaction ignored = neo4jDb.beginTx();
                Result queryResult = neo4jDb.execute(GET_TAXONOMY_DETAILS_BY_NAME_CYPHER_QUERY,params ) )
        {
            while ( queryResult.hasNext() )
            {
                TaxonomyNode node = getTaxonomyNodeDetail(queryResult, basePath);
                result.add(node);

            }
        }
        Taxonomies taxonomies = null;
        if(!result.isEmpty()){
            taxonomies = new Taxonomies();
            taxonomies.setTaxonomies(result);
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
                Map<String, Object> row = queryResult.next();
                if(row.containsKey("path")) {
                    PathImpl path = (PathImpl) row.get("path");
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
                Map<String, Object> row = queryResult.next();
                if(row.containsKey("r")) {
                    List<Relationship> relationshipList = (List<Relationship>) row.get("r");
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
                Map<String,Object> row = queryResult.next();
                if(row.containsKey("taxonomyId")) {
                    result = Long.parseLong("" + row.get("taxonomyId"));
                }
            }
        }
        return result;
    }

    private TaxonomyNode getTaxonomyNodeDetail(Result queryResult,String basePath) {
        TaxonomyNode result;Map<String,Object> row = queryResult.next();
        result = new TaxonomyNode();

        if(row.containsKey("node")) {
            Node node = (Node) row.get("node");
            result = getTaxonomyBaseNodeFromQueryResult(node);
            result.setChildrenLinks(getLinkList(result.getTaxonomyId(),basePath));
        }
        if(row.containsKey("parentId")) {
            String parentIdValue =  (String) row.get("parentId");
            if(parentIdValue != null && !parentIdValue.isEmpty()) {
                long parentId = Long.parseLong("" + row.get("parentId"));
                result.setParentLink(basePath + parentId);

                result.setSiblingsLinks(getLinkList(parentId, basePath));
                result.getSiblingsLinks().remove(basePath + result.getTaxonomyId());
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

    private List<String> getLinkList(long id, String basePath){
        List<String> result = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put( "id", ""+id );

        try ( Transaction ignored = neo4jDb.beginTx();
                Result queryResult = neo4jDb.execute(GET_LINK_LIST_CYPHER_QUERY,params ) ) {
            while ( queryResult.hasNext() ) {
                Map<String, Object> row = queryResult.next();
                for (Map.Entry<String, Object> nodes : row.entrySet()) {
                    if("taxonomyId".equals(nodes.getKey())){
                        result.add(basePath + nodes.getValue());
                    }
                }
            }
        }
        if(!result.isEmpty()) {
            return result;
        }else{
            return null;
        }
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
                for (Map.Entry<String, Object> nodes : row.entrySet()) {
                    if("node".equals(nodes.getKey())) {
                        Node node = (Node) nodes.getValue();
                        TaxonomyNode taxonomyNode = getTaxonomyBaseNodeFromQueryResult(node);
                        queryResultList.add(taxonomyNode);
                    }
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
