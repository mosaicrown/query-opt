package Trees;

import Actors.Operation;
import Actors.Provider;
import Data.Attribute;
import Data.AttributeConstraint;
import Misc.Pair;
import Misc.Triple;
import Statistics.Metrics.BasicMetric;
import Statistics.Metrics.CostMetric;
import Statistics.Metrics.ProviderMetric;
import Trees.Semantics.Features;
import Trees.Semantics.Policy.RelationalProfilePolicy.PolicyMovesGenerator;
import Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;
import Trees.Semantics.SemanticOperations.Encryption;
import Trees.Semantics.TreeNodeSemantics;
import Trees.Semantics.TreeNodeVolatileCostEngine;

import java.util.LinkedList;
import java.util.List;

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

    /**
     * This method validates tn's sub-tree analyzing the eventual maximum cost of:
     * 1) motion to home provider
     * 1) decryption applied to enforce plaintext distribution of data
     *
     * @param tn  TreeNode to be validates
     * @param <T> Operation extender
     * @return <code>true</code> if tn's sub-tree is validated
     */
    public static <T extends Operation> boolean validateCost(TreeNode<T> tn) {
        boolean flag = false;
        //warranty of full plaintext execution on home provider
        if (!(tn.getElement().getCost().Ct > tn.getOracle().getElement().getCost().Ct)
                && (tn.getOracle().getElement().getExecutor().selfDescription().equals(tn.getElement().getExecutor().selfDescription())))
            return true;
        //execution on external provider with temporary smaller total cost, consider impact of total decryption
        if (tn.getElement().getCost().Ct < tn.getOracle().getElement().getCost().Ct) {
            //retrieve oracle's information
            CostMetric m1 = tn.getOracle().getElement().getCost();
            //build new temporary incremental information
            CostMetric m2 = tn.getElement().getCost().copy();

            //retrieve tn's output relation profile with metrics
            List<Triple<RelationProfile, BasicMetric, Provider>> outTable = new LinkedList<>();
            outTable.add(new Triple<>(tn.getElement().getOutput_rp(), tn.getElement().getOp_metric(), tn.getElement().getExecutor()));

            //enforce configuration and compute eventual decryption moves
            PolicyMovesGenerator generator = new PolicyMovesGenerator();
            generator.setP1(home);
            List<Pair<List<AttributeConstraint>, Provider>> p1pairs = generator.decryptionMovesToHome(tn);

            //simulate eventual decryption
            Triple<CostMetric, BasicMetric, List<Attribute>> tempres = Encryption.applyEncryption(outTable, p1pairs.get(0).getFirst(), home);

            //eventual incremental cost of decryption and movement
            m2.Cc += tempres.getFirst().Cc;
            m2.Cm += tempres.getFirst().Cm;
            //NB no need to update estimate of total sub-tree cost

            //cost comparison
            if (m1.getIncrCost() > m2.getIncrCost())
                return true;
        }
        return flag;

    }

}
