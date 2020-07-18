package datastructure.tree.querytree;

import datastructure.tree.querytree.operator.Operator;
import datastructure.tree.querytree.component.QueryTreeNode;

import java.util.Iterator;
import java.util.List;

public class QueryTree implements Iterable<Operator> {

    public enum Traversal {
        LEFT, RIGHT, UP, DOWN
    }

    private QueryTreeNode root;
    private int size;

    public QueryTree(Operator root) {
        this.root = new QueryTreeNode(root, null);
        this.size = 1;
    }

    public void setRoot(Operator root) {
        this.root = new QueryTreeNode(root, null);
    }

    public int getSize() {
        return size;
    }

    public void add(List<Traversal> traversals, Traversal target, Operator operator) {

        QueryTreeNode pointer = traverse(traversals);
        QueryTreeNode nodeToAdd = new QueryTreeNode(operator, pointer);

        // perform an insert if left child is occupied
        if(pointer.hasLeftChild() && target == Traversal.LEFT) {
            insertLeft(pointer, nodeToAdd);
        } else if(pointer.hasRightChild() && target == Traversal.RIGHT) {
            insertRight(pointer, nodeToAdd);
        } else if(pointer.hasParent() && target == Traversal.UP) {
            insertAbove(pointer, nodeToAdd);
        } else if(pointer.hasOnlyChild() && target == Traversal.DOWN) {
            insertBelow(pointer, nodeToAdd);
        } else {
            switch(target) {
                case LEFT:
                    pointer.setLeftChild(nodeToAdd);
                    break;
                case RIGHT:
                    pointer.setRightChild(nodeToAdd);
                    break;
                case UP:
                    pointer.setParent(nodeToAdd);
                    break;
                case DOWN:
                    pointer.setOnlyChild(nodeToAdd);
                    break;
            }
        }
    }

    private void insertLeft(QueryTreeNode parent, QueryTreeNode leftChild) {
        
    }

    private void insertRight(QueryTreeNode parent, QueryTreeNode rightChild) {

    }

    private void insertAbove(QueryTreeNode parent, QueryTreeNode grandParent) {

    }

    private void insertBelow(QueryTreeNode parent, QueryTreeNode onlyChild) {

    }

    public Operator get(List<Traversal> traversals, Traversal target) {
        QueryTreeNode pointer = traverse(traversals);
    }

    public void remove(List<Traversal> traversals, Traversal target) {
        QueryTreeNode pointer = traverse(traversals);
    }

    public void removeSubtree(List<Traversal> traversals, Traversal target) {
        QueryTreeNode pointer = traverse(traversals);
    }

    public String getStructure() {

        StringBuilder structure = new StringBuilder();

        QueryTreeNode pointer = root;
        pointer.setVisited(true);
        int nodesVisited = 1;

        structure.append("Root: ").append(pointer.getOperator()).append("\n");

        while(nodesVisited != size) {

            if(pointer.getOnlyChild() != null && ! pointer.getOnlyChild().isVisited()) {

                pointer = pointer.getOnlyChild();
                pointer.setVisited(true);
                nodesVisited++;
                structure.append("Down: ").append(pointer.getOperator()).append("\n");

            } else if(pointer.getLeftChild() != null && ! pointer.getLeftChild().isVisited()) {

                pointer = pointer.getLeftChild();
                pointer.setVisited(true);
                nodesVisited++;
                structure.append("Left: ").append(pointer.getOperator()).append("\n");

            } else if(pointer.getRightChild() != null && ! pointer.getRightChild().isVisited()) {

                pointer = pointer.getRightChild();
                pointer.setVisited(true);
                nodesVisited++;
                structure.append("Right: ").append(pointer.getOperator()).append("\n");

            } else {
                pointer = pointer.getParent();
                structure.append("Up\n");
            }
        }

        resetVisited();
        return structure.toString();
    }

    private QueryTreeNode traverse(List<Traversal> traversals) {

        QueryTreeNode pointer = root;

        for(Traversal traversal : traversals) {
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
                case DOWN:
                    pointer = pointer.getOnlyChild();
                    break;
            }
        }

        return pointer;
    }

    private void resetVisited() {

        QueryTreeNode pointer = root;
        pointer.setVisited(false);
        int nodesVisited = 1;

        while (nodesVisited != size) {

            if (pointer.getOnlyChild() != null && pointer.getOnlyChild().isVisited()) {

                pointer = pointer.getOnlyChild();
                pointer.setVisited(false);
                nodesVisited++;

            } else if (pointer.getLeftChild() != null && pointer.getLeftChild().isVisited()) {

                pointer = pointer.getLeftChild();
                pointer.setVisited(false);
                nodesVisited++;

            } else if (pointer.getRightChild() != null && pointer.getRightChild().isVisited()) {

                pointer = pointer.getRightChild();
                pointer.setVisited(false);
                nodesVisited++;

            } else {
                pointer = pointer.getParent();
            }
        }
    }

    @Override
    public Iterator<Operator> iterator() {
        return new PreorderTraversal();
    }

    private class PreorderTraversal implements Iterator<Operator> {

        QueryTreeNode pointer;
        int nodesVisited;

        public PreorderTraversal() {
            this.pointer = root;
            this.nodesVisited = 0;
        }

        @Override
        public boolean hasNext() {
            if(nodesVisited == size) {
                resetVisited();
            }
            return nodesVisited != size;
        }

        @Override
        public Operator next() {

            Operator returnOperator = pointer.getOperator();
            pointer.setVisited(true);
            nodesVisited++;

            boolean lastNode = nodesVisited == size;

            if(! lastNode) {

                boolean foundNextNode = false;

                while(! foundNextNode) {

                    if(pointer.getOnlyChild() != null && ! pointer.getOnlyChild().isVisited()) {
                        pointer = pointer.getOnlyChild();
                        foundNextNode = true;

                    } else if(pointer.getLeftChild() != null && ! pointer.getLeftChild().isVisited()) {
                        pointer = pointer.getLeftChild();
                        foundNextNode = true;

                    } else if(pointer.getRightChild() != null && ! pointer.getRightChild().isVisited()) {
                        pointer = pointer.getRightChild();
                        foundNextNode = true;

                    } else {
                        pointer = pointer.getParent();
                    }
                }
            }

            return returnOperator;
        }
    }
}