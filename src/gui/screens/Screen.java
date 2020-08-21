package gui.screens;

import gui.ScreenController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public abstract class Screen {

    public static final double defaultWidth = 1080.0, defaultHeight = 720.0;

    public enum Type {
        TERMINAL_SCREEN, TABLES_SCREEN, USERS_SCREEN, OPTIONS_SCREEN, HELP_SCREEN
    }

    private Button terminalButton, tablesButton, usersButton, optionsButton, helpButton;
    private double buttonWidth, buttonHeight;
    private double fontSize;

    public abstract Scene getScreen();

    public HBox getButtonLayout(ScreenController screenController) {

        // creating the buttons
        this.terminalButton = new Button("Terminal");
        this.tablesButton   = new Button("Tables");
        this.usersButton    = new Button("Users");
        this.optionsButton  = new Button("Options");
        this.helpButton     = new Button("Help");

        // setting the preferred sizes
        buttonWidth = defaultWidth / 5.0;
        buttonHeight = 50.0;

        terminalButton.setPrefSize(buttonWidth, buttonHeight);
        tablesButton.setPrefSize(buttonWidth, buttonHeight);
        usersButton.setPrefSize(buttonWidth, buttonHeight);
        optionsButton.setPrefSize(buttonWidth, buttonHeight);
        helpButton.setPrefSize(buttonWidth, buttonHeight);

        // setting the minimum sizes
        terminalButton.setMinSize(0, 0);
        tablesButton.setMinSize(0, 0);
        usersButton.setMinSize(0, 0);
        optionsButton.setMinSize(0, 0);
        helpButton.setMinSize(0, 0);

        // setting font sizes
        fontSize = 25.0;

        terminalButton.setFont(new Font(fontSize));
        tablesButton.setFont(new Font(fontSize));
        usersButton.setFont(new Font(fontSize));
        optionsButton.setFont(new Font(fontSize));
        helpButton.setFont(new Font(fontSize));

        // setting some styling
        String buttonStyle = " -fx-background-color: rgb(100, 100, 100); -fx-text-fill: white; -fx-padding: 0;";

        // adds a sick drop shadow effect to the buttons
        terminalButton.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
        tablesButton.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
        usersButton.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
        optionsButton.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
        helpButton.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        terminalButton.setStyle(buttonStyle);
        tablesButton.setStyle(buttonStyle);
        usersButton.setStyle(buttonStyle);
        optionsButton.setStyle(buttonStyle);
        helpButton.setStyle(buttonStyle);

        // more styling
        String buttonEnteredStyle = "-fx-background-color: rgb(150, 150, 150); -fx-text-fill: white; -fx-border-color: transparent;";
        String buttonExitedStyle = buttonStyle;

        terminalButton.setOnMouseEntered(e -> {
            terminalButton.setStyle(buttonEnteredStyle);
        });
        tablesButton.setOnMouseEntered(e -> {
            tablesButton.setStyle(buttonEnteredStyle);
        });
        usersButton.setOnMouseEntered(e -> {
            usersButton.setStyle(buttonEnteredStyle);
        });
        optionsButton.setOnMouseEntered(e -> {
            optionsButton.setStyle(buttonEnteredStyle);
        });
        helpButton.setOnMouseEntered(e -> {
            helpButton.setStyle(buttonEnteredStyle);
        });

        terminalButton.setOnMouseExited(e -> {
            terminalButton.setStyle(buttonExitedStyle);
        });
        tablesButton.setOnMouseExited(e -> {
            tablesButton.setStyle(buttonExitedStyle);
        });
        usersButton.setOnMouseExited(e -> {
            usersButton.setStyle(buttonExitedStyle);
        });
        optionsButton.setOnMouseExited(e -> {
            optionsButton.setStyle(buttonExitedStyle);
        });
        helpButton.setOnMouseExited(e -> {
            helpButton.setStyle(buttonExitedStyle);
        });


        // removing the outline that appears when a button was clicked
        terminalButton.setFocusTraversable(false);
        tablesButton.setFocusTraversable(false);
        usersButton.setFocusTraversable(false);
        optionsButton.setFocusTraversable(false);
        helpButton.setFocusTraversable(false);

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
        HBox.setMargin(terminalButton, new Insets(10, 5, 10, 10));
        HBox.setMargin(tablesButton, new Insets(10, 5, 10, 5));
        HBox.setMargin(usersButton, new Insets(10, 5, 10, 5));
        HBox.setMargin(optionsButton, new Insets(10, 5, 10, 5));
        HBox.setMargin(helpButton, new Insets(10, 10, 10, 5));

        return buttonLayout;
    }

    public void adjustButtonWidth(double newScreenWidth) {
        terminalButton.setPrefWidth(newScreenWidth / 5.0);
        tablesButton.setPrefWidth(newScreenWidth / 5.0);
        usersButton.setPrefWidth(newScreenWidth / 5.0);
        optionsButton.setPrefWidth(newScreenWidth / 5.0);
        helpButton.setPrefWidth(newScreenWidth / 5.0);
    }
}