package Trees.Semantics;

import Actors.Operation;
import Actors.Provider;
import Actors.SimpleCostEngine;
import Statistics.Metrics.BasicMetric;
import Statistics.Metrics.CostMetric;
import Statistics.Metrics.UDFMetric;
import Trees.TreeNode;

public class TreeNodeSemantics {

    public static <T extends Operation> void deriveCostBarriers(TreeNode<T> tn, Provider p) {
        CostMetric m = SimpleCostEngine.computeExecutionVsMotionCost(tn.getElement(), p);
        if (m.Ce > 1000 * m.Cm)
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

    public static <T extends Operation> void synthetizeExecutionCost(TreeNode<T> tn) {

        for (TreeNode<T> tns : tn.getSons()
                ) {
            synthetizeExecutionCost(tns);
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
        cm.Ce = op.getOp_metric().CPU_time * exec.getMetrics().Kcpu + op.getOp_metric().IO_time * exec.getMetrics().Kio;
        cm.Ct = cm.Ce;

        //synthetizing cost
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

}
