package gui.old;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import gui.old.ScreenManager;

public class Window extends Application {

    private final int WIDTH = 1080, HEIGHT = 720;
    private ScreenManager screenManager;
    private Button button1, button2;
    private Scene scene1, scene2;

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage window) throws Exception {

        window.setTitle("System Catalog");
        window.setWidth(WIDTH);
        window.setHeight(HEIGHT);

        /*screenManager = new ScreenManager(window);

        window.setScene(screenManager.getScene());
        window.show();*/

        button1 = new Button("Go to scene 2");
        button1.setMinSize(0, 0);
        button1.setPrefSize((WIDTH / 2), 100);
        button1.setMaxWidth(Double.MAX_VALUE);
        button1.setOnAction(e -> window.setScene(scene2));
        button2 = new Button("Go to scene 1");
        button2.setMinSize(0, 0);
        button2.setPrefSize(WIDTH / 2, 100);
        button2.setMaxWidth(Double.MAX_VALUE);
        button2.setOnAction(e -> window.setScene(scene1));

        HBox layout2 = new HBox(0);
        layout2.getChildren().addAll(button1, button2);

        scene1 = new Scene(layout2);

        window.setScene(scene1);
        window.show();

        window.widthProperty().addListener((observable, oldValue, newValue) -> {
            double width = (double) newValue;
            button1.setPrefWidth(width);
            button2.setPrefWidth(width);
            //button1.setPrefWidth(width);
            //button2.setPrefWidth(width);
            //button1.setMinSize();
        });

        window.heightProperty().addListener((observable, oldValue, newValue) -> {
            double height = (double) newValue;
            double scale = height / 720.0;
            double setToHeight = 100.0 * scale;
            button1.setPrefHeight(setToHeight);
            button2.setPrefHeight(setToHeight);
            //button1.setPrefHeight(height / 2);
        });
    }
}
