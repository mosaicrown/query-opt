package Trees;

import Actors.Operation;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class TreeNode<E extends Serializable> implements Serializable{

    private TreeNode<E> parent;

    private List<TreeNode<E>> sons;

    private E element;

    public TreeNode() {
        parent = null;
        sons = new LinkedList<>();
        element = null;
    }

    public TreeNode(E elem) {
        parent = null;
        sons = new LinkedList<>();
        element = elem;
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
        for (TreeNode<E> tn:sons
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
        if (sons == null)
            return true;
        return false;
    }

    public boolean isRoot(){
        if(parent == null)
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
}
