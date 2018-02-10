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

public class Test2 {
    /**
     * This is a pure SQL relational query test
     *
     * The query is like: select t1.a1, t2.a2 from t1 join t2 on t2.a2
     * The query took 0.88 seconds on a table of 100000 and 1300 tuples with size 0.042 and 0.13KB
     */

    public static void main(String[] args) {
        /**
         * Operation Metrics
         */
        BasicMetric m1 = new BasicMetric(4200, 0.042, 4200, 0.042, 0.08, 0.076);
        BasicMetric m2 = new BasicMetric(169, 0.13, 169, 0.13, 0.00002, 0.009);
        BasicMetric m3 = new BasicMetric(4369, 0.042, 30, 0.042, 0.0093, 0.00001);
        BasicMetric m4 = new BasicMetric(30, 0.042, 30, 0.042, 0.0002, 0.007);

        /**
         * Check operation metrics
         */
        System.out.println(m1.toString());
        System.out.println(m2.toString());
        System.out.println(m3.toString());
        System.out.println(m4.toString());

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
        Operation o1 = new Operation("IndexScan");
        Operation o2 = new Operation("ClusteredIndexScan");
        Operation o3 = new Operation("InnerJoinHashMatch");
        Operation o4 = new Operation("select");


        //set operation metrics
        o1.setOp_metric(m1);
        o2.setOp_metric(m2);
        o3.setOp_metric(m3);
        o4.setOp_metric(m4);

        //set executors
        o1.setExecutor(provider1);
        o2.setExecutor(provider1);
        o3.setExecutor(provider1);
        o4.setExecutor(provider1);


        //set costs
        CostMetric cm1 = new CostMetric();
        CostMetric cm2 = new CostMetric();
        CostMetric cm3 = new CostMetric();
        CostMetric cm4 = new CostMetric();

        cm1.setAllZero();
        cm2.setAllZero();
        cm3.setAllZero();
        cm4.setAllZero();

        o1.setCost(cm1);
        o2.setCost(cm2);
        o3.setCost(cm3);
        o4.setCost(cm4);

        /**
         * Adding policies
         */
        Policy p1 = new Policy();
        Policy p2 = new Policy();
        Policy p3 = new Policy();
        Policy p4 = new Policy();

        o1.setPolicy(p1);
        o2.setPolicy(p2);
        o3.setPolicy(p3);
        o4.setPolicy(p4);


        /**
         * Building the tree
         */
        List<TreeNode<Operation>> opsons1 = new LinkedList<>();
        TreeNode q1 = new TreeNode<Operation>(o1);
        TreeNode q2 = new TreeNode<Operation>(o2);
        opsons1.add(q1);
        opsons1.add(q2);

        TreeNode<Operation> q3 = new TreeNode<>(o3);
        q3.setSons(opsons1);

        List<TreeNode<Operation>> opsons2 = new LinkedList<>();
        opsons2.add(q3);

        TreeNode<Operation> query = new TreeNode<>(o4);
        query.setSons(opsons2);

        /**
         * ADD THE VISIBILITY FEATURE TO THE LEAFS
         */
        q1.getInfo().addFeature(Features.PUBLIC);
        q2.getInfo().addFeature(Features.RESERVED);
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
