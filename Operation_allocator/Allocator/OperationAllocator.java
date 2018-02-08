package Allocator;

import Actors.Operation;
import Actors.Provider;
import Statistics.Metrics.CostMetric;
import Trees.Semantics.Policy.PolicyChecker;
import Trees.Semantics.Policy.PolicyPair;
import Trees.TreeNode;
import Trees.TreeNodeCostEngine;

import java.util.List;

public class OperationAllocator<T extends Operation> {

    private TreeNode<T> query;
    private TreeNode<T> oracle;

    private Provider p1;
    private Provider p2;
    private Provider p3;

    private PolicyChecker validator;


    public OperationAllocator() {
        query = null;
        oracle = null;
        p1 = null;
        p2 = null;
        p3 = null;
        validator = new PolicyChecker();
    }

    public TreeNode<T> getQuery() {
        return query;
    }

    public void setQuery(TreeNode<T> query) {
        this.query = query;
    }

    public TreeNode<T> getOracle() {
        return oracle;
    }

    public void setOracle(TreeNode<T> oracle) {
        this.oracle = oracle;
    }

    public void computeAllocation() {
        validator.setP1(p1);
        validator.setP2(p2);
        validator.setP3(p3);
        compute(query);
    }

    private void compute(TreeNode<T> tn){
        for (TreeNode<T> tns:query.getSons()
                ) {
            //move down to the leafs
            compute(tns);
        }
        //comeback
        List<PolicyPair> p3pairs = validator.allowedTwistP3(tn.getElement().getPolicy());
        List<PolicyPair> p2pairs = validator.allowedTwistP2(tn.getElement().getPolicy());
        int dimp3p = p3pairs.size();
        int dimp2p = p2pairs.size();
        //best alternatives
        PolicyPair bestp3 = null;
        PolicyPair bestp2 = null;
        //best alternatives costs
        CostMetric cmbestp3 = null;
        CostMetric cmbestp2 = null;

        if(dimp2p>0){
            //chose the best cost alternative pair for provider p2
            if(dimp3p>0){
                //chose the best cost alternative pair for provider p3

                //select the best alternative between provider 2 nd 3
            }
        }
        else{
            //allocate the operation to proprietary provider and launch cost control mechanism
        }

        //try to assign operation to the cheapest alternative using semantics and the continue backtrack
        //if the total cost threshold principle is not satisfied then launch validation/correction algorithm

    }

    public Provider getP1() {
        return p1;
    }

    public void setP1(Provider p1) {
        this.p1 = p1;
    }

    public Provider getP2() {
        return p2;
    }

    public void setP2(Provider p2) {
        this.p2 = p2;
    }

    public Provider getP3() {
        return p3;
    }

    public void setP3(Provider p3) {
        this.p3 = p3;
    }
}
