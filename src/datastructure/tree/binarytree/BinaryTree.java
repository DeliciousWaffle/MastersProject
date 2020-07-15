package datastructure.tree.binarytree;

import datastructure.tree.binarytree.component.BinaryTreeNode;

import java.util.Iterator;
import java.util.List;

/**
 * A class representing a simple generic binary tree. Nodes are connected bi-directionally which
 * is a little strange. Figured out how to make a class iterable which is cool. Can only do a preorder
 * traversal, but I think that's all I will need. Adding new nodes is also a bit strange as a series of
 * locations are provided in order to insert at the correct location. Class will be used by other tree
 * classes as a sort of a base to get work done.
 * @param <T> is the type that this binary tree is
 */
public class BinaryTree<T> implements Iterable<T> {

    // locations in which to traverse the tree, basically how you get around
    public enum Traverse {
        LEFT, RIGHT, UP
    }

    private BinaryTreeNode<T> root;
    private int size;

    /**
     * Creates a new instance of a binary tree. Forcing myself to set a root initially
     * because yeah.
     * @param element
     */
    public BinaryTree(T element) {
        this.root = new BinaryTreeNode<>(element, null);
        this.size = 1;
    }

    /**
     * @param element is the element that the root will hold
     */
    public void setRoot(T element) {
        this.root = new BinaryTreeNode<>(element, null);
    }

    /**
     * @return the element stored in the root
     */
    public T getRoot() { return root.getData(); }

    /**
     * Given a series of traversals to perform and an element, adds a new node at the
     * given child location. If the child location is occupied (ie. not null) then
     * this method performs an insert of the new node between the last node in the
     * traversed list and the node located at child location.
     * @param traversals are a list of traversals to perform
     * @param childLocation is the location of the child to add
     * @param element is the element to insert
     */
    public void addChild(List<Traverse> traversals, Traverse childLocation, T element) {

        // only want children
        if(childLocation == Traverse.UP) {
            System.out.println("In BinaryTree.addChild()");
            System.out.println("UP not supported!");
            return;
        }

        BinaryTreeNode<T> pointer = traverse(traversals);
        BinaryTreeNode<T> child = new BinaryTreeNode<>(element, pointer);

        // left child is occupied, perform a confusing insert
        if(pointer.hasLeftChild() && childLocation == Traverse.LEFT) {

            BinaryTreeNode<T> parentsLeftChild = pointer.getLeftChild();
            parentsLeftChild.setParent(child);
            child.setLeftChild(parentsLeftChild);
            pointer.setLeftChild(child);

        // right child is occupied, perform a confusing insert
        } else if(pointer.hasRightChild() && childLocation == Traverse.RIGHT) {

            BinaryTreeNode<T> parentsRightChild = pointer.getRightChild();
            parentsRightChild.setParent(child);
            child.setRightChild(parentsRightChild);
            pointer.setRightChild(child);

        // we're good to go, just add regularly
        } else {

            switch (childLocation) {
                case LEFT:
                    pointer.setLeftChild(child);
                    break;
                case RIGHT:
                    pointer.setRightChild(child);
                    break;
            }
        }

        // don't forget to increase size!
        size++;
    }

    /**
     * Given a series of traversals and a child location returns the data contained in the child.
     * @param traversals are a list of traversals to perform
     * @param childLocation is the location of the child to get the data from
     * @return the data of the last traversed node
     */
    public T getData(List<Traverse> traversals, Traverse childLocation) {

        if(childLocation == Traverse.UP) {
            return null;
        }

        BinaryTreeNode<T> parent = traverse(traversals);

        if(childLocation == Traverse.LEFT) {
            return parent.getLeftChild().getData();
        } else {
            return parent.getRightChild().getData();
        }
    }

    /**
     * Given a series of traversals, and the location of the child to remove, removes the child.
     * @param traversals are a list of traversals to perform
     */
    public void removeLeafNode(List<Traverse> traversals, Traverse childLocation) {

        // don't support UP operations
        if(childLocation == Traverse.UP) {
            return;
        }

        BinaryTreeNode<T> parent = traverse(traversals);
        BinaryTreeNode<T> childToRemove;

        if(childLocation == Traverse.LEFT) {
            childToRemove = parent.getLeftChild();
            // make sure that this node to remove is a leaf node
            if(! childToRemove.isLeafNode()) {
                return;
            }
            parent.setLeftChild(null);
        } else {
            childToRemove = parent.getRightChild();
            if(! childToRemove.isLeafNode()) {
                return;
            }
            parent.setRightChild(null);
        }

        size--;
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

    /**
     * @return the size of the tree
     */
    public int getSize() {
        return size;
    }

    /**
     * Prints the structure of the tree using a preorder traversal.
     * Used for debugging purposes.
     */
    public void printStructure() {

        BinaryTreeNode<T> pointer = root;
        pointer.setVisited(true);
        int nodesVisited = 1;

        System.out.println("Root: " + pointer.getData());

        while(nodesVisited != getSize()) {

            if (pointer.getLeftChild() != null && ! pointer.getLeftChild().isVisited()) {

                pointer = pointer.getLeftChild();
                pointer.setVisited(true);
                nodesVisited++;

                System.out.println("Left: " + pointer.getData());

            } else if (pointer.getRightChild() != null && ! pointer.getRightChild().isVisited()) {

                pointer = pointer.getRightChild();
                nodesVisited++;
                pointer.setVisited(true);

                System.out.println("Right: " + pointer.getData());

            } else {

                pointer = pointer.getParent();
                System.out.println("Up");
            }
        }

        resetVisitStatus();
    }

    /**
     * Helper method called after performing a traversal of the tree. Resets the visit status
     * of each node so that calling a traversal method twice on the same binary tree doesn't cause
     * weird stuff from happening.
     */
    private void resetVisitStatus() {

        BinaryTreeNode<T> pointer = root;
        pointer.setVisited(false);
        int nodesVisited = 1;

        while(nodesVisited != getSize()) {

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

    /**
     * Returns a new iterator for the binary tree. Can now use those spicy foreach loops!
     * @return an iterator for the binary tree
     */
    @Override
    public Iterator<T> iterator() {
        return new PreorderTraversal();
    }

    /**
     * Performs a preorder traversal of the binary tree.
     */
    private class PreorderTraversal implements Iterator<T> {

        BinaryTreeNode<T> pointer;
        int nodesVisited;

        public PreorderTraversal() {
            this.pointer = root;
            this.nodesVisited = 0;
        }

        @Override
        public boolean hasNext() {

            if(nodesVisited == getSize()) {
                resetVisitStatus();
            }

            return nodesVisited != getSize();
        }

        @Override
        public T next() {

            T returnData = pointer.getData();
            pointer.setVisited(true);
            nodesVisited++;

            // set the pointer to the next node to get
            boolean lastNode = nodesVisited == getSize();

            if(! lastNode) {

                // when a leaf node is reach, need to traverse back up the tree and check for unvisited nodes
                boolean foundNextNode = false;

                while (! foundNextNode) {

                    if(pointer.getLeftChild() != null && !pointer.getLeftChild().isVisited()) {
                        pointer = pointer.getLeftChild();
                        foundNextNode = true;

                    } else if(pointer.getRightChild() != null && !pointer.getRightChild().isVisited()) {
                        pointer = pointer.getRightChild();
                        foundNextNode = true;

                    } else {
                        pointer = pointer.getParent();
                    }
                }
            }

            return returnData;
        }
    }
}