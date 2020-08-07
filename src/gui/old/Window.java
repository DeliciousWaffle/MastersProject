package gui.old;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import gui.old.ScreenManager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class Window extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        final int initWidth = 720;      //initial width
        final int initHeight = 1080;    //initial height
        final Pane root = new Pane();   //necessary evil

        Pane controller = new Pane();   //initial view
        controller.setPrefWidth(initWidth);     //if not initialized
        controller.setPrefHeight(initHeight);   //if not initialized
        root.getChildren().add(controller);     //necessary evil

        Scale scale = new Scale(1, 1, 0, 0);
        scale.xProperty().bind(root.widthProperty().divide(initWidth));     //must match with the one in the controller
        scale.yProperty().bind(root.heightProperty().divide(initHeight));   //must match with the one in the controller
        root.getTransforms().add(scale);

        ScreenManager screenManager = new ScreenManager(stage);

        Scene scene = screenManager.getScene();
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

        //add listener for the use of scene.setRoot()
        scene.rootProperty().addListener(new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> arg0, Parent oldValue, Parent newValue) {
                scene.rootProperty().removeListener(this);
                scene.setRoot(root);
                ((Region) newValue).setPrefWidth(initWidth);     //make sure is a Region!
                ((Region) newValue).setPrefHeight(initHeight);   //make sure is a Region!
                root.getChildren().clear();
                root.getChildren().add(newValue);
                scene.rootProperty().addListener(this);
            }
        });
    }
}
/*
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

        button1 = new Button("Go to scene 2");
        button1.setMinSize(0, 0);
        button1.setPrefSize(500, 100);
        button1.setMaxWidth(Double.MAX_VALUE);
        button1.setOnAction(e -> window.setScene(scene2));
        button2 = new Button("Go to scene 1");
        button2.setMinSize(0, 0);
        button2.setPrefSize(200, 100);
        button2.setMaxWidth(Double.MAX_VALUE);
        button2.setOnAction(e -> window.setScene(scene1));

        HBox layout2 = new HBox(0);
        layout2.getChildren().addAll(button1, button2);

        scene1 = new Scene(layout2);

        window.setScene(scene1);
        window.show();

        window.widthProperty().addListener((observable, oldValue, newValue) -> {
            double width = (double) newValue;
            double scale = width / 1080.0;
            double setToWidth = 200.0 * scale;
            button1.setPrefWidth(500 * scale);
            button2.setPrefWidth(setToWidth);

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
}*/
