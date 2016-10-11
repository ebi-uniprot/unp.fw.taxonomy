package uk.ac.ebi.uniprot.taxonomyservice.imports.util;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by lgonzales on 11/10/16.
 */
public class PrometheusPushGatwayMetricTest {

    @Test
    public void getFormattedMetricReturnFormattedMetric(){
        Map<String,String> labelValue = new HashMap<>();
        labelValue.put("label","value");
        PrometheusPushGatwayMetric metric = new PrometheusPushGatwayMetric("metric.name","metric help",1.2,labelValue);
        String formattedMetric = metric.getFormattedMetric();

        assertEquals(formattedMetric, "metric.name{label=\"value\"} 1.2");
    }

    @Test
    public void getFormattedMetricTwoLabelsReturnFormattedMetric(){
        Map<String,String> labelValue = new HashMap<>();
        labelValue.put("first","firstValue");
        labelValue.put("second","secondValue");
        PrometheusPushGatwayMetric metric = new PrometheusPushGatwayMetric("metric.name","metric help",1,labelValue);
        String formattedMetric = metric.getFormattedMetric();

        assertEquals(formattedMetric, "metric.name{first=\"firstValue\",second=\"secondValue\"} 1");
    }

    @Test
    public void getFormattedMetricWithoutLabelsReturnFormattedMetric(){
        PrometheusPushGatwayMetric metric = new PrometheusPushGatwayMetric("metric.name","metric help",1.2,null);
        String formattedMetric = metric.getFormattedMetric();

        assertEquals(formattedMetric, "metric.name 1.2");
    }

    @Test
    public void getFormattedMetricHelpWithoutHelpReturnFormattedMetric(){
        PrometheusPushGatwayMetric metric = new PrometheusPushGatwayMetric("metric.name","metric help",1.2,null);

        String formattedHelp = metric.getFormattedMetricHelp();

        assertEquals(formattedHelp, "# HELP metric.name metric help");
    }

    @Test
    public void getFormattedMetricHelpReturnFormattedMetric(){
        PrometheusPushGatwayMetric metric = new PrometheusPushGatwayMetric("metric.name",null,1.2,null);

        String formattedHelp = metric.getFormattedMetricHelp();

        assertEquals(formattedHelp, null);
    }

}