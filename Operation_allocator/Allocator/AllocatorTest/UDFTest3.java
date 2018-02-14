package Allocator.AllocatorTest;

import Actors.Operation;
import Actors.Provider;
import Allocator.OperationAllocator;
import Statistics.Metrics.BasicMetric;
import Statistics.Metrics.CostMetric;
import Statistics.Metrics.ProviderMetric;
import Statistics.Metrics.UDFMetric;
import Statistics.UDFprofilers.CubicProfile;
import Statistics.UDFprofilers.Profiler;
import Trees.Semantics.Features;
import Trees.Semantics.Policy.Policy;
import Trees.Semantics.Policy.SimplePolicyGenerator;
import Trees.Semantics.TreeNodeSemantics;
import Trees.TreeNode;
import Trees.TreeNodeCostEngine;

import java.util.LinkedList;
import java.util.List;

public class UDFTest3 {
    /**
     * This test allocates operations of complex hybrid query plan that uses 2 UDF special functions
     * with cubic CPU & IO profile
     * NB Computational paradigm is still relational, UDF computation is done inside a VM
     * Average instruction complexity is used do describe code execution
     */
    public static void main(String[] args) {
        /**
         * Operation Metrics
         */
        BasicMetric m1 = new BasicMetric(2300, 0.04, 120.0, 0.04, 0.09, 0.35);
        BasicMetric m2 = new BasicMetric(120, 0.04, 35, 0.04, 0.98, 0.0001);
        BasicMetric m3 = new BasicMetric(600, 0.014, 300.0, 0.014, 0.06, 0.28);
        BasicMetric m4 = new BasicMetric(3.5, 0.012, 3.5, 0.012, 0.00023, 0.007);
        BasicMetric m5 = new BasicMetric(230, 0.03, 230.0, 0.03, 0.079, 0.263);
        BasicMetric m6 = new BasicMetric(3.5, 0.012, 0.42, 0.12, 0.007, 0.0003);
        BasicMetric m7 = new BasicMetric(230, 0.03, 60, 0.03, 0.11, 0.001);
        BasicMetric m8 = new BasicMetric(94, 0.024, 8.9, 0.038, 0.34, 0.07);
        BasicMetric m9 = new BasicMetric(8.0, 0.038, 8.9, 0.038, 0.0004, 0.07);

        UDFMetric udfm1 = new UDFMetric<CubicProfile>(15, 0.25e-9, 400e3, (m3.outputSize + m2.outputSize),
                (m3.outputTupleSize + m2.outputTupleSize) / 2, 2.8, 0.074);

        Profiler profiler = new CubicProfile(100, 32, 4.5, 2.7);
        udfm1.setProfiler(profiler);

        UDFMetric udfm2 = new UDFMetric<CubicProfile>(15, 0.25e-9, 400e3, udfm1.outputSize,
                udfm1.outputTupleSize, 34, 0.074);
        udfm2.setProfiler(profiler);

        /**
         * Check operation metrics
         */
        System.out.println(m1.toString());
        System.out.println(m2.toString());
        System.out.println(m3.toString());
        System.out.println(m4.toString());
        System.out.println(m5.toString());
        System.out.println(m6.toString());
        System.out.println(m7.toString());
        System.out.println(m9.toString());

        System.out.println(udfm1.toString());
        System.out.println(udfm2.toString());
        /**
         * Provider
         */
        double k = 1.4;
        ProviderMetric pm1 = new ProviderMetric(0,          0.37, k*0.37, 1.5*0.06e-6, 45*0.06e-6);
        ProviderMetric pm2 = new ProviderMetric(0.04e-6,    0.0528, k*0.0528, 1.5*0.04e-6, 45*0.04e-6);
        ProviderMetric pm3 = new ProviderMetric(0.037e-6,   0.0336, k*0.0336, 1.5*0.037e-6, 45*0.037e-6);

        Provider provider1 = new Provider("Proprietary", pm1);
        Provider provider2 = new Provider("EC2", pm2);
        Provider provider3 = new Provider("Cheap", pm3);

        System.out.println("Provider" + provider1.selfDescription());
        System.out.println(provider1.getMetrics().toString());
        System.out.println("Provider" + provider2.selfDescription());
        System.out.println(provider2.getMetrics().toString());
        System.out.println("Provider" + provider3.selfDescription());
        System.out.println(provider3.getMetrics().toString());
        /**
         * Operations
         */
        Operation o1 = new Operation("IndexScan1");
        Operation o2 = new Operation("Filter2");
        Operation o3 = new Operation("ClusteredScan3");
        Operation o4 = new Operation("scan4");
        Operation o5 = new Operation("scan5");
        Operation o6 = new Operation("Filter6");
        Operation o7 = new Operation("Filter7");
        Operation o8 = new Operation("ComputeScalar8");
        Operation o9 = new Operation("Select");

        Operation udfo1 = new Operation("udf cubic 1");
        Operation udfo2 = new Operation("udf cubic 2");

        //set operation metrics
        o1.setOp_metric(m1);
        o2.setOp_metric(m2);
        o3.setOp_metric(m3);
        o4.setOp_metric(m4);
        o5.setOp_metric(m5);
        o6.setOp_metric(m6);
        o7.setOp_metric(m7);
        o8.setOp_metric(m8);
        o9.setOp_metric(m9);

        udfo1.setOp_metric(udfm1);
        udfo2.setOp_metric(udfm2);

        //set executors
        o1.setExecutor(provider1);
        o2.setExecutor(provider1);
        o3.setExecutor(provider1);
        o4.setExecutor(provider1);
        o5.setExecutor(provider1);
        o6.setExecutor(provider1);
        o7.setExecutor(provider1);
        o8.setExecutor(provider1);
        o9.setExecutor(provider1);

        udfo1.setExecutor(provider1);
        udfo2.setExecutor(provider1);

        //set costs
        CostMetric cm1 = new CostMetric();
        CostMetric cm2 = new CostMetric();
        CostMetric cm3 = new CostMetric();
        CostMetric cm4 = new CostMetric();
        CostMetric cm5 = new CostMetric();
        CostMetric cm6 = new CostMetric();
        CostMetric cm7 = new CostMetric();
        CostMetric cm8 = new CostMetric();
        CostMetric cm9 = new CostMetric();
        CostMetric cm10 = new CostMetric();
        CostMetric cm11 = new CostMetric();

        cm1.setAllZero();
        cm2.setAllZero();
        cm3.setAllZero();
        cm4.setAllZero();
        cm5.setAllZero();
        cm6.setAllZero();
        cm7.setAllZero();
        cm8.setAllZero();
        cm9.setAllZero();
        cm10.setAllZero();
        cm11.setAllZero();

        o1.setCost(cm1);
        o2.setCost(cm2);
        o3.setCost(cm3);
        o4.setCost(cm4);
        o5.setCost(cm5);
        o6.setCost(cm6);
        o7.setCost(cm7);
        o8.setCost(cm8);
        o9.setCost(cm9);

        udfo1.setCost(cm10);
        udfo2.setCost(cm11);


        /**
         * Adding policies
         */
        Policy p1 = new Policy();
        Policy p2 = new Policy();
        Policy p3 = new Policy();
        Policy p4 = new Policy();
        Policy p5 = new Policy();
        Policy p6 = new Policy();
        Policy p7 = new Policy();
        Policy p8 = new Policy();
        Policy p9 = new Policy();
        Policy p10 = new Policy();
        Policy p11 = new Policy();

        o1.setPolicy(p1);
        o2.setPolicy(p2);
        o3.setPolicy(p3);
        o4.setPolicy(p4);
        o5.setPolicy(p5);
        o6.setPolicy(p6);
        o7.setPolicy(p7);
        o8.setPolicy(p8);
        o9.setPolicy(p9);
        udfo1.setPolicy(p10);
        udfo2.setPolicy(p11);


        /**
         * Building the tree
         */
        TreeNode q1 = new TreeNode<Operation>(o1);

        List<TreeNode<Operation>> opsons1 = new LinkedList<>();
        opsons1.add(q1);

        TreeNode q2 = new TreeNode<Operation>(o2);
        q2.setSons(opsons1);

        TreeNode q3 = new TreeNode<Operation>(o3);

        List<TreeNode<Operation>> opsons2 = new LinkedList<>();
        opsons2.add(q3);
        opsons2.add(q2);

        TreeNode<Operation> qudf1 = new TreeNode<>(udfo1);
        qudf1.setSons(opsons2);

        TreeNode<Operation> qudf2 = new TreeNode<>(udfo2);

        List<TreeNode<Operation>> opsons3 = new LinkedList<>();
        opsons3.add(qudf1);
        qudf2.setSons(opsons3);

        TreeNode q4 = new TreeNode<Operation>(o4);
        List<TreeNode<Operation>> opsons4 = new LinkedList<>();
        opsons4.add(q4);

        TreeNode q6 = new TreeNode<Operation>(o6);
        q6.setSons(opsons4);

        TreeNode q5 = new TreeNode<Operation>(o5);
        List<TreeNode<Operation>> opsons5 = new LinkedList<>();
        opsons5.add(q5);

        TreeNode q7 = new TreeNode<Operation>(o7);
        q7.setSons(opsons5);

        List<TreeNode<Operation>> opsons6 = new LinkedList<>();
        opsons6.add(q7);
        opsons6.add(qudf2);
        opsons6.add(q6);

        TreeNode q8 = new TreeNode<Operation>(o8);
        q8.setSons(opsons6);

        List<TreeNode<Operation>> opsons7 = new LinkedList<>();
        opsons7.add(q8);

        TreeNode query= new TreeNode<Operation>(o9);
        query.setSons(opsons7);

        /**
         * ADD THE VISIBILITY FEATURE TO THE LEAFS
         */
        q1.getInfo().addFeature(Features.RESERVED);
        q3.getInfo().addFeature(Features.PUBLIC);
        q4.getInfo().addFeature(Features.CONFIDENTIAL);
        q5.getInfo().addFeature(Features.RESERVED);

        /**
         * Provider for semantics
         */
        ProviderMetric pms = new ProviderMetric(0.04e-6,    0.0528, k*0.0528, 1.5*0.04e-6, 45*0.04e-6);
        Provider providers = new Provider("Provider for semantics", pms);
        /**
         * Semantics
         */
        TreeNodeCostEngine.setHome(provider1);
        TreeNodeSemantics.computeUDFProfiles(query);
        TreeNodeSemantics.deriveCostBarriers(query, providers);
        System.out.println("\nPrinting query after semantic barriers computed:");
        System.out.println(query.printTree());
        //Not considered encryption
        TreeNodeSemantics.synthetizeExecutionCost(query);

        System.out.println("\nPrinting query plan before policy generation");
        System.out.println(query.printTree());
        /**
         * Policy generation
         */
        SimplePolicyGenerator policyGenerator = new SimplePolicyGenerator();
        policyGenerator.generateCandidates(query);

        System.out.println("\nPrinting query plan after policy generation");
        System.out.println(query.printTree());
        /**
         * Oracle creation and referencing
         */
        TreeNode oracle = query.deepClone();
        TreeNode.bindOracle(query, oracle);
        /**
         * Operation allocator allocation and configuration
         */
        OperationAllocator engine = new OperationAllocator();
        //set providers
        engine.setP1(provider1);
        engine.setP2(provider2);
        engine.setP3(provider3);
        //set query and oracle
        engine.setOracle(oracle);
        engine.setQuery(query);
        /**
         * ALLOCATION TEST
         */
        System.out.println("\nPrinting allocation algorithm decisions");
        engine.computeAllocation();
        /**
         * Print results
         */
        System.out.println("\nPrinting query plan on single provider");
        System.out.println(oracle.printTree());
        System.out.println("\nPrinting query plan after allocation");
        System.out.println(query.printTree());

    }
}
