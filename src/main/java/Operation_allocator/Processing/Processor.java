package Operation_allocator.Processing;

import java.io.IOException;
import java.util.*;

import Operation_allocator.Actors.*;

import Operation_allocator.Allocator.*;

import Operation_allocator.Data.Attribute;

import Operation_allocator.DebugManager.Debugger;
import Operation_allocator.DebugManager.LogType;
import Operation_allocator.DebugManager.Report;
import Operation_allocator.Misc.Pair;

import Operation_allocator.Processing.NamespaceEnforcer.NamesEnforcer;
import Operation_allocator.Processing.Reader.SimpleReader;
import Operation_allocator.Processing.Writer.SimpleWriter;

import Operation_allocator.Trees.ExhaustiveSearch.BinaryDepthExplorer;
import Operation_allocator.Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;
import Operation_allocator.Trees.Semantics.TreeNodeSemantics;
import Operation_allocator.Trees.Semantics.TreeNodeVolatileCostEngine;
import Operation_allocator.Trees.TreeNode;
import Operation_allocator.Trees.TreeNodeCostEngine;


/**
 * This class acts like a simple processor for the whole project
 */
public class Processor {

    private static boolean debug_mode = false;
    private static boolean removeUniformVis = false;
    private static int maximumDepthStrategy = 0;
    private final static CharSequence depthPrefix = "depth=";
    private static Debugger debugger = null;

    @SuppressWarnings("unchecked")
    public static void main(String args[]) {

        /**
         * Debugger to store log trace
         */
        debugger = Debugger.getDebugger();


        /**
         * Data to be fetch
         */
        //DB schema to be simulated
        List<Attribute> attributes;
        //Provider test set with metrics and security profile authorizations
        List<Provider> providers;
        //Query to be processed
        TreeNode<Operation> query;

        /**
         * Assignment engine
         */
        OperationAllocator<Operation> engine = new OperationAllocator();

        /**
         * Depth strategy tracker
         */
        BinaryDepthExplorer strategyExplorer = new BinaryDepthExplorer();

        /**
         * Configure running test parameters
         */
        configureParameters(args);
        //safe depth exploration
        if (maximumDepthStrategy > 10)
            maximumDepthStrategy = 10;

        /**
         * Simple input parser to speed up testing
         * Params: 0->query, 1->schema, 2->authorizations, 3/4/5 -> visibility, debug mode and depth
         */

        /**
         * Input DOM parser
         */
        SimpleReader xmlReader;
        //XML inputs
        if (args.length > 0) {
            if (args.length > 3 || args.length == 3)
                xmlReader = new SimpleReader(args[0], args[1], args[2]);
            else
                xmlReader = new SimpleReader(args[0]);
        } else
            throw new RuntimeException("Query file not specified");
        //load configuration
        Pair<List<Attribute>, List<Provider>> conf = xmlReader.loadConfiguration();
        attributes = conf.getFirst();
        providers = conf.getSecond();
        //load query
        query = xmlReader.loadQuery(attributes, providers);

        /**
         * Output DOM parser
         */
        SimpleWriter xmlWriter = new SimpleWriter();

        /**
         * Names enforcement, ensuring the namespace to be consistent
         */
        //configuration
        NamesEnforcer nmse = new NamesEnforcer();
        nmse.setSchema(attributes);
        nmse.setQuery(query);
        nmse.setP1(providers.get(0));
        nmse.setP2(providers.get(1));
        nmse.setP3(providers.get(2));
        //name resolution
        nmse.resolveConflicts();

        /**
         * Configure semantics
         */
        configureSemanticsAndEngine(providers, engine, strategyExplorer);

        /**
         * Query processing & secure assignment
         */
        run(providers, query, engine, strategyExplorer);

        /**
         * Results written to XML files
         */
        xmlWriter.setQuery(query);
        xmlWriter.setCustomd(args[0]);
        xmlWriter.writeResults();
        xmlWriter.writeResultsExtended();

        System.out.println("\tDEBUG_MODE:" + debug_mode + "  U.V.R.:" + removeUniformVis + "  MAX_DEPTH:"
                + maximumDepthStrategy);

        //debug
        if (debug_mode) {

            debugger.leaveTrace(
                    new Report(
                            "Processor",
                            LogType.GENERAL_INFO,
                            "\tDEBUG_MODE:\t" + debug_mode + "\t  U.V.R.:\t" + removeUniformVis +
                                    "\t  MAX_DEPTH:\t" + maximumDepthStrategy
                    )
            );

            //retrieving console parameters
            debugger.leaveTrace(
                    new Report(
                            "Processor",
                            LogType.GENERAL_INFO,
                            "\t" + Arrays.toString(args)
                    )
            );
        }

        /**
         * Write log persistently
         */
        if (debug_mode)
            try {
                debugger.writeLog();
            } catch (IOException e) {
                System.out.println("IO Exception caused by debugger log write: " + e.getMessage());
            }

    }

    @SuppressWarnings("unchecked")
    private static void configureSemanticsAndEngine(List<Provider> providers, OperationAllocator<Operation> engine, BinaryDepthExplorer ex) {
        //semantics settings
        TreeNodeSemantics.setHomeProvider(providers.get(0));
        TreeNodeVolatileCostEngine.setHomeProvider(providers.get(0));
        TreeNodeCostEngine.setHome(providers.get(0));

        OperationAllocator.setDebug_mode(debug_mode);
        OperationAllocator.setDebugger(debugger);
        RelationProfile.setRemoveUniformVis(removeUniformVis);

        //set maximum exploration depth
        BinaryDepthExplorer.setMaximumDepth(Processor.maximumDepthStrategy);
        //hook strategy explorer to operation allocator
        OperationAllocator.setExplorer(ex);

        //test provider
        Provider ptest = new Provider("Provider for semantics", providers.get(0).getMetrics().deepClone());
        ptest.getMetrics().Km = providers.get(1).getMetrics().Km;
        TreeNodeSemantics.setTestProvider(ptest);
        //set engine providers
        engine.setP1(providers.get(0));
        engine.setP2(providers.get(1));
        engine.setP3(providers.get(2));
    }

    /**
     * Static parameters configuration by console inputs
     *
     * @param args
     */
    private static void configureParameters(String[] args) {
        if (args.length > 3) {
            Processor.removeUniformVis = args[3].equals("no_uvr");
            Processor.debug_mode = args[3].equals("debug_mode");
            if (args[3].contains(depthPrefix))
                Processor.maximumDepthStrategy = Integer.parseInt(args[3].substring(6));

            if (args.length > 4) {
                Processor.removeUniformVis = Processor.removeUniformVis || args[4].equals("no_uvr");
                Processor.debug_mode = Processor.debug_mode || args[4].equals("debug_mode");
                if (args[4].contains(depthPrefix))
                    Processor.maximumDepthStrategy = Integer.parseInt(args[4].substring(6));

                if (args.length > 5) {
                    Processor.removeUniformVis = Processor.removeUniformVis || args[5].equals("no_uvr");
                    Processor.debug_mode = Processor.debug_mode || args[5].equals("debug_mode");
                    if (args[5].contains(depthPrefix))
                        Processor.maximumDepthStrategy = Integer.parseInt(args[5].substring(6));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void run(List<Provider> providers, TreeNode<Operation> query, OperationAllocator<Operation> engine, BinaryDepthExplorer explorator) {
        /**
         * Semantics
         */
        /*****/long time1 = System.nanoTime();
        //udf completion, attributes synthesizing and computation of output relation profiles
        TreeNodeSemantics.synthesizePlanAttributes(query);
        /*****/long time2 = System.nanoTime();
        if (debug_mode) {
            debugger.leaveTrace(
                    new Report("Processor",
                            LogType.STEP_COMPLETION,
                            "\tQUERY AFTER ATTRIBUTES SYNTHESIZING\n\n\t" + query.printTree()
                    )
            );
        }

        /*****/long time3 = System.nanoTime();
        //semantic enrichment, oracle's cost of execution synthesizing
        TreeNodeSemantics.deriveCostBarriers(query);
        TreeNodeSemantics.synthesizeExecutionCost(query);
        /*****/long time4 = System.nanoTime();
        if (debug_mode) {
            debugger.leaveTrace(
                    new Report("Processor",
                            LogType.STEP_COMPLETION,
                            "\tQUERY AFTER SEMANTIC ENRICHMENT\n\n\t" + query.printTree()
                    )
            );
        }

        /*****/long time5 = System.nanoTime();
        //deriving operation's minimum required view
        TreeNodeSemantics.derivePlanMinimumReqView(query);
        /*****/long time6 = System.nanoTime();
        if (debug_mode) {
            debugger.leaveTrace(
                    new Report("Processor",
                            LogType.STEP_COMPLETION,
                            "\tQUERY AFTER MINIMUM REQUIRED VIEW DERIVATION\n\n\t" + query.printTree()
                    )
            );
        }

        /**
         * Oracle creation and referencing
         */

        /*****/long time7 = System.nanoTime();
        TreeNode<Operation> oracle = query.deepClone();
        TreeNode.bindOracle(query, oracle);
        /*****/long time8 = System.nanoTime();

        /**
         * Operation allocator configuration
         */
        //set query and oracle
        engine.setOracle(oracle);
        engine.setQuery(query);
        if (debug_mode) {
            debugger.leaveTrace(
                    new Report("Processor",
                            LogType.STEP_COMPLETION,
                            "\tORACLE CLONED"
                    )
            );
        }

        /**
         * Run exploration
         */
        /*****/long time9 = System.nanoTime();
        //engine computation
        while (explorator.existAlternative())
            engine.computeAllocation();
        /*****/long time10 = System.nanoTime();
        if (debug_mode) {
            debugger.leaveTrace(
                    new Report("Processor",
                            LogType.STEP_COMPLETION,
                            "\tQUERY ASSIGNMENT COMPUTED"
                    )
            );
        }

        /**
         * Correct plan, referencing and compute best alternative
         */
        /*****/long time11 = System.nanoTime();
        //best allocation computation
        explorator.setBestMode(true);
        explorator.retrieveBestStrategy();
        engine.computeAllocation();
        /*****/long time12 = System.nanoTime();
        if (debug_mode) {
            debugger.leaveTrace(
                    new Report("Processor",
                            LogType.STEP_COMPLETION,
                            "\tBEST ASSIGNMENT FOUND"
                    )
            );
        }


        /**
         * DEBUG INFO
         */

        if (debug_mode) {
            if (removeUniformVis)
                debugger.leaveTrace(
                        new Report("Processor",
                                LogType.GENERAL_INFO,
                                "\tREMOVED UNIFORM VISIBILITY TO C.E.S.\n" +
                                        "\tOutRelProf: " + query.getElement().getOutput_rp().toString()
                        )
                );
        }

        if (debug_mode) {
            debugger.leaveTrace(
                    new Report("Processor",
                            LogType.STEP_COMPLETION,
                            "\tORACLE AFTER PROCESSING\n\t" + oracle.printTree()
                    )
            );
            debugger.leaveTrace(
                    new Report("Processor",
                            LogType.STEP_COMPLETION,
                            "\tQUERY AFTER PROCESSING\n\t" + query.printTree()
                    )
            );
        }


        if (debug_mode) {
            debugger.leaveTrace(
                    new Report("Processor",
                            LogType.GENERAL_INFO,
                            "\tSTRATEGY SPACE\n\t" + explorator.printStrategySpace()
                    )
            );
        }
        /**
         * Debug info: brief results
         */
        System.out.println("\tSINGLE-PROVIDER PLAN:\n");
        System.out.println("\t" + oracle.printTreeAssignments());
        System.out.println("\n\tMULTI-PROVIDER PLAN:\n");
        System.out.println("\t" + query.printTreeAssignments());

        /**
         * Terminal printing of performance results
         */
        long delta1 = (time2 - time1);
        long delta2 = (time4 - time3);
        long delta3 = (time6 - time5);
        long delta4 = (time8 - time7);
        long delta5 = (time10 - time9);
        long delta6 = (time12 - time11);
        long totTime = delta1 + delta2 + delta3 + delta4 + delta5 + delta6;
        double c1 = query.getElement().getCost().Ct;
        double c2 = oracle.getElement().getCost().Ct;
        System.out.println("\n\tQUERY ALLOCATOR COST RESULTS:\n");
        System.out.println("\tMulti-provider plan expected cost:\t\t" + c1 + "\tsaved:\t" + String.format("%.1f", 100 * (1 - c1 / c2)) + "%");
        System.out.println("\n\tQUERY ALLOCATOR TIME PERFORMANCES:\n");
        System.out.println("\tSynthesizing attributes time:\t\t\t" + delta1 / 1e9 + "\tsec\t" + String.format("%.1f", (double) 100 * delta1 / totTime) + "%");
        System.out.println("\tDeriving barriers + single provider cost:\t" + delta2 / 1e9 + "\tsec\t" + String.format("%.1f", (double) 100 * delta2 / totTime) + "%");
        System.out.println("\tDeriving minimum required view:\t\t\t" + delta3 / 1e9 + "\tsec\t" + String.format("%.1f", (double) 100 * delta3 / totTime) + "%");
        System.out.println("\tOracle cloning and binding:\t\t\t" + delta4 / 1e9 + "\tsec\t" + String.format("%.1f", (double) 100 * delta4 / totTime) + "%");
        System.out.println("\tQuery cost & policy based exploration:\t\t" + delta5 / 1e9 + "\tsec\t" + String.format("%.1f", (double) 100 * delta5 / totTime) + "%");
        System.out.println("\tQuery best allocation retrieval:\t\t" + delta6 / 1e9 + "\tsec\t" + String.format("%.1f", (double) 100 * delta6 / totTime) + "%");
        System.out.println("\tTOTAL ASSIGNMENT TIME:\t\t\t\t" + totTime / 1e9 + "\tsec\n");
    }


}
