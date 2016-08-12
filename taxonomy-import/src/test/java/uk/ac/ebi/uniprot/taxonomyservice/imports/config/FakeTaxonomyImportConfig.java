package uk.ac.ebi.uniprot.taxonomyservice.imports.config;

import uk.ac.ebi.uniprot.taxonomyservice.imports.utils.TaxonomyImportTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.sql.DataSource;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.TestPropertySource;

/**
 * This class is responsible to override external resources for Integration test taxonomy full load.
 * Basically it override the Original Oracle database with an in memory database (HSQL) and initial load it with fake
 * information
 * It also make sure that it create Neo4J Database in a temporary folder and that this temporary folder is removed at
 * the end of the test.
 *
 * Created by lgonzales on 05/08/16.
 */
@Configuration
@EnableBatchProcessing
@TestPropertySource(locations = "classpath:application.properties")
public class FakeTaxonomyImportConfig extends TaxonomyImportConfig{

    private static final Logger logger = LoggerFactory.getLogger(FakeTaxonomyImportConfig.class);

    private Path neo4jDatabasePath;

    @Bean
    @Override
    public DataSource getReadDatasource(){
        return new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.HSQL)
                .setScriptEncoding("UTF-8")
                .ignoreFailedDrops(true)
                .addScript("taxonomySchema.sql")
                .addScripts("loadTaxonomyPublicNode.sql",
                            "loadTaxonomyPublicMerged.sql",
                            "loadTaxonomyPublicDeleted.sql")
                .build();
    }

    /**
     * This method create batchInserter in a temporary folder.
     *
     * @return Batch insert created in temporary folder
     * @throws IOException
     */
    @Override
    public BatchInserter getBatchInserter() throws IOException {
        if(batchInserter == null){
            this.batchInserter = createBatchInserter(getNeo4jDatabasePath().toFile());
        }
        return batchInserter;
    }

    protected Path getNeo4jDatabasePath()throws IOException {
        if(neo4jDatabasePath == null){
            neo4jDatabasePath = Files.createTempDirectory("neo4j");
        }
        return neo4jDatabasePath;
    }

    @Override
    public void destroy() throws Exception {
        super.destroy();
        TaxonomyImportTestUtils.cleanNeo4JTemporaryData(getNeo4jDatabasePath());
    }
}