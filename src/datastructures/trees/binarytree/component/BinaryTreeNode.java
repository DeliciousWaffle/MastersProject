package datastructures.trees.binarytree.component;

/**
 * Class representing a node used for a binary tree. Contains a reference to
 * a left child, a right child, and a parent.
 * @param <E> is an element that is node contains
 */
public class BinaryTreeNode<E>{

    private E data;
    private BinaryTreeNode<E> leftChild, rightChild, parent;
    private boolean visited;

    public BinaryTreeNode(E data, BinaryTreeNode<E> parent) {
        this.data = data;
        this.leftChild = null;
        this.rightChild = null;
        this.parent = parent;
        this.visited = false;
    }

    public void setData(E data) {
        this.data = data;
    }

    public E getData() {
        return data;
    }

    public void setLeftChild(BinaryTreeNode<E> leftChild) {
        this.leftChild = leftChild;
    }

    public BinaryTreeNode<E> getLeftChild() {
        return leftChild;
    }

    public void setRightChild(BinaryTreeNode<E> rightChild) {
        this.rightChild = rightChild;
    }

    public BinaryTreeNode<E> getRightChild() {
        return rightChild;
    }

    public void setParent(BinaryTreeNode<E> parent) {
        this.parent = parent;
    }

    public BinaryTreeNode<E> getParent() {
        return parent;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isVisited() {
        return visited;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeafNode() {
        return leftChild == null && rightChild == null;
    }

    public boolean hasLeftChild() { return leftChild != null; }

    public boolean hasRightChild() { return rightChild != null; }
}