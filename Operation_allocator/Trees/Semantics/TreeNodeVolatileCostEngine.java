package Trees.Semantics;

import Actors.Operation;
import Actors.Provider;
import Data.Attribute;
import Data.AttributeConstraint;
import Misc.Triple;
import Statistics.Metrics.BasicMetric;
import Statistics.Metrics.BasicMetricUpdater;
import Statistics.Metrics.CostMetric;
import Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;
import Trees.Semantics.SemanticOperations.Encryption;
import Trees.TreeNode;

import java.util.LinkedList;
import java.util.List;

public class TreeNodeVolatileCostEngine {
    private static Provider home = null;

    //first thing to set before to use methods
    public static void setHomeProvider(Provider p) {
        home = p;
    }

    /**
     * This method receives attribute encryption/decryption directives, then looks up for information about tree
     * structure, simulates application of encryption to attributes, simulates update of operation metrics and
     * returns new tree node cost
     *
     * NB Requires home provider to be configured
     *
     * @param tn  Operation node that needs encryption enforcement
     * @param la  List of attribute constraints to be applied
     * @param <T> Generic operation
     */
    public static <T extends Operation> CostMetric simulateEncryptionAndComputeCost(TreeNode<T> tn, List<AttributeConstraint> la, Provider endp) {
        //create result cost metric
        CostMetric ccmm = new CostMetric();
        ccmm.setAllZero();
        BasicMetric bbmm = null;

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
            //simulate encryption
            Triple<CostMetric, BasicMetric, List<Attribute>> tempres = Encryption.applyEncryption(sonTable, la, endp);
            ccmm = tempres.getFirst();
            bbmm = tempres.getSecond();
        } else {//case non leaf node
            BasicMetric bm = null;
            Triple<RelationProfile, BasicMetric, Provider> temp = null;
            List<Triple<RelationProfile, BasicMetric, Provider>> sonsTable = new LinkedList<>();
            for (TreeNode<T> tns : tn.getSons()
                    ) {
                sonsTable.add(new Triple<>(tns.getElement().getOutput_rp(), tns.getElement().getOp_metric(), tns.getElement().getExecutor()));
            }
            //simulate encryption
            Triple<CostMetric, BasicMetric, List<Attribute>> tempres = Encryption.applyEncryption(sonsTable, la, endp);
            ccmm = tempres.getFirst();
            bbmm = tempres.getSecond();
        }
        //retrieve metrics to end simulation
        double oldAttributeVolume = tn.getOracle().getElement().getOp_metric().inputSize;
        double newAttributeVolume = bbmm.inputSize;

        double oldTime = tn.getOracle().getElement().getOp_metric().CPU_time;
        bbmm.CPU_time = BasicMetricUpdater.updateCPUtime(oldTime, oldAttributeVolume, newAttributeVolume);

        oldTime = tn.getOracle().getElement().getOp_metric().IO_time;
        bbmm.IO_time = BasicMetricUpdater.updateIOtime(oldTime, oldAttributeVolume, newAttributeVolume);

        //calculate new cost of execution
        ccmm.Ce += bbmm.CPU_time * endp.getMetrics().Kcpu / 3600 + bbmm.IO_time * endp.getMetrics().Kio / 3600;
        //calculate new simulation incremental cost
        ccmm.Ct += ccmm.Cm;
        ccmm.Ct += ccmm.Cc;
        ccmm.Ct += ccmm.Ce;
        //calculate new simulation total cost
        for (TreeNode<T> tns : tn.getSons()
                ) {
            ccmm.Ct += tns.getElement().getCost().Ct;
        }

        //simulation result
        return ccmm;
    }
}
