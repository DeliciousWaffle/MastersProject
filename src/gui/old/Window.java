package gui.old;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import gui.old.ScreenManager;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class Window extends Application
{

    @Override
    public void start(Stage primaryStage)
    {

        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: green;");
        root.getChildren().add(playerHand());

        Scene scene = new Scene(root, 500, 450);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }

    HBox playerHand()
    {
        Image image = new Image(getClass().getResourceAsStream("a.png"));
        ImageView imageView1 = new ImageView(image);
        imageView1.setFitHeight(75);
        imageView1.setFitWidth(50);
        HBox.setMargin(imageView1, new Insets(0, -20, 0, 0));
        imageView1.setOnMouseEntered((event) -> {
            HBox.setMargin(imageView1, new Insets(0, 20, 0, 0));
        });
        imageView1.setOnMouseExited((event) -> {
            HBox.setMargin(imageView1, new Insets(0, -20, 0, 0));
        });
        imageView1.setOnMouseClicked((event) -> {
            System.out.println("Play card!");
        });

        ImageView imageView2 = new ImageView(image);
        imageView2.setFitHeight(75);
        imageView2.setFitWidth(50);
        HBox.setMargin(imageView2, new Insets(0, -20, 0, 0));
        imageView2.setOnMouseEntered((event) -> {
            HBox.setMargin(imageView2, new Insets(30, -20, 0, 0));
        });
        imageView2.setOnMouseExited((event) -> {
            HBox.setMargin(imageView2, new Insets(0, -20, 0, 0));
        });
        imageView2.setOnMouseClicked((event) -> {
            System.out.println("Play card!");
        });

        ImageView imageView3 = new ImageView(image);
        imageView3.setFitHeight(75);
        imageView3.setFitWidth(50);
        HBox.setMargin(imageView3, new Insets(0, -20, 0, 0));
        imageView3.setOnMouseEntered((event) -> {
            HBox.setMargin(imageView3, new Insets(0, 20, 0, 20));
        });
        imageView3.setOnMouseExited((event) -> {
            HBox.setMargin(imageView3, new Insets(0, -20, 0, 0));
        });
        imageView3.setOnMouseClicked((event) -> {
            System.out.println("Play card!");
        });

        ImageView imageView4 = new ImageView(image);
        imageView4.setFitHeight(75);
        imageView4.setFitWidth(50);
        HBox.setMargin(imageView4, new Insets(0, -20, 0, 0));
        imageView4.setOnMouseEntered((event) -> {
            HBox.setMargin(imageView4, new Insets(0, 20, 0, 20));
        });
        imageView4.setOnMouseExited((event) -> {
            HBox.setMargin(imageView4, new Insets(0, -20, 0, 0));
        });
        imageView4.setOnMouseClicked((event) -> {
            System.out.println("Play card!");
        });

        ImageView imageView5 = new ImageView(image);
        imageView5.setFitHeight(75);
        imageView5.setFitWidth(50);
        HBox.setMargin(imageView5, new Insets(0, -20, 0, 0));
        imageView5.setOnMouseEntered((event) -> {
            HBox.setMargin(imageView5, new Insets(30, -20, 0, 0));
        });
        imageView5.setOnMouseExited((event) -> {
            HBox.setMargin(imageView5, new Insets(0, -20, 0, 0));
        });
        imageView5.setOnMouseClicked((event) -> {
            System.out.println("Play card!");
        });

        HBox hBox = new HBox(imageView1, imageView2, imageView3, imageView4, imageView5);
        hBox.setMinSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        hBox.setPrefSize(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
        hBox.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
        //hBox.setStyle("-fx-background-color: red;");
        return hBox;
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
