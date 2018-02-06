package Actors;

import Statistics.Metrics.BasicMetric;
import Statistics.Metrics.CostMetric;

import java.io.Serializable;

public class Operation implements Serializable{

    private BasicMetric op_metric;
    private Provider executor;
    private CostMetric cost;

    private String name;

    public Operation (){

    }

    public Operation(String n){
        name=n;
    }

    public String toString() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOp_metric(BasicMetric op_metric) {
        this.op_metric = op_metric;
    }

    public void setExecutor(Provider executor) {
        this.executor = executor;
    }

    public void setCost(CostMetric cost) {
        this.cost = cost;
    }

    public BasicMetric getOp_metric() {
        return op_metric;
    }

    public Provider getExecutor() {
        return executor;
    }

    public CostMetric getCost() {
        return cost;
    }
}