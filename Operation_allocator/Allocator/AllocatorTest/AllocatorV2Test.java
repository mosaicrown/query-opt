package Allocator.AllocatorTest;

import Actors.Operation;
import Actors.Operations.*;
import Actors.Provider;
import Allocator.OperationAllocator;
import Data.Attribute;
import Data.AttributeConstraint;
import Data.AttributeState;
import Statistics.Metrics.BasicMetric;
import Statistics.Metrics.CostMetric;
import Statistics.Metrics.ProviderMetric;
import Statistics.Metrics.UDFMetric;
import Statistics.UDFprofilers.Profiler;
import Statistics.UDFprofilers.PseudolinearProfile;
import Trees.Semantics.TreeNodeSemantics;
import Trees.Semantics.TreeNodeVolatileCostEngine;
import Trees.TreeNode;
import Trees.TreeNodeCostEngine;

import java.util.LinkedList;
import java.util.List;

public class AllocatorV2Test {
    /**
     * This test allocates operations of complex hybrid query plan that uses 2 UDF special functions
     * with pseudo-linear CPU & IO profile
     * NB Computational paradigm is still relational, UDF computation is done inside a VM
     * Average instruction complexity is used do describe code execution
     */
    public static void main(String[] args) {
        /**
         * Provider definition
         */
        double k = 1.4;
        ProviderMetric pm1 = new ProviderMetric(0, 0.37, k * 0.37, 1.5 * 0.06e-6, 1.5 * 0.06e-6, 45 * 0.06e-6, 45 * 0.06e-6);
        ProviderMetric pm2 = new ProviderMetric(0.04e-6, 0.0528, k * 0.0528, 1.5 * 0.04e-6, 1.5 * 0.04e-6, 45 * 0.04e-6, 45 * 0.04e-6);
        ProviderMetric pm3 = new ProviderMetric(0.037e-6, 0.0336, k * 0.0336, 1.5 * 0.037e-6, 1.5 * 0.037e-6, 45 * 0.037e-6, 45 * 0.037e-6);

        Provider provider1 = new Provider("Proprietary", pm1);
        Provider provider2 = new Provider("EC2", pm2);
        Provider provider3 = new Provider("Cheap", pm3);

        /**
         * Operation Metrics
         */
        BasicMetric m1 = new BasicMetric(2300, 0.04, 120.0, 0.04, 0.09, 0.35);
        BasicMetric m2 = new BasicMetric(120, 0.04, 3.5, 0.04, 0.98, 0.0001);
        BasicMetric m3 = new BasicMetric(600, 0.014, 300.0, 0.014, 0.06, 0.28);
        BasicMetric m4 = new BasicMetric(3.5, 0.012, 3.5, 0.012, 0.00023, 0.007);
        BasicMetric m5 = new BasicMetric(230, 0.03, 230.0, 0.03, 0.079, 0.263);
        BasicMetric m6 = new BasicMetric(3.5, 0.012, 0.42, 0.12, 0.007, 0.0003);
        BasicMetric m7 = new BasicMetric(230, 0.03, 60, 0.03, 0.11, 0.001);
        BasicMetric m8 = new BasicMetric(94, 0.024, 8.9, 0.038, 0.34, 0.07);
        BasicMetric m9 = new BasicMetric(8.0, 0.038, 8.9, 0.038, 0.0004, 0.07);

        UDFMetric udfm1 = new UDFMetric<PseudolinearProfile>(15, 0.25e-9, 400e3, (m3.outputSize + m2.outputSize),
                (m3.outputTupleSize + m2.outputTupleSize) / 2, 28, 0.074);

        Profiler profiler = new PseudolinearProfile(100, 3.2, 4.5);
        udfm1.setProfiler(profiler);

        UDFMetric udfm2 = new UDFMetric<PseudolinearProfile>(15, 0.25e-9, 400e3, udfm1.outputSize,
                udfm1.outputTupleSize, 34, 0.074);
        udfm2.setProfiler(profiler);

        /**
         * Set up simulated tables attributes
         */

        Attribute a1 = new Attribute("T1.A", 0.01);
        Attribute a2 = new Attribute("T1.B", 0.02);
        Attribute a3 = new Attribute("T1.C", 0.01);

        Attribute a4 = new Attribute("T2.D", 0.01);
        Attribute a5 = new Attribute("T2.E", 0.03);

        Attribute a6 = new Attribute("T3.F", 0.01);
        Attribute a7 = new Attribute("T3.G", 0.001);
        Attribute a8 = new Attribute("T3.H", 0.001);

        Attribute a9 = new Attribute("T4.I", 0.02);
        Attribute a10 = new Attribute("T4.L", 0.01);

        List<Attribute> t1 = new LinkedList<>();
        t1.add(a1);
        t1.add(a2);
        t1.add(a3);

        List<Attribute> t2 = new LinkedList<>();
        t2.add(a4);
        t2.add(a5);

        List<Attribute> t3 = new LinkedList<>();
        t3.add(a6);
        t3.add(a7);
        t3.add(a8);

        List<Attribute> t4 = new LinkedList<>();
        t4.add(a9);
        t4.add(a10);

        /**
         * Set up operations projected and homogeneous sets
         */
        List<Attribute> to1 = new LinkedList<>();
        to1.add(a1);
        to1.add(a2);
        Operation o1 = new Projection(to1);
        o1.setName("Projection1");

        List<Attribute> to2 = new LinkedList<>();
        to2.add(a4);
        Operation o2 = new Projection(to2);
        o2.setName("Projection2");

        List<Attribute> to3 = new LinkedList<>();
        to3.add(a1);
        to3.add(a4);
        Operation o3 = new JoinNAry(to3);
        o3.setName("Join3");

        List<Attribute> to4 = new LinkedList<>();
        to4.add(a6);
        to4.add(a7);
        to4.add(a8);
        Operation o4 = new Projection(to4);
        o4.setName("Projection4");

        List<Attribute> to5 = new LinkedList<>();
        to5.add(a9);
        to5.add(a10);
        Operation o5 = new Projection(to5);
        o5.setName("Projection5");

        List<Attribute> to6 = new LinkedList<>();
        to6.add(a6);
        Operation o6 = new Selection(to6);
        o6.setName("Selection6");

        List<Attribute> to7 = new LinkedList<>();
        to7.add(a10);
        Operation o7 = new Selection(to7);
        o7.setName("Selection7");

        List<Attribute> to8 = new LinkedList<>();
        to8.add(a1);
        to8.add(a6);
        to8.add(a10);
        Operation o8 = new CartesianProduct(to8);
        o8.setName("CartesianProduct8");

        List<Attribute> to9 = new LinkedList<>();
        to9.add(a6);
        to9.add(a10);
        Operation o9 = new Selection(to9);
        o9.setName("Selection9");


        List<Attribute> to10 = new LinkedList<>();
        to10.add(a1);
        to10.add(a2);
        List<Attribute> to11 = new LinkedList<>();
        to11.add(a1);
        to11.add(a2);
        Operation udfo1 = new UDF(to10, to11);
        udfo1.setName("UDF PS.LIN.1");

        Operation udfo2 = new UDF(to10, to11);
        udfo2.setName("UDF PS.LIN.2");

        /**
         * Set up leafs tables attributes
         */
        o1.setInputAttributes(t1);
        o2.setInputAttributes(t2);
        o4.setInputAttributes(t3);
        o5.setInputAttributes(t4);

        /**
         * Set up operation constraints
         */
        List<AttributeConstraint> acl3 = new LinkedList<>();
        acl3.add(new AttributeConstraint(a1, AttributeState.DETSYMENC));
        acl3.add(new AttributeConstraint(a4, AttributeState.DETSYMENC));

        udfo1.setConstraints(acl3);
        udfo2.setConstraints(acl3);

        List<AttributeConstraint> acl6 = new LinkedList<>();
        acl6.add(new AttributeConstraint(a6, AttributeState.DETSYMENC));
        o6.setConstraints(acl6);

        List<AttributeConstraint> acl7 = new LinkedList<>();
        acl7.add(new AttributeConstraint(a10, AttributeState.DETSYMENC));
        o7.setConstraints(acl7);

        List<AttributeConstraint> acl9 = new LinkedList<>();
        acl9.add(new AttributeConstraint(a6, AttributeState.DETSYMENC));
        acl9.add(new AttributeConstraint(a10, AttributeState.DETSYMENC));
        o9.setConstraints(acl9);

        /**
         * Set up performance & cost metrics, set up executors
         */

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
         * Adding providers authorizations
         */
        List<Attribute> p3plains = new LinkedList<>();
        List<Attribute> p3encs = new LinkedList<>();
        p3encs.add(a1);
        p3encs.add(a2);
        p3encs.add(a3);
        p3encs.add(a4);
        p3encs.add(a5);
        p3encs.add(a6);
        p3encs.add(a7);
        p3encs.add(a8);
        p3encs.add(a9);
        p3encs.add(a10);

        List<Attribute> p2plains = new LinkedList<>();
        List<Attribute> p2encs = new LinkedList<>();
        p2encs.add(a1);
        p2encs.add(a2);
        p2encs.add(a3);
        p2encs.add(a4);
        p2encs.add(a5);
        p2plains.add(a6);
        p2plains.add(a7);
        p2plains.add(a8);
        p2plains.add(a9);
        p2plains.add(a10);

        List<Attribute> p1plains = new LinkedList<>();
        List<Attribute> p1encs = new LinkedList<>();
        p1plains.add(a1);
        p1plains.add(a2);
        p1plains.add(a3);
        p1plains.add(a4);
        p1plains.add(a5);
        p1plains.add(a6);
        p1plains.add(a7);
        p1plains.add(a8);
        p1plains.add(a9);
        p1plains.add(a10);

        provider1.setAplains(p1plains);
        provider1.setAencs(p1encs);

        provider2.setAplains(p2plains);
        provider2.setAencs(p2encs);

        provider3.setAplains(p3plains);
        provider3.setAencs(p3encs);

        /**
         * Building the tree
         */
        TreeNode q1 = new TreeNode<Operation>(o1);
        TreeNode q2 = new TreeNode<Operation>(o2);

        List<TreeNode<Operation>> opsons1 = new LinkedList<>();
        opsons1.add(q1);
        opsons1.add(q2);

        TreeNode q3 = new TreeNode<Operation>(o3);
        q3.setSons(opsons1);

        List<TreeNode<Operation>> opsons2 = new LinkedList<>();
        opsons2.add(q3);


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

        TreeNode query = new TreeNode<Operation>(o9);
        query.setSons(opsons7);

        /**
         * Provider for semantics
         */
        System.out.println("1) QUERY STRUCTURE\n");
        System.out.println(query.printTree() + "\n");
        ProviderMetric pms = new ProviderMetric(0.04e-6, 0.0528, k * 0.0528, 1.5 * 0.04e-6, 1.5 * 0.04e-6, 45 * 0.04e-6, 45 * 0.04e-6);
        Provider providers = new Provider("Provider for semantics", pms);

        /**
         * Semantics
         */
        //settings
        TreeNodeSemantics.setHomeProvider(provider1);
        TreeNodeVolatileCostEngine.setHomeProvider(provider1);
        TreeNodeCostEngine.setHome(provider1);

        //udf completion, attributes synthesizing and computation of output relation profiles
        TreeNodeSemantics.synthesizePlanAttributes(query);
        System.out.println("\n2) QUERY AFTER UDF METRICS COMPLETION, ATTRIBUTES SYNTHESIZING AND OUTPUT RELATION PROFILE COMPUTED \n");
        System.out.println(query.printTree() + "\n");

        //semantic enrichment, oracle's cost of execution synthesizing
        TreeNodeSemantics.deriveCostBarriers(query, providers);
        TreeNodeSemantics.synthesizeExecutionCost(query);
        System.out.println("\n3) QUERY SEMANTIC ENRICHMENT, EXECUTION COST SYNTHESIZING \n");
        System.out.println(query.printTree() + "\n");

        //deriving operation's minimum required view
        TreeNodeSemantics.derivePlanMinimumReqView(query);
        System.out.println("\n4) QUERY MINIMUM REQUIRED VIEW \n");
        System.out.println(query.printTree() + "\n");

        /**
         * Oracle creation and referencing
         */
        TreeNode oracle = query.deepClone();
        TreeNode.bindOracle(query, oracle);
        System.out.println("\n5) ORACLE CREATION \n");
        System.out.println(query.printTreeReferences() + "\n");
        System.out.println("\n6) ORACLE \n");
        System.out.println(query.printTree() + "\n");

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
        //engine computation
        engine.computeAllocation();

        System.out.println("\n7) QUERY ASSIGNMENT RESULTS \n");
        System.out.println(query.printTree() + "\n");

        /**
         * Underlining choices and costs
         */
        System.out.println("\n8) UNDERLINING ASSIGNMENT RESULTS \n");
        System.out.println("Query\n" + query.printTreeAssignments() + "\n");
        System.out.println("Oracle\n" + oracle.printTreeAssignments() + "\n");
    }
}
