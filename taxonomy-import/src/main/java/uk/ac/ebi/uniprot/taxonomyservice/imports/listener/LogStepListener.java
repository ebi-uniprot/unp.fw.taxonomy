package uk.ac.ebi.uniprot.taxonomyservice.imports.listener;

import uk.ac.ebi.uniprot.taxonomyservice.imports.util.PrometheusPushGatwayMetric;
import uk.ac.ebi.uniprot.taxonomyservice.imports.util.PushGatwayMetricWritter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * Log statistics of taxonomy import step
 *
 * Created 02/12/15
 * @author Edd
 */
public class LogStepListener implements StepExecutionListener {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(LogStepListener.class);

    @Autowired
    private Environment env;

    @Override public void beforeStep(StepExecution stepExecution) {
        LOGGER.info("Taxonomy import STEP '{}' starting.", stepExecution.getStepName());
    }

    @Override public ExitStatus afterStep(StepExecution stepExecution) {
        LOGGER.info("=====================================================");
        LOGGER.info("              Taxonomy import Step Statistics                 ");
        LOGGER.info("Step name     : {}", stepExecution.getStepName());
        LOGGER.info("Exit status   : {}", stepExecution.getExitStatus().getExitCode());
        LOGGER.info("Read count    : {}", stepExecution.getReadCount());
        LOGGER.info("Write count   : {}", stepExecution.getWriteCount());
        LOGGER.info("Skip count    : {} ({} read / {} processing /{} write)", stepExecution.getSkipCount(),
                stepExecution.getReadSkipCount(), stepExecution.getProcessSkipCount(),
                stepExecution.getWriteSkipCount());
        File metricFile = new File(env.getProperty("taxonomy.batch.metric.file.path"));
        try {
            Map<String,String> labels = new HashMap<>();
            labels.put("stepName",stepExecution.getStepName().toLowerCase());
            PushGatwayMetricWritter metricWritter = new PushGatwayMetricWritter(metricFile);
            if(stepExecution.getExitStatus().getExitCode().equals("COMPLETED")) {
                metricWritter.writeMetric(new PrometheusPushGatwayMetric("taxonomy_service_import_step_status",null,
                        1, labels));
            }else {
                metricWritter.writeMetric(new PrometheusPushGatwayMetric("taxonomy_service_import_step_status",null,
                        0, labels));
            }
            metricWritter.writeMetric(new PrometheusPushGatwayMetric("taxonomy_service_import_step_read_count",null,
                    stepExecution.getReadCount(),labels));
            metricWritter.writeMetric(new PrometheusPushGatwayMetric("taxonomy_service_import_step_write_count",null,
                    stepExecution.getWriteCount(),labels));
            metricWritter.writeMetric(new PrometheusPushGatwayMetric("taxonomy_service_import_step_skip_skipst",null,
                    stepExecution.getSkipCount(),labels));
            metricWritter.writeMetric(new PrometheusPushGatwayMetric("taxonomy_service_import_step_read_skips",null,
                    stepExecution.getReadSkipCount(),labels));
            metricWritter.writeMetric(new PrometheusPushGatwayMetric("taxonomy_service_import_step_processing_skips",
                    null, stepExecution.getProcessSkipCount(),labels));
            metricWritter.writeMetric(new PrometheusPushGatwayMetric("taxonomy_service_import_step_write_skips",null,
                    stepExecution.getWriteSkipCount(),labels));
        }catch (IOException ioe){
            LOGGER.error("Unable to save PushGatway metrics ",ioe);
        }finally {
            LOGGER.info("PushGatwayMetric saved with success in file "+env.getProperty("taxonomy.batch.metric.file.path"));
        }
        LOGGER.info("=====================================================");
        return stepExecution.getExitStatus();
    }
}
