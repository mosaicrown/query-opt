package Trees;

import Actors.Operation;
import Actors.Provider;
import Statistics.Metrics.CostMetric;
import Trees.Semantics.Features;

public class QueryCostEngine {


    /**
     *  NB the cost engine does not apply/correct operations features status
     */
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
                    m.Cc += tns.getElement().getOp_metric().outputSize * tns.getElement().getExecutor().getMetrics().Kc1;
                }
                //case homomorphic
                else if (f == Features.ENCRYPTEDHOM) {
                    m.Cc += tns.getElement().getOp_metric().outputSize * tns.getElement().getExecutor().getMetrics().Kc2;
                }
            }
            //case encrypted -> no encryption
            else if (f == Features.NOTENCRYPTED) {
                //case decrypt asymmetric
                if (tns.getInfo().hasFeature(Features.ENCRYPTEDSYM)) {
                    m.Cc += tns.getElement().getOp_metric().outputSize * tns.getElement().getExecutor().getMetrics().Kc1;
                }
                //case decrypt homomorphic
                else if (tns.getInfo().hasFeature(Features.ENCRYPTEDHOM)) {
                    m.Cc += tns.getElement().getOp_metric().outputSize * tns.getElement().getExecutor().getMetrics().Kc2;
                }
            }
            //case wrong encryption double cost
            else {
                m.Cc += tns.getElement().getOp_metric().outputSize * (tns.getElement().getExecutor().getMetrics().Kc2
                        + tns.getElement().getExecutor().getMetrics().Kc1);
            }

            /**
             * Compute motion cost
             */
            if (!tns.getElement().getExecutor().selfDescription().equals(candidate.selfDescription())) {
                m.Cm += tns.getElement().getOp_metric().outputSize * (tns.getElement().getExecutor().getMetrics().Km + candidate.getMetrics().Km);
            }

        }//end foreach son

        /**
         * Compute execution cost
         */
        m.Ce += tn.getElement().getOp_metric().CPU_time * candidate.getMetrics().Kcpu + tn.getElement().getOp_metric().IO_time * candidate.getMetrics().Kio;

        /**
         * Cost summation
         */
        m.Ct += m.Ce + m.Cc + m.Cm;

        return m;
    }

}
