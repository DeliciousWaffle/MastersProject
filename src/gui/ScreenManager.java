package gui;

import gui.screens.*;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Responsible for changing between the Terminal, Schema, Users, and Help tabs.
 * Since each of these tabs has its own unique interface, this is just a nice
 * way to keep everything organized. This is a singleton class, meaning there
 * is only one instance of this class allowed when running the program.
 */

public class ScreenManager {

    private static ScreenManager screenManager = null;
    private Stage primaryStage;
    public static final int TERMINAL_SCREEN = 0, SCHEMA_SCREEN = 1, USERS_SCREEN = 2, HELP_SCREEN = 3;
    private Screen[] screens;
    private int currentScreen;

    private ScreenManager(Stage primaryStage) {

        this.primaryStage = primaryStage;

        screens = new Screen[] {
                new TerminalScreen(this), new SchemaScreen(this),
                new UsersScreen(this),    new HelpScreen(this)
        };

        currentScreen = TERMINAL_SCREEN;
    }

    public static ScreenManager getInstance(Stage primaryStage) {

        if(screenManager == null) {
            screenManager = new ScreenManager(primaryStage);
        }

        return screenManager;
    }

    public void setCurrentScreen(int currentScreen) {
        this.currentScreen = currentScreen;
        primaryStage.setScene(screens[currentScreen].getScene());
    }

    public int getCurrentScreen() {
        return currentScreen;
    }
}
