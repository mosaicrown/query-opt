package Allocator.AllocatorTest;

import Actors.Operation;
import Actors.Provider;
import Allocator.OperationAllocator;
import Statistics.Metrics.BasicMetric;
import Statistics.Metrics.CostMetric;
import Statistics.Metrics.ProviderMetric;
import Trees.Semantics.Features;
import Trees.Semantics.Policy.Policy;
import Trees.Semantics.Policy.SimplePolicyGenerator;
import Trees.Semantics.TreeNodeSemantics;
import Trees.TreeNode;
import Trees.TreeNodeCostEngine;

import java.util.LinkedList;
import java.util.List;

public class Test1 {
    /**
     * This is a pure SQL relational query test
     *
     * The query is like: select t.name from t
     * The query took 0.05 seconds on a table of 10000 tuples with size 0.042 KB
     */

    public static void main(String[] args) {
        /**
         * Operation Metrics
         */
        BasicMetric m1 = new BasicMetric(420, 0.042, 420, 0.042, 0.054, 0.028);
        BasicMetric m2 = new BasicMetric(40, 0.042, 40, 0.042, 0.0039, 0.0009);
        BasicMetric m3 = new BasicMetric(300, 0.028, 300, 0.028, 0.02, 0.026);
        BasicMetric m4 = new BasicMetric(40, 0.036, 90, 0.036, 0.004, 0.0007);
        BasicMetric m5 = new BasicMetric(350, 0.028, 300, 0.028, 0.02, 0.026);
        BasicMetric m6 = new BasicMetric(40, 0.036, 9, 0.036, 0.0021, 0.0007);
        BasicMetric m7 = new BasicMetric(9, 0.036, 5, 0.036, 0.0021, 0.0012);

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

        /**
         * Provider metrics
         */
        ProviderMetric pm1 = new ProviderMetric(0, 0.41, 0.78, 1.5*0.06e-6, 1.9*0.06e-6);
        ProviderMetric pm2 = new ProviderMetric(0.04e-6, 0.37, 0.61, 1.5*0.04e-6, 1.9*0.04e-6);
        ProviderMetric pm3 = new ProviderMetric(0.037e-6, 0.29, 0.42, 1.5*0.037e-6, 1.9*0.037e-6);
        /**
         * Provider
         */
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
        Operation o1 = new Operation("ClusteredIndexScan");
        Operation o2 = new Operation("Filter");
        Operation o3 = new Operation("ClusteredIndexSeek1");
        Operation o4 = new Operation("NestedLoops1");
        Operation o5 = new Operation("ClusteredIndexSeek2");
        Operation o6 = new Operation("NestedLoops2");
        Operation o7 = new Operation("Select");


        //set operation metrics
        o1.setOp_metric(m1);
        o2.setOp_metric(m2);
        o3.setOp_metric(m3);
        o4.setOp_metric(m4);
        o5.setOp_metric(m5);
        o6.setOp_metric(m6);
        o7.setOp_metric(m7);

        //set executors
        o1.setExecutor(provider1);
        o2.setExecutor(provider1);
        o3.setExecutor(provider1);
        o4.setExecutor(provider1);
        o5.setExecutor(provider1);
        o6.setExecutor(provider1);
        o7.setExecutor(provider1);


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
        o5.setCost(cm5);
        o6.setCost(cm6);
        o7.setCost(cm7);

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

        o1.setPolicy(p1);
        o2.setPolicy(p2);
        o3.setPolicy(p3);
        o4.setPolicy(p4);
        o5.setPolicy(p5);
        o6.setPolicy(p6);
        o7.setPolicy(p7);


        /**
         * Building the tree
         */
        List<TreeNode<Operation>> opsons1 = new LinkedList<>();
        TreeNode q1 = new TreeNode<Operation>(o1);
        opsons1.add(q1);

        TreeNode<Operation> q2 = new TreeNode<>(o2);
        q2.setSons(opsons1);

        TreeNode<Operation> q3 = new TreeNode<>(o3);

        List<TreeNode<Operation>> opsons2 = new LinkedList<>();
        opsons2.add(q2);
        opsons2.add(q3);
        TreeNode<Operation> q4 = new TreeNode<>(o4);
        q4.setSons(opsons2);

        TreeNode<Operation> q5 = new TreeNode<>(o5);

        List<TreeNode<Operation>> opsons3 = new LinkedList<>();
        opsons3.add(q4);
        opsons3.add(q5);
        TreeNode<Operation> q6 = new TreeNode<>(o6);
        q6.setSons(opsons3);

        List<TreeNode<Operation>> opsons4 = new LinkedList<>();
        opsons4.add(q6);
        TreeNode<Operation> query = new TreeNode<>(o7);
        query.setSons(opsons4);

        /**
         * ADD THE VISIBILITY FEATURE TO THE LEAFS
         */
        q1.getInfo().addFeature(Features.RESERVED);
        q3.getInfo().addFeature(Features.PUBLIC);
        q5.getInfo().addFeature(Features.PUBLIC);
        /**
         * Provider for semantics
         */
        ProviderMetric pms = new ProviderMetric(0.04e-6, 0.2, 0.61, 1.5*0.04e-6, 1.9*0.04e-6);
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
