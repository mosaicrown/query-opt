package Actors;

import Data.Attribute;
import Statistics.Metrics.ProviderMetric;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Provider implements Serializable {

    //provider descriptor
    private String name;
    //provider cost metrics
    private ProviderMetric metrics;
    //sets of attributes to specify policy visibility
    private List<Attribute> aplains;
    private List<Attribute> aencs;

    public Provider() {
        aplains = new LinkedList<>();
        aencs = new LinkedList<>();
    }

    public Provider(String n, ProviderMetric m) {
        name = n;
        metrics = m;
    }


    public String selfDescription() {
        return name;
    }

    public Provider identity() {
        return this;
    }

    public ProviderMetric getMetrics() {
        return metrics;
    }

    public List<Attribute> getAplains() {
        return aplains;
    }

    public void setAplains(List<Attribute> aplains) {
        this.aplains = aplains;
    }

    public List<Attribute> getAencs() {
        return aencs;
    }

    public void setAencs(List<Attribute> aencs) {
        this.aencs = aencs;
    }
}
