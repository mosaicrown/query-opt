package Trees;

import Actors.Operation;
import Actors.Provider;
import Statistics.Metrics.BasicMetric;
import Statistics.Metrics.CostMetric;
import Statistics.Metrics.ProviderMetric;
import Trees.Semantics.Features;

public class TreeNodeCostEngine {

    public static Provider home;

    public static Provider getHome() {
        return home;
    }

    public static void setHome(Provider home) {
        TreeNodeCostEngine.home = home;
    }

    /**
     * NB TreeNodeCostEngine applies cost metric to TreeNode tn
     */
    public static <T extends Operation> CostMetric computeAndSetCost(TreeNode<T> tn, Provider candidate) {
        CostMetric m = new CostMetric();
        m.setAllZero();

        for (TreeNode<T> tns : tn.getSons()
                ) {
            /**
             * Sums the cost of all sons
             */
            m.Ct += tns.getElement().getCost().Ct;

        }
        /**
         * Compute execution cost
         */
        m.Ce += tn.getElement().getOp_metric().CPU_time * candidate.getMetrics().Kcpu / 3600 + tn.getElement().getOp_metric().IO_time * candidate.getMetrics().Kio / 3600;
        /**
         * Retrieve motion and encryption costs
         */
        m.Cc += tn.getEncryption().getDeltaMetric().Cc;
        m.Cm += tn.getEncryption().getDeltaMetric().Cm;
        /**
         * Cost summation
         */
        m.Ct += m.Ce + m.Cc + m.Cm;
        /**
         * Set up new cost
         */
        tn.getElement().setCost(m);

        return m;
    }

    public static <T extends Operation> boolean validateCost(TreeNode<T> tn) {
        boolean flag = false;
        if (tn.getElement().getCost().Ct == tn.getOracle().getElement().getCost().Ct
                && (tn.getOracle().getElement().getExecutor().selfDescription().equals(tn.getElement().getExecutor().selfDescription())))
            return true;
        if (tn.getElement().getCost().Ct < tn.getOracle().getElement().getCost().Ct) {
            //retrieve oracle's information
            CostMetric m1 = tn.getOracle().getElement().getCost();
            //build new incremental information
            CostMetric m2 = new CostMetric();
            m2.setAllZero();
            //retrieve operation + executor metrics
            ProviderMetric pm = tn.getElement().getExecutor().getMetrics();
            BasicMetric op = tn.getElement().getOp_metric();
            //compute new incremental metrics
            //execution cost
            m2.Ce = op.CPU_time * pm.Kcpu / 3600 + op.IO_time * pm.Kio / 3600;
            //motion cost
            if (!(tn.getOracle().getElement().getExecutor().selfDescription().equals(tn.getElement().getExecutor().selfDescription())))
                m2.Cm = op.outputSize * pm.Km;
            //encryption cost
            if (tn.getInfo().hasFeature(Features.ENCRYPTEDSYM))
                m2.Cc = op.outputSize * tn.getOracle().getElement().getExecutor().getMetrics().Kc_dse;//TODO CORRECTION
            else if (tn.getInfo().hasFeature(Features.ENCRYPTEDHOM))
                m2.Cc = op.outputSize * tn.getOracle().getElement().getExecutor().getMetrics().Kc_dse;//TODO CORRECTION
            //cost comparison
            if (m1.getIncrCost() > m2.getIncrCost())
                return true;
        }
        return flag;

    }

}
