package Trees.Semantics;

import Actors.Operation;
import Actors.Operations.*;
import Actors.Provider;
import Actors.SimpleCostEngine;
import Data.Attribute;
import Data.AttributeConstraint;
import Statistics.Metrics.BasicMetric;
import Statistics.Metrics.CostMetric;
import Statistics.Metrics.UDFMetric;
import Trees.Semantics.Policy.RelationalProfilePolicy.MinimumRequiredView;
import Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;
import Trees.Semantics.SemanticExceptions.RelationalOperationArityIncorrectException;
import Trees.TreeNode;

import java.util.LinkedList;
import java.util.List;

public class TreeNodeSemantics {

    /**
     * TODO for both the two subsequent methods: Do CPU_time and IO_time changes after data expansion/contraction?
     */

    public static <T extends Operation> void deriveCostBarriers(TreeNode<T> tn, Provider p) {
        CostMetric m = SimpleCostEngine.computeExecutionVsMotionCost(tn.getElement(), p);
        if (m.Ce > 10 * m.Cm && !tn.isRoot())
            deriveCostBarriersComplement(tn, Features.EXPCPUDOMCOST);
        else {
            tn.getInfo().addFeature(Features.EXPDATADOMCOST);
            for (TreeNode<T> tns : tn.getSons()
                    ) {
                deriveCostBarriers(tns, p);
            }
        }
    }

    private static <T extends Operation> void deriveCostBarriersComplement(TreeNode<T> tn, Features f) {
        tn.getInfo().addFeature(f);
        for (TreeNode<T> tns : tn.getSons()
                ) {
            deriveCostBarriersComplement(tns, f);
        }

    }

    public static <T extends Operation> void computeUDFProfiles(TreeNode<T> tn) {
        for (TreeNode<T> tns : tn.getSons()
                ) {
            computeUDFProfiles(tns);
        }
        BasicMetric bm = tn.getElement().getOp_metric();
        if (bm instanceof UDFMetric)
            ((UDFMetric) bm).computeMetrics();

    }

    //method for oracle' execution cost (single provider plan)
    public static <T extends Operation> void synthesizeExecutionCost(TreeNode<T> tn) {

        for (TreeNode<T> tns : tn.getSons()
                ) {
            synthesizeExecutionCost(tns);
        }

        //getting the treenode operation
        T op = tn.getElement();
        //getting the executor
        Provider exec = op.getExecutor();

        //metric cost creation
        CostMetric cm = new CostMetric();
        //set the cost to zero
        cm.setAllZero();

        //apply cost formula
        cm.Ce = op.getOp_metric().CPU_time * exec.getMetrics().Kcpu / 3600 + op.getOp_metric().IO_time * exec.getMetrics().Kio / 3600;
        cm.Ct = cm.Ce;

        //synthesizing cost
        if (tn.isLeaf())
            cm.Ct = cm.Ce;
        else
            for (TreeNode<T> tns : tn.getSons()
                    ) {
                cm.Ct += tns.getElement().getCost().Ct;
            }
        //hook the cost
        op.setCost(cm);
    }

    /**
     * This method synthesizes operations' attributes starting from leafs' inputs and applying operation
     * For a non leaf node the whole set of attributes have to be collected and transformed
     * <p>
     * NB Specific for a single provider plan, has to be used only in first query definition after specific operation
     * constraints and sets of particular attributes have been set
     *
     * @param tn
     * @param <T>
     */
    public static <T extends Operation> void synthesizePlanAttributes(TreeNode<T> tn) {
        for (TreeNode<T> tns : tn.getSons()
                ) {
            synthesizePlanAttributes(tns);
        }
        if (tn.isLeaf()) {
            List<RelationProfile> lrp = new LinkedList<>();
            tn.getElement().computeOutRelProf(lrp);
        } else {
            List<Attribute> la = new LinkedList<>();
            //retrieve sons's attributes
            for (TreeNode<T> tns : tn.getSons()
                    ) {
                la = RelationProfile.union(tns.getElement().getOutput_rp().getRvp(), tns.getElement().getOutput_rp().getRve());
            }
            //la is a list deep copied
            tn.getElement().setInputAttributes(la);
            //NB    1) here operation custom sets have already been set
            //      2) at first definition operations' basic metrics have to be inserted manually
            //now compute output relation profile
            TreeNodeSemantics.computeOperationOutputRelProf(tn);

        }
    }

    /**
     * Compute minimum required view over tn's sons' output relation profile
     *
     * @param tn
     * @param <T>
     */
    public static <T extends Operation> void derivePlanMinimumReqView(TreeNode<T> tn) {
        for (TreeNode<T> tns : tn.getSons()
                ) {
            TreeNodeSemantics.derivePlanMinimumReqViewComplement(tns, tn.getElement().getConstraints());
        }
    }

    public static <T extends Operation> void derivePlanMinimumReqViewComplement(TreeNode<T> tn, List<AttributeConstraint> lac) {
        tn.getElement().setMinimumReqView(
                MinimumRequiredView.computeMinimumReqView(tn.getElement().getOutput_rp(), lac)
        );
        for (TreeNode<T> tns : tn.getSons()
                ) {
            TreeNodeSemantics.derivePlanMinimumReqViewComplement(tns, tn.getElement().getConstraints());
        }
    }

    /**
     * Brief explanation: Rebuild BasicMetric information
     * if single attributes are encrypted or decrypted, table dimensions change and
     * have to be reconstructed considering each input table's attribute
     * <p>
     * This method has to update the dimensions of input tables because of encryption made by sons' operations
     * (father operation not allowed to see those attributes in plaintext)
     * <p>
     * NB: even if the change of state of an attribute is made by son operation the information is stored inside father,
     * this helps the allocation algorithm in planning assignments with the less information corruption(levels isolation)
     */

    /**
     * This method has to be used when decryption is applied (a change made by operation itself,
     * the operation has the authorization to do that because of minimum required view respected)
     */
    public static <T extends Operation> void updateOperationOutputMetrics(TreeNode<T> tn) {
        //old metrics and cardinality
        BasicMetric oldm = tn.getElement().getOp_metric();
        double oldCardinality = oldm.getOutNofTuples();
        //new metrics
        double newDimension = 0;
        List<Attribute> outa = RelationProfile.union(tn.getElement().getOutput_rp().getRvp(), tn.getElement().getOutput_rp().getRve());
        for (Attribute a : outa
                ) {
            newDimension += a.getDimension();
        }
        //update old metrics with new values (shadow pointer)
        oldm.setOutputTupleSize(newDimension);
        oldm.setOutputSize(newDimension * oldCardinality);
    }

    public static <T extends Operation> void computeOperationOutputRelProf(TreeNode<T> tn) throws RelationalOperationArityIncorrectException {
        //some relational arity exception checks
        Operation op = tn.getElement();
        if (tn.isLeaf() && (op instanceof JoinNAry || op instanceof CartesianProduct))
            throw new RelationalOperationArityIncorrectException();
        if (!tn.isLeaf()) {
            if (op instanceof JoinNAry || op instanceof CartesianProduct)
                if (tn.getSons().size() < 1)
                    throw new RelationalOperationArityIncorrectException();
            if (op instanceof GroupBy || op instanceof Projection || op instanceof Selection)
                if (tn.getSons().size() != 1)
                    throw new RelationalOperationArityIncorrectException();
        }
        //retrieve relation output profile of sons operation
        List<RelationProfile> lrp = new LinkedList<>();
        for (TreeNode<T> tns : tn.getSons()
                ) {
            lrp.add(RelationProfile.copyRP(tns.getElement().getOutput_rp()));
        }
        tn.getElement().computeOutRelProf(lrp);
    }


}
