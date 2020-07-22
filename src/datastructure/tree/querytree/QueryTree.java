package datastructure.tree.querytree;

import datastructure.tree.querytree.operator.CartesianProduct;
import datastructure.tree.querytree.operator.Operator;
import datastructure.tree.querytree.component.QueryTreeNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

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

    public QueryTree(QueryTree toCopy) {

        String toCopyStructure = toCopy.getStructure();

        String[] tokens = toCopyStructure.split("\n");

        for(int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if(token.contains(":")) {
                int keepUpUntilIndex = token.indexOf(":");
                tokens[i] = token.substring(0, keepUpUntilIndex);
            }
        }

        Stack<Traversal> traversals = new Stack<>();

        int i = 0;

        for(Operator operator : toCopy) {

            String traversalToken = tokens[i];

            if(traversalToken.equals("Root")) {
                this.setRoot(operator.copy(operator));
            } else {

                while(traversalToken.equals("Up")) {
                    traversals.pop();
                    i++;
                    traversalToken = tokens[i];
                }

                Traversal traversal = null;

                switch(traversalToken) {
                    case "Left":
                        traversal = Traversal.LEFT;
                        break;
                    case "Right":
                        traversal = Traversal.RIGHT;
                        break;
                    case "Down":
                        traversal = Traversal.DOWN;
                        break;
                }

                this.add(traversals, traversal, operator.copy(operator));
                traversals.push(traversal);
            }

            i++;
        }

        // set the size too!
        this.size = toCopy.size;
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
                // will only occur if placing above root node
                case UP:
                    nodeToAdd.setParent(null);
                    root = nodeToAdd;
                    root.setOnlyChild(pointer);
                    pointer.setParent(root);
                    break;
                case DOWN:
                    pointer.setOnlyChild(nodeToAdd);
                    break;
            }
        }

        size++;
    }

    private void insertLeft(QueryTreeNode parent, QueryTreeNode leftChildToAdd) {

        QueryTreeNode leftChild = parent.getLeftChild();
        parent.setLeftChild(leftChildToAdd);
        leftChildToAdd.setOnlyChild(leftChild);
        leftChild.setParent(leftChildToAdd);
    }

    private void insertRight(QueryTreeNode parent, QueryTreeNode rightChildToAdd) {

        QueryTreeNode rightChild = parent.getRightChild();
        parent.setRightChild(rightChildToAdd);
        rightChildToAdd.setOnlyChild(rightChild);
        rightChild.setParent(rightChildToAdd);
    }

    private void insertAbove(QueryTreeNode pointer, QueryTreeNode parentToAdd) {

        QueryTreeNode pointersParent = pointer.getParent();
        pointer.setParent(null);

        Traversal pointersParentChildLocation = null;

        if(pointersParent.hasOnlyChild() && pointersParent.getOnlyChild() == pointer) {
            pointersParentChildLocation = Traversal.DOWN;
        } else if(pointersParent.hasLeftChild() && pointersParent.getLeftChild() == pointer) {
            pointersParentChildLocation = Traversal.LEFT;
        } else if(pointersParent.hasRightChild() && pointersParent.getRightChild() == pointer) {
            pointersParentChildLocation = Traversal.RIGHT;
        }


        pointer.setParent(parentToAdd);
        parentToAdd.setOnlyChild(pointer);
        parentToAdd.setParent(pointersParent);

        if(pointersParentChildLocation == null) {
            System.out.println("In QueryTree.insertAbove()");
            System.out.println("Unknown location!");
            return;
        }

        switch(pointersParentChildLocation) {
            case DOWN:
                pointersParent.setOnlyChild(parentToAdd);
                break;
            case LEFT:
                pointersParent.setLeftChild(parentToAdd);
                break;
            case RIGHT:
                pointersParent.setRightChild(parentToAdd);
                break;
        }
    }

    private void insertBelow(QueryTreeNode parent, QueryTreeNode onlyChildToAdd) {

        QueryTreeNode parentsOnlyChild = parent.getOnlyChild();
        parent.setOnlyChild(onlyChildToAdd);
        onlyChildToAdd.setOnlyChild(parentsOnlyChild);
        parentsOnlyChild.setParent(onlyChildToAdd);
    }

    public Operator get(List<Traversal> traversals, Traversal target) {

        QueryTreeNode pointer = traverse(traversals);

        switch(target) {
            case LEFT:
                return pointer.getLeftChild().getOperator();
            case RIGHT:
                return pointer.getRightChild().getOperator();
            case UP:
                return pointer.getParent().getOperator();
            case DOWN:
            default:
                return pointer.getOnlyChild().getOperator();
        }
    }

    public void set(List<Traversal> traversals, Traversal target, Operator operator) {

        QueryTreeNode pointer = traverse(traversals);

        switch(target) {
            case LEFT:
                pointer.getLeftChild().setOperator(operator);
                break;
            case RIGHT:
                pointer.getRightChild().setOperator(operator);
                break;
            case UP:
                pointer.getParent().setOperator(operator);
                break;
            case DOWN:
                pointer.getOnlyChild().setOperator(operator);
                break;
        }
    }

    public void remove(List<Traversal> traversals, Traversal target) {

        QueryTreeNode pointer = traverse(traversals);

        switch(target) {
            case LEFT:
                if (! pointer.getLeftChild().hasAnyChildren()) {
                    pointer.setLeftChild(null);
                } else {
                    QueryTreeNode toRemove = pointer.getLeftChild();
                    // get the location of the target's child
                    if(toRemove.hasOnlyChild()) {
                        QueryTreeNode toRemovesChild = toRemove.getOnlyChild();
                        toRemovesChild.setParent(pointer);
                        pointer.setLeftChild(toRemovesChild);
                    } else {
                        if(toRemove.hasLeftChild()) {
                            QueryTreeNode toRemovesChild = toRemove.getLeftChild();
                            toRemovesChild.setParent(pointer);
                            pointer.setLeftChild(toRemovesChild);
                        }
                        if(toRemove.hasRightChild()) {
                            QueryTreeNode toRemovesChild = toRemove.getRightChild();
                            toRemovesChild.setParent(pointer);
                            pointer.setLeftChild(toRemovesChild);
                        }
                    }
                }
                break;
            case RIGHT:
                if(! pointer.getRightChild().hasAnyChildren()) {
                    pointer.setRightChild(null);
                } else {
                    QueryTreeNode toRemove = pointer.getRightChild();
                    if(toRemove.hasOnlyChild()) {
                        QueryTreeNode toRemovesChild = toRemove.getOnlyChild();
                        toRemovesChild.setParent(pointer);
                        pointer.setRightChild(toRemovesChild);
                    } else {
                        if(toRemove.hasLeftChild()) {
                            QueryTreeNode toRemovesChild = toRemove.getLeftChild();
                            toRemovesChild.setParent(pointer);
                            pointer.setRightChild(toRemovesChild);
                        }
                        if(toRemove.hasRightChild()) {
                            QueryTreeNode toRemovesChild = toRemove.getRightChild();
                            toRemovesChild.setParent(pointer);
                            pointer.setRightChild(toRemovesChild);
                        }
                    }
                }
                break;
            case UP:
                if(pointer.getParent().getParent() == null) {
                    pointer.setParent(null);
                    root = pointer;
                } else {
                    QueryTreeNode toRemove = pointer.getParent();
                    QueryTreeNode toRemovesParent = toRemove.getParent();
                    toRemovesParent.setOnlyChild(pointer);
                    pointer.setParent(toRemovesParent);
                }
                break;
            case DOWN:
                if(! pointer.getOnlyChild().hasAnyChildren()) {
                    pointer.setOnlyChild(null);
                } else {
                    QueryTreeNode toRemove = pointer.getOnlyChild();
                    if(toRemove.hasOnlyChild()) {
                        QueryTreeNode toRemovesChild = toRemove.getOnlyChild();
                        toRemovesChild.setParent(pointer);
                        pointer.setOnlyChild(toRemovesChild);
                    // can't perform the removal without creating 3 children
                    } else {
                        System.out.println("In QueryTree.remove()");
                        System.out.println("Removal violates the property of a having at most 2 child nodes!");
                        return;
                    }
                }
                break;
        }

        size--;
    }

    public String getStructure() {

        StringBuilder structure = new StringBuilder();

        QueryTreeNode pointer = root;
        pointer.setVisited(true);
        int nodesVisited = 1;

        structure.append("Root: ").append(pointer.getOperator()).append("\n");
        //System.out.println("Root: " + pointer.getOperator());

        while(nodesVisited != size) {

            if(pointer.hasOnlyChild() && pointer.getOnlyChild() != null && ! pointer.getOnlyChild().isVisited()) {

                pointer = pointer.getOnlyChild();
                pointer.setVisited(true);
                nodesVisited++;
                structure.append("Down: ").append(pointer.getOperator()).append("\n");
                //System.out.println("Down: " + pointer.getOperator());

            } else if(pointer.getLeftChild() != null && ! pointer.getLeftChild().isVisited()) {

                pointer = pointer.getLeftChild();
                pointer.setVisited(true);
                nodesVisited++;
                structure.append("Left: ").append(pointer.getOperator()).append("\n");
                //System.out.println("Left: " + pointer.getOperator());

            } else if(pointer.getRightChild() != null && ! pointer.getRightChild().isVisited()) {

                pointer = pointer.getRightChild();
                pointer.setVisited(true);
                nodesVisited++;
                structure.append("Right: ").append(pointer.getOperator()).append("\n");
                //System.out.println("Right: " + pointer.getOperator());

            } else {
                pointer = pointer.getParent();
                structure.append("Up\n");
                //System.out.println("Up");
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

            if(pointer.getOnlyChild() != null && pointer.hasOnlyChild() && pointer.getOnlyChild().isVisited()) {

                pointer = pointer.getOnlyChild();
                pointer.setVisited(false);
                nodesVisited++;

            } else if(pointer.getLeftChild() != null && pointer.getLeftChild().isVisited()) {

                pointer = pointer.getLeftChild();
                pointer.setVisited(false);
                nodesVisited++;

            } else if(pointer.getRightChild() != null && pointer.getRightChild().isVisited()) {

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

                    if(pointer.getOnlyChild() != null && pointer.hasOnlyChild() && ! pointer.getOnlyChild().isVisited()) {
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