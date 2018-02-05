package Actors;

import Statistics.Metrics.ProviderMetric;

public class Provider {

    private String name;

    private ProviderMetric metrics;

    public Provider() {

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


}
