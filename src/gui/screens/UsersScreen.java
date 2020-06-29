package gui.screens;

import gui.ScreenManager;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class UsersScreen implements Screen {

    private ScreenManager screenManager;
    private Scene scene;

    public UsersScreen(ScreenManager screenManager) {

        this.screenManager = screenManager;

        Button terminalButton = new Button("Terminal");
        Button schemaButton =   new Button("Schema");
        Button usersButton =    new Button("Users");
        Button helpButton =     new Button("Help");

        terminalButton.setOnAction(e -> screenManager.setCurrentScreen(ScreenManager.TERMINAL_SCREEN));
        schemaButton.setOnAction  (e -> screenManager.setCurrentScreen(ScreenManager.SCHEMA_SCREEN));
        usersButton.setOnAction   (e -> screenManager.setCurrentScreen(ScreenManager.USERS_SCREEN));
        helpButton.setOnAction    (e -> screenManager.setCurrentScreen(ScreenManager.HELP_SCREEN));

        terminalButton.setFocusTraversable(false);
        schemaButton.setFocusTraversable(false);
        usersButton.setFocusTraversable(true);
        helpButton.setFocusTraversable(false);

        int width = 270;
        terminalButton.setPrefWidth(width);
        schemaButton.setPrefWidth(width);
        usersButton.setPrefWidth(width);
        helpButton.setPrefWidth(width);

        terminalButton.setFont(Font.font(25));
        schemaButton.setFont(Font.font(25));
        usersButton.setFont(Font.font(25));
        helpButton.setFont(Font.font(25));

        HBox tabs = new HBox();
        tabs.getChildren().addAll(terminalButton, schemaButton, usersButton, helpButton);

        HBox thing = new HBox();
        thing.getChildren().addAll(userScrollPane(), userScrollPane());

        VBox userScreenLayout = new VBox();
        userScreenLayout.getChildren().add(tabs);
        userScreenLayout.getChildren().add(thing);

        scene = new Scene(userScreenLayout, 1080, 720);
    }
// TODO create and add thang
    private ScrollPane userScrollPane() {

        // create a list of users in the system
        VBox vBox = new VBox();
        vBox.setMinWidth(540);
        for(int i = 0; i < 50; i++) {

            Button button = new Button("Something");
            button.setPrefWidth(540);
            button.setFont(Font.font(25));
            vBox.getChildren().add(button);
        }

        ScrollPane scrollPane = new ScrollPane(vBox);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    @Override
    public Scene getScene() { return scene; }
}
