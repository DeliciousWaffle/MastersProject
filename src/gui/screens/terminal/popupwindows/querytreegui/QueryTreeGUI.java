package gui.screens.terminal.popupwindows.querytreegui;

import datastructures.trees.querytree.QueryTree;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.List;

public class QueryTreeGUI {

    private double screenWidth = 500, screenHeight = 500;
    private double center = screenWidth / screenHeight;
    private double upOffset, downOffset, leftOffset, rightOffset;

    private GridPane gridPane;

    public QueryTreeGUI(QueryTree queryTree) {

        List<List<QueryTree.Traversal>> everyNodesLocation = queryTree.getEveryNodesLocation();

        // Will be using a grid pane to position nodes
        gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.gridLinesVisibleProperty().set(true);

        // will need to know the tree height and its width to properly position nodes
        int treeHeight = 0;

        List<QueryTree.Traversal> lastNodesLocation = everyNodesLocation.get(everyNodesLocation.size() - 1);

        for(QueryTree.Traversal traversal : lastNodesLocation) {
            if(traversal == QueryTree.Traversal.UP) {
                break;
            }
            treeHeight++;
        }

        /* getting the width of the tree, width of below is 4 {{d, f}, {b}, {a, e, g}, {c}}
                                   a
                                 /   \
                                b     c
                              /   \
                             d     e
                             |     |
                             f     g
         */

        // will be used for calculating width
        int numLeftTraversals = 0, numRightTraversals = 0;

        // edge case: tree with a width of 1
        boolean encounteredOnlyChildren = true;

        for(QueryTree.Traversal traversal : lastNodesLocation) {
            if(traversal == QueryTree.Traversal.LEFT) {
                encounteredOnlyChildren = false;
                numLeftTraversals++;
            }
            if(traversal == QueryTree.Traversal.RIGHT) {
                encounteredOnlyChildren = false;
                numRightTraversals++;
            }
        }

        // the 1 comes from having at least a width of one for the tree
        int treeWidth = 1 + numLeftTraversals + ((numRightTraversals - numLeftTraversals) + 1);

        if(encounteredOnlyChildren) {
            treeWidth = 1;
        }

        for(List<QueryTree.Traversal> currentNodesLocation : queryTree.getEveryNodesLocation()) {

            Label label = new Label(queryTree.get(currentNodesLocation, QueryTree.Traversal.NONE).toString());


        }
    }

    public GridPane getGridPane() {
        return gridPane;
    }
}