package uk.ac.ebi.uniprot.taxonomyservice.imports.setup;

import java.nio.file.Path;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This class is used to initialize and shutdown GraphDatabaseService during integration test
 *
 * Created by lgonzales on 09/08/16.
 */
public class Neo4JGraphDatabase{

    private static final Logger logger = LoggerFactory.getLogger(Neo4JGraphDatabase.class);

    private GraphDatabaseService neo4jDb;

    public Neo4JGraphDatabase(Path neo4JPath){
        assertThat(neo4JPath, notNullValue());

        neo4jDb = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(neo4JPath.toFile())
                .setConfig(GraphDatabaseSettings.read_only, "true")
                .newGraphDatabase();
        assertThat(neo4jDb, notNullValue());
        logger.info("Neo4JGraphDatabase initialized with success for path: "+neo4JPath);
    }

    /**
     * This method will shutdown batchInsert
     */
    public void shutdown(){
        assertThat(neo4jDb, notNullValue());
        neo4jDb.shutdown();
    }

    public GraphDatabaseService getNeo4jDb(){
        return neo4jDb;
    }

}
