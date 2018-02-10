package Allocator;

import Actors.Operation;
import Actors.Provider;
import Statistics.Metrics.CostMetric;
import Trees.Semantics.Features;
import Trees.Semantics.MetaChoke;
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

    private void compute(TreeNode<T> tn) {
        for (TreeNode<T> tns : tn.getSons()
                ) {
            //move down to the leafs
            compute(tns);
        }
        //comeback
        List<PolicyPair> p3pairs = validator.allowedTwistP3(tn.getElement().getPolicy(), tn);
        List<PolicyPair> p2pairs = validator.allowedTwistP2(tn.getElement().getPolicy(), tn);
        int dimp3p = p3pairs.size();
        int dimp2p = p2pairs.size();
        //best alternatives
        PolicyPair bestp3 = null;
        PolicyPair bestp2 = null;
        PolicyPair bestpExt = null;
        //best alternatives costs
        CostMetric cmbestp3 = null;
        CostMetric cmbestp2 = null;
        CostMetric cmbestExt = null;
        CostMetric cmp1 = null;

        if (dimp2p > 0) {
            //chose the best cost alternative pair for provider p2
            int ii = 0;
            CostMetric temp2 = null;
            cmbestp2 = TreeNodeCostEngine.computeCost(tn, p2pairs.get(0).provider, p2pairs.get(0).feature);
            bestp2 = p2pairs.get(0);
            while (p2pairs.size() - ii > 0) {
                temp2 = TreeNodeCostEngine.computeCost(tn, p2pairs.get(ii).provider, p2pairs.get(ii).feature);
                if (temp2.Ct < cmbestp2.Ct) {
                    cmbestp2 = temp2;
                    bestp2 = p2pairs.get(ii);
                }
                ii++;
            }
            if (dimp3p > 0) {
                //chose the best cost alternative pair for provider p3
                int jj = 0;
                CostMetric temp3 = null;
                cmbestp3 = TreeNodeCostEngine.computeCost(tn, p3pairs.get(0).provider, p3pairs.get(0).feature);
                bestp3 = p3pairs.get(0);
                while (p3pairs.size() - jj > 0) {
                    temp3 = TreeNodeCostEngine.computeCost(tn, p3pairs.get(jj).provider, p3pairs.get(jj).feature);
                    if (temp3.Ct < cmbestp2.Ct) {
                        cmbestp3 = temp3;
                        bestp3 = p3pairs.get(jj);
                    }
                    jj++;
                }
                //select the best alternative between provider 2 nd 3 (case exists alternative on both providers)
                if (cmbestp2.Ct < cmbestp3.Ct) {
                    cmbestExt = cmbestp2;
                    bestpExt = bestp2;
                } else {
                    cmbestExt = cmbestp3;
                    bestpExt = bestp3;
                }
            } else {//case no alternative on provider 3
                cmbestExt = cmbestp2;
                bestpExt = bestp2;
            }
        }//end dimp2p > 0
        else {
            //implies cmbestExt equal to null
            cmp1 = TreeNodeCostEngine.computeCost(tn, p1, Features.NOTENCRYPTED);
        }
        /**
         * Here starts operation assignment
         */
        if (cmp1 == null) {
            cmp1 = TreeNodeCostEngine.computeCost(tn, p1, Features.NOTENCRYPTED);
            /**
             * Try to assign operation to the cheapest alternative using semantics and the continue backtrack
             * if the total cost threshold principle is not satisfied then launch validation/correction algorithm
             */
            if (cmbestExt.Ct < tn.getOracle().getElement().getCost().Ct) {
                //assign the operation to best external provider and continue
                //set new cost metric
                tn.getElement().setCost(cmbestExt);
                //delete old data feature if exists
                tn.getInfo().removeFeature(Features.ENCRYPTEDSYM);
                tn.getInfo().removeFeature(Features.ENCRYPTEDHOM);
                tn.getInfo().removeFeature(Features.NOTENCRYPTED);
                //set new data feature
                tn.getInfo().addFeature(bestpExt.feature);
                //set new executor
                tn.getElement().setExecutor(bestpExt.provider);
                /**
                 * Debug info
                 */
                System.out.print("CASE 1 Allocating of:"+tn.getElement().toString()+"\tto:"+bestpExt.provider.selfDescription());
                System.out.println("\t"+tn.getElement().getCost().toString());
            } else if ((cmbestExt.getIncrCost() < cmp1.getIncrCost()) && tn.getInfo().hasFeature(Features.EXPCPUDOMCOST)) {
                //assign operation to external provider
                //set new cost metric
                tn.getElement().setCost(cmbestExt);
                //delete old data feature if exists
                tn.getInfo().removeFeature(Features.ENCRYPTEDSYM);
                tn.getInfo().removeFeature(Features.ENCRYPTEDHOM);
                tn.getInfo().removeFeature(Features.NOTENCRYPTED);
                //set new data feature
                tn.getInfo().addFeature(bestpExt.feature);
                //set new executor
                tn.getElement().setExecutor(bestpExt.provider);
                /**
                 * Debug info
                 */
                System.out.print("CASE 2 Allocating of:"+tn.getElement().toString()+"\tto:"+bestpExt.provider.selfDescription());
                System.out.println("\t"+tn.getElement().getCost().toString());

            } else {
                //assign operation to proprietary provider and launch validation/correction algorithm
                //set new cost metric
                tn.getElement().setCost(cmp1);
                //delete old data feature if exists
                tn.getInfo().removeFeature(Features.ENCRYPTEDSYM);
                tn.getInfo().removeFeature(Features.ENCRYPTEDHOM);
                //set new data feature
                tn.getInfo().addFeature(Features.NOTENCRYPTED);
                //set new executor
                tn.getElement().setExecutor(p1);
                /**
                 * Validation step
                 */
                /**
                 * Debug info
                 */
                System.out.print("CASE 3 Allocating of:"+tn.getElement().toString()+"\tto:"+p1.selfDescription());
                System.out.println("\t"+tn.getElement().getCost().toString());
                //launch validation/correction algorithm
                validateDecision(tn);
                //need to correct synthesized cost (may have been corrections)
                correctCost(tn);
            }
        } else { //implies no execution alternative to provider 1 found
            /**
             * Allocate the operation to proprietary provider removing encryption and launching cost control mechanism
             */
            //set new cost metric
            tn.getElement().setCost(cmp1);
            //delete old data feature if exists
            tn.getInfo().removeFeature(Features.ENCRYPTEDSYM);
            tn.getInfo().removeFeature(Features.ENCRYPTEDHOM);
            //set new data feature
            tn.getInfo().addFeature(Features.NOTENCRYPTED);
            //set new executor
            tn.getElement().setExecutor(p1);
            /**
             * Debug info
             */
            System.out.print("CASE 4 Allocating of:"+tn.getElement().toString()+"\tto:"+p1.selfDescription());
            System.out.println("\t"+tn.getElement().getCost().toString());
            /**
             * Check the total incremental cost and if it is greater than oracle cost launch validation/correction algorithm
             */
            if (tn.getElement().getCost().Ct > tn.getOracle().getElement().getCost().Ct) {
                /**
                 * Validation step
                 */
                //launch validation/correction algorithm, else continue
                validateDecision(tn);
                //need to correct synthesized cost (may have been corrections)
                correctCost(tn);
            }
        }

    }

    private void validateDecision(TreeNode<T> tn) {
        /**
         * TODO
         * When the validation/correction algorithm is thrown on a TreeNode tn only his offspring is validated
         * tn is forced to provider 1 without check of alternatives
         */
        for (TreeNode<T> tns : tn.getSons()
                ) {
            if (!TreeNodeCostEngine.validateCost(tns)) {
                //launch correction function
                correctDecision(tns);
            }
        }
        //at this point there may have been a correction, need to update tree synthesized cost
        /**
         * Debug info
         */
        System.out.println("\tBefore correction: "+tn.getElement().getCost().toString());
        correctCost(tn);
        /**
         * Debug info
         */
        System.out.println("\tAfter correction: "+tn.getElement().getCost().toString());
    }

    private void correctDecision(TreeNode<T> tn) {
        //compute new cost metric with new fixed assignment
        CostMetric cmp1 = TreeNodeCostEngine.computeCost(tn, p1, Features.NOTENCRYPTED);
        //assign operation
        tn.getElement().setCost(cmp1);
        //delete old data feature if exists
        tn.getInfo().removeFeature(Features.ENCRYPTEDSYM);
        tn.getInfo().removeFeature(Features.ENCRYPTEDHOM);
        //set new data feature
        tn.getInfo().addFeature(Features.NOTENCRYPTED);
        //set new executor
        tn.getElement().setExecutor(p1);
        //validate assignment
        validateDecision(tn);
        /**
         * Debug info
         */
        System.out.print("Correction of:"+tn.getElement().toString()+"\tto:"+p1.selfDescription());
        System.out.println("\t"+tn.getElement().getCost().toString());

    }

    private void correctCost(TreeNode<T> tn) {
        Features f = null;
        MetaChoke mt = tn.getInfo();
        if (mt.hasFeature(Features.ENCRYPTEDSYM))
            f = Features.ENCRYPTEDSYM;
        else if (mt.hasFeature(Features.ENCRYPTEDHOM))
            f = Features.ENCRYPTEDHOM;
        else
            f = Features.NOTENCRYPTED;
        CostMetric cm = TreeNodeCostEngine.computeCost(tn, tn.getElement().getExecutor(), f);
        tn.getElement().setCost(cm);
    }

    public Provider getP1() {
        return p1;
    }

    public void setP1(Provider p1) {
        TreeNodeCostEngine.setHome(p1);
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
