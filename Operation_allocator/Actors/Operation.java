package Actors;

import Data.Attribute;
import Data.AttributeConstraint;
import Statistics.Metrics.BasicMetric;
import Statistics.Metrics.CostMetric;
import Trees.Semantics.Policy.Policy;
import Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Operation implements Serializable {

    /**
     * TODO find a solution to prevent corruption of information caused by modification of attribute' shadow copies
     * precisely to toString, outputRelationProfile and cost update after corrections
     */
    private String name;

    protected CostMetric cost;
    protected BasicMetric op_metric;
    protected Provider executor;

    protected RelationProfile output_rp;
    protected RelationProfile minimumReqView;

    protected List<Attribute> inputAttributes;
    protected List<AttributeConstraint> constraints;

    protected EncOperation postEncryption;
    protected EncOperation preDecryption;


    public Operation() {
        output_rp = new RelationProfile();
        minimumReqView = new RelationProfile();
        constraints = new LinkedList<>();
        postEncryption = new Encryption();
        preDecryption = new Decryption();
    }

    /**
     * Method to override
     */
    public void computeOutRelProf() {
    }

    ;

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

    public EncOperation getPostEncryption() {
        return postEncryption;
    }

    public void setPostEncryption(EncOperation postEncryption) {
        this.postEncryption = postEncryption;
    }

    public EncOperation getPreDecryption() {
        return preDecryption;
    }

    public void setPreDecryption(EncOperation preDecryption) {
        this.preDecryption = preDecryption;
    }

    public RelationProfile getMinimumReqView() {
        return minimumReqView;
    }

    public void setMinimumReqView(RelationProfile minimumReqView) {
        this.minimumReqView = minimumReqView;
    }

    public List<Attribute> getInputAttributes() {
        return inputAttributes;
    }

    public void setInputAttributes(List<Attribute> inputAttributes) {
        this.inputAttributes = inputAttributes;
    }

    public void addInputAttributes(List<Attribute> li) {
        inputAttributes = RelationProfile.union(inputAttributes, li);
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
