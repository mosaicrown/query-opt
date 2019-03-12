package Operation_allocator.Trees;

import Operation_allocator.Trees.Semantics.SemanticOperations.EncOperation;
import Operation_allocator.Actors.Operation;
import Operation_allocator.Data.Attribute;
import Operation_allocator.Data.AttributeConstraint;
import Operation_allocator.Trees.Semantics.MetaChoke;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public final class TreeNode<T extends Operation> implements Serializable {

    private TreeNode<T> parent;
    private List<TreeNode<T>> sons;
    private TreeNode<T> oracle;

    private T element;
    private MetaChoke info;

    //fictitious operation added to the plan
    private EncOperation encryption;

    public TreeNode() {
        parent = null;
        sons = new LinkedList<>();
        oracle = null;

        element = null;
        info = new MetaChoke<Object>();

        encryption = new EncOperation();
    }

    public TreeNode(T elem) {
        parent = null;
        sons = new LinkedList<>();
        oracle = null;

        element = elem;
        info = new MetaChoke<Object>();

        encryption = new EncOperation();
    }

    public TreeNode<T> getOracle() {
        return oracle;
    }

    public void setOracle(TreeNode<T> oracle) {
        this.oracle = oracle;
    }

    public MetaChoke getInfo() {
        return info;
    }

    public void setInfo(MetaChoke info) {
        this.info = info;
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    public List<TreeNode<T>> getSons() {
        return sons;
    }

    public void setSons(List<TreeNode<T>> sons) {
        this.sons = sons;
        for (TreeNode<T> tn : sons
                ) {
            tn.setParent(this);
        }
    }

    public T getElement() {
        return element;
    }

    public void setElement(T element) {
        this.element = element;
    }

    public boolean isLeaf() {
        if (sons.isEmpty())
            return true;
        return false;
    }

    public boolean isRoot() {
        if (parent == null)
            return true;
        return false;
    }

    public EncOperation getEncryption() {
        return encryption;
    }

    public void setEncryption(EncOperation encryption) {
        this.encryption = encryption;
    }

    @SuppressWarnings("unchecked")
    public TreeNode<T> deepClone() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (TreeNode<T>) ois.readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Complete toString to make easy to see all the content of an operation
     *
     * @return
     */
    public String printTree() {
        return printTreeWithLevel(1);
    }

    public String printTreeWithLevel(int j) {
        String tabs = "";
        for (int i = 0; i < j; i++)
            tabs += "\t";
        String s = this.getElement().toString()
                + "\t Fam.name:  " + this.getElement().getFamilyname()
                + "\n";
        s += tabs;
        if (this.getElement().getExecutor() != null)
            s += "\t Executor:  " + this.getElement().getExecutor().selfDescription();
        s += "\t Ct:" + this.getElement().getCost().Ct
                + "\t Ce:" + this.getElement().getCost().Ce
                + "\t Cm:" + this.getElement().getCost().Cm
                + "\t Cc:" + this.getElement().getCost().Cc
                + "\n";
        if (this.getElement().getOp_metric() != null) {
            s += tabs;
            s += "\t Op. metrics:  " + this.getElement().getOp_metric().toString()
                    + "\n";
        }

        s += tabs;
        s += "\t Features:  ";
        for (Object f : this.getInfo().getFeatures()
                ) {
            s += "*" + f.toString();
        }
        s += "\n";

        if (this.getElement().getMinimumReqView() != null) {
            s += tabs;
            s += "\t Minimum required view:  " + this.getElement().getMinimumReqView().toString()
                    + "\n";
        }
        if (this.getElement().getOutput_rp() != null) {
            s += tabs;
            s += "\t Output Relation Profile:  " + this.getElement().getOutput_rp().toString()
                    + "\n";
        }
        if (this.getElement().getInputAttributes().size() > 0) {
            s += tabs;
            s += "\t Input attributes:  | ";
            for (Attribute a : this.getElement().getInputAttributes()
                    ) {
                s += a.longToString() + " | ";
            }
            s += "\n";
        }
        if (this.getElement().getHomogeneousSet().size() > 0) {
            s += tabs;
            s += "\t Homogeneous set:  | ";
            for (Attribute a : this.getElement().getHomogeneousSet()
                    ) {
                s += a.shortToString() + " | ";
            }
            s += "\n";
        }
        if (this.getElement().getProjectedSet().size() > 0) {
            s += tabs;
            s += "\t Projected set:  | ";
            for (Attribute a : this.getElement().getProjectedSet()
                    ) {
                s += a.shortToString() + " | ";
            }
            s += "\n";
        }
        if (this.getElement().getConstraints() != null) {
            s += tabs;
            s += "\t Attribute constraints:  | ";
            for (AttributeConstraint a : this.getElement().getConstraints()
                    ) {
                s += a.longToString() + " | ";
            }
            s += "\n";
        }

        s += tabs;
        s += "\t Applied encryption:  " + this.getEncryption().toString();
        s += "\n";

        for (TreeNode<T> tns : this.getSons()
                ) {
            s += tabs + "|____" + tns.printTreeWithLevel(j + 1);
        }
        return s;
    }

    public String printTreeReferences() {
        return printTreeReferencesWithLevel(1);
    }

    private String printTreeReferencesWithLevel(int j) {
        String s = "" + this.hashCode();
        if (oracle != null)
            s += "-->" + oracle.hashCode();
        s += "\n";

        for (TreeNode<T> tns : this.getSons()
                ) {
            for (int i = 0; i < j; i++)
                s += "\t";
            s += "|__" + tns.printTreeReferencesWithLevel(j + 1);
        }
        return s;
    }

    public String printTreeAssignments() {
        return printTreeAssignmentslev(1);
    }

    private String printTreeAssignmentslev(int j) {
        String s = this.getElement().toString() + "-->";
        s += this.getElement().getExecutor().selfDescription();
        s += " (cost: " + this.getElement().getCost().Ct + ")";
        s += "\n";

        for (TreeNode<T> tns : this.getSons()
                ) {
            for (int i = 0; i < j; i++)
                s += "\t";
            s += "|__" + tns.printTreeAssignmentslev(j + 1);
        }
        return s;
    }

    /**
     * Bind equal TreeNodes
     *
     * @param qo
     */
    public static <Z extends Operation> void bindOracle(TreeNode<Z> q, TreeNode<Z> qo) {
        q.setOracle(qo);
        int i = 0;
        List sons = q.getSons();
        if (sons != null)
            while (i < sons.size()) {
                bindOracle(q.getSons().get(i), qo.getSons().get(i));
                i++;
            }
    }
}
