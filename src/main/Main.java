package main;

import datastructures.datacontroller.DataController;
import gui.ScreenController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * My Master's Project (CS 599) for Western Illinois University.
 * 2020 04 12
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        // handles all the data used by the system
        DataController dataController = new DataController();

        // handles what screen to show the user
        ScreenController screenController = new ScreenController(primaryStage, dataController);
    }

    public static void main(String[] args) {
        launch(args);
    }
}