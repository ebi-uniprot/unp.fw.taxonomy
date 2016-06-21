package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

/**
 * Mock taxonomy data access that create and return mock results based on imported CSV files with mock values
 *
 * Created by lgonzales on 19/02/16.
 */
public class FakeTaxonomyDataAccess extends Neo4jTaxonomyDataAccess {

    private static final String LOAD_CSV = "LOAD CSV WITH HEADERS FROM {csvPath} AS row FIELDTERMINATOR ',' ";

    private static final String IMPORT_CYPHER_NODE_QUERY = LOAD_CSV +
            "MERGE (node:Node { taxonomyId : row.TAX_ID }) " +
            "SET node += {taxonomyId : row.TAX_ID, mnemonic : row.SPTR_CODE, mnemonicLowerCase : " +
            "lower(row.SPTR_CODE), scientificName : row.SPTR_SCIENTIFIC, scientificNameLowerCase : " +
            "lower(row.SPTR_SCIENTIFIC), commonName : row.SPTR_COMMON, commonNameLowerCase : " +
            "lower(row.SPTR_COMMON), synonym : row .SPTR_SYNONYM, rank : row.RANK} " +
            "MERGE (parent:Node {taxonomyId:row.PARENT_ID}) " +
            "MERGE (node)-[:CHILD_OF]-(parent)";

    private static final String IMPORT_CYPHER_MERGED_QUERY  = LOAD_CSV +
            "MERGE (node:Merged { taxonomyId : row.OLD_TAX_ID })-[:MERGED_TO]-(node1:Node { taxonomyId : " +
            "row.NEW_TAX_ID})";

    private static final String IMPORT_CYPHER_DELETED_QUERY  = LOAD_CSV +
            "MERGE(n:Node {taxonomyId:row.TAX_ID}) SET n:Deleted REMOVE n:Node";

    private static final String DELETE_NODE_CYPHER_QUERY = "MATCH (n:Node)-[r]-() WHERE n.taxonomyId={id} DELETE n,r";

    public FakeTaxonomyDataAccess(){
        this("");
    }

    public FakeTaxonomyDataAccess(String filePath) {
        super("");
        GraphDatabaseService neo4jDbTest = new TestGraphDatabaseFactory().newImpermanentDatabase();

        importNeo4JData(neo4jDbTest, "/neo4JMockNodeData.csv",IMPORT_CYPHER_NODE_QUERY);
        importNeo4JData(neo4jDbTest, "/neo4JMockMergedData.csv",IMPORT_CYPHER_MERGED_QUERY);
        importNeo4JData(neo4jDbTest, "/neo4JMockDeletedData.csv",IMPORT_CYPHER_DELETED_QUERY);
        deleteUnWantedRoot(neo4jDbTest,"0",DELETE_NODE_CYPHER_QUERY);

        setNeo4jDb(neo4jDbTest);
        registerStop(neo4jDbTest);
    }

    public void setNeo4jDb(GraphDatabaseService neo4jDb){
        this.neo4jDb = neo4jDb;
    }

    public GraphDatabaseService getNeo4jDb(){
        return this.neo4jDb;
    }

    private static void importNeo4JData(GraphDatabaseService neo4jDb,String resourcePath, String query) {
        URL csvFilePath = Neo4jTaxonomyDataAccessTest.class.getResource(resourcePath);
        Map<String, Object> params = new HashMap<>();
        params.put( "csvPath",csvFilePath.toString());

        try ( Transaction tx = neo4jDb.beginTx();
                Result queryResult = neo4jDb.execute(query,params ) )
        {
            tx.success();
        }
    }

    private static void deleteUnWantedRoot(GraphDatabaseService neo4jDb,String id, String query) {
        Map<String, Object> params = new HashMap<>();
        params.put( "id",id);

        try ( Transaction tx = neo4jDb.beginTx();
                Result queryResult = neo4jDb.execute(query,params ) )
        {
            while (queryResult.hasNext()) {
                Map<String, Object> row = queryResult.next();
                System.out.println(row);
            }
            tx.success();
        }
    }

}
