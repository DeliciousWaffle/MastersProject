package main;

import gui.ScreenController;
import javafx.application.Application;
import javafx.stage.Stage;
import systemcatalog.SystemCatalog;

/**
 * @author Jake Bussa
 *
 * Application that is intended to emulate an SQL-like environment along with illustrating how queries get processed.
 * Tables and users exist like a typical database management system and commands can be used to either query or change
 * these variables.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        // handles all the data processing used by the application
        SystemCatalog systemCatalog = new SystemCatalog();

        // handles all GUI logic like button clicks, switching between screens, launching windows, etc.
        new ScreenController(primaryStage, systemCatalog);
    }

    public static void main(String[] args) {
        launch(args);
    }
}