package Trees.Semantics;

import Actors.Operation;
import Actors.Provider;
import Statistics.Metrics.CostMetric;
import Trees.TreeNode;

public class TreeNodeSemantics {

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
