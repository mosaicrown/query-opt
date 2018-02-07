package Trees.Semantics.SemanticTests;

import Actors.Operation;
import Actors.Provider;
import Statistics.Metrics.BasicMetric;
import Statistics.Metrics.CostMetric;
import Statistics.Metrics.ProviderMetric;
import Statistics.Metrics.UDFMetric;
import Statistics.UDFprofilers.Profiler;
import Statistics.UDFprofilers.UDF_typ1;
import Trees.Semantics.TreeNodeSemantics;
import Trees.TreeNode;

import java.util.LinkedList;
import java.util.List;

public class SynthetizeTest {
    public static void main(String[] args) {
        /**
         * Metrics definition + operation creation + simple plan creation
         * NB:  prices of target machine amazon EC2 t2.xlarge 4 vCPU 16 GB RAM outbound traffic 50 TB/month
         *      IOPS and Cipher quantity prices coherent
         */
        /**
         * Operation Metrics
         */
        BasicMetric m1 = new BasicMetric(0, 0, 12000.0, 4.0, 0.09, 0.002);
        BasicMetric m2 = new BasicMetric(0, 0, 9870.0, 22.0, 0.07, 0.028);
        UDFMetric m3 = new UDFMetric<UDF_typ1>(18, 2.5e-8, 40e3, (m1.outputSize + m2.outputSize),
                (m1.outputTupleSize + m2.outputTupleSize) / 2, 2800, 10);
        Profiler profiler = new UDF_typ1();
        m3.setProfiler(profiler);
        m3.computeMetrics();
        /**
         * Check operation metrics
         */
        System.out.println(m1.toString());
        System.out.println(m2.toString());
        System.out.println(m3.toString());
        /**
         * Provider metrics
         */
        ProviderMetric pm1 = new ProviderMetric(0.04e-6, 0.37, 0.98, 0.37 * (10e-3 / 5), 0.37 * (10e-3 / 4));
        /**
         * Provider
         */
        Provider provider1 = new Provider("EC2", pm1);
        System.out.println("Provider" + provider1.selfDescription());
        System.out.println(provider1.getMetrics().toString());
        /**
         * Operations
         */
        Operation o1 = new Operation("scan");
        Operation o2 = new Operation("scan");
        Operation o3 = new Operation("Algorithm quadratic complexity");

        //set operation metrics
        o1.setOp_metric(m1);
        o2.setOp_metric(m2);
        o3.setOp_metric(m3);

        //set executors
        o1.setExecutor(provider1);
        o2.setExecutor(provider1);
        o3.setExecutor(provider1);

        //set costs
        CostMetric cm1 = new CostMetric();
        CostMetric cm2 = new CostMetric();
        CostMetric cm3 = new CostMetric();

        cm1.setAllZero();
        cm2.setAllZero();
        cm3.setAllZero();

        o1.setCost(cm1);
        o2.setCost(cm2);
        o3.setCost(cm3);

        /**
         * Building the tree
         */
        TreeNode<Operation> query = new TreeNode<>(o3);
        List<TreeNode<Operation>> opsons = new LinkedList<>();
        opsons.add(new TreeNode<Operation>(o1));
        opsons.add(new TreeNode<Operation>(o2));
        query.setSons(opsons);
        /**
         * Semantics
         */
        TreeNodeSemantics.synthetizeExecutionCost(query);
        /**
         * cloning query + semantics
         */
        TreeNode<Operation> queryclone=query.deepClone();
        for (TreeNode<Operation> opcs:queryclone.getSons()
             ) {
            System.out.println(opcs.getParent().toString());
        }
        /**
         * check instances
         */
        for (TreeNode<Operation> ops:query.getSons()
                ) {
            System.out.println(ops.getParent().toString());
        }


    }
}
