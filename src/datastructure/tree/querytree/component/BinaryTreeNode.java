package datastructure.tree.querytree.component;

public class BinaryTreeNode<E> {

    private E data;
    private BinaryTreeNode<E> leftChild, rightChild;

    public BinaryTreeNode(E data) {
        this.data = data;
        this.leftChild = null;
        this.rightChild = null;
    }

    public void setData(E data) {
        this.data = data;
    }

    public void setLeftChild(BinaryTreeNode<E> leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(BinaryTreeNode<E> rightChild) {
        this.rightChild = rightChild;
    }

    public E getData() {
        return data;
    }

    public BinaryTreeNode<E> getLeftChild() {
        return leftChild;
    }

    public BinaryTreeNode<E> getRightChild() {
        return rightChild;
    }
}