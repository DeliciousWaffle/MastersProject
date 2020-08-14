package gui.screens.terminal.popupwindows.querytreegui;

import datastructures.trees.querytree.QueryTree;
import gui.screens.Screen;
import gui.screens.terminal.popupwindows.querytreegui.components.QueryNodeGUI;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryTreeGUI {

    private ScrollPane container;

    public QueryTreeGUI(QueryTree queryTree) {

        // pref screen width and height of the container
        double prefContainerWidth = 1080;
        double prefContainerHeight = 720;

        // canvas screen width and height, increases if nodes x and y positions go offscreen, buffer prevents clipping
        double canvasWidth = prefContainerWidth;
        double canvasHeight = prefContainerHeight;
        double canvasBuffer = 50;

        // starting points for where to draw the first node of the query tree
        double startX = canvasWidth / 2;
        double startY = 100;

        // contains the canvas, allows to scroll horizontally or vertically, which is nifty
        this.container = new ScrollPane();
        container.setPrefWidth(prefContainerWidth);
        container.setPrefHeight(prefContainerHeight);

        // where we are drawing to, width and height can change
        Canvas canvas = new Canvas();
        canvas.setWidth(canvasWidth);
        canvas.setHeight(canvasHeight);

        // used for drawing to the canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // getting an estimate of the positions of the nodes that will be drawn to screen
        List<QueryNodeGUI> queryNodeGUIs = new ArrayList<>();

        // spacing between nodes
        double nodeOffset = 100;

        for(List<QueryTree.Traversal> currentNodesLocation : queryTree.getEveryNodesLocation()) {

            double x = 0;
            double y = 0;

            for(QueryTree.Traversal traversal : currentNodesLocation) {
                switch(traversal) {
                    case NONE:
                        x = startX;
                        y = startY;
                        break;
                    case LEFT:
                        x -= nodeOffset;
                        y += nodeOffset;
                        break;
                    case RIGHT:
                        x += nodeOffset;
                        y += nodeOffset;
                        break;
                    case UP:
                        y -= nodeOffset;
                        break;
                    case DOWN:
                        y += nodeOffset;
                        break;
                }
            }

            // creating the node
            String text = queryTree.get(currentNodesLocation, QueryTree.Traversal.NONE).toString();
            QueryNodeGUI queryNodeGUI = new QueryNodeGUI(text, x, y);
            queryNodeGUIs.add(queryNodeGUI);
        }

        // after getting the locations of all nodes, now getting the line positions
        List<Line> lines = new ArrayList<>();

        for(int i = 1; i < queryNodeGUIs.size(); i++) {

            QueryNodeGUI prevQueryNodeGUI = queryNodeGUIs.get(i - 1);
            QueryNodeGUI currQueryNodeGUI = queryNodeGUIs.get(i);

            double prevX = prevQueryNodeGUI.getX();
            double prevY = prevQueryNodeGUI.getY();

            double currX = currQueryNodeGUI.getX();
            double currY = currQueryNodeGUI.getY();

            Line line = new Line(prevX, prevY, currX, currY);
            lines.add(line);
        }

        gc.setFill(Color.BLUE);
        gc.fillOval(startX, startY, 15,15);

        // will need to resize the canvas if any nodes go offscreen, prevents them from being clipped
        for(QueryNodeGUI queryNodeGUI : queryNodeGUIs) {

            double x = queryNodeGUI.getX();
            double y = queryNodeGUI.getY();

            // increase canvas width if the current node's x position goes offscreen
            if(x <= 0) {
                canvasWidth += canvasBuffer;
                queryNodeGUI.setX(x - canvasBuffer);
                System.out.println("yee widht");
            }

            // increase canvas height if the current node's y position goes offscreen
            if(y >= canvasWidth) {
                canvasHeight += canvasBuffer;
                System.out.println("yes height");
            }
        }

        canvas.setWidth(canvasWidth);
        canvas.setHeight(canvasHeight);

        // finally drawing to the canvas, drawing lines first, so they appear behind the nodes
        for(QueryNodeGUI queryNodeGUI : queryNodeGUIs) {
            queryNodeGUI.render(gc);
        }



        // drawing nodes now
        /*for(Line line : lines) {
            gc.setFill(Color.BLACK);
            gc.setLineWidth(5);
            gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        }*/

        // finally add the canvas to the container
        container.setContent(canvas);
    }

    public ScrollPane getContainer() {
        return container;
    }

    /*
    private GridPane gridPane;

    public QueryTreeGUI(QueryTree queryTree) {

        List<List<QueryTree.Traversal>> everyNodesLocation = queryTree.getEveryNodesLocation();

        // Will be using a grid pane to position nodes
        gridPane = new GridPane();
        gridPane.setPrefSize(Screen.defaultWidth, Screen.defaultHeight);
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.setAlignment(Pos.CENTER);
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

        /getting the width of the tree, width of below is 4 {{d, f}, {b}, {a, e, g}, {c}}
                                   a
                                 /   \
                                b     c
                              /   \
                             d     e
                             |     |
                             f     g


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

        treeWidth--;

        for(List<QueryTree.Traversal> currentNodesLocation : queryTree.getEveryNodesLocation()) {
            System.out.println(currentNodesLocation);
            System.out.println(queryTree.get(currentNodesLocation, QueryTree.Traversal.NONE).toString());
        }

        List<Integer> cols = new ArrayList<>();
        List<Integer> rows = new ArrayList<>();

        for(List<QueryTree.Traversal> currentNodesLocation : queryTree.getEveryNodesLocation()) {

            // used for determining where to place the label
            int colLocation = treeWidth;
            int rowLocation = 0;

            // iterate through the traversals for this node to get where it should be placed
            for(QueryTree.Traversal traversal : currentNodesLocation) {
                switch(traversal) {
                    case NONE:
                        colLocation--;
                        break;
                    case LEFT:
                        colLocation--; rowLocation++;
                        break;
                    case RIGHT:
                        colLocation+=2; rowLocation++;
                        break;
                    case UP:
                        rowLocation--;
                        break;
                    case DOWN:
                        rowLocation++;
                        break;
                }
            }

            cols.add(colLocation);
            rows.add(rowLocation);

            Label label = new Label(queryTree.get(currentNodesLocation, QueryTree.Traversal.NONE).toString());
            label.setFont(new Font(20));
            GridPane.setHalignment(label, HPos.CENTER);
            gridPane.add(label, colLocation, rowLocation);
            //gridPane.add(new Line(0, 0, 100, 100), colLocation, colLocation + 1, rowLocation, rowLocation + 1);

        }

        for(int i = 1; i < cols.size(); i++) {
            System.out.println(cols.get(i) + " " + rows.get(i));
            int prevCol = cols.get(i - 1);
            int prevRow = rows.get(i - 1);
            int currCol = cols.get(i);
            int currRow = rows.get(i);

            System.out.println("prevCol: " + prevCol + " prevRow: " + prevRow + " currCol: " + currCol + " currRow: " + currRow);
            // vertical line
            if(currCol == prevCol) {
                Line l = new Line(0, 0, 0, 0);
                l.setStroke(Color.RED);
                GridPane.setHalignment(l, HPos.CENTER);
                GridPane.setValignment(l, VPos.BOTTOM);
                Label label = (Label)gridPane.getChildren().get(1);
                l.endYProperty().bind(gridPane.heightProperty().divide(10));
                gridPane.add(l,  prevCol, prevRow, currCol, ++currRow);
            }
            // / line
            if(currCol < prevCol) {

            }
            // \ line
            if(currCol > prevCol) {

            }
            break;
        }
    }

    public GridPane getGridPane() {
        return gridPane;
    }*/
}