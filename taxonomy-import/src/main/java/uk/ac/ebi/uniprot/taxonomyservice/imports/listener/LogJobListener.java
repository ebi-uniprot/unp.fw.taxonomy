package uk.ac.ebi.uniprot.taxonomyservice.imports.listener;

import uk.ac.ebi.uniprot.taxonomyservice.imports.util.PushGatwayMetricWritter;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * Log statistics of taxonomy import job.
 *
 * Created 03/12/15
 * @author Edd
 */
public class LogJobListener implements JobExecutionListener {
    // logger
    private static final Logger LOGGER = LoggerFactory.getLogger(LogJobListener.class);

    @Autowired
    private Environment env;

    @Override public void beforeJob(JobExecution jobExecution) {
        LOGGER.info("Taxonomy import starting.");
    }

    @Override public void afterJob(JobExecution jobExecution) {
        LOGGER.info("Taxonomy import complete.\n");

        // compute duration
        Duration.between(jobExecution.getEndTime().toInstant(), jobExecution.getStartTime().toInstant());
        long durationMillis = jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime();
        String duration = String.format("%d hrs, %d min, %d sec",
                TimeUnit.MILLISECONDS.toHours(durationMillis),
                TimeUnit.MILLISECONDS.toMinutes(durationMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                        .toHours(durationMillis)),
                TimeUnit.MILLISECONDS.toSeconds(durationMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                        .toMinutes(durationMillis))
        );

        LOGGER.info("=====================================================");
        LOGGER.info("              Taxonomy import Job Statistics                 ");
        LOGGER.info("Exit status   : {}", jobExecution.getExitStatus().getExitCode());
        LOGGER.info("Start time    : {}", jobExecution.getStartTime());
        LOGGER.info("End time      : {}", jobExecution.getEndTime());
        LOGGER.info("Duration      : {}", duration);
        long skipCount = 0L;
        long readSkips = 0L;
        long writeSkips = 0L;
        long processingSkips = 0L;
        long readCount = 0L;
        long writeCount = 0L;

        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            readSkips += stepExecution.getReadSkipCount();
            writeSkips += stepExecution.getWriteSkipCount();
            processingSkips += stepExecution.getProcessSkipCount();
            readCount += stepExecution.getReadCount();
            writeCount += stepExecution.getWriteCount();
            skipCount += stepExecution.getSkipCount();

        }
        LOGGER.info("Read count    : {}", readCount);
        LOGGER.info("Write count   : {}", writeCount);
        LOGGER.info("Skip count    : {} ({} read / {} processing / {} write)", skipCount, readSkips, processingSkips,
                writeSkips);

        File metricFile = new File(env.getProperty("taxonomy.batch.metric.file.path"));
        try {
            PushGatwayMetricWritter metricWritter = new PushGatwayMetricWritter(metricFile);
            if(jobExecution.getExitStatus().getExitCode().equals("COMPLETED")) {
                metricWritter.writeMetric("taxonomy_service_import_job_status", 1);
            }else {
                metricWritter.writeMetric("taxonomy_service_import_job_status", 0);
            }
            metricWritter.writeMetric("taxonomy_service_import_job_start_time", jobExecution.getStartTime().getTime());
            metricWritter.writeMetric("taxonomy_service_import_job_end_time", jobExecution.getEndTime().getTime());
            metricWritter.writeMetric("taxonomy_service_import_job_duration", durationMillis);
            metricWritter.writeMetric("taxonomy_service_import_job_read_count", readCount);
            metricWritter.writeMetric("taxonomy_service_import_job_write_count", writeCount);
            metricWritter.writeMetric("taxonomy_service_import_job_skip_skipst", skipCount);
            metricWritter.writeMetric("taxonomy_service_import_job_read_skips", readSkips);
            metricWritter.writeMetric("taxonomy_service_import_job_processing_skips", processingSkips);
            metricWritter.writeMetric("taxonomy_service_import_job_write_skips", writeSkips);
        }catch (IOException ioe){
            LOGGER.error("Unable to save PushGatway metrics ",ioe);
        }finally {
            LOGGER.info("PushGatwayMetric saved with success in file "+env.getProperty("taxonomy.batch.metric.file.path"));
        }
        LOGGER.info("=====================================================");
        jobExecution.getExitStatus();
    }
}
