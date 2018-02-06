package Trees;

import java.util.List;

public class TreeNode<E> {

    private TreeNode<E> parent;

    private List<TreeNode<E>> sons;

    private E element;

    public TreeNode() {

    }

    public TreeNode(E elem) {
        parent = null;
        sons = null;
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
}
