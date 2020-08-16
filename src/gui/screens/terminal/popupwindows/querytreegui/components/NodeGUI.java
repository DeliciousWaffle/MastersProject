package gui.screens.terminal.popupwindows.querytreegui.components;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class NodeGUI {

    private final String text;
    private double x, y;
    private final double width, height;
    private final double textSize;

    public NodeGUI(String text, double x, double y) {

        this.text = text;
        this.x = x;
        this.y = y;

        this.textSize = 35;

        // used to get the actual pixel width and height of the text
        Text temp = new Text(text);
        temp.setFont(new Font(textSize));

        this.width = temp.getLayoutBounds().getWidth();
        this.height = temp.getLayoutBounds().getHeight();
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

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void render(GraphicsContext gc) {

        // background border
        gc.setFill(Color.BLACK);
        gc.fillRoundRect(x - 2 - 10 - width / 2, y - 2 - height + 10 - 5, width + 4 + 20, height + 4 + 10, 20, 20);

        // background
        gc.setFill(Color.rgb(90, 90, 90));
        gc.fillRoundRect(x - 10 - width / 2, y - height + 10 - 5, width + 20, height + 10, 20, 20);

        // text
        gc.setFill(Color.WHITE);
        Font font = new Font(text, textSize);
        gc.setFont(font);
        gc.fillText(text, x - width / 2, y);
    }
}