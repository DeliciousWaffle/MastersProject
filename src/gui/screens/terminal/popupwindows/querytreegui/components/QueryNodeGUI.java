package gui.screens.terminal.popupwindows.querytreegui.components;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class QueryNodeGUI {

    private String text;
    private double x, y;
    private double backgroundWidth, backgroundHeight;
    private double textWidth, textHeight;

    public QueryNodeGUI(String text, double x, double y) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.backgroundWidth = 100;
        this.backgroundHeight = 50;

    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void render(GraphicsContext gc) {
        double xOffset = text.length() * 7;
        // background border
        gc.setFill(Color.BLACK);
        gc.fillRect(x - 2 - (xOffset + 35), y - 2, backgroundWidth + 4 + (xOffset * 2), backgroundHeight + 4);

        // background
        gc.setFill(Color.rgb(70, 70, 70));
        gc.fillRect(x - (xOffset + 35), y, backgroundWidth + (xOffset * 2), backgroundHeight);


gc.setFill(Color.RED);
gc.fillOval(x, y, 10, 10);
        // text
        //gc.setFill(Color.BLACK);
        Font font = new Font(text, 35);
        gc.setFont(font);

        gc.fillText(text, x - xOffset, y + 35);
        //gc.fillText(text, x, y);
    }
}