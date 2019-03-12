package Operation_allocator.Trees.Semantics;

import Operation_allocator.Actors.Operation;
import Operation_allocator.Actors.Operations.*;
import Operation_allocator.Actors.Operations.SetOperations.Intersection;
import Operation_allocator.Actors.Operations.SetOperations.Subtraction;
import Operation_allocator.Actors.Operations.SetOperations.Union;
import Operation_allocator.Actors.Provider;
import Operation_allocator.Actors.SimpleCostEngine;
import Operation_allocator.Data.Attribute;
import Operation_allocator.Data.AttributeConstraint;
import Operation_allocator.DebugManager.Debugger;
import Operation_allocator.DebugManager.LogType;
import Operation_allocator.DebugManager.Report;
import Operation_allocator.Misc.Triple;
import Operation_allocator.Statistics.Metrics.BasicMetric;
import Operation_allocator.Statistics.Metrics.BasicMetricUpdater;
import Operation_allocator.Statistics.Metrics.CostMetric;
import Operation_allocator.Statistics.Metrics.UDFMetric;
import Operation_allocator.Trees.Semantics.Policy.RelationalProfilePolicy.MinimumRequiredView;
import Operation_allocator.Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;
import Operation_allocator.Trees.Semantics.SemanticExceptions.RelationalPlanConfigurationError;
import Operation_allocator.Trees.TreeNode;
import Operation_allocator.Trees.TreeNodeCostEngine;
import Operation_allocator.Trees.Semantics.Features;

import java.util.LinkedList;
import java.util.List;

public class TreeNodeSemantics {

    private static Provider home = null;
    private static Provider ptest = null;

    private static boolean debug_mode = false;
    private static Debugger debugger = null;

    public static void setDebug_mode(boolean debug_mode) {
        TreeNodeSemantics.debug_mode = debug_mode;
    }

    public static void setDebugger(Debugger debugger) {
        TreeNodeSemantics.debugger = debugger;
    }

    //first thing to set before to use methods
    public static void setHomeProvider(Provider p) {
        home = p;
    }

    //first thing to set before to use methods
    public static void setTestProvider(Provider p) {
        ptest = p;
    }

    public static <T extends Operation> void deriveCostBarriers(TreeNode<T> tn) {
        CostMetric m = SimpleCostEngine.computeExecutionVsMotionCost(tn.getElement(), ptest);
        if (m.Ce > 10 * m.Cm && !tn.isRoot())
            deriveCostBarriersComplement(tn, Features.EXPCPUDOMCOST);
        else {
            tn.getInfo().addFeature(Features.EXPDATADOMCOST);
            for (TreeNode<T> tns : tn.getSons()
            ) {
                deriveCostBarriers(tns);
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

        TreeNodeCostEngine.computeAndSetCost(tn, home);

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
            //need to create fake relation profile
            RelationProfile inrp = new RelationProfile();
            //new artificial relation profile made by attributes' list deep copy
            inrp.setRvp(RelationProfile.copyLoA(tn.getElement().getInputAttributes()));
            lrp.add(inrp);
            //now compute output relation profile
            tn.getElement().computeOutRelProf(lrp);
            //new udf metrics
            BasicMetric bm = tn.getElement().getOp_metric();
            if (bm instanceof UDFMetric)
                ((UDFMetric) bm).computeMetrics();
            //update operation output metrics
            TreeNodeSemantics.updateOperationOutputMetrics(tn);
        } else {
            List<Attribute> la = new LinkedList<>();
            List<Attribute> latemp = new LinkedList<>();
            //retrieve sons's attributes and metrics
            double temp = 0;
            double newInputTuple = 0;
            //delete old data
            tn.getElement().getOp_metric().inputSize = 0;
            for (TreeNode<T> tns : tn.getSons()
            ) {
                temp = 0;
                la = RelationProfile.union(RelationProfile.union(la, tns.getElement().getOutput_rp().getRvp()), tns.getElement().getOutput_rp().getRve());
                latemp = RelationProfile.union(tns.getElement().getOutput_rp().getRvp(), tns.getElement().getOutput_rp().getRve());
                for (Attribute aaa : latemp
                ) {
                    newInputTuple += aaa.getDimension();
                    temp += aaa.getDimension();
                }
                tn.getElement().getOp_metric().inputSize += tns.getElement().getOp_metric().getOutNofTuples() * temp;
            }
            //la is a list deep copied
            tn.getElement().setInputAttributes(la);
            tn.getElement().getOp_metric().setInputTupleSize(newInputTuple);
            //new udf metrics
            BasicMetric bm = tn.getElement().getOp_metric();
            if (bm instanceof UDFMetric)
                ((UDFMetric) bm).computeMetrics();
            //NB    1) here operation custom sets have already been set
            //      2) at first definition operations' basic metrics have to be inserted manually
            //now compute output relation profile
            TreeNodeSemantics.computeOperationOutputRelProf(tn);
            //update operation output metrics
            TreeNodeSemantics.updateOperationOutputMetrics(tn);

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

    public static <T extends Operation> void computeOperationOutputRelProf(TreeNode<T> tn) throws RelationalPlanConfigurationError {
        //some relational arity exception checks
        Operation op = tn.getElement();
        if (tn.isLeaf() && (op instanceof JoinNAry || op instanceof CartesianProduct || op instanceof Union
                || op instanceof Intersection || op instanceof Subtraction))
            throw new RelationalPlanConfigurationError();
        if (!tn.isLeaf()) {
            if (op instanceof JoinNAry || op instanceof CartesianProduct || op instanceof Union
                    || op instanceof Intersection || op instanceof Subtraction)
                if (tn.getSons().size() < 1)
                    throw new RelationalPlanConfigurationError();
            if (op instanceof GroupBy || op instanceof Projection || op instanceof Selection)
                if (tn.getSons().size() != 1)
                    throw new RelationalPlanConfigurationError();
        }
        //retrieve relation output profile of sons operation
        List<RelationProfile> lrp = new LinkedList<>();
        for (TreeNode<T> tns : tn.getSons()
        ) {
            lrp.add(RelationProfile.copyRP(tns.getElement().getOutput_rp()));
        }
        if (tn.isLeaf()) {
            //need to create fake relation profile
            RelationProfile inrp = new RelationProfile();
            //new artificial relation profile made by attributes' list deep copy
            inrp.setRvp(RelationProfile.copyLoA(tn.getElement().getInputAttributes()));
            lrp.add(inrp);
        }
        tn.getElement().computeOutRelProf(lrp);
    }

    public static <T extends Operation> boolean simulateOperationOutputRelProf(TreeNode<T> tn, List<Attribute> newInputsSet, Provider exec) {
        //retrieve relation output profile of sons operation
        List<RelationProfile> lrp = new LinkedList<>();
        for (TreeNode<T> tns : tn.getSons()
        ) {
            lrp.add(RelationProfile.copyRP(tns.getElement().getOutput_rp()));
        }
        if (tn.isLeaf()) {
            //need to create fake relation profile
            RelationProfile inrp = new RelationProfile();
            //new artificial relation profile made by attributes' list deep copy
            inrp.setRvp(RelationProfile.copyLoA(tn.getElement().getInputAttributes()));
            lrp.add(inrp);
        }
        RelationProfile sOutRelProf = tn.getElement().simulateOutRelProf(lrp, newInputsSet);
        if (!sOutRelProf.isAuthorizedFor(exec)) {
            if (debug_mode)
                debugger.leaveTrace(
                        new Report("TreeNodeSemantics",
                                LogType.OPERATION_SIMULATION_FAILURE,
                                "\tCANDIDATE NOT COMPLIANT: Simulated to\t" + exec.selfDescription()
                                        + " OutRelProf:\t" + sOutRelProf.toString()
                        )
                );
            return false;
        }
        return true;
    }

    /**
     * This method receives attribute encryption/decryption directives, then integrates information about tree structure,
     * applies encryption to attributes, updates operation metrics and flushes updates in output
     * A simple model helps to infer new time metrics
     * NB Requires home provider to be configured
     *
     * @param tn  Operation node that needs encryption enforcement
     * @param la  List of attribute constraints to be applied
     * @param <T> Generic operation
     */
    public static <T extends Operation> void applyEncryptionAndPropagateEffects(TreeNode<T> tn, List<AttributeConstraint> la, Provider endp) {
        if (tn.isLeaf()) {
            //First create a fake relation profile
            RelationProfile inrp = new RelationProfile();
            //new artificial relation profile made by attributes' list deep copy
            inrp.setRvp(RelationProfile.copyLoA(tn.getOracle().getElement().getInputAttributes()));
            //new artificial metric with output custom stats
            BasicMetric bm = tn.getOracle().getElement().getOp_metric().copyBasicMetric();
            bm.outputTupleSize = bm.inputTupleSize;
            bm.outputSize = bm.inputSize;
            //completion of the set of inputs
            Triple<RelationProfile, BasicMetric, Provider> t = new Triple<>(inrp, bm, home);
            List<Triple<RelationProfile, BasicMetric, Provider>> sonTable = new LinkedList<>();
            sonTable.add(t);
            //perform encryption
            tn.getEncryption().performOperation(sonTable, la, endp);
        } else {//case non leaf node
            BasicMetric bm = null;
            Triple<RelationProfile, BasicMetric, Provider> temp = null;
            List<Triple<RelationProfile, BasicMetric, Provider>> sonsTable = new LinkedList<>();
            for (TreeNode<T> tns : tn.getSons()
            ) {
                sonsTable.add(new Triple<>(tns.getElement().getOutput_rp(), tns.getElement().getOp_metric(), tns.getElement().getExecutor()));
            }
            //perform encryption
            tn.getEncryption().performOperation(sonsTable, la, endp);
        }

        //now set new attributes, infer stats and propagate effects
        //new input attributes
        tn.getElement().setInputAttributes(tn.getEncryption().getA());
        //new executor
        tn.getElement().setExecutor(endp);
        //new input metrics
        tn.getElement().getOp_metric().inputSize = tn.getEncryption().getInputBasicMetric().inputSize;
        tn.getElement().getOp_metric().inputTupleSize = tn.getEncryption().getInputBasicMetric().inputTupleSize;


        //update execution time metrics
        double oldAttributeVolume = tn.getOracle().getElement().getOp_metric().inputSize;
        double newAttributeVolume = tn.getElement().getOp_metric().inputSize;

        double oldTime = tn.getOracle().getElement().getOp_metric().CPU_time;
        tn.getElement().getOp_metric().CPU_time = BasicMetricUpdater.updateCPUtime(oldTime, oldAttributeVolume, newAttributeVolume);

        oldTime = tn.getOracle().getElement().getOp_metric().IO_time;
        tn.getElement().getOp_metric().IO_time = BasicMetricUpdater.updateIOtime(oldTime, oldAttributeVolume, newAttributeVolume);

        //update output metrics
        TreeNodeSemantics.updateOperationOutputMetrics(tn);

        //compute new output relation profile
        TreeNodeSemantics.computeOperationOutputRelProf(tn);
        //note that input attributes dimension are flushed into output
    }


}
