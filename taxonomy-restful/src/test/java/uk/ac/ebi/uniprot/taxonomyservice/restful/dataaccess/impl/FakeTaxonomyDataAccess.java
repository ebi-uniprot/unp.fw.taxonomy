package uk.ac.ebi.uniprot.taxonomyservice.restful.dataaccess.impl;

import com.google.inject.Singleton;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;
import org.testcontainers.containers.Neo4jContainer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Mock taxonomy data access that create and return mock results based on imported CSV files with mock values
 *
 * Created by lgonzales on 19/02/16.
 */

@Singleton
public class FakeTaxonomyDataAccess extends Neo4jTaxonomyDataAccess implements AutoCloseable {

    private static final String LOAD_CSV = "LOAD CSV WITH HEADERS FROM $csvPath AS row FIELDTERMINATOR ',' ";

    private static final String IMPORT_CYPHER_NODE_QUERY = LOAD_CSV +
            "MERGE (node:Node { taxonomyId: row.TAX_ID }) " +
            "SET node += {taxonomyId: row.TAX_ID, mnemonic: row.SPTR_CODE, mnemonicLowerCase: " +
            "lower(row.SPTR_CODE), scientificName: row.SPTR_SCIENTIFIC, scientificNameLowerCase: " +
            "lower(row.SPTR_SCIENTIFIC), commonName: row.SPTR_COMMON, commonNameLowerCase: " +
            "lower(row.SPTR_COMMON), synonym: row.SPTR_SYNONYM, rank: row.RANK, " +
            "superregnum: row.SUPER_REGNUM, hidden: row.HIDDEN} " +
            "MERGE (parent:Node { taxonomyId: row.PARENT_ID }) " +
            "MERGE (node)-[:CHILD_OF]->(parent)";

    private static final String IMPORT_CYPHER_MERGED_QUERY = LOAD_CSV +
            "MERGE (node:Merged { taxonomyId: row.OLD_TAX_ID })-[:MERGED_TO]->(node1:Node { taxonomyId: " +
            "row.NEW_TAX_ID})";

    private static final String IMPORT_CYPHER_DELETED_QUERY = LOAD_CSV +
            "MERGE (n:Node {taxonomyId: row.TAX_ID}) SET n:Deleted REMOVE n:Node";

    private static final String DELETE_NODE_CYPHER_QUERY = "MATCH (n:Node)-[r]-() WHERE n.taxonomyId=$id DELETE n, r";

    private static final Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:5.26.2-community")
            .withAdminPassword("password")
            .withEnv("NEO4J_dbms_memory_heap_initial__size", "512M")
            .withEnv("NEO4J_dbms_memory_heap_max__size", "1G");

    static {
        neo4jContainer.start();
    }
    private final Driver testDriver; // Explicitly store the test driver instance

    public FakeTaxonomyDataAccess() {
        super(neo4jContainer.getBoltUrl(), "neo4j", "password");
        // Explicitly initialize the driver since the superclass constructor has not finished yet
        this.testDriver = GraphDatabase.driver(neo4jContainer.getBoltUrl(), AuthTokens.basic("neo4j", "password"));

        try {
            // Import mock data
            importNeo4JData(testDriver, "/neo4JMockNodeData.csv", IMPORT_CYPHER_NODE_QUERY);
            importNeo4JData(testDriver, "/neo4JMockMergedData.csv", IMPORT_CYPHER_MERGED_QUERY);
            importNeo4JData(testDriver, "/neo4JMockDeletedData.csv", IMPORT_CYPHER_DELETED_QUERY);
            deleteUnWantedRoot(testDriver, "0", DELETE_NODE_CYPHER_QUERY);
            deleteUnWantedRoot(testDriver, "50", DELETE_NODE_CYPHER_QUERY);

            this.neo4jDb = new Neo4JQueryExecutor(testDriver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setNeo4jDb(Neo4JQueryExecutor neo4jDb) {
        this.neo4jDb = neo4jDb;
    }

    public Neo4JQueryExecutor getNeo4jDb() {
        return this.neo4jDb;
    }

    /**
     * Imports mock CSV data into the Neo4j database.
     */
    private static void importNeo4JData(Driver driver, String resourcePath, String query) {
        URL csvFilePath = FakeTaxonomyDataAccess.class.getClassLoader().getResource(resourcePath);

        if (csvFilePath == null) {
            throw new IllegalArgumentException("Resource not found: " + resourcePath);
        }

        Map<String, Object> params = new HashMap<>();
        params.put("csvPath", csvFilePath.toString());

        try (Session session = driver.session();
             Transaction tx = session.beginTransaction()) {
            tx.run(query, params);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes unwanted root nodes from the Neo4j database.
     */
    private static void deleteUnWantedRoot(Driver driver, String id, String query) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);

        try (Session session = driver.session();
             Transaction tx = session.beginTransaction()) {
            Result result = tx.run(query, params);
            while (result.hasNext()) {
                Record row = result.next();
                System.out.println("DELETED ROW: " + row.asMap());
            }
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        super.close();
        testDriver.close();  // Close the explicitly created driver
        neo4jContainer.stop();
    }
}