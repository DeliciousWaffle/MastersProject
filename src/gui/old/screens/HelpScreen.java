package gui.old.screens;

import gui.old.ScreenManager;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

public class HelpScreen implements Screen {

    private ScreenManager screenManager;
    private Scene scene;

    public HelpScreen(ScreenManager screenManager) {

        this.screenManager = screenManager;

        Button terminalButton = new Button("Terminal");
        Button schemaButton =   new Button("Schema");
        Button usersButton =    new Button("Users");
        Button helpButton =     new Button("Help");

        terminalButton.setFocusTraversable(false);
        schemaButton.setFocusTraversable(false);
        usersButton.setFocusTraversable(false);
        helpButton.setFocusTraversable(true);

        int width = 270;
        terminalButton.setPrefWidth(width);
        schemaButton.setPrefWidth(width);
        usersButton.setPrefWidth(width);
        helpButton.setPrefWidth(width);

        terminalButton.setFont(Font.font(25));
        schemaButton.setFont(Font.font(25));
        usersButton.setFont(Font.font(25));
        helpButton.setFont(Font.font(25));

        terminalButton.setOnAction(e -> screenManager.setCurrentScreen(ScreenManager.TERMINAL_SCREEN));
        schemaButton.setOnAction  (e -> screenManager.setCurrentScreen(ScreenManager.SCHEMA_SCREEN));
        usersButton.setOnAction   (e -> screenManager.setCurrentScreen(ScreenManager.USERS_SCREEN));
        helpButton.setOnAction    (e -> screenManager.setCurrentScreen(ScreenManager.HELP_SCREEN));

        HBox tabs = new HBox();
        tabs.getChildren().addAll(terminalButton, schemaButton, usersButton, helpButton);

        Label l1 = new Label("HelpScreen");

        BorderPane layout = new BorderPane();
        layout.setTop(tabs);
        layout.setCenter(l1);
        scene = new Scene(layout, 1080, 720);
    }

    @Override
    public Scene getScene() { return scene; }
}
