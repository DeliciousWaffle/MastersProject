package gui.current;

import gui.current.scenes.Screen;
import javafx.application.Application;
import javafx.stage.Stage;

public class TempMain extends Application {

    private ScreenController screenController;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setWidth(Screen.defaultWidth);
        primaryStage.setHeight(Screen.defaultHeight);

        screenController = new ScreenController(primaryStage);

        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            screenController.scaleWidth();
        });

        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
           screenController.scaleHeight();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}