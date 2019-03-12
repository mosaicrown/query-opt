package Operation_allocator.Trees.ExhaustiveSearch;

import Operation_allocator.Statistics.Metrics.CostMetric;

import java.util.LinkedList;

public class BinaryTree {

    //Tree structure info
    private BinaryTree parent = null;
    private BinaryTree leftSon = null;
    private BinaryTree rightSon = null;
    //Tree trace (decision) info [0 is used to indicate is a left son]
    private int lineage = 0;
    //alternative cost metric
    private CostMetric cost = null;

    public BinaryTree(int lin) {
        lineage = lin;
    }

    public BinaryTree getParent() {
        return parent;
    }

    public BinaryTree createLeftSon() {
        BinaryTree ls = new BinaryTree(0);
        ls.setParent(this);
        this.setLeftSon(ls);
        return ls;
    }

    public BinaryTree createRightSon() {
        BinaryTree rs = new BinaryTree(1);
        rs.setParent(this);
        this.setRightSon(rs);
        return rs;
    }

    public BinaryTree getLeftSon() {
        return leftSon;
    }

    public void setLeftSon(BinaryTree leftSon) {
        this.leftSon = leftSon;
    }

    public BinaryTree getRightSon() {
        return rightSon;
    }

    public void setRightSon(BinaryTree rightSon) {
        this.rightSon = rightSon;
    }

    public void setParent(BinaryTree parent) {
        this.parent = parent;
    }

    public CostMetric getCost() {
        return cost;
    }

    public void setCost(CostMetric cost) {
        this.cost = cost;
    }

    public int getLineage() {
        return lineage;
    }

    /**
     * Returns path from root to leaf
     *
     * @param leaf
     * @return
     */
    public static String getStrategyChoices(BinaryTree leaf) {
        String choices = "";
        while (!leaf.isRoot()) {
            Integer i = leaf.getLineage();
            choices = i.toString() + choices;
            leaf = leaf.getParent();
        }
        return choices;
    }

    /**
     * Looks for the cheapest strategy tree leaf
     *
     * @param btree tree root
     * @return cheapest strategy leaf
     */
    private static BinaryTree selectBest(BinaryTree btree) {
        BinaryTree rbest = null, lbest = null;
        BinaryTree lson = btree.getLeftSon();
        BinaryTree rson = btree.getRightSon();
        //if btree is a leaf (a leaf has a final cost metric)
        if (lson == null && rson == null) {
            return btree;
        } else {
            if (lson != null)
                lbest = selectBest(lson);
            if (rson != null)
                rbest = selectBest(rson);
        }
        if (lbest != null) {
            if (rbest != null)
                if (rbest.getCost().Ct < lbest.getCost().Ct)
                    return rbest;
            return lbest;
        } else if (rbest != null)
            return rbest;
        return null;
    }

    /**
     * Best strategy choices returned
     *
     * @return
     */
    public String getBestStrategyChoices() {
        BinaryTree bestLeaf = BinaryTree.selectBest(this);
        return BinaryTree.getStrategyChoices(bestLeaf);
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public String printAlternativesTree() {
        return printTreeAlternativesWithLevel(0);
    }

    private String printTreeAlternativesWithLevel(int j) {
        String s = "Lev-" + j + "(" + this.lineage + ")";
        if (this.getCost() != null)
            s += "--> Ct: " + this.getCost().Ct;
        s += "\n";

        for (BinaryTree tns : this.getSons()
        ) {
            for (int i = 0; i < j + 2; i++)
                s += "\t";
            s += "|__" + tns.printTreeAlternativesWithLevel(j + 1);
        }
        return s;
    }

    private LinkedList<BinaryTree> getSons() {
        LinkedList<BinaryTree> l = new LinkedList<BinaryTree>();
        if (this.getLeftSon() != null)
            l.add(this.getLeftSon());
        if (this.getRightSon() != null)
            l.add(this.getRightSon());
        return l;
    }

}
