package datastructure.tree.querytree.component;

import datastructure.tree.querytree.operator.Operator;

public class QueryTreeNode {

    private Operator operator;
    private QueryTreeNode parent;
    private QueryTreeNode[] children;
    private boolean visited;

    public QueryTreeNode(Operator operator, QueryTreeNode parent) {
        this.operator = operator;
        this.parent = parent;
        this.children = null;
        this.visited = false;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setParent(QueryTreeNode parent) {
        this.parent = parent;
    }

    public QueryTreeNode getParent() {
        return parent;
    }

    /**
     * This node will only have a single child.
     * @param onlyChild is the child of this node.
     */
    public void setOnlyChild(QueryTreeNode onlyChild) {
        children = new QueryTreeNode[] { onlyChild };
    }

    /**
     * @return this node's only child
     */
    public QueryTreeNode getOnlyChild() {
        return children[0];
    }

    /**
     * This node will have 2 children. Sets the left child of this node.
     * @param leftChild is the left child of this node
     */
    public void setLeftChild(QueryTreeNode leftChild) {
        if(! hasChildren()) {
            children = new QueryTreeNode[2];
        }
        children[0] = leftChild;
    }

    /**
     * @return the left child of this node
     */
    public QueryTreeNode getLeftChild() {
        return children[0];
    }

    /**
     * This node will have 2 children. Sets the right child of this node.
     * @param rightChild is the right child of this node
     */
    public void setRightChild(QueryTreeNode rightChild) {
        if(! hasChildren()) {
            children = new QueryTreeNode[2];
        }
        children[1] = rightChild;
    }

    /**
     * @return the right child of this node
     */
    public QueryTreeNode getRightChild() {
        return children[1];
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isVisited() {
        return visited;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public boolean hasChildren() {
        return children != null;
    }

    public boolean hasOnlyChild() {
        return hasLeftChild() && children.length == 1;
    }

    public boolean hasLeftChild() {
        return hasChildren() && children[0] != null;
    }

    public boolean hasRightChild() {
        return hasChildren() && children[1] != null;
    }
}