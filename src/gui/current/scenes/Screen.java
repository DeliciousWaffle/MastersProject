package gui.current.scenes;

import gui.current.ScreenController;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public abstract class Screen {

    public static final double defaultWidth = 1080.0, defaultHeight = 720.0;

    public enum Type {
        TERMINAL_SCREEN, TABLES_SCREEN, USERS_SCREEN, OPTIONS_SCREEN, HELP_SCREEN
    }

    public abstract Scene getScreen();

    public HBox getButtonLayout(ScreenController screenController) {

        // creating the buttons
        Button terminalButton = new Button("Terminal");
        Button tablesButton   = new Button("Schema");
        Button usersButton    = new Button("Users");
        Button optionsButton  = new Button("Options");
        Button helpButton     = new Button("Help");

        // setting the preferred sizes
        terminalButton.setPrefSize(defaultWidth / 5.0, 50.0);
        tablesButton.setPrefSize(defaultWidth / 5.0, 50.0);
        usersButton.setPrefSize(defaultWidth / 5.0, 50.0);
        optionsButton.setPrefSize(defaultWidth / 5.0, 50.0);
        helpButton.setPrefSize(defaultWidth / 5.0, 50.0);

        // setting the minimum sizes
        terminalButton.setMinSize(0, 0);
        tablesButton.setMinSize(0, 0);
        usersButton.setMinSize(0, 0);
        optionsButton.setMinSize(0, 0);
        helpButton.setMinSize(0, 0);

        // setting font sizes
        terminalButton.setFont(new Font(25.0));
        tablesButton.setFont(new Font(25.0));
        usersButton.setFont(new Font(25.0));
        optionsButton.setFont(new Font(25.0));
        helpButton.setFont(new Font(25.0));

        // setting what screen to go on press
        terminalButton.setOnAction(e -> {
            screenController.setScreen(Type.TERMINAL_SCREEN);
        });
        tablesButton.setOnAction(e -> {
            screenController.setScreen(Type.TABLES_SCREEN);
        });
        usersButton.setOnAction(e -> {
            screenController.setScreen(Type.USERS_SCREEN);
        });
        optionsButton.setOnAction(e -> {
            screenController.setScreen(Type.OPTIONS_SCREEN);
        });
        helpButton.setOnAction(e -> {
            screenController.setScreen(Type.HELP_SCREEN);
        });

        // adding each to a horizontal layout
        HBox buttonLayout = new HBox();
        buttonLayout.getChildren().addAll(terminalButton, tablesButton, usersButton,
                optionsButton, helpButton);

        return buttonLayout;
    }
}