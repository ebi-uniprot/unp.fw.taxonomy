package uk.ac.ebi.uniprot.taxonomyservice.imports.setup;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created 12/01/16
 * @author Edd
 */
@Configuration
@EnableBatchProcessing
public class JobTestRunnerConfig {
    @Bean
    public JobLauncherTestUtils utils() throws Exception {
        return new JobLauncherTestUtils();
    }
}

