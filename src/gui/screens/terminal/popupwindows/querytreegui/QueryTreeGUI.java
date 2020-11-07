package gui.screens.terminal.popupwindows.querytreegui;

import datastructures.querytree.QueryTree;
import datastructures.querytree.operator.Operator;
import files.io.FileType;
import files.io.IO;
import gui.screens.terminal.popupwindows.querytreegui.components.NodeGUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A GUI representation of the query tree and needs a healthy refactor.
 */
public class QueryTreeGUI {

    private final ScrollPane container;
    private final BorderPane centerScrollPane;

    public QueryTreeGUI(QueryTree queryTree) {

        // pref screen width and height of the container
        double prefContainerWidth = 1080;
        double prefContainerHeight = 720;

        // canvas screen width and height, increases if nodes x and y positions go offscreen, buffer prevents clipping
        double canvasWidth = prefContainerWidth;
        double canvasHeight = prefContainerHeight - 100;
        double canvasBuffer = 100;

        // starting points for where to draw the first node of the query tree, startX will be tweaked later
        double startX = canvasWidth / 2;
        double startY = 100;

        // contains the canvas, allows to scroll horizontally or vertically, which is nifty
        this.container = new ScrollPane();
        container.getStylesheets().add(IO.readCSS(FileType.CSS.SCROLL_PANE_STYLE));
        container.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        container.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        container.setHvalue(0.5); // set scroll bar in the middle by default
        container.setStyle("-fx-background-color: rgb(30, 30, 30);");

        // where we are drawing to, width and height can change upon window resize
        Canvas canvas = new Canvas();
        canvas.setWidth(canvasWidth);
        canvas.setHeight(canvasHeight);

        // used for drawing to the canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();

        List<NodeGUI> nodeGUIList = new ArrayList<>();

        // extracting the string representation of each operator, if that operator is a projection or selection, will
        // modify that string to include line breaks so that it is easier to see
        List<String> nodeTextList = queryTree.getOperatorsAndLocations(QueryTree.TreeTraversal.PREORDER)
                .keySet()
                .stream()
                .map(operator -> {
                    if (operator.getType() == Operator.Type.PROJECTION ||
                            operator.getType() == Operator.Type.COMPOUND_SELECTION) {
                        String text = operator.toString();
                        text = text.replaceAll("∧ ", "∧\n");
                        char[] tokens = text.toCharArray();
                        int occurrences = 0;
                        for (int i = 0; i < tokens.length - 1; i++) {
                            if (tokens[i] == ',') {
                                occurrences++;
                                if (occurrences == 3) {
                                    tokens[i + 1] = '\n'; // replace ", " with ",\n"
                                    occurrences = 0;
                                }
                            }
                        }
                        return new String(tokens);
                    } else if (operator.getType() == Operator.Type.INNER_JOIN) {
                        String text = operator.toString();
                        return text.replaceAll("= ", "=\n");
                    } else {
                        return operator.toString();
                    }
                })
                .collect(Collectors.toList());

        List<List<QueryTree.Traversal>> nodeLocationList =
                new ArrayList<>(queryTree.getOperatorsAndLocations(QueryTree.TreeTraversal.PREORDER).values());

        // spacing between nodes
        double nodeOffset = 100.0, totalHeightOffset = 0.0;

        // creating and adding to the nodeGUI list
        for (int i = 0; i < nodeTextList.size(); i++) {

            String text = nodeTextList.get(i);
            List<QueryTree.Traversal> location = nodeLocationList.get(i);

            // adjust the width and height of the node
            Text temp = new Text(text);
            temp.setFont(new Font(35));
            double height = temp.getLayoutBounds().getHeight();

            // adjust the height if needed
            if (height > 46.552734375) {
                double heightOffset = (height - 46.552734375);
                totalHeightOffset += heightOffset;
            }

            // figure out where to draw the current operator on screen
            double x = startX, y = startY;

            List<QueryTree.Traversal> workingTraversal = new ArrayList<>();

            for (QueryTree.Traversal traversal : location) {
                workingTraversal.add(traversal);

                // get the height of the node located at the current working traversal
                Operator operator = queryTree.get(workingTraversal, QueryTree.Traversal.NONE);
                String operatorString = operator.toString();
                if (operator.getType() == Operator.Type.PROJECTION ||
                        operator.getType() == Operator.Type.COMPOUND_SELECTION) {
                    operatorString = operator.toString();
                    operatorString = operatorString.replaceAll("∧ ", "∧\n");
                    char[] tokens = operatorString.toCharArray();
                    int occurrences = 0;
                    for (int q = 0; q < tokens.length - 1; q++) {
                        if (tokens[q] == ',') {
                            occurrences++;
                            if (occurrences == 3) {
                                tokens[q + 1] = '\n'; // replace ", " with ",\n"
                                occurrences = 0;
                            }
                        }
                    }
                    operatorString = new String(tokens);
                } else if (operator.getType() == Operator.Type.INNER_JOIN) {
                    operatorString = operator.toString();
                    operatorString = operatorString.replaceAll("= ", "=\n");
                }
                temp = new Text(operatorString);
                temp.setFont(new Font(35));
                double width = temp.getLayoutBounds().getWidth();
                height = temp.getLayoutBounds().getHeight();
                if (height > 46.552734375) {
                    double heightOffset = (height - 46.552734375);
                    totalHeightOffset += heightOffset;
                }

                switch (traversal) {
                    case DOWN: {
                        y += nodeOffset;
                        y += height;
                        break;
                    }
                    case LEFT: {
                        x -= nodeOffset;
                        y += nodeOffset;
                        x -= width / 2;
                        y += height;
                        break;
                    }
                    case RIGHT: {
                        x += nodeOffset;
                        y += nodeOffset;
                        x += width / 2;
                        y += height;
                        break;
                    }
                }
            }
            // create the node
            nodeGUIList.add(new NodeGUI(text, x, y));
        }

        // will need to resize the canvas if any nodes go offscreen, prevents them from being clipped
        for (NodeGUI nodeGUI : nodeGUIList) {

            double x = nodeGUI.getX();
            double y = nodeGUI.getY();

            double width = nodeGUI.getWidth();
            double height = nodeGUI.getHeight();

            // increase canvas width if a node moves right offscreen
            while (x + width / 2 >= canvasWidth) {
                canvasWidth += canvasBuffer;
            }

            // increase canvas width if a node moves left offscreen
            while (nodeGUI.getX() - width / 2 <= 0) { // note: using nodeGUI.getX() because var x won't change

                // will need to translate all nodes to the right too
                for (NodeGUI toTranslate : nodeGUIList) {
                    toTranslate.setX(toTranslate.getX() + canvasBuffer);
                }

                canvasWidth += canvasBuffer;
            }

            // increase canvas height if the current node moves up offscreen
            while (nodeGUI.getY() - height - 100 <= 0) {

                for (NodeGUI toTranslate : nodeGUIList) {
                    toTranslate.setY(toTranslate.getY() + canvasBuffer);
                }

                canvasHeight += canvasBuffer;
            }

            // increase canvas height if the current node moves down offscreen
            while (((y + height) + canvasBuffer) >= canvasHeight) {
                canvasHeight += canvasBuffer;
            }
        }

        canvas.setWidth(canvasWidth);
        canvas.setHeight(canvasHeight);

        // after getting the locations of all nodes, now getting the line positions
        List<Line> lines = new ArrayList<>();

        int offset = 0;

        for(int i = 1; i < nodeGUIList.size(); i++) {

            NodeGUI prevQueryNodeGUI = nodeGUIList.get(i - 1);
            NodeGUI currQueryNodeGUI = nodeGUIList.get(i);

            int prevTraversalSize = nodeLocationList.get(i-1).size();
            int currTraversalSize = nodeLocationList.get(i).size();

            if(currTraversalSize <= prevTraversalSize) {

                int temp = (currTraversalSize - prevTraversalSize);
                temp--;
                offset += temp;

                try {
                    prevQueryNodeGUI = nodeGUIList.get(offset + (i - 1));
                } catch (Exception e) {
                    System.out.println("Problem with getting lines to connect!");
                }
            }

            double prevX = prevQueryNodeGUI.getX();
            double prevY = prevQueryNodeGUI.getY();

            double currX = currQueryNodeGUI.getX();
            double currY = currQueryNodeGUI.getY();

            Line line = new Line(prevX, prevY, currX, currY);
            lines.add(line);
        }

        // finally drawing to the canvas, drawing lines first, so they appear behind the nodes
        for(Line line : lines) {

            // border for the line
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(8);
            gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());

            gc.setStroke(Color.WHITE);
            gc.setLineWidth(4);
            gc.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        }

        for(NodeGUI nodeGUI : nodeGUIList) {
            nodeGUI.render(gc);
        }

        // will add the canvas to a border pane which will recenter the whole tree when window is resized
        centerScrollPane = new BorderPane();
        centerScrollPane.setPrefSize(prefContainerWidth, prefContainerHeight);
        centerScrollPane.setBackground(new Background(
                new BackgroundFill(Color.rgb(30, 30, 30), CornerRadii.EMPTY, Insets.EMPTY)));
        centerScrollPane.setCenter(canvas);
        BorderPane.setAlignment(canvas, Pos.CENTER);

        // speeding up the scroll speed
        canvas.setOnScroll(e -> {
            double deltaY = e.getDeltaY() * 1.2;
            double width = container.getContent().getBoundsInLocal().getWidth();
            double vValue = container.getVvalue();
            container.setVvalue(vValue + -(deltaY / width));
        });

        // finally add everything to the container
        container.setContent(centerScrollPane);
        container.setPrefSize(prefContainerWidth, prefContainerHeight);
        container.setMinSize(0, 0);
    }

    public ScrollPane getContainer() {
        return container;
    }

    public void adjustWidth(double width) {
        centerScrollPane.setPrefWidth(width);
    }

    public void adjustHeight(double height) {
        centerScrollPane.setPrefHeight(height);
    }
}