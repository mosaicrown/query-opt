package Actors;

import Data.Attribute;
import Data.AttributeConstraint;
import Statistics.Metrics.BasicMetric;
import Statistics.Metrics.CostMetric;
import Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Operation implements Serializable {

    //operation descriptor
    protected String name;
    //operation family
    protected String familyname;
    //basic statistics
    protected CostMetric cost;
    protected BasicMetric op_metric;
    protected Provider executor;
    //security policy artifacts
    protected RelationProfile output_rp;
    protected RelationProfile minimumReqView;
    //additional policy specifiers
    protected List<Attribute> inputAttributes;
    protected List<AttributeConstraint> constraints;    //this in particular specifies the eventual encryption type

    public Operation(String n) {

        output_rp = new RelationProfile();
        minimumReqView = new RelationProfile();

        inputAttributes = new LinkedList<>();
        constraints = new LinkedList<>();

        name = n;
        familyname = "Generic op";
    }

    public Operation() {
        output_rp = new RelationProfile();
        minimumReqView = new RelationProfile();

        inputAttributes = new LinkedList<>();
        constraints = new LinkedList<>();

        name = "No op name";
        familyname = "Generic op";
    }

    /**
     * Method to override to produce output relation profile
     * typical of particular operation
     */
    public void computeOutRelProf(List<RelationProfile> lrp) {
    }

    public List<Attribute> getHomogeneousSet(){
        return new LinkedList<>();
    }

    public String toString() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamilyname() {
        return familyname;
    }

    public void setFamilyname(String familyname) {
        this.familyname = familyname;
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
