package datastructures.trees.querytree;

import datastructures.trees.querytree.operator.Operator;
import datastructures.trees.querytree.component.QueryTreeNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A representation of a relational algebra query tree. Basically looks like a binary tree, but with a large occurrence
 * of unary nodes. Structurally, this class is strange. Each node has a reference to its parent along with any child
 * nodes that it may have. This means that you can traverse to any node from a starting node. Why did I do this?
 * Good question, ask past me. Each node contains a relational algebra operator which applies an operation to the
 * result set produced by child nodes. This class also contains a method of traversing to nodes of a query tree by
 * supplying a list of traversals to reach that location.
 */
public final class QueryTree {

    /**
     * Enum representing the types of traversals that can be taken in order to reach an element
     * in the the query tree. Also has some methods for making deep copies of junk. Yes this is necessary.
     */
    public enum Traversal {

        LEFT, RIGHT, UP, DOWN, NONE;

        public static final List<Traversal> NO_TRAVERSALS = new ArrayList<>();

        /**
         * Creates a deep copy, that is, a separate copy of the given traversal.
         * @param toCopy is the traversal to copy
         * @return a copy of the given traversal
         */
        public static Traversal copyTraversal(Traversal toCopy) {
            switch(toCopy) {
                case LEFT:
                    return Traversal.LEFT;
                case RIGHT:
                    return Traversal.RIGHT;
                case UP:
                    return Traversal.UP;
                case DOWN:
                    return Traversal.DOWN;
                case NONE:
                default:
                    return Traversal.NONE;
            }
        }

        /**
         * Creates a deep copy of a given list of traversals.
         * @param toCopyList is the list to copy
         * @return a copy of the given traversal list
         */
        public static List<Traversal> copyTraversalList(List<Traversal> toCopyList) {
            return toCopyList.stream()
                    .map(Traversal::copyTraversal)
                    .collect(Collectors.toList());
        }
    }

    // root and size of the query tree
    private QueryTreeNode root;
    private int size;

    /**
     * Default constructor, creates a query tree with the given operator as the root node.
     * @param operator is the operator to set the root to
     */
    public QueryTree(Operator operator) {
        this.root = new QueryTreeNode(operator, null);
        this.size = 1;
    }

    /**
     * Copy constructor used for creating a deep copy of the given query tree.
     * @param toCopy is the query tree to copy
     */
    public QueryTree(QueryTree toCopy) {

        // get the each operator and its location from the query tree to copy
        Map<Operator, List<Traversal>> operatorsAndLocations = toCopy.getOperatorsAndLocations();

        // using the above info, make the copy
        for(Map.Entry<Operator, List<Traversal>> entry : operatorsAndLocations.entrySet()) {
            Operator operator = entry.getKey();
            List<Traversal> traversals = entry.getValue();
            this.add(traversals, Traversal.NONE, operator);
        }
    }

    /**
     * @param operator is the operator to set the root to
     */
    public void setRoot(Operator operator) {
        this.root = new QueryTreeNode(operator, null);
        this.size = 1;
    }

    /**
     * @return the number of nodes contained in the query tree
     */
    public int getSize() {
        return size;
    }

    /**
     * Adds a new node to the query tree at the given location. If a node is present at the given location, this
     * method performs an insert at that location.
     * @param traversals a list of traversals used to get to the location of where to add
     * @param targetLocation is the location of where the operator is to be added
     * @param operatorToAdd is the operator to add
     */
    public void add(List<Traversal> traversals, Traversal targetLocation, Operator operatorToAdd) {

        // might make changes to the list, will need a separate copy to prevent unwanted mutation
        traversals = Traversal.copyTraversalList(traversals);

        // check that the root node exists before doing any adding, this will only fire when creating a deep copy
        if(root == null || (traversals.isEmpty() && targetLocation == Traversal.NONE)) {
            setRoot(operatorToAdd);
            return;
        }

        // if there is no target location, set it to the last traversal in the traversal list
        if(targetLocation == Traversal.NONE) {
            int lastIndex = traversals.size() - 1;
            targetLocation = traversals.remove(lastIndex);
        }

        QueryTreeNode pointer = traverse(traversals);
        QueryTreeNode nodeToAdd = new QueryTreeNode(operatorToAdd, pointer);

        // perform an insertion if there is a child occupying the target location of where to add
        if (pointer.hasLeftChild() && targetLocation == Traversal.LEFT) {
            insertLeft(pointer, nodeToAdd);

        } else if (pointer.hasRightChild() && targetLocation == Traversal.RIGHT) {
            insertRight(pointer, nodeToAdd);

        } else if (pointer.hasParent() && targetLocation == Traversal.UP) {
            insertAbove(pointer, nodeToAdd);

        } else if (pointer.hasOnlyChild() && targetLocation == Traversal.DOWN) {
            insertBelow(pointer, nodeToAdd);

        // otherwise, just add the child node at the given location
        } else {
            switch (targetLocation) {
                case LEFT:
                    pointer.setLeftChild(nodeToAdd);
                    break;
                case RIGHT:
                    pointer.setRightChild(nodeToAdd);
                    break;
                case UP: // will only occur if placing above root node
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

        // don't forget to increment the size!
        size++;
    }

    /**
     * Given a list of traversals and a target location, returns the operator at that location.
     * @param traversals is a list of traversals to get to a location
     * @param target is the target location of the operator to retrieve
     * @return the operator at the provided traversal list and target traversal
     */
    public Operator get(List<Traversal> traversals, Traversal target) {

        QueryTreeNode pointer = traverse(traversals);

        switch (target) {
            case LEFT:
                return pointer.getLeftChild().getOperator();
            case RIGHT:
                return pointer.getRightChild().getOperator();
            case UP:
                return pointer.getParent().getOperator();
            case DOWN:
                return pointer.getOnlyChild().getOperator();
            case NONE:
            default:
                return pointer.getOperator();
        }
    }

    /**
     * Sets the operator at the given traversal list and target traversal location. Effectively overwriting
     * the node that is currently there.
     * @param traversals is a list of traversals to get to a location
     * @param target is the target location of where to set the operator
     * @param operator is the operator to set
     */
    public void set(List<Traversal> traversals, Traversal target, Operator operator) {

        QueryTreeNode pointer = traverse(traversals);

        switch (target) {
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
            case NONE:
                pointer.setOperator(operator);
                break;
        }
    }

    /**
     * Given a list of traversals for the first operator and the second, swaps those two operators in the query tree.
     * @param traversals1 is a list of traversals needed to get to the first operator
     * @param traversals2 is a list of traversals needed to get to the second operator
     */
    public void swap(List<Traversal> traversals1, List<Traversal> traversals2) {
        Operator operator1 = traverse(traversals1).getOperator();
        Operator operator2 = traverse(traversals2).getOperator();
        set(traversals1, Traversal.NONE, operator2);
        set(traversals2, Traversal.NONE, operator1);
    }

    /**
     * Removes a node at the given location. If the target to remove has a parent or any children references,
     * modifies these to reflect the changes of the deletion. I pray that the references don't get corrupted
     * in some way, shape, or form.
     * @param traversals is a list of traversals needed to get to the location
     * @param target is the location of the node to remove
     */
    public void remove(List<Traversal> traversals, Traversal target) {

        QueryTreeNode pointer = traverse(traversals);

        switch (target) {
            case LEFT: {

                if (! pointer.getLeftChild().hasAnyChildren()) {
                    pointer.setLeftChild(null);

                } else {

                    QueryTreeNode toRemove = pointer.getLeftChild();

                    if (toRemove.hasOnlyChild()) {
                        QueryTreeNode toRemovesChild = toRemove.getOnlyChild();
                        toRemovesChild.setParent(pointer);
                        pointer.setLeftChild(toRemovesChild);

                    } else {

                        if (toRemove.hasLeftChild()) {
                            QueryTreeNode toRemovesChild = toRemove.getLeftChild();
                            toRemovesChild.setParent(pointer);
                            pointer.setLeftChild(toRemovesChild);
                        }

                        if (toRemove.hasRightChild()) {
                            QueryTreeNode toRemovesChild = toRemove.getRightChild();
                            toRemovesChild.setParent(pointer);
                            pointer.setLeftChild(toRemovesChild);
                        }
                    }
                }

                break;
            }

            case RIGHT: {

                if (! pointer.getRightChild().hasAnyChildren()) {
                    pointer.setRightChild(null);

                } else {

                    QueryTreeNode toRemove = pointer.getRightChild();

                    if (toRemove.hasOnlyChild()) {
                        QueryTreeNode toRemovesChild = toRemove.getOnlyChild();
                        toRemovesChild.setParent(pointer);
                        pointer.setRightChild(toRemovesChild);

                    } else {

                        if (toRemove.hasLeftChild()) {
                            QueryTreeNode toRemovesChild = toRemove.getLeftChild();
                            toRemovesChild.setParent(pointer);
                            pointer.setRightChild(toRemovesChild);
                        }

                        if (toRemove.hasRightChild()) {
                            QueryTreeNode toRemovesChild = toRemove.getRightChild();
                            toRemovesChild.setParent(pointer);
                            pointer.setRightChild(toRemovesChild);
                        }
                    }
                }

                break;
            }

            case UP: {

                if (pointer.getParent().getParent() == null) {
                    pointer.setParent(null);
                    root = pointer;

                } else {
                    QueryTreeNode toRemove = pointer.getParent();
                    QueryTreeNode toRemovesParent = toRemove.getParent();
                    toRemovesParent.setOnlyChild(pointer);
                    pointer.setParent(toRemovesParent);
                }

                break;
            }

            case DOWN: {

                if (! pointer.getOnlyChild().hasAnyChildren()) {
                    pointer.setOnlyChild(null);

                } else {

                    QueryTreeNode toRemove = pointer.getOnlyChild();

                    if (toRemove.hasOnlyChild()) {
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

            case NONE: {

                if (!pointer.hasAnyChildren()) {

                    QueryTreeNode pointersParent = pointer.getParent();

                    if (pointersParent.hasLeftChild() && pointersParent.getLeftChild() == pointer) {
                        pointersParent.setLeftChild(null);
                    } else if (pointersParent.hasRightChild() && pointersParent.getRightChild() == pointer) {
                        pointersParent.setRightChild(null);
                    } else if (pointersParent.hasOnlyChild() && pointersParent.getOnlyChild() == pointer) {
                        pointersParent.setOnlyChild(null);
                    }

                } else {

                    if (pointer.hasOnlyChild()) {

                        QueryTreeNode pointersParent = pointer.getParent();
                        QueryTreeNode pointersChild = pointer.getOnlyChild();

                        if (pointersParent.hasLeftChild() && pointersParent.getLeftChild() == pointer) {
                            pointersParent.setLeftChild(pointersChild);
                        } else if (pointersParent.hasRightChild() && pointersParent.getRightChild() == pointer) {
                            pointersParent.setRightChild(pointersChild);
                        } else if (pointersParent.hasOnlyChild() && pointersParent.getOnlyChild() == pointer) {
                            pointersParent.setOnlyChild(pointersChild);
                        }

                        pointersChild.setParent(pointersParent);

                    } else {

                        QueryTreeNode pointersParent = pointer.getParent();

                        // if the parent has more than 1 child too, the removal will
                        // violate what it means to be a binary tree
                        if (! pointersParent.hasOnlyChild()) {
                            System.out.println("In QueryTree.remove()");
                            System.out.println("Removal will create a non-binary tree");

                        } else {
                            QueryTreeNode pointersLeftChild = pointer.getLeftChild();
                            QueryTreeNode pointersRightChild = pointer.getRightChild();
                            pointersParent.setOnlyChild(null);
                            pointersParent.setLeftChild(pointersLeftChild);
                            pointersParent.setRightChild(pointersRightChild);
                            pointersLeftChild.setParent(pointersParent);
                            pointersRightChild.setParent(pointersParent);
                        }
                    }
                }

                break;
            }
        }

        size--;
    }

    /**
     * Given the location of a node in the query tree, removes it along with any children that it may have.
     * @param traversals is a list of traversals to get to a node
     */
    public void removeSubtree(List<Traversal> traversals) {

        QueryTreeNode pointer = traverse(traversals);

        // need to update size to reflect changes
        int numChildren = getNumNodesInSubtree(pointer, 0);
        size -= numChildren;

        // removing the node now
        QueryTreeNode parent = pointer.getParent();

        Traversal locationWithRespectToParent = getLocationWithRespectToParent(pointer, parent);

        switch(locationWithRespectToParent) {
            case LEFT:
                parent.setLeftChild(null);
                break;
            case RIGHT:
                parent.setRightChild(null);
                break;
            case DOWN:
                parent.setOnlyChild(null);
                break;
        }
    }

    /**
     * @param type is the type of operator to get the occurrence of
     * @return the occurrence of the given type
     */
    public int getTypeOccurrence(Operator.Type type) {
        return getOperatorsAndLocationsOfType(type).size();
    }

    /**
     * @param typeToGet is the type of operator to get from the query tree
     * @return a list of operators from this query tree based on the type to get
     */
    public Map<Operator, List<Traversal>> getOperatorsAndLocationsOfType(Operator.Type typeToGet) {
        return getOperatorsAndLocations().entrySet().stream()
                .filter(entry -> entry.getKey().getType() == typeToGet)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * @return a map containing each operator and its location in a preorder traversal
     */
    public Map<Operator, List<Traversal>> getOperatorsAndLocations() {
        Map<Operator, List<Traversal>> operatorsAndLocations = new LinkedHashMap<>();
        getNodesAndLocations().forEach((k, v) -> operatorsAndLocations.put(k.getOperator(), v));
        return operatorsAndLocations;
    }

    /**
     * @param traversals is a list of traversals needed to get to a particular node
     * @return the node present at the given list of traversals
     */
    private QueryTreeNode traverse(List<Traversal> traversals) {

        QueryTreeNode pointer = root;

        for (Traversal traversal : traversals) {
            switch (traversal) {
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

    /**
     * Inserts a new node between the parent and the parent's current left child.
     * @param parent is the parent node
     * @param leftChildToAdd is the left child node to insert
     */
    private void insertLeft(QueryTreeNode parent, QueryTreeNode leftChildToAdd) {
        QueryTreeNode leftChild = parent.getLeftChild();
        parent.setLeftChild(leftChildToAdd);
        leftChildToAdd.setOnlyChild(leftChild);
        leftChild.setParent(leftChildToAdd);
    }

    /**
     * Inserts a new node between the parent and the parent's current right child.
     * @param parent is the parent node
     * @param rightChildToAdd is the right child node to insert
     */
    private void insertRight(QueryTreeNode parent, QueryTreeNode rightChildToAdd) {
        QueryTreeNode rightChild = parent.getRightChild();
        parent.setRightChild(rightChildToAdd);
        rightChildToAdd.setOnlyChild(rightChild);
        rightChild.setParent(rightChildToAdd);
    }

    /**
     * Inserts a new node between the pointer and the pointer's current parent.
     * @param pointer is the pointer node
     * @param parentToAdd is the parent node to insert
     */
    private void insertAbove(QueryTreeNode pointer, QueryTreeNode parentToAdd) {

        QueryTreeNode pointersParent = pointer.getParent();
        pointer.setParent(null);

        Traversal pointersLocation = null;

        if (pointersParent.hasOnlyChild() && pointersParent.getOnlyChild() == pointer) {
            pointersLocation = Traversal.DOWN;
        } else if (pointersParent.hasLeftChild() && pointersParent.getLeftChild() == pointer) {
            pointersLocation = Traversal.LEFT;
        } else if (pointersParent.hasRightChild() && pointersParent.getRightChild() == pointer) {
            pointersLocation = Traversal.RIGHT;
        }

        pointer.setParent(parentToAdd);
        parentToAdd.setOnlyChild(pointer);
        parentToAdd.setParent(pointersParent);

        if (pointersLocation == null) {
            System.out.println("In QueryTree.insertAbove()");
            System.out.println("Unknown location!");
            return;
        }

        switch (pointersLocation) {
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

    /**
     * Inserts a new node between the parent and the parent's current only child
     * @param parent is the parent node
     * @param onlyChildToAdd is the only child node to add
     */
    private void insertBelow(QueryTreeNode parent, QueryTreeNode onlyChildToAdd) {
        QueryTreeNode parentsOnlyChild = parent.getOnlyChild();
        parent.setOnlyChild(onlyChildToAdd);
        onlyChildToAdd.setOnlyChild(parentsOnlyChild);
        parentsOnlyChild.setParent(onlyChildToAdd);
    }

    /**
     * @param pointer is the pointer node
     * @param parent is the pointer node's parent
     * @return the location of the pointer with respect to its parent
     */
    private Traversal getLocationWithRespectToParent(QueryTreeNode pointer, QueryTreeNode parent) {
        if(parent.hasOnlyChild() && parent.getOnlyChild() == pointer) {
            return Traversal.DOWN;
        } else if(parent.hasLeftChild() && parent.getLeftChild() == pointer) {
            return Traversal.LEFT;
        } else if(parent.hasRightChild() && parent.getRightChild() == pointer) {
            return Traversal.RIGHT;
        } else {
            System.out.println("QueryTree.getLocationWithRespectToParent()");
            return Traversal.NONE;
        }
    }

    /**
     * Given a pointer to a node, returns the number of nodes present in that node's subtree. Did a recursion.
     * @param pointer is the pointer to a node
     * @param numChildren is the number of children in that node's subtree
     * @return number of children in a given node's subtree
     */
    private int getNumNodesInSubtree(QueryTreeNode pointer, int numChildren) {

        if (pointer == null) {
            return numChildren;
        }

        numChildren++;

        numChildren = getNumNodesInSubtree(pointer.getOnlyChild(), numChildren);
        numChildren = getNumNodesInSubtree(pointer.getLeftChild(), numChildren);
        numChildren = getNumNodesInSubtree(pointer.getRightChild(), numChildren);

        return numChildren;
    }

    /**
     * @return a map containing each node contained in the query tree and its location in a preorder traversal
     */
    private Map<QueryTreeNode, List<Traversal>> getNodesAndLocations() {

        // using a linked hash map because the order of additions (preorder traversal) needs to be maintained
        Map<QueryTreeNode, List<Traversal>> nodesAndLocations = new LinkedHashMap<>();
        List<Traversal> traversals = new ArrayList<>();

        // add the root
        QueryTreeNode pointer = root;
        pointer.setVisited(true);
        int nodesVisited = 1;
        nodesAndLocations.put(root, Traversal.copyTraversalList(traversals));

        // keep traversing through the tree until all elements have been visited
        while (nodesVisited != size) {

            // traverse to the only child
            if (pointer.hasOnlyChild() && pointer.getOnlyChild() != null && ! pointer.getOnlyChild().isVisited()) {

                pointer = pointer.getOnlyChild();
                pointer.setVisited(true);
                nodesVisited++;

                traversals.add(Traversal.DOWN);
                nodesAndLocations.put(pointer, Traversal.copyTraversalList(traversals));

            // traverse to the left child
            } else if (pointer.getLeftChild() != null && ! pointer.getLeftChild().isVisited()) {

                pointer = pointer.getLeftChild();
                pointer.setVisited(true);
                nodesVisited++;

                traversals.add(Traversal.LEFT);
                nodesAndLocations.put(pointer, Traversal.copyTraversalList(traversals));

            // traverse to the right child
            } else if (pointer.getRightChild() != null && ! pointer.getRightChild().isVisited()) {

                pointer = pointer.getRightChild();
                pointer.setVisited(true);
                nodesVisited++;

                traversals.add(Traversal.RIGHT);
                nodesAndLocations.put(pointer, Traversal.copyTraversalList(traversals));

            // traverse up
            } else {

                pointer = pointer.getParent();

                int lastIndex = traversals.size() - 1;
                traversals.remove(lastIndex);
            }
        }

        // reset the visit status of each node
        nodesAndLocations.keySet().forEach(e -> e.setVisited(false));

        return nodesAndLocations;
    }

    /**
     * @return a string representation of the query tree
     */
    @Override
    public String toString() {

        StringBuilder print = new StringBuilder();
        print.append("Query Tree Size: ").append(size).append("\n");

        Map<Operator, List<Traversal>> operatorsAndLocations = getOperatorsAndLocations();

        for(Map.Entry<Operator, List<Traversal>> entry : operatorsAndLocations.entrySet()) {

            Operator operator = entry.getKey();
            List<Traversal> traversals = entry.getValue();

            print.append(operator.toString()).append("\n").append("Location: ");
            if (traversals.isEmpty()) {
                print.append("Root");

            } else {
                traversals.forEach(e -> print.append(e).append(", "));

                // remove ", "
                print.delete(print.length() - 2, print.length());
            }
        }

        return print.toString();
    }
}