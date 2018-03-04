package Allocator;

import Actors.Operation;
import Actors.Provider;
import Data.AttributeConstraint;
import Misc.Pair;
import Statistics.Metrics.CostMetric;
import Trees.Semantics.Features;
import Trees.Semantics.MetaChoke;
import Trees.Semantics.Policy.PolicyPair;
import Trees.Semantics.Policy.RelationalProfilePolicy.PolicyMovesGenerator;
import Trees.Semantics.TreeNodeSemantics;
import Trees.Semantics.TreeNodeVolatileCostEngine;
import Trees.TreeNode;
import Trees.TreeNodeCostEngine;

import java.util.LinkedList;
import java.util.List;

public class OperationAllocator<T extends Operation> {

    private TreeNode<T> query;
    private TreeNode<T> oracle;

    private Provider p1;
    private Provider p2;
    private Provider p3;

    private PolicyMovesGenerator generator;


    public OperationAllocator() {
        query = null;
        oracle = null;
        p1 = null;
        p2 = null;
        p3 = null;
        generator = new PolicyMovesGenerator();
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
        generator.setP1(p1);
        generator.setP2(p2);
        generator.setP3(p3);
        TreeNodeVolatileCostEngine.setHomeProvider(p1);
        TreeNodeCostEngine.setHome(p1);
        TreeNodeSemantics.setHomeProvider(p1);
        compute(query);
    }

    private void compute(TreeNode<T> tn) {
        for (TreeNode<T> tns : tn.getSons()
                ) {
            //move down to the leafs
            compute(tns);
        }
        //comeback
        List<Pair<List<AttributeConstraint>, Provider>> p3pairs = generator.allowedMoves(p3, tn);
        List<Pair<List<AttributeConstraint>, Provider>> p2pairs = generator.allowedMoves(p2, tn);
        List<Pair<List<AttributeConstraint>, Provider>> p1pairs = generator.decryptionMovesToHome(tn);
        List<Pair<List<AttributeConstraint>, Provider>> bestpair = new LinkedList<>();
        int dimp3p = p3pairs.size();
        int dimp2p = p2pairs.size();
        //best alternatives
        PolicyPair bestp3 = null;
        PolicyPair bestp2 = null;
        PolicyPair bestpExt = null;
        //best alternatives costs
        CostMetric cmbestp3 = null;
        CostMetric cmbestp2 = null;
        CostMetric cmbestp1 = null;
        CostMetric cmbestExt = null;
        CostMetric cmp1 = null;

        if (dimp3p > 0) {
            //chose the best cost alternative pair for provider p3
            cmbestp3 = TreeNodeVolatileCostEngine.simulateEncryptionAndComputeCost(tn, p3pairs.get(0).getFirst(), p3);
            bestpair.add(p3pairs.get(0));
            cmbestExt = cmbestp3;
        }
        if (dimp2p > 0) {
            //chose the best cost alternative pair for provider p2
            cmbestp2 = TreeNodeVolatileCostEngine.simulateEncryptionAndComputeCost(tn, p2pairs.get(0).getFirst(), p2);
            if (dimp3p > 0) {
                if (cmbestp3.Ct > cmbestp2.Ct) {
                    bestpair.remove(0);
                    bestpair.add(p2pairs.get(0));
                    cmbestExt = cmbestp2;
                }
            } else {
                bestpair.add(p2pairs.get(0));
                cmbestExt = cmbestp2;
            }
        }
        cmbestp1 = TreeNodeVolatileCostEngine.simulateEncryptionAndComputeCost(tn, p1pairs.get(0).getFirst(), p1);

        /**
         * Here starts operation assignment
         */
        if (cmbestExt != null) {
            /**
             * Try to assign operation to the cheapest alternative using semantics and the continue backtrack
             * if the total cost threshold principle is not satisfied then launch validation/correction algorithm
             */
            if (cmbestExt.Ct < tn.getOracle().getElement().getCost().Ct) {
                //assign the operation to best external provider and continue
                //1) apply encryption moves
                //2) update output metrics
                //3) re-compute output relation profile
                TreeNodeSemantics.applyEncryptionAndPropagateEffects(tn, bestpair.get(0).getFirst(), bestpair.get(0).getSecond());
                //2) compute new tn's cost and hook it
                TreeNodeCostEngine.computeAndSetCost(tn, bestpair.get(0).getSecond());
                //set new executor
                tn.getElement().setExecutor(bestpair.get(0).getSecond());
                /**
                 * Debug info
                 */
                System.out.print("CASE 1 Allocating of:" + tn.getElement().toString() + "\tto:" + bestpair.get(0).getSecond().selfDescription());
                System.out.println("\t" + tn.getElement().getCost().toString());
            } else if ((cmbestExt.getIncrCost() < cmbestp1.getIncrCost()) && tn.getInfo().hasFeature(Features.EXPCPUDOMCOST)) {
                //assign operation to external provider
                TreeNodeSemantics.applyEncryptionAndPropagateEffects(tn, bestpair.get(0).getFirst(), bestpair.get(0).getSecond());
                TreeNodeCostEngine.computeAndSetCost(tn, bestpair.get(0).getSecond());
                tn.getElement().setExecutor(bestpair.get(0).getSecond());
                /**
                 * Debug info
                 */
                System.out.print("CASE 2 Allocating of:" + tn.getElement().toString() + "\tto:" + bestpair.get(0).getSecond().selfDescription());
                System.out.println("\t" + tn.getElement().getCost().toString());

            } else {
                //assign operation to proprietary provider and launch validation/correction algorithm
                TreeNodeSemantics.applyEncryptionAndPropagateEffects(tn, p1pairs.get(0).getFirst(), p1);
                //2) compute new tn's cost and hook it
                TreeNodeCostEngine.computeAndSetCost(tn, p1);
                //set new executor
                tn.getElement().setExecutor(p1);
                /**
                 * Debug info
                 */
                System.out.print("CASE 3 Allocating of:" + tn.getElement().toString() + "\tto:" + p1.selfDescription());
                System.out.println("\t" + tn.getElement().getCost().toString());
                /**
                 * Validation step
                 */
                //launch validation/correction algorithm
                validateDecision(tn);
                //need to correct synthesized cost (may have been corrections)
                correctCost(tn);
            }
        } else { //implies no execution alternative to provider 1 found
            /**
             * Allocate the operation to proprietary provider removing encryption and launching cost control mechanism
             */
            TreeNodeSemantics.applyEncryptionAndPropagateEffects(tn, p1pairs.get(0).getFirst(), p1);
            //2) compute new tn's cost and hook it
            TreeNodeCostEngine.computeAndSetCost(tn, p1);
            //set new executor
            tn.getElement().setExecutor(p1);
            /**
             * Debug info
             */
            System.out.print("CASE 4 Allocating of:" + tn.getElement().toString() + "\tto:" + p1.selfDescription());
            System.out.println("\t" + tn.getElement().getCost().toString());
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
        System.out.println("\tBefore correction: " + tn.getElement().getCost().toString());
        correctCost(tn);
        /**
         * Debug info
         */
        System.out.println("\tAfter correction: " + tn.getElement().getCost().toString());
    }

    private void correctDecision(TreeNode<T> tn) {
        //compute new cost metric with new fixed assignment
        List<Pair<List<AttributeConstraint>, Provider>> tpair = generator.decryptionMovesToHome(tn);
        TreeNodeSemantics.applyEncryptionAndPropagateEffects(tn, tpair.get(0).getFirst(), p1);
        TreeNodeCostEngine.computeAndSetCost(tn, p1);
        //set new executor
        tn.getElement().setExecutor(p1);
        //validate assignment
        validateDecision(tn);
        /**
         * Debug info
         */
        System.out.print("Correction of:" + tn.getElement().toString() + "\tto:" + p1.selfDescription());
        System.out.println("\t" + tn.getElement().getCost().toString());

    }

    private void correctCost(TreeNode<T> tn) {
        //at this point there may have been multiple subsequent corrections (parent-son), this means that the previous
        //decision correction has to be corrected again, enforcing data distribution and updating decryption cost to zero
        List<Pair<List<AttributeConstraint>, Provider>> tpair = generator.decryptionMovesToHome(tn);
        TreeNodeSemantics.applyEncryptionAndPropagateEffects(tn, tpair.get(0).getFirst(), p1);
        TreeNodeCostEngine.computeAndSetCost(tn, p1);
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
