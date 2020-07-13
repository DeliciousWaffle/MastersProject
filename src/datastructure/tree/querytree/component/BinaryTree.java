package datastructure.tree.querytree.component;

import java.util.Iterator;

public class BinaryTree<T> implements Iterable<T> {

    private BinaryTreeNode<T> root;
    private int size;

    public BinaryTree(BinaryTreeNode<T> root) {

        this.root = root;
        this.size++;
    }

    public BinaryTreeNode<T> getRoot() { return root; }

    public void setLeftChild(BinaryTreeNode<T> current, BinaryTreeNode<T> leftChild) {
        current.setLeftChild(leftChild);
        size++;
    }

    public void setRightChild(BinaryTreeNode<T> current, BinaryTreeNode<T> rightChild) {
        current.setRightChild(rightChild);
        size++;
    }

    public BinaryTreeNode<E> getLeftChild(BinaryTreeNode<E> current) {
        return current.getLeftChild();
    }

    public BinaryTreeNode<E> getRightChild(BinaryTreeNode<E> current) {
        return current.getRightChild();
    }

    public BinaryTreeNode<E> setGetLeftChild(BinaryTreeNode<E> current, BinaryTreeNode<E> leftChild) {
        setLeftChild(current, leftChild);
        return current.getLeftChild();
    }

    public BinaryTreeNode<E> setGetRightChild(BinaryTreeNode<E> current, BinaryTreeNode<E> rightChild) {
        setRightChild(current, rightChild);
        return current.getRightChild();
    }

    @Override
    public Iterator<E> iterator() {
        return new PreorderTraversal(this);
    }

    private class PreorderTraversal implements Iterator<BinaryTreeNode> {

        BinaryTree<BinaryTreeNode> binaryTree;

        public PreorderTraversal(BinaryTree<BinaryTreeNode<E>> binaryTree) {
            this.binaryTree = binaryTree;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public BinaryTreeNode<E> next() {
            return null;
        }
    }
}