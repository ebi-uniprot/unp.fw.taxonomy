package uk.ac.ebi.uniprot.taxonomyservice.imports.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * This class is responsible to write an application metric to a file.
 *
 * Created by lgonzales on 10/10/16.
 */
public class PushGatwayMetricWritter {

    private File metricFile;

    public PushGatwayMetricWritter(File metricFile){
        this.metricFile = metricFile;
    }

    public void writeMetric(String name, double value) throws IOException{
        PrometheusPushGatwayMetric
                metric = new PrometheusPushGatwayMetric(name,null,value);
        writeMetric(metric);
    }


    public void writeMetric(String name, double value, String help) throws IOException{
        PrometheusPushGatwayMetric metric = new PrometheusPushGatwayMetric(name,help,value);
        writeMetric(metric);
    }

    public void writeMetrics(List<PushGatwayMetric> metrics) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(metricFile.toPath(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)){
            for(PushGatwayMetric metric : metrics){
                writeMetric(writer, metric);
            }
        }
    }

    public void writeMetric(PushGatwayMetric metric) throws IOException{
        try (BufferedWriter writer = Files.newBufferedWriter(metricFile.toPath(), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)){
            writeMetric(writer, metric);
        }
    }

    private void writeMetric(BufferedWriter writer, PushGatwayMetric metric) throws IOException {
        if(metric.hasHelp()){
            writer.append(metric.getFormattedMetricHelp());
            writer.newLine();
        }
        writer.append(metric.getFormattedMetric());
        writer.newLine();
    }
}
