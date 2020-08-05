package gui.current;

import gui.current.scenes.Screen;
import javafx.application.Application;
import javafx.stage.Stage;

public class TempMain extends Application {

    private ScreenController screenController;

    @Override
    public void start(Stage primaryStage) {
        screenController = new ScreenController(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}