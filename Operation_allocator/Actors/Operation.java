package Actors;

import Data.AttributeConstraint;
import Statistics.Metrics.BasicMetric;
import Statistics.Metrics.CostMetric;
import Trees.Semantics.Policy.Policy;
import Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Operation implements Serializable {

    private String name;

    private CostMetric cost;
    private BasicMetric op_metric;
    private Provider executor;

    private RelationProfile input_rp;
    private RelationProfile output_rp;
    private RelationProfile minimumReqView;

    private List<AttributeConstraint> constraints;

    private Policy policy;

    public Operation() {
        policy = new Policy();
        input_rp = new RelationProfile();
        output_rp = new RelationProfile();
        minimumReqView = new RelationProfile();
        constraints = new LinkedList<>();
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public Operation(String n) {
        name = n;
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

    public List<AttributeConstraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<AttributeConstraint> constraints) {
        this.constraints = constraints;
    }

    public void addConstraint(AttributeConstraint c) {
        constraints.add(c);
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

    public RelationProfile getInput_rp() {
        return input_rp;
    }

    public void setInput_rp(RelationProfile input_rp) {
        this.input_rp = input_rp;
    }

    public RelationProfile getOutput_rp() {
        return output_rp;
    }

    public void setOutput_rp(RelationProfile output_rp) {
        this.output_rp = output_rp;
    }

    public RelationProfile getMinimunReqView() {
        return minimumReqView;
    }

    public void setMinimunReqView(RelationProfile minimunReqView) {
        this.minimumReqView = minimunReqView;
    }

    public Operation deepClone() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Operation) ois.readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
