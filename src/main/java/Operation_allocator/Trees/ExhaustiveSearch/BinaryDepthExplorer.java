package Operation_allocator.Trees.ExhaustiveSearch;

import Operation_allocator.Statistics.Metrics.CostMetric;

/**
 * This class drives execution in depth mode
 * Binary strategy is supported
 */
public final class BinaryDepthExplorer {

    private static int MAXIMUM_DEPTH = 0;
    private int actualDepth = 0;

    //strategy state
    private BinaryTree global = null;
    //strategy root
    private BinaryTree root = null;

    //current strategy list
    private String currMoves = "";

    private String bestStrategy = "";
    //best strategy mode
    private boolean bestMode = false;
    //best strategy index
    private int index = 0;

    public BinaryDepthExplorer() {
        root = new BinaryTree(0);
        global = root;
    }

    /**
     * Return strategy move to be applied
     * False stands for left binary move
     *
     * @return
     */
    public boolean getNextStrategyMove() {
        //best strategy mode
        if (bestMode)
            return getNextBestStrategyMove();
        //default move to right(no risk and move to home)
        boolean move = true;
        /**
         * look at the state and ask the question
         */
        if (actualDepth < MAXIMUM_DEPTH) {
            //retrace the path to the consistent exploration point
            actualDepth++;
            if ((actualDepth - 1) < currMoves.length()) {
                return Boolean.parseBoolean(currMoves.substring(actualDepth, actualDepth));
            }
            //left strategy outsource
            if (global.getLeftSon() == null) {
                global = global.createLeftSon();
                currMoves = currMoves + "0";
                return false;
            } else {
                //right strategy
                global = global.createRightSon();
                currMoves = currMoves + "1";
                return true;
            }
        }
        return move;
    }

    /**
     * Register a strategy cost
     *
     * @param cm
     */
    public void registerStrategyResult(CostMetric cm) {
        //discard registration if best mode is triggered
        if (bestMode)
            return;
        //set strategy final cost
        global.setCost(cm.copy());
        if (global != root) {
            //jump to closest strategy (the one with the last opposite choice)
            global = travelToUncompleteFather(global);

        }
    }

    /**
     * Retrieve best execution strategy
     */
    public void retrieveBestStrategy() {
        bestStrategy = root.getBestStrategyChoices();
    }

    private BinaryTree travelToUncompleteFather(BinaryTree actualBtree) {
        while (actualBtree.getParent() != null) {
            actualBtree = actualBtree.getParent();
            //reconstruct strategy choices path
            currMoves = currMoves.substring(0, currMoves.length() - 1);
            //adjust depth for next search
            actualDepth--;
            if (actualBtree.getRightSon() == null)
                break;
        }
        return actualBtree;
    }

    /**
     * Return next move to implement best strategy
     *
     * @return move
     */
    public boolean getNextBestStrategyMove() {
        if (index < bestStrategy.length()) {
            boolean move = '1' == bestStrategy.charAt(index);
            index++;
            return move;
        }
        return true;
    }

    /**
     * Answer to the possibility to look for new strategy
     *
     * @return
     */
    public boolean existAlternative() {
        //is there still the possibility to explore?
        if (root.getCost() != null || global.getRightSon() != null)
            return false;
        return true;
    }

    public static int getMaximumDepth() {
        return MAXIMUM_DEPTH;
    }

    public static void setMaximumDepth(int maximumDepth) {
        MAXIMUM_DEPTH = maximumDepth;
    }

    public BinaryTree getGlobal() {
        return global;
    }

    public void setGlobal(BinaryTree global) {
        this.global = global;
    }

    public boolean isBestMode() {
        return bestMode;
    }

    public void setBestMode(boolean bestMode) {
        this.bestMode = bestMode;
    }

    public String printStrategySpace() {
        return root.printAlternativesTree();
    }
}
