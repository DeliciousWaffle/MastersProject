package gui.current;

import gui.current.scenes.terminal.TerminalScene;
import gui.current.scenes.users.UsersScene;
import gui.old.ScreenManager;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Launch extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        TerminalScene terminalScene = new TerminalScene();
        UsersScene usersScene = new UsersScene();

        // terminal scene
        Button terminalButton = new Button("Terminal");
        terminalButton.setOnAction(e -> {
            terminalScene.start(primaryStage);
        });

        // tables scene
        Button tablesButton = new Button("Tables");

        // users scene
        Button usersButton = new Button("Users");
        usersButton.setOnAction(e -> {
            usersScene.start(primaryStage);
        });

        // help scene
        Button helpButton = new Button("Help");

        Group group = new Group();
        group.getChildren().addAll(terminalButton, usersButton);
        Scene scene = new Scene(group, 1000, 1000);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}