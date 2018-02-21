package Trees;

import Actors.Operation;
import Actors.Provider;
import Statistics.Metrics.BasicMetric;
import Statistics.Metrics.CostMetric;
import Statistics.Metrics.ProviderMetric;
import Trees.Semantics.Features;

public class TreeNodeCostEngine {


    /**
     * NB the cost engine does not apply/correct operations features status
     */
    public static Provider home;

    public static Provider getHome() {
        return home;
    }

    public static void setHome(Provider home) {
        TreeNodeCostEngine.home = home;
    }

    public static <T extends Operation> CostMetric computeCost(TreeNode<T> tn, Provider candidate, Features f) {
        CostMetric m = new CostMetric();
        m.setAllZero();

        for (TreeNode<T> tns : tn.getSons()
                ) {
            /**
             * Sums the cost of all sons
             */
            m.Ct += tns.getElement().getCost().Ct;

            /**
             * Compute encryption cost
             */
            //case no encrypted data -> encryption
            if (tns.getInfo().hasFeature(Features.NOTENCRYPTED)) {
                //case symmetric
                if (f == Features.ENCRYPTEDSYM) {
                    m.Cc += tns.getElement().getOp_metric().outputSize * tns.getElement().getExecutor().getMetrics().Kc_dse;//TODO CORRECTION
                }
                //case homomorphic
                else if (f == Features.ENCRYPTEDHOM) {
                    m.Cc += tns.getElement().getOp_metric().outputSize * tns.getElement().getExecutor().getMetrics().Kc_dse;//TODO CORRECTION
                }
            }
            //case encrypted -> no encryption
            else if (f == Features.NOTENCRYPTED) {
                //case decrypt asymmetric
                if (tns.getInfo().hasFeature(Features.ENCRYPTEDSYM)) {
                    m.Cc += tns.getElement().getOp_metric().outputSize * candidate.getMetrics().Kc_dse;//TODO CORRECTION
                }
                //case decrypt homomorphic
                else if (tns.getInfo().hasFeature(Features.ENCRYPTEDHOM)) {
                    m.Cc += tns.getElement().getOp_metric().outputSize * candidate.getMetrics().Kc_dse;//TODO CORRECTION
                }
            }
            //case wrong encryption double cost
            else {
                m.Cc += tns.getElement().getOp_metric().outputSize * (candidate.getMetrics().Kc_dse//TODO CORRECTION
                        + candidate.getMetrics().Kc_dse);//TODO CORRECTION
            }

            /**
             * Compute motion cost
             */
            if (!tns.getElement().getExecutor().selfDescription().equals(candidate.selfDescription())) {
                m.Cm += tns.getElement().getOp_metric().outputSize * (tns.getElement().getExecutor().getMetrics().Km + candidate.getMetrics().Km);
            }

        }//end foreach son

        /**
         * Compute leaf motion cost
         */
        if (tn.isLeaf()) {
            if (!candidate.selfDescription().equals(home.selfDescription())) {
                m.Cm += tn.getElement().getOp_metric().inputSize * (tn.getElement().getExecutor().getMetrics().Km + candidate.getMetrics().Km);
            }
        }
        /**
         * Compute leaf encryption cost
         */
        if (tn.isLeaf()) {
            //case symmetric
            if (f == Features.ENCRYPTEDSYM) {
                m.Cc += tn.getElement().getOp_metric().outputSize * home.getMetrics().Kc_dse;//TODO CORRECTION
            }
            //case homomorphic
            else if (f == Features.ENCRYPTEDHOM) {
                m.Cc += tn.getElement().getOp_metric().outputSize * home.getMetrics().Kc_dse;//TODO CORRECTION
            }
        }
        /**
         * Compute root motion cost
         */
        if (tn.isRoot() && !candidate.selfDescription().equals(home.selfDescription())) {
            m.Cm += tn.getElement().getOp_metric().outputSize * (candidate.getMetrics().Km + home.getMetrics().Km);
        }
        /**
         * Compute root decryption cost
         */
        if (tn.isRoot()) {
            if (f == Features.ENCRYPTEDSYM)
                m.Cc += tn.getElement().getOp_metric().outputSize * tn.getElement().getExecutor().getMetrics().Kc_dse;//TODO CORRECTION
            else if (f == Features.ENCRYPTEDHOM)
                m.Cc += tn.getElement().getOp_metric().outputSize * tn.getElement().getExecutor().getMetrics().Kc_dse;//TODO CORRECTION
        }
        /**
         * Compute execution cost
         */
        m.Ce += tn.getElement().getOp_metric().CPU_time * candidate.getMetrics().Kcpu / 3600 + tn.getElement().getOp_metric().IO_time * candidate.getMetrics().Kio / 3600;

        /**
         * Cost summation
         */
        m.Ct += m.Ce + m.Cc + m.Cm;

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
