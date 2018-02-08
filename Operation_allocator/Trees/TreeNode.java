package Trees;

import Actors.Operation;
import Trees.Semantics.Features;
import Trees.Semantics.MetaChoke;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class TreeNode<E extends Operation> implements Serializable {

    private TreeNode<E> parent;

    private List<TreeNode<E>> sons;

    private E element;

    private MetaChoke info;

    public TreeNode() {
        parent = null;
        sons = new LinkedList<>();
        element = null;
        info = new MetaChoke<Object>();
    }

    public TreeNode(E elem) {
        parent = null;
        sons = new LinkedList<>();
        element = elem;
        info = new MetaChoke<Object>();
    }

    public MetaChoke getInfo() {
        return info;
    }

    public void setInfo(MetaChoke info) {
        this.info = info;
    }

    public TreeNode<E> getParent() {
        return parent;
    }

    public void setParent(TreeNode<E> parent) {
        this.parent = parent;
    }

    public List<TreeNode<E>> getSons() {
        return sons;
    }

    public void setSons(List<TreeNode<E>> sons) {
        this.sons = sons;
        for (TreeNode<E> tn : sons
                ) {
            tn.setParent(this);
        }
    }

    public E getElement() {
        return element;
    }

    public void setElement(E element) {
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

    public TreeNode<E> deepClone() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (TreeNode<E>) ois.readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public String printTree() {
        return printTreeWithLevel(1);
    }

    public String printTreeWithLevel(int j) {
        String s = this.getElement().toString()
                + "\t Ct:" + this.getElement().getCost().Ct
                + "\t Ce:" + this.getElement().getCost().Ce
                + "\t Cm:" + this.getElement().getCost().Cm
                + "\t Cc:" + this.getElement().getCost().Cc;
        s += "\tFeatures:";
        for (Object f : this.getInfo().getFeatures()
                ) {
            s += " " + f.toString();
        }
        s += "\tPolicy: " + this.getElement().getPolicy().printPolicy();
        s += "\n";

        for (TreeNode<E> tns : this.getSons()
                ) {
            for (int i = 0; i < j; i++)
                s += "\t";
            s += "|__" + tns.printTreeWithLevel(j + 1);
        }
        return s;
    }
}
