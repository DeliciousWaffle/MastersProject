package datastructure.tree.querytree.component;

import datastructure.tree.querytree.operator.Operator;

public class QueryTreeNode {

    private Operator operator;
    private QueryTreeNode parent;
    private QueryTreeNode[] children;
    private boolean visited;
    private boolean canPipelineSubtree;

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
        return hasOnlyChild() ? children[0] : null;
    }

    /**
     * This node will have 2 children. Sets the left child of this node.
     * @param leftChild is the left child of this node
     */
    public void setLeftChild(QueryTreeNode leftChild) {
        if(! hasAnyChildren()) {
            children = new QueryTreeNode[2];
        }
        children[0] = leftChild;
    }

    /**
     * @return the left child of this node
     */
    public QueryTreeNode getLeftChild() {
        return hasLeftChild() ? children[0] : null;
    }

    /**
     * This node will have 2 children. Sets the right child of this node.
     * @param rightChild is the right child of this node
     */
    public void setRightChild(QueryTreeNode rightChild) {
        if(! hasAnyChildren()) {
            children = new QueryTreeNode[2];
        }
        children[1] = rightChild;
    }

    /**
     * @return the right child of this node
     */
    public QueryTreeNode getRightChild() {
        return hasRightChild() ? children[1] : null;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setCanPipelineSubtree(boolean canPipelineSubtree) {
        this.canPipelineSubtree = canPipelineSubtree;
    }

    public boolean canPipelineSubtree() {
        return canPipelineSubtree;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public boolean hasAnyChildren() {
        return children != null;
    }

    public boolean hasOnlyChild() {
        return hasAnyChildren() && children.length == 1 && children[0] != null;
    }

    public boolean hasLeftChild() {
        return hasAnyChildren() && children.length == 2 && children[0] != null;
    }

    public boolean hasRightChild() {
        return hasAnyChildren() && children.length == 2 && children[1] != null;
    }

    @Override
    public String toString() {
        StringBuilder print = new StringBuilder();
        print.append(operator).append("\n");
        if(! hasAnyChildren()) {
            print.append("Children: null");
        } else {
            if(hasOnlyChild()) {
                print.append("Only Child: ").append(getOnlyChild().getOperator());
            } else {
                if (hasLeftChild()) {
                    print.append("Left Child:").append(getLeftChild().getOperator()).append("\n");
                } else {
                    print.append("Left Child: null\n");
                }
                if (hasRightChild()) {
                    print.append("Right Child: ").append(getRightChild().getOperator());
                } else {
                    print.append("Right Child: null");
                }
            }
        }
        return print.toString();
    }
}