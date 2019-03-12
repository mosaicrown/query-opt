package Operation_allocator.Allocator;

import Operation_allocator.Actors.Operation;
import Operation_allocator.Actors.Provider;
import Operation_allocator.Data.AttributeConstraint;
import Operation_allocator.DebugManager.Debugger;
import Operation_allocator.DebugManager.LogType;
import Operation_allocator.DebugManager.Report;
import Operation_allocator.Misc.Pair;
import Operation_allocator.Statistics.Metrics.CostMetric;
import Operation_allocator.Trees.ExhaustiveSearch.BinaryDepthExplorer;
import Operation_allocator.Trees.Semantics.Features;
import Operation_allocator.Trees.Semantics.Policy.RelationalProfilePolicy.PolicyMovesGenerator;
import Operation_allocator.Trees.Semantics.Policy.RelationalProfilePolicy.RelationProfile;
import Operation_allocator.Trees.Semantics.TreeNodeSemantics;
import Operation_allocator.Trees.Semantics.TreeNodeVolatileCostEngine;
import Operation_allocator.Trees.TreeNode;
import Operation_allocator.Trees.TreeNodeCostEngine;

import java.util.LinkedList;
import java.util.List;

public class OperationAllocator<T extends Operation> {

    private TreeNode<T> query;
    private TreeNode<T> oracle;

    private Provider p1;
    private Provider p2;
    private Provider p3;

    private static boolean debug_mode = false;
    private static BinaryDepthExplorer explorer = null;
    private static Debugger debugger = null;

    public static void setDebug_mode(boolean deb) {
        OperationAllocator.debug_mode = deb;
    }

    public static void setExplorer(BinaryDepthExplorer ex) {
        explorer = ex;
    }

    private PolicyMovesGenerator generator;


    public OperationAllocator() {
        query = null;
        oracle = null;
        p1 = null;
        p2 = null;
        p3 = null;
        generator = new PolicyMovesGenerator();
    }

    public TreeNode<T> getQuery() {
        return query;
    }

    public void setQuery(TreeNode<T> query) {
        this.query = query;
    }

    public static void setDebugger(Debugger debugger) {
        OperationAllocator.debugger = debugger;
    }

    public TreeNode<T> getOracle() {
        return oracle;
    }

    public void setOracle(TreeNode<T> oracle) {
        this.oracle = oracle;
    }

    public void computeAllocation() {
        generator.setP1(p1);
        generator.setP2(p2);
        generator.setP3(p3);
        TreeNodeVolatileCostEngine.setHomeProvider(p1);
        TreeNodeCostEngine.setHome(p1);
        TreeNodeSemantics.setHomeProvider(p1);
        compute(query);
        if (debug_mode) {
            //semantics configuration
            TreeNodeSemantics.setDebug_mode(true);
            TreeNodeSemantics.setDebugger(debugger);
            //relation profile configuration
            RelationProfile.setDebug_mode(true);
            RelationProfile.setDebugger(debugger);
        }
    }

    private void compute(TreeNode<T> tn) {
        for (TreeNode<T> tns : tn.getSons()
        ) {
            //move down to the leafs
            compute(tns);
        }
        //comeback
        List<Pair<List<AttributeConstraint>, Provider>> p3pairs = generator.allowedMoves(p3, tn);
        List<Pair<List<AttributeConstraint>, Provider>> p2pairs = generator.allowedMoves(p2, tn);
        List<Pair<List<AttributeConstraint>, Provider>> p1pairs = generator.decryptionMovesToHome(tn);
        List<Pair<List<AttributeConstraint>, Provider>> bestpair = new LinkedList<>();
        int dimp3p = 0;
        if (p3pairs != null)
            dimp3p = p3pairs.size();
        int dimp2p = 0;
        if (p2pairs != null)
            dimp2p = p2pairs.size();

        /**
         * DEBUG INFO
         */
        if (debug_mode)
            debugger.leaveTrace(
                    new Report(
                            "OperationAllocator",
                            LogType.GENERAL_INFO,
                            "\tAsking moves to p3 for op: " + tn.getElement().toString()
                                    + "\tAsking moves to p2 for op: " + tn.getElement().toString()
                                    + "\tAsking moves to p1 for op: " + tn.getElement().toString()
                    )
            );


        //best alternatives costs
        CostMetric cmbestp3 = null;
        CostMetric cmbestp2 = null;
        CostMetric cmbestp1 = null;
        CostMetric cmbestExt = null;
        CostMetric cmp1 = null;

        /**
         * New block
         * Even if the moves generator found a solution it is not true that it would be a valid one
         * The reason is that c.e.s. could merge equivalent set after operation execution and the new
         * set of sets could lead to a policy exception (need to simulate operation)
         */

        if (dimp3p > 0) {
            //chose the best cost alternative pair for provider p3
            cmbestp3 = TreeNodeVolatileCostEngine.simulateEncryptionAndComputeCost(tn, p3pairs.get(0).getFirst(), p3);
            if (cmbestp3 == null)
                dimp3p = 0;
            else {
                bestpair.add(p3pairs.get(0));
                cmbestExt = cmbestp3;
                if (debug_mode)
                    debugger.leaveTrace(
                            new Report(
                                    "OperationAllocator",
                                    LogType.DATA_TRACE,
                                    "\tCost of allocating to p3 op: " + tn.getElement().toString() + "\t cost: "
                                            + cmbestp3.toString()
                            )
                    );
            }
        }
        if (dimp2p > 0) {
            //chose the best cost alternative pair for provider p2
            cmbestp2 = TreeNodeVolatileCostEngine.simulateEncryptionAndComputeCost(tn, p2pairs.get(0).getFirst(), p2);
            if (cmbestp2 == null)
                dimp2p = 0;
            else {
                if (dimp3p > 0) {
                    if (cmbestp3.Ct > cmbestp2.Ct) {
                        bestpair.remove(0);
                        bestpair.add(p2pairs.get(0));
                        cmbestExt = cmbestp2;
                    }
                } else {
                    bestpair.add(p2pairs.get(0));
                    cmbestExt = cmbestp2;
                    if (cmbestExt == null)
                        dimp2p = 0;
                }
                if (debug_mode)
                    debugger.leaveTrace(
                            new Report(
                                    "OperationAllocator",
                                    LogType.DATA_TRACE,
                                    "\tCost of allocating to p2 op: " + tn.getElement().toString() + "\t cost: "
                                            + cmbestp2.toString()
                            )
                    );
            }

        }

        cmbestp1 = TreeNodeVolatileCostEngine.simulateEncryptionAndComputeCost(tn, p1pairs.get(0).getFirst(), p1);

        if (debug_mode) {
            debugger.leaveTrace(
                    new Report(
                            "OperationAllocator",
                            LogType.DATA_TRACE,
                            "\tCost of allocating to p1 op: " + tn.getElement().toString() + "\t cost: "
                                    + cmbestp1.toString()
                    )
            );
            debugger.leaveTrace(
                    new Report(
                            "OperationAllocator",
                            LogType.ASSIGNMENT_CANDIDATES,
                            "\tNumber of execution alternatives found for op:\t"+tn.getElement().toString()
                                    +"\t:\tdim3p: " + dimp3p + "\tdim2p: " + dimp2p
                    )
            );
        }

        /**
         * At this point the cost allocator knows the best cost option to execute operation outsourcing
         */

        /**
         * Here starts operation assignment
         */
        if (cmbestExt != null && !tn.isLeaf()) {
            /**
             * Try to assign operation to the cheapest alternative using semantics and the continue backtrack
             * if the total cost threshold principle is not satisfied then launch validation/correction algorithm
             */
            if (cmbestExt.Ct < tn.getOracle().getElement().getCost().Ct) {
                //assign the operation to best external provider and continue
                //1) apply encryption moves
                TreeNodeSemantics.applyEncryptionAndPropagateEffects(tn, bestpair.get(0).getFirst(), bestpair.get(0).getSecond());
                //2) compute new tn's cost and hook it
                TreeNodeCostEngine.computeAndSetCost(tn, bestpair.get(0).getSecond());
                /**
                 * Debug info
                 */
                if (debug_mode)
                    debugger.leaveTrace(
                            new Report(
                                    "OperationAllocator",
                                    LogType.OPERATION_ASSIGNMENT,
                                    "\tCASE 1 Allocating of: " + tn.getElement().toString() + "\tto:"
                                            + bestpair.get(0).getSecond().selfDescription()
                                            + "\n\t" + tn.getElement().getCost().toString()
                            )
                    );
            } else if (tn.getInfo().hasFeature(Features.EXPCPUDOMCOST)) {
                //ask to the strategy explorer for next move (the algorithm tries to maximize strategy profit)
                boolean move = explorer.getNextStrategyMove();
                if (!move) {
                    //try outsourcing operation
                    //assign operation to external provider
                    TreeNodeSemantics.applyEncryptionAndPropagateEffects(tn, bestpair.get(0).getFirst(), bestpair.get(0).getSecond());
                    TreeNodeCostEngine.computeAndSetCost(tn, bestpair.get(0).getSecond());
                    /**
                     * Debug info
                     */
                    if (debug_mode)
                        debugger.leaveTrace(
                                new Report(
                                        "OperationAllocator",
                                        LogType.OPERATION_ASSIGNMENT,
                                        "\tCASE 2 Allocating of: " + tn.getElement().toString() + "\tto:"
                                                + bestpair.get(0).getSecond().selfDescription()
                                                + "\n\t" + tn.getElement().getCost().toString()
                                )
                        );

                } else {
                    //do not outsource
                    //assign operation to proprietary provider and launch validation/correction algorithm
                    TreeNodeSemantics.applyEncryptionAndPropagateEffects(tn, p1pairs.get(0).getFirst(), p1);
                    //2) compute new tn's cost and hook it
                    TreeNodeCostEngine.computeAndSetCost(tn, p1);
                    /**
                     * Debug info
                     */
                    if (debug_mode)
                        debugger.leaveTrace(
                                new Report(
                                        "OperationAllocator",
                                        LogType.OPERATION_ASSIGNMENT,
                                        "\tCASE 3 Allocating of: " + tn.getElement().toString() + "\tto:"
                                                + p1.selfDescription()
                                                + "\n\t" + tn.getElement().getCost().toString()
                                )
                        );
                    /**
                     * Validation step
                     */
                    //launch validation/correction algorithm
                    validateDecision(tn);
                }
            }
        } else { //implies no execution alternative to provider 1 found (or leaf operator)
            /**
             * Allocate the operation to proprietary provider removing encryption and launching cost control mechanism
             */
            TreeNodeSemantics.applyEncryptionAndPropagateEffects(tn, p1pairs.get(0).getFirst(), p1);
            //2) compute new tn's cost and hook it
            TreeNodeCostEngine.computeAndSetCost(tn, p1);
            /**
             * Debug info
             */
            if (debug_mode)
                debugger.leaveTrace(
                        new Report(
                                "OperationAllocator",
                                LogType.OPERATION_ASSIGNMENT,
                                "\tCASE 4 Allocating of: " + tn.getElement().toString() + "\tto:"
                                        + p1.selfDescription()
                                        + "\n\t" + tn.getElement().getCost().toString()
                        )
                );
            /**
             * Check the total incremental cost and if it is greater than oracle cost launch validation/correction algorithm
             */
            if (tn.getElement().getCost().Ct > tn.getOracle().getElement().getCost().Ct) {
                /**
                 * Validation step
                 */
                //launch validation/correction algorithm, else continue
                validateDecision(tn);
            }
        }
        //root final validation
        if (tn.isRoot()) {
            if (!tn.getElement().getExecutor().selfDescription().equals(p1.selfDescription())) {
                //extra validation step
                CostMetric incrCost = new CostMetric();
                if (!TreeNodeCostEngine.validateCost(tn, incrCost)) {
                    //launch correction function
                    correctDecision(tn);
                } else {
                    CostMetric oldC = tn.getElement().getCost();
                    oldC.Cm += incrCost.Cm;
                    oldC.Cc += incrCost.Cc;
                    oldC.Ct += incrCost.Cm + incrCost.Cc;
                }
            }
            //register a final strategy result
            explorer.registerStrategyResult(tn.getElement().getCost());
            if (debug_mode)
                debugger.leaveTrace(
                        new Report(
                                "OperationAllocator",
                                LogType.PLAN_ALTERNATIVE,
                                "\tREGISTERED ALTERNATIVE with cost: " + tn.getElement().getCost().Ct
                        )
                );
        }

    }

    private void validateDecision(TreeNode<T> tn) {
        /**
         * When the validation/correction algorithm is thrown on a TreeNode tn only his offspring is validated
         * tn is forced to provider 1 without check of alternatives
         */
        if (debug_mode)
            debugger.leaveTrace(
                    new Report(
                            "OperationAllocator",
                            LogType.ASSIGNMENT_VALIDATION,
                            "\tjumped to son"
                    )
            );

        for (TreeNode<T> tns : tn.getSons()
        ) {
            if (!TreeNodeCostEngine.validateCost(tns, new CostMetric())) {
                //launch correction function
                correctDecision(tns);
            }
        }
        //at this point there may have been a correction, need to update tree synthesized cost
        /**
         * Debug info
         */
        if (debug_mode)
            debugger.leaveTrace(
                    new Report(
                            "OperationAllocator",
                            LogType.ASSIGNMENT_VALIDATION,
                            "\tBefore correction of : " + tn.getElement().toString() + " cost: "
                                    + tn.getElement().getCost().toString()
                    )
            );
        correctCost(tn);
        /**
         * Debug info
         */
        if (debug_mode)
            debugger.leaveTrace(
                    new Report(
                            "OperationAllocator",
                            LogType.ASSIGNMENT_VALIDATION,
                            "\tAfter correction of : " + tn.getElement().toString() + " cost: "
                                    + tn.getElement().getCost().toString()
                    )
            );
    }

    private void correctDecision(TreeNode<T> tn) {
        //compute new cost metric with new fixed assignment
        List<Pair<List<AttributeConstraint>, Provider>> tpair = generator.decryptionMovesToHome(tn);
        TreeNodeSemantics.applyEncryptionAndPropagateEffects(tn, tpair.get(0).getFirst(), p1);
        /**
         * Debug info
         */
        if (debug_mode)
            debugger.leaveTrace(
                    new Report(
                            "OperationAllocator",
                            LogType.ASSIGNMENT_CORRECTION,
                            "\tCorrection of: " + tn.getElement().toString() + "\tto:" + p1.selfDescription()
                                    + "\t" + tn.getElement().getCost().toString()
                    )
            );
        //validate assignment
        validateDecision(tn);

    }

    private void correctCost(TreeNode<T> tn) {
        //at this point there may have been multiple subsequent corrections (parent-son), this means that the previous
        //decision correction has to be corrected again, enforcing data distribution and updating decryption cost to zero
        List<Pair<List<AttributeConstraint>, Provider>> tpair = generator.decryptionMovesToHome(tn);
        TreeNodeSemantics.applyEncryptionAndPropagateEffects(tn, tpair.get(0).getFirst(), p1);
        TreeNodeCostEngine.computeAndSetCost(tn, p1);
    }

    public Provider getP1() {
        return p1;
    }

    public void setP1(Provider p1) {
        TreeNodeCostEngine.setHome(p1);
        this.p1 = p1;
    }

    public Provider getP2() {
        return p2;
    }

    public void setP2(Provider p2) {
        this.p2 = p2;
    }

    public Provider getP3() {
        return p3;
    }

    public void setP3(Provider p3) {
        this.p3 = p3;
    }
}
