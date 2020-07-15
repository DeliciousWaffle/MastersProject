package datastructure.tree.binarytree;

import datastructure.tree.binarytree.component.BinaryTreeNode;

import java.util.Iterator;
import java.util.List;

/**
 * A class representing a simple generic binary tree. Nodes are connected bi-directionally which
 * is a little strange. Adding new nodes is also a bit strange as a series of locations are provided
 * in order to insert at the correct location.
 * @param <T> is the type that this binary tree is
 */
public class BinaryTree<T> implements Iterable<T> {

    // locations in which to move the pointer of the tree
    public enum Traverse {
        LEFT, RIGHT, UP
    }

    private BinaryTreeNode<T> root;
    private int size;

    private boolean debugging;

    public BinaryTree(T element) {
        this.root = new BinaryTreeNode<>(element, null);
        this.size = 1;
        this.debugging = false;
    }

    public void setRoot(T element) {
        this.root = new BinaryTreeNode<>(element, null);
    }

    public T getRoot() { return root.getData(); }

    /**
     * Given a series of traversals to perform and an element, adds a new node at the
     * given location. If adding an element in the location of a child node that exists,
     * performs an insertion.
     * @param traversals are a list of traversals to perform
     * @param element is the element to insert
     */
    public void addChild(List<Traverse> traversals, Traverse location, T element) {

        // only want children
        if(location == Traverse.UP) {
            System.out.println("In BinaryTree.addChild()");
            System.out.println("UP not supported!");
            return;
        }

        BinaryTreeNode<T> pointer = traverse(traversals);
        BinaryTreeNode<T> child = new BinaryTreeNode<>(element, pointer);

        if(pointer.hasLeftChild() && location == Traverse.LEFT) {

            BinaryTreeNode<T> parentsLeftChild = pointer.getLeftChild();
            parentsLeftChild.setParent(child);
            child.setLeftChild(parentsLeftChild);
            pointer.setLeftChild(child);

        } else if(pointer.hasRightChild() && location == Traverse.RIGHT) {

            BinaryTreeNode<T> parentsRightChild = pointer.getRightChild();
            parentsRightChild.setParent(child);
            child.setRightChild(parentsRightChild);
            pointer.setRightChild(child);

        } else {

            switch (location) {
                case LEFT:
                    pointer.setLeftChild(child);
                    break;
                case RIGHT:
                    pointer.setRightChild(child);
                    break;
            }
        }

        size++;
    }

    /**
     * Given a series of traversals, returns the data contained in the last traversed node.
     * @param traversals are a list of traversals to perform
     * @return the data of the last traversed node
     */
    public T getData(List<Traverse> traversals) {
        return traverse(traversals).getData();
    }

    /**
     * Given a series of traversals, removes the last traversed node.
     * @param traversals are a list of traversals to perform
     * @return whether the removal was successful
     */
    public boolean removeLeafNode(List<Traverse> traversals) {

        BinaryTreeNode<T> toRemove = traverse(traversals);

        // make sure that this node to remove is a leaf node
        if(! toRemove.isLeafNode()) {
            return false;
        }

        // edge case: removing the root
        if(toRemove.isRoot()) {
            root = null;
            return true;
        }

        // remove the parent's reference to the node to be remove
        BinaryTreeNode<T> toRemovesParent = toRemove.getParent();

        // figure out the location of the node to remove
        if(toRemovesParent.getLeftChild() == toRemove) {
            toRemovesParent.setLeftChild(null);
        } else {
            toRemovesParent.setRightChild(null);
        }

        return true;
    }

    /**
     * Helper method that traverses through the tree with the given traversals.
     * @param traversals are a list of traversals to perform
     * @return pointer to the last node traversed
     */
    private BinaryTreeNode<T> traverse(List<Traverse> traversals) {

        BinaryTreeNode<T> pointer = root;

        for(Traverse traversal : traversals) {
            switch(traversal) {
                case LEFT:
                    pointer = pointer.getLeftChild();
                    break;
                case RIGHT:
                    pointer = pointer.getRightChild();
                    break;
                case UP:
                    pointer = pointer.getParent();
                    break;
            }
        }

        return pointer;
    }

    public int getSize() {
        return size;
    }

    public void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }

    @Override
    public Iterator<T> iterator() {
        return new PreorderTraversal(this);
    }

    private class PreorderTraversal implements Iterator<T> {

        BinaryTree<T> binaryTree;
        BinaryTreeNode<T> pointer;
        int nodesVisited;

        public PreorderTraversal(BinaryTree<T> binaryTree) {
            this.binaryTree = binaryTree;
            this.pointer = binaryTree.root;
            this.nodesVisited = 0;

            System.out.print(debugging ? "Root: " + pointer.getData() + "\n" : "");
        }

        @Override
        public boolean hasNext() {

            if(nodesVisited == binaryTree.getSize()) {
                resetVisited();
            }

            return nodesVisited != binaryTree.getSize();
        }

        @Override
        public T next() {

            T returnData = pointer.getData();
            pointer.setVisited(true);
            nodesVisited++;

            boolean lastNode = nodesVisited == binaryTree.getSize();

            if(! lastNode) {

                boolean foundNextNode = false;

                while (! foundNextNode) {

                    if (pointer.getLeftChild() != null && !pointer.getLeftChild().isVisited()) {
                        pointer = pointer.getLeftChild();
                        foundNextNode = true;
                        System.out.print(debugging ? "Left: " + pointer.getData() + "\n" : "");

                    } else if (pointer.getRightChild() != null && !pointer.getRightChild().isVisited()) {
                        pointer = pointer.getRightChild();
                        foundNextNode = true;
                        System.out.print(debugging ? "Right: " + pointer.getData() + "\n" : "");

                    } else {
                        pointer = pointer.getParent();
                        System.out.print(debugging ? "Up\n" : "");
                    }
                }
            }

            return returnData;
        }

        public void resetVisited() {

            pointer = binaryTree.root;
            pointer.setVisited(false);
            nodesVisited = 1;

            while(nodesVisited != binaryTree.getSize()) {

                if (pointer.getLeftChild() != null && pointer.getLeftChild().isVisited()) {
                    pointer = pointer.getLeftChild();
                    pointer.setVisited(false);
                    nodesVisited++;

                } else if (pointer.getRightChild() != null && pointer.getRightChild().isVisited()) {
                    pointer = pointer.getRightChild();
                    nodesVisited++;
                    pointer.setVisited(false);

                } else {
                    pointer = pointer.getParent();
                }
            }
        }
    }
}