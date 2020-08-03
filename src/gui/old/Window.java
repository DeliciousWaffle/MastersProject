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

        screenManager = new ScreenManager(window);

        window.setScene(screenManager.getScene());
        window.show();
/*
        button1.setPrefWidth(WIDTH / 2);

        button1 = new Button("Go to scene 2");
        button1.setOnAction(e -> window.setScene(scene2));
        button2 = new Button("Go to scene 1");
        button2.setOnAction(e -> window.setScene(scene1));

        VBox layout1 = new VBox(4);
        layout1.getChildren().add(button1);
        layout1.getChildren().add(new Label("This is just some text"));
        HBox layout2 = new HBox(20);
        layout2.getChildren().add(button2);
        layout2.getChildren().add(new Label(""));

        window.widthProperty().addListener((observable, oldValue, newValue) -> {
            double width = (double) newValue;
            button1.setPrefWidth(width / 2);
        });

        window.heightProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(window.getHeight());
            double height = (double) newValue;
            button1.setPrefHeight(height / 2);
        });

        scene1 = new Scene(layout1, 300, 300);
        scene2 = new Scene(layout2, 500, 300);

        window.setScene(scene1);*/
    }
}
