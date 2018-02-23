package Actors;

import Statistics.Metrics.CostMetric;

import java.io.Serializable;

public class EncOperation implements Serializable {

    protected CostMetric cmetric;
    protected Provider executor;

    public EncOperation(){
        cmetric = new CostMetric();
        cmetric.setAllZero();
        executor = null;
    }

    public EncOperation(Provider p) {
        executor = p;
        cmetric = new CostMetric();
        cmetric.setAllZero();
    }

    public Provider getExecutor() {
        return executor;
    }

    public void setExecutor(Provider executor) {
        this.executor = executor;
    }

    public void applyOp(){};
}
