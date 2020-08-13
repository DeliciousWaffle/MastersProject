package main;

import gui.ScreenController;
import javafx.application.Application;
import javafx.stage.Stage;
import systemcatalog.SystemCatalog;

/**
 * My Master's Project (CS 599) for Western Illinois University.
 * 2020 04 12
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        // handles all the data used by the system
        SystemCatalog systemCatalog = new SystemCatalog();
        // handles what screen to show the user
        ScreenController screenController = new ScreenController(primaryStage, systemCatalog);
    }

    public static void main(String[] args) {
        launch(args);
    }
}