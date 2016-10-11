package uk.ac.ebi.uniprot.taxonomyservice.imports.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * This class is responsible to represent a Prometheus metric and it expose it in a formatted way to push it to
 * prometheus
 *
 * Created by lgonzales on 10/10/16.
 */
public class PrometheusPushGatwayMetric extends PushGatwayMetric {

    public PrometheusPushGatwayMetric(String name, String help,double value) {
        super(name, help, value);
    }

    public PrometheusPushGatwayMetric(String name, String help,double value, Map<String,String> labelsKeyValue) {
        super(name, help, value,labelsKeyValue);
    }

    public String getFormattedMetric(){
        StringBuilder response = new StringBuilder();
        response.append(name);
        if(labelsKeyValue != null && !labelsKeyValue.isEmpty()) {
            response.append("{");
            for (Map.Entry<String,String> label: labelsKeyValue.entrySet()) {
                response.append(String.format("%s=\"%s\",", label.getKey(), escapeLabelValue(String.valueOf(label
                        .getValue()))));
            }
            response.setLength(response.length() - 1);
            response.append("}");
        }
        response.append(" ");
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.DOWN);
        response.append(df.format(value));
        return response.toString();
    }

    public String getFormattedMetricHelp(){
        if (help != null) {
            StringBuilder response = new StringBuilder();
            response.append("# HELP ").append(name).append(" ").append(escapeHelp(help));
            return response.toString();
        }
        return null;
    }

    private String escapeHelp(String s) {
        return s.replace("\\", "\\\\").replace("\n", "\\n");
    }

    private String escapeLabelValue(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

}
