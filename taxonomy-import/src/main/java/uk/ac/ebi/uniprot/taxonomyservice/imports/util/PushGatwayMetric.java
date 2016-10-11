package uk.ac.ebi.uniprot.taxonomyservice.imports.util;

import java.util.Map;

/**
 * This class is responsible to represent an application metric.
 *
 * Created by lgonzales on 10/10/16.
 */
public abstract class PushGatwayMetric{

    protected String name;
    protected String help;
    protected double value;
    protected Map<String,String> labelsKeyValue;


    public PushGatwayMetric(String name, String help,double value, Map<String,String> labelsKeyValue){
        this(name, help, value);
        this.labelsKeyValue = labelsKeyValue;
    }

    public PushGatwayMetric(String name, String help, double value){
        this.name = name;
        this.help = help;
        this.value = value;
    }

    public boolean hasHelp(){
        return help != null && !help.isEmpty();
    }

    public abstract String getFormattedMetric();

    public abstract String getFormattedMetricHelp();

}
