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

public class ComputeUDFTest {
    public static void main(String[] args) {
        /**
         * Operation Metrics
         */
        BasicMetric m1 = new BasicMetric(0, 0, 12000.0, 4.0, 0.09, 0.002);
        BasicMetric m2 = new BasicMetric(0, 0, 9870.0, 22.0, 0.07, 0.028);
        BasicMetric m3 = new BasicMetric(0, 0, 30000.0, 2.0, 0.09, 0.1);
        BasicMetric m4 = new BasicMetric(0, 0, 1000.0, 40.0, 0.004, 0.007);

        UDFMetric udfm1 = new UDFMetric<UDF_typ1>(18, 2.5e-8, 40e3, (m1.outputSize + m2.outputSize),
                (m1.outputTupleSize + m2.outputTupleSize) / 2, 2800, 10);
        Profiler profiler = new UDF_typ1();
        udfm1.setProfiler(profiler);

        UDFMetric udfm2 = new UDFMetric<UDF_typ1>(18, 2.5e-8, 40e3, (m3.outputSize + m4.outputSize),
                (m3.outputTupleSize + m4.outputTupleSize) / 2, 2800, 10);
        udfm2.setProfiler(profiler);

        UDFMetric udfm3 = new UDFMetric<UDF_typ1>(18, 2.5e-8, 40e3, (m1.outputSize + m4.outputSize),
                (m1.outputTupleSize + m4.outputTupleSize) / 2, 2800, 10);
        udfm3.setProfiler(profiler);

        /**
         * Check operation metrics
         */
        System.out.println(m1.toString());
        System.out.println(m2.toString());
        System.out.println(m3.toString());
        System.out.println(m4.toString());

        System.out.println(udfm1.toString());
        System.out.println(udfm2.toString());
        System.out.println(udfm3.toString());
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
        Operation o3 = new Operation("mergescan");
        Operation o4 = new Operation("merge");

        Operation udfo1 = new Operation("udf quad 1");
        Operation udfo2 = new Operation("udf quad 2");
        Operation udfo3 = new Operation("udf quad 3");

        //set operation metrics
        o1.setOp_metric(m1);
        o2.setOp_metric(m2);
        o3.setOp_metric(m3);
        o4.setOp_metric(m4);

        udfo1.setOp_metric(udfm1);
        udfo2.setOp_metric(udfm2);
        udfo3.setOp_metric(udfm3);

        //set executors
        o1.setExecutor(provider1);
        o2.setExecutor(provider1);
        o3.setExecutor(provider1);
        o4.setExecutor(provider1);

        udfo1.setExecutor(provider1);
        udfo2.setExecutor(provider1);
        udfo3.setExecutor(provider1);

        //set costs
        CostMetric cm1 = new CostMetric();
        CostMetric cm2 = new CostMetric();
        CostMetric cm3 = new CostMetric();
        CostMetric cm4 = new CostMetric();
        CostMetric cm5 = new CostMetric();
        CostMetric cm6 = new CostMetric();
        CostMetric cm7 = new CostMetric();

        cm1.setAllZero();
        cm2.setAllZero();
        cm3.setAllZero();
        cm4.setAllZero();
        cm5.setAllZero();
        cm6.setAllZero();
        cm7.setAllZero();

        o1.setCost(cm1);
        o2.setCost(cm2);
        o3.setCost(cm3);
        o4.setCost(cm4);

        udfo1.setCost(cm5);
        udfo2.setCost(cm6);
        udfo3.setCost(cm7);

        /**
         * Building the tree
         */
        List<TreeNode<Operation>> opsons1 = new LinkedList<>();
        opsons1.add(new TreeNode<Operation>(o1));
        opsons1.add(new TreeNode<Operation>(o2));
        TreeNode<Operation> query1 = new TreeNode<>(udfo1);
        query1.setSons(opsons1);

        List<TreeNode<Operation>> opsons2 = new LinkedList<>();
        opsons2.add(new TreeNode<Operation>(o3));
        TreeNode<Operation> query2 = new TreeNode<>(udfo2);
        query2.setSons(opsons2);

        List<TreeNode<Operation>> opsons3 = new LinkedList<>();
        opsons3.add(query1);
        opsons3.add(query2);
        TreeNode<Operation> query3 = new TreeNode<>(udfo3);
        query3.setSons(opsons3);

        List<TreeNode<Operation>> opsons4 = new LinkedList<>();
        opsons4.add(query3);
        TreeNode<Operation> query = new TreeNode<>(o4);
        query.setSons(opsons4);

        ProviderMetric economicMetric = pm1.deepClone();
        economicMetric.Kcpu/=4;
        economicMetric.Kio/=3;
        economicMetric.Kc1/=2;
        economicMetric.Kc2/=2;
        economicMetric.Km/=2;

        Provider cheapProvider = new Provider("cheap", economicMetric);

        /**
         * Semantics
         */
        System.out.println("\nPrinting query plan");
        TreeNodeSemantics.computeUDFProfiles(query);
        TreeNodeSemantics.deriveCostBarriers(query, cheapProvider);
        TreeNodeSemantics.synthetizeExecutionCost(query);
        System.out.println(query.printTree());


    }
}
