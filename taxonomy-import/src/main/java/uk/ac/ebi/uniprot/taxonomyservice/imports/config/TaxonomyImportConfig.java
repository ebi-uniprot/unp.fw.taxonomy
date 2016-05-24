package uk.ac.ebi.uniprot.taxonomyservice.imports.config;

import uk.ac.ebi.uniprot.taxonomyservice.imports.listener.LogJobListener;
import uk.ac.ebi.uniprot.taxonomyservice.imports.listener.LogStepListener;
import uk.ac.ebi.uniprot.taxonomyservice.imports.mapper.TaxonomyDeletedItemReaderMapper;
import uk.ac.ebi.uniprot.taxonomyservice.imports.mapper.TaxonomyMergeItemReaderMapper;
import uk.ac.ebi.uniprot.taxonomyservice.imports.mapper.TaxonomyNodeItemReaderMapper;
import uk.ac.ebi.uniprot.taxonomyservice.imports.model.TaxonomyImportDelete;
import uk.ac.ebi.uniprot.taxonomyservice.imports.model.TaxonomyImportMerge;
import uk.ac.ebi.uniprot.taxonomyservice.imports.model.TaxonomyImportNode;
import uk.ac.ebi.uniprot.taxonomyservice.imports.writer.Neo4JDeletedItemWriterWithBatchInserter;
import uk.ac.ebi.uniprot.taxonomyservice.imports.writer.Neo4JMergedItemWriterWithBatchInserter;
import uk.ac.ebi.uniprot.taxonomyservice.imports.writer.Neo4JNodeItemWriterWithBatchInserter;
import uk.ac.ebi.uniprot.taxonomyservice.imports.writer.Neo4JRelationshipItemWriterWithBatchInserter;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * This class is responsible to set up taxonomy initial full load.
 * Basically it loads taxonomy in 4 steps
 * step 1: Load nodes
 * step 2: Load Parent Relationship
 * step 3: Load Merged Relationship
 * step 4: Load Deleted nodes
 *
 * Created by lgonzales on 25/04/16.
 */
@Configuration
@EnableBatchProcessing
@PropertySource(value = "classpath:/application.properties")
public class TaxonomyImportConfig {

    private static final Logger logger = LoggerFactory.getLogger(TaxonomyImportConfig.class);

    private static final String TAXONOMY_LOAD_JOB_NAME = "TAXONOMY_LOAD_JOB_NAME";
    private static final String TAXONOMY_LOAD_NODE_STEP_NAME = "TAXONOMY_LOAD_NODE";
    private static final String TAXONOMY_LOAD_RELATIONSHIP_STEP_NAME = "TAXONOMY_LOAD_RELATIONSHIP";
    private static final String TAXONOMY_LOAD_MERGED_STEP_NAME = "TAXONOMY_LOAD_MERGED";
    private static final String TAXONOMY_LOAD_DELETED_STEP_NAME = "TAXONOMY_LOAD_DELETED";

    private static final int CHUNK_SIZE = 100000;

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Autowired
    private Environment env;

    private BatchInserter batchInserter;

    private DataSource readDatasource;

    @BeforeJob
    public void initializeJobExternalResources() throws IOException, SQLException {

    }

    @AfterJob
    public void shutdownInserter(){
        batchInserter.shutdown();
        logger.info("shutdown Neo4J batchInserter");
    }

    @Bean
    public Job importTaxonomyNodeJob() throws SQLException,IOException {
        return jobBuilders.get(TAXONOMY_LOAD_JOB_NAME)
                .flow(importTaxonomyNodeStep())
                .next(importTaxonomyRelationShipStep())
                .next(importTaxonomyMergedStep())
                .next(importTaxonomyDeletedStep())
                .end()
                .listener(logJobListener())
                .build();
    }

    @Bean
    public JobExecutionListener logJobListener() {
        return new LogJobListener();
    }

    @Bean
    public LogStepListener logStepListener() {
        return new LogStepListener();
    }

    @Bean
    public Step importTaxonomyNodeStep() throws SQLException,IOException {
        return stepBuilders.get(TAXONOMY_LOAD_NODE_STEP_NAME)
                .<TaxonomyImportNode,TaxonomyImportNode>chunk(CHUNK_SIZE)
                .<TaxonomyImportNode>reader(itemNodeReader())
                .<TaxonomyImportNode>writer(itemWriter())
                .listener(logStepListener())
                .build();
    }

    @Bean
    public Step importTaxonomyRelationShipStep() throws SQLException,IOException {
        return stepBuilders.get(TAXONOMY_LOAD_RELATIONSHIP_STEP_NAME)
                .<TaxonomyImportNode,TaxonomyImportNode>chunk(CHUNK_SIZE)
                .<TaxonomyImportNode>reader(itemNodeReader())
                .<TaxonomyImportNode>writer(itemRelationshipWriter())
                .listener(logStepListener())
                .build();
    }

    @Bean
    public Step importTaxonomyMergedStep() throws SQLException,IOException {
        return stepBuilders.get(TAXONOMY_LOAD_MERGED_STEP_NAME)
                .<TaxonomyImportMerge,TaxonomyImportMerge>chunk(CHUNK_SIZE)
                .<TaxonomyImportMerge>reader(itemMergedReader())
                .<TaxonomyImportMerge>writer(itemMergedWriter())
                .listener(logStepListener())
                .build();
    }

    @Bean
    public Step importTaxonomyDeletedStep() throws SQLException,IOException {
        return stepBuilders.get(TAXONOMY_LOAD_DELETED_STEP_NAME)
                .<TaxonomyImportDelete,TaxonomyImportDelete>chunk(CHUNK_SIZE)
                .<TaxonomyImportDelete>reader(itemDeletedReader())
                .<TaxonomyImportDelete>writer(itemDeletedWriter())
                .listener(logStepListener())
                .build();
    }

    @Bean
    public ItemReader<TaxonomyImportNode> itemNodeReader() throws SQLException {
        JdbcCursorItemReader itemReader = new JdbcCursorItemReader();
        itemReader.setDataSource(getReadDatasource());
        itemReader.setSql("select tax_id,parent_id,hidden,internal,rank,gc_id,mgc_id,ncbi_scientific,ncbi_common," +
                "sptr_scientific,sptr_common,sptr_synonym,sptr_code,tax_code,sptr_ff from taxonomy.v_public_node");
        itemReader.setRowMapper(new TaxonomyNodeItemReaderMapper());

        return itemReader;
    }

    @Bean
    public ItemReader<TaxonomyImportMerge> itemMergedReader() throws SQLException {
        JdbcCursorItemReader itemReader = new JdbcCursorItemReader();
        itemReader.setDataSource(getReadDatasource());
        itemReader.setSql("select old_tax_id,new_tax_id from taxonomy.v_public_merged");
        itemReader.setRowMapper(new TaxonomyMergeItemReaderMapper());

        return itemReader;
    }

    @Bean
    public ItemReader<TaxonomyImportDelete> itemDeletedReader() throws SQLException {
        JdbcCursorItemReader itemReader = new JdbcCursorItemReader();
        itemReader.setDataSource(getReadDatasource());
        itemReader.setSql("select tax_id, sysdate created from taxonomy.v_public_deleted");
        itemReader.setRowMapper(new TaxonomyDeletedItemReaderMapper());

        return itemReader;
    }

    @Bean
    public ItemWriter<TaxonomyImportNode> itemWriter()throws IOException {
        return new Neo4JNodeItemWriterWithBatchInserter(getBatchInserter());
    }

    @Bean
    public ItemWriter<TaxonomyImportNode> itemRelationshipWriter()throws IOException {
        return new Neo4JRelationshipItemWriterWithBatchInserter(getBatchInserter());
    }

    @Bean
    public ItemWriter<TaxonomyImportMerge> itemMergedWriter() throws IOException {
        return new Neo4JMergedItemWriterWithBatchInserter(getBatchInserter());
    }

    @Bean
    public ItemWriter<TaxonomyImportDelete> itemDeletedWriter() throws IOException {
        return new Neo4JDeletedItemWriterWithBatchInserter(getBatchInserter());
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean
    public JobRepository jobRepository() throws Exception {
        return new MapJobRepositoryFactoryBean(transactionManager()).getObject();
    }


    @Bean
    public JobLauncher jobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository());
        return jobLauncher;
    }

    public DataSource getReadDatasource() throws SQLException {
        if(this.readDatasource == null){
            String databaseURL= env.getProperty("taxonomy.database.url");
            String databaseUserName= env.getProperty("taxonomy.database.user.name");
            String databasePassword= env.getProperty("taxonomy.database.password");
            String databaseDriverClassName= env.getProperty("taxonomy.database.driver.class.name");
            DriverManagerDataSource ds = new DriverManagerDataSource(databaseURL, databaseUserName, databasePassword);
            ds.setDriverClassName(databaseDriverClassName);
            this.readDatasource = ds;
            logger.info("TaxonomyDataSource initialized for "+databaseURL);
        }
        return this.readDatasource;

    }

    public BatchInserter getBatchInserter() throws IOException {
        if((batchInserter == null)){
            File neo4jDatabasePath = new File(env.getProperty("neo4j.database.path"));
            batchInserter = BatchInserters.inserter(neo4jDatabasePath);

            Label nodeLabel = DynamicLabel.label( "Node" );
            batchInserter.createDeferredSchemaIndex( nodeLabel ).on( "taxonomyId" ).create();

            logger.info("Neo4J batchInserter initialized");
        }
        return batchInserter;
    }
}
