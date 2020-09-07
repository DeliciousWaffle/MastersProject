package datastructures.trees.querytree;

import datastructures.trees.querytree.operator.Operator;
import datastructures.trees.querytree.component.QueryTreeNode;
import datastructures.trees.querytree.operator.types.Relation;

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
public final class QueryTree implements Iterable<Operator> {

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

    /*public QueryTree(QueryTree toCopy) {

        String toCopyStructure = toCopy.getTreeStructure();
        String[] tokens = toCopyStructure.split("\n");

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.contains(":")) {
                int keepUpUntilIndex = token.indexOf(":");
                tokens[i] = token.substring(0, keepUpUntilIndex);
            }
        }

        Stack<Traversal> traversals = new Stack<>();
        int i = 0;

        for (Operator operator : toCopy) {

            String traversalToken = tokens[i];

            if (traversalToken.equals("Root")) {
                this.setRoot(operator.copy(operator));

            } else {

                while (traversalToken.equals("Up")) {
                    traversals.pop();
                    i++;
                    traversalToken = tokens[i];
                }

                Traversal traversal = Traversal.valueOf(traversalToken.toUpperCase());
                this.add(traversals, traversal, operator.copy(operator));
                traversals.push(traversal);
            }

            i++;
        }

        this.size = toCopy.size;
    }*/

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

    // TODO remove
    public List<List<Traversal>> getEveryOperatorsLocation() {

        List<List<Traversal>> everyNodesLocation = new ArrayList<>();

        // get the structure of the tree and remove stuff that we don't need
        String treeStructure = getTreeStructure();
        String[] traversalTokens = treeStructure.split("\n");

        for (int i = 0; i < traversalTokens.length; i++) {
            String token = traversalTokens[i];
            if (token.contains(":")) {
                int keepUpUntilIndex = token.indexOf(":");
                traversalTokens[i] = token.substring(0, keepUpUntilIndex);
            }
        }

        int i = 0;

        Stack<Traversal> workingTraversals = new Stack<>();

        for (Operator operator : this) {

            String traversalToken = traversalTokens[i];

            while (traversalToken.equals("Up")) {
                workingTraversals.pop();
                i++;
                traversalToken = traversalTokens[i];
            }

            Traversal traversal = Traversal.valueOf(traversalToken.toUpperCase());
            workingTraversals.push(traversal);

            /*
            this is very weird, but not doing this causes everyNodesLocation's to contain the same list of
            traversals for all its data which is not what we want
             */
            Stack<Traversal> copyWorkingTraversals = new Stack<>();

            for (Traversal workingTraversal : workingTraversals) {

                traversal = null;

                switch (workingTraversal) {
                    case NONE:
                        traversal = Traversal.NONE;
                        break;
                    case LEFT:
                        traversal = Traversal.LEFT;
                        break;
                    case RIGHT:
                        traversal = Traversal.RIGHT;
                        break;
                    case DOWN:
                        traversal = Traversal.DOWN;
                        break;
                }

                copyWorkingTraversals.push(traversal);
            }

            everyNodesLocation.add(copyWorkingTraversals);
            i++;
        }

        return everyNodesLocation;
    }

    // TODO remove
    public void tryToPipelineSubtree(List<Traversal> traversals, Traversal location) {
        QueryTreeNode pointer = traverse(traversals);
        switch (location) {
            case NONE:
                if (pointer.hasAnyChildren()) {
                    pointer.setCanPipelineSubtree(true);
                }
                break;
            case LEFT:
                if (pointer.getLeftChild().hasAnyChildren()) {
                    pointer.getLeftChild().setCanPipelineSubtree(true);
                }
                break;
            case RIGHT:
                if (pointer.getRightChild().hasAnyChildren()) {
                    pointer.getRightChild().setCanPipelineSubtree(true);
                }
                break;
            case UP:
                if (pointer.getParent().hasAnyChildren()) {
                    pointer.getParent().setCanPipelineSubtree(true);
                }
                break;
            case DOWN:
                if (pointer.getOnlyChild().hasAnyChildren()) {
                    pointer.getOnlyChild().setCanPipelineSubtree(true);
                }
                break;
        }
    }

    // TODO remove
    public boolean canPipelineSubtree(List<Traversal> traversals, Traversal location) {
        QueryTreeNode pointer = traverse(traversals);
        switch (location) {
            case NONE:
                return pointer.canPipelineSubtree();
            case LEFT:
                return pointer.getLeftChild().canPipelineSubtree();
            case RIGHT:
                return pointer.getRightChild().canPipelineSubtree();
            case UP:
                return pointer.getParent().canPipelineSubtree();
            case DOWN:
            default:
                return pointer.getOnlyChild().canPipelineSubtree();
        }
    }

    /**
     * Adds a new node to the query tree at the given location. If a node is present at the given location, this
     * method performs an insert at that location.
     * @param traversals a list of traversals used to get to the location of where to add
     * @param targetLocation is the location of where the operator is to be added
     * @param operatorToAdd is the operator to add
     */
    public void add(List<Traversal> traversals, Traversal targetLocation, Operator operatorToAdd) {

        // check that the root node exists before doing any adding, this will only fire when creating a deep copy
        if(root == null) {
            setRoot(operatorToAdd);
            return;
        }

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

    // TODO remove
    /**
     * Returns a list of traversals needed to get to the target relation name.
     *
     * @param relationName is the name of the relation to find
     * @return a list of traversals needed to get to that relation
     */
    public List<Traversal> getRelationLocation(String relationName) {

        String structure = getTreeStructure();
        String[] tokens = structure.split("\n");

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.contains(":")) {
                int keepUpUntilIndex = token.indexOf(":");
                tokens[i] = token.substring(0, keepUpUntilIndex);
            }
        }

        Stack<Traversal> traversals = new Stack<>();
        int i = 0;
        boolean doneSearching = false;

        for (Operator operator : this) {

            if (!doneSearching) {

                String traversalToken = tokens[i];

                if (traversalToken.equals("Root")) {
                    i++;
                    continue;

                } else {

                    while (traversalToken.equals("Up")) {
                        traversals.pop();
                        i++;
                        traversalToken = tokens[i];
                    }

                    Traversal traversal = null;

                    switch (traversalToken) {
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

                    if (operator.getType() == Operator.Type.RELATION) {

                        if (((Relation) operator).getTableName().equalsIgnoreCase(relationName)) {
                            doneSearching = true;
                        }
                    }

                    traversals.push(traversal);
                }

                i++;
            }
        }

        // didn't find the relation for some reason
        if (!doneSearching) {
            System.out.println("In QueryTree.getRelationLocation()");
            System.out.println("Couldn't find relation location. Searching for: " + relationName);
            return new ArrayList<>();
        }

        List<Traversal> traversalsToReturn = new ArrayList<>();

        while (!traversals.isEmpty()) {
            traversalsToReturn.add(traversals.pop());
        }

        // will need to swap because previously used a stack
        for (i = 0; i < traversalsToReturn.size() / 2; i++) {
            Traversal temp = traversalsToReturn.get(i);
            traversalsToReturn.set(i, traversalsToReturn.get(traversalsToReturn.size() - i - 1));
            traversalsToReturn.set(traversalsToReturn.size() - i - 1, temp);
        }

        return traversalsToReturn;
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

        if (pointersParent.hasOnlyChild() && pointersParent.getOnlyChild() == pointer) {
            pointersParentChildLocation = Traversal.DOWN;
        } else if (pointersParent.hasLeftChild() && pointersParent.getLeftChild() == pointer) {
            pointersParentChildLocation = Traversal.LEFT;
        } else if (pointersParent.hasRightChild() && pointersParent.getRightChild() == pointer) {
            pointersParentChildLocation = Traversal.RIGHT;
        }


        pointer.setParent(parentToAdd);
        parentToAdd.setOnlyChild(pointer);
        parentToAdd.setParent(pointersParent);

        if (pointersParentChildLocation == null) {
            System.out.println("In QueryTree.insertAbove()");
            System.out.println("Unknown location!");
            return;
        }

        switch (pointersParentChildLocation) {
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

    public void swap(List<Traversal> traversals1, List<Traversal> traversals2) {

    }

    public void remove(List<Traversal> traversals, Traversal target) {

        QueryTreeNode pointer = traverse(traversals);

        switch (target) {
            case LEFT: {

                if (!pointer.getLeftChild().hasAnyChildren()) {
                    pointer.setLeftChild(null);

                } else {

                    QueryTreeNode toRemove = pointer.getLeftChild();

                    // get the location of the target's child
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

                if (!pointer.getRightChild().hasAnyChildren()) {
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

                if (!pointer.getOnlyChild().hasAnyChildren()) {
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

    // TODO remove
    public int getNumRelations() {
        int numRelations = 0;
        for (Operator operator : this) {
            if (operator.getType() == Operator.Type.RELATION) {
                numRelations++;
            }
        }
        return numRelations;
    }

    /**
     * @param type is the type of operator to get the occurrence of
     * @return the occurrence of the given type
     */
    public int getTypeOccurrence(Operator.Type type) {
        return getOperatorsOfType(type).size();
    }

    /**
     * @param typeToGet is the type of operator to get from the query tree
     * @return a list of operators from this query tree based on the type to get
     */
    public List<Operator> getOperatorsOfType(Operator.Type typeToGet) {
        return getOperatorsAndLocations().keySet().stream()
                .filter(k -> k.getType() == typeToGet)
                .collect(Collectors.toList());
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
     * @return a map containing each query tree node contained and its location in a preorder traversal
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

    // TODO remove
    private void resetVisited() {

        QueryTreeNode pointer = root;
        pointer.setVisited(false);
        int nodesVisited = 1;

        while (nodesVisited != size) {

            if (pointer.getOnlyChild() != null && pointer.hasOnlyChild() && pointer.getOnlyChild().isVisited()) {

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

    // TODO remove
    public String getTreeStructure() {

        StringBuilder structure = new StringBuilder();

        QueryTreeNode pointer = root;
        pointer.setVisited(true);
        int nodesVisited = 1;

        structure.append("Root: ").append(pointer.getOperator()).append("\n");
        //System.out.println("Root: " + pointer.getOperator());

        while (nodesVisited != size) {

            if (pointer.hasOnlyChild() && pointer.getOnlyChild() != null && !pointer.getOnlyChild().isVisited()) {

                pointer = pointer.getOnlyChild();
                pointer.setVisited(true);
                nodesVisited++;
                structure.append("Down: ").append(pointer.getOperator()).append("\n");
                //System.out.println("Down: " + pointer.getOperator());

            } else if (pointer.getLeftChild() != null && !pointer.getLeftChild().isVisited()) {

                pointer = pointer.getLeftChild();
                pointer.setVisited(true);
                nodesVisited++;
                structure.append("Left: ").append(pointer.getOperator()).append("\n");
                //System.out.println("Left: " + pointer.getOperator());

            } else if (pointer.getRightChild() != null && !pointer.getRightChild().isVisited()) {

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

    // TODO remove all below
    @Override
    public Iterator<Operator> iterator() {
        return new PreorderTraversalIterator();
    }

        private class PreorderTraversalIterator implements Iterator<Operator> {

            QueryTreeNode pointer;
            int nodesVisited;

            public PreorderTraversalIterator() {
                this.pointer = root;
                this.nodesVisited = 0;
            }

            @Override
            public boolean hasNext() {

                if (nodesVisited == size) {
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

                if (!lastNode) {

                    boolean foundNextNode = false;

                    while (!foundNextNode) {

                        if (pointer.getOnlyChild() != null && pointer.hasOnlyChild() && !pointer.getOnlyChild().isVisited()) {
                            pointer = pointer.getOnlyChild();
                            foundNextNode = true;

                        } else if (pointer.getLeftChild() != null && !pointer.getLeftChild().isVisited()) {
                            pointer = pointer.getLeftChild();
                            foundNextNode = true;

                        } else if (pointer.getRightChild() != null && !pointer.getRightChild().isVisited()) {
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