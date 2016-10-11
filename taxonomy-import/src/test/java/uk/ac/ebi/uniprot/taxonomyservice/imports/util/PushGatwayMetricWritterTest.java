package uk.ac.ebi.uniprot.taxonomyservice.imports.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by lgonzales on 11/10/16.
 */
public class PushGatwayMetricWritterTest {

    private File testFile;

    @Before
    public void createFile() throws IOException {
        testFile = File.createTempFile("metric", "Writer");
    }

    @After
    public void deleteFile() throws IOException{
        if(testFile != null) {
            testFile.delete();
        }
    }

    @Test
    public void writeMetricSimpleMetric() throws IOException {
        PushGatwayMetricWritter writer = new PushGatwayMetricWritter(testFile);
        writer.writeMetric("metric.name", 1.2);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(testFile)));){
            assertEquals(br.readLine(),"metric.name 1.2");
        }
    }

    @Test
    public void writeMetricSimpleIntegerValueMetric() throws IOException {
        PushGatwayMetricWritter writer = new PushGatwayMetricWritter(testFile);
        writer.writeMetric("metric.name", 1.0);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(testFile)));){
            assertEquals(br.readLine(),"metric.name 1");
        }
    }


    @Test
    public void writeMetricWithHelpMetric() throws IOException {
        PushGatwayMetricWritter writer = new PushGatwayMetricWritter(testFile);
        writer.writeMetric("metric.name", 1.3, "metric help");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(testFile)));){
            assertEquals(br.readLine(),"# HELP metric.name metric help");
            assertEquals(br.readLine(),"metric.name 1.3");
        }
    }

    @Test
    public void writeMetricWithHelpAndOneLabel() throws IOException {
        PushGatwayMetricWritter writer = new PushGatwayMetricWritter(testFile);
        List<PushGatwayMetric> metrics = new ArrayList<>();
        Map<String,String> labelValue = new HashMap<>();
        labelValue.put("first","firstValue");
        PushGatwayMetric metric = new PrometheusPushGatwayMetric("metric.name", "metric help",1.3,labelValue);
        metrics.add(metric);
        writer.writeMetrics(metrics);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(testFile)));){
            assertEquals(br.readLine(),"# HELP metric.name metric help");
            assertEquals(br.readLine(),"metric.name{first=\"firstValue\"} 1.3");
        }
    }

    @Test
    public void writeMetricWithHelpAndMultipleLabels() throws IOException {
        PushGatwayMetricWritter writer = new PushGatwayMetricWritter(testFile);
        List<PushGatwayMetric> metrics = new ArrayList<>();
        Map<String,String> labelValue = new HashMap<>();
        labelValue.put("first","firstValue");
        labelValue.put("second","secondValue");
        PushGatwayMetric metric = new PrometheusPushGatwayMetric("metric.name", "metric help",1.3,labelValue);
        metrics.add(metric);
        writer.writeMetrics(metrics);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(testFile)));){
            assertEquals(br.readLine(),"# HELP metric.name metric help");
            assertEquals(br.readLine(),"metric.name{first=\"firstValue\",second=\"secondValue\"} 1.3");
        }
    }


}