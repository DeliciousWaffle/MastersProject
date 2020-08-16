package gui.screens.terminal.popupwindows.querytreegui;

import datastructures.trees.querytree.QueryTree;
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

import java.util.ArrayList;
import java.util.List;

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
        container.setStyle("-fx-background-color: rgb(30, 30, 30);");

        // where we are drawing to, width and height can change
        Canvas canvas = new Canvas();
        canvas.setWidth(canvasWidth);
        canvas.setHeight(canvasHeight);

        // used for drawing to the canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // getting an estimate of the positions of the nodes that will be drawn to screen
        List<NodeGUI> nodeGUIS = new ArrayList<>();

        // spacing between nodes
        double nodeOffset = 100;

        // adding the nodes, their locations will be tweaked later
        for(List<QueryTree.Traversal> currentNodesLocation : queryTree.getEveryNodesLocation()) {
            // creating the node
            String text = queryTree.get(currentNodesLocation, QueryTree.Traversal.NONE).toString();
            NodeGUI nodeGUI = new NodeGUI(text, 0, 0);
            nodeGUIS.add(nodeGUI);
        }

        for(int i = 0; i < nodeGUIS.size(); i++) {

            NodeGUI currentNodeGUI = nodeGUIS.get(i);
            List<QueryTree.Traversal> currentNodesLocation = queryTree.getEveryNodesLocation().get(i);

            double x = 0;
            double y = 0;

            for(int j = 0; j < currentNodesLocation.size(); j++) {

                QueryTree.Traversal traversal = currentNodesLocation.get(j);

                // some nodes will need to be spaced further apart, to take into consideration their widths, prevents overlapping
                double widthOffset = 0;

                switch (traversal) {
                    case NONE:
                        x = startX;
                        y = startY;
                        break;
                    case LEFT:
                        widthOffset = nodeGUIS.get(j).getWidth() / 4;
                        x -= (nodeOffset + widthOffset);
                        y += nodeOffset;
                        break;
                    case RIGHT:
                        widthOffset = nodeGUIS.get(j).getWidth() / 4;
                        x += (nodeOffset + widthOffset);
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

            // updating the x and y positions
            currentNodeGUI.setX(x);
            currentNodeGUI.setY(y);
        }

        // will need to resize the canvas if any nodes go offscreen, prevents them from being clipped
        for(NodeGUI nodeGUI : nodeGUIS) {

            double x = nodeGUI.getX();
            double y = nodeGUI.getY();

            double width = nodeGUI.getWidth();
            double height = nodeGUI.getHeight();

            // rare situation in which node goes offscreen in the right direction, increase canvas width
            if(x + width/2 >= canvasWidth) {
                canvasWidth+= canvasBuffer;
            }

            // increase canvas width if a node moves left offscreen
            if(x - width/2 <= 0) {

                // will need to translate all nodes to the right too
                for(NodeGUI toTranslate : nodeGUIS) {
                    toTranslate.setX(toTranslate.getX() + canvasBuffer);
                }

                canvasWidth+= canvasBuffer;
            }

            // increase canvas height if the current node's y position goes offscreen
            if(((y + height) + canvasBuffer) >= canvasHeight) {
                canvasHeight += canvasBuffer;
            }
        }

        canvas.setWidth(canvasWidth);
        canvas.setHeight(canvasHeight);

        // after getting the locations of all nodes, now getting the line positions
        List<Line> lines = new ArrayList<>();

        int offset = 0;

        for(int i = 1; i < nodeGUIS.size(); i++) {

            NodeGUI prevQueryNodeGUI = nodeGUIS.get(i - 1);
            NodeGUI currQueryNodeGUI = nodeGUIS.get(i);

            int prevTraversalSize = queryTree.getEveryNodesLocation().get(i-1).size();
            int currTraversalSize = queryTree.getEveryNodesLocation().get(i).size();

            if(currTraversalSize <= prevTraversalSize) {

                int temp = (currTraversalSize - prevTraversalSize);
                temp--;
                offset += temp;

                try {
                    prevQueryNodeGUI = nodeGUIS.get(offset + (i - 1));
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

        for(NodeGUI nodeGUI : nodeGUIS) {
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