package uk.ac.ebi.uniprot.taxonomyservice.imports.config;

import uk.ac.ebi.uniprot.taxonomyservice.imports.setup.JobTestRunnerConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.ac.ebi.uniprot.taxonomyservice.imports.config.TaxonomyImportConfig.TAXONOMY_LOAD_DELETED_STEP_NAME;
import static uk.ac.ebi.uniprot.taxonomyservice.imports.config.TaxonomyImportConfig.TAXONOMY_LOAD_MERGED_STEP_NAME;
import static uk.ac.ebi.uniprot.taxonomyservice.imports.config.TaxonomyImportConfig.TAXONOMY_LOAD_NODE_STEP_NAME;
import static uk.ac.ebi.uniprot.taxonomyservice.imports.config.TaxonomyImportConfig
        .TAXONOMY_LOAD_RELATIONSHIP_STEP_NAME;

/**
 * This class execute the integration test for taxonomy import and assert it completed with success status
 *
 * Created by lgonzales on 05/08/16.
 */
@ActiveProfiles(profiles = {"embeddedServer"})
@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest
@ContextConfiguration(
        initializers = ConfigFileApplicationContextInitializer.class,
        classes = {FakeTaxonomyImportConfig.class, JobTestRunnerConfig.class},
        loader = SpringApplicationContextLoader.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class TaxonomyImportConfigIT {

    private static final Logger logger = LoggerFactory.getLogger(TaxonomyImportConfigIT.class);

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void successfullJobRun() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        BatchStatus status = jobExecution.getStatus();
        assertThat(status, is(BatchStatus.COMPLETED));

        StepExecution stepExecution = getStepByName(TAXONOMY_LOAD_NODE_STEP_NAME,jobExecution);
        assertStepExecution(stepExecution,22,0,22,0);

        stepExecution = getStepByName(TAXONOMY_LOAD_RELATIONSHIP_STEP_NAME,jobExecution);
        assertStepExecution(stepExecution,22,0,22,0);

        stepExecution = getStepByName(TAXONOMY_LOAD_MERGED_STEP_NAME,jobExecution);
        assertStepExecution(stepExecution,12,0,12,0);

        stepExecution = getStepByName(TAXONOMY_LOAD_DELETED_STEP_NAME,jobExecution);
        assertStepExecution(stepExecution,21,0,21,0);

        logger.info("Completed TaxonomyImportConfigIT integration test");

    }

    private void assertStepExecution(StepExecution stepExecution,int readCount,int readSkipCount, int writeCount,
            int writeSkipCount) {
        assertThat(stepExecution,notNullValue());

        assertThat(stepExecution.getReadCount(), is(readCount));
        assertThat(stepExecution.getReadSkipCount(), is(readSkipCount));
        assertThat(stepExecution.getWriteCount(), is(writeCount));
        assertThat(stepExecution.getWriteSkipCount(), is(writeSkipCount));
    }

    private StepExecution getStepByName(String stepName, JobExecution jobExecution) {
        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            if (stepExecution.getStepName().equals(stepName)) {
                return stepExecution;
            }
        }

        throw new IllegalArgumentException("Step name not recognized: " + stepName);
    }

}
