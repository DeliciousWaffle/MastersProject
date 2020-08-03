package gui.current.scenes.terminal;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TerminalScene extends Application {

    @Override
    public void start(Stage primaryStage) {
        Text text = new Text("Terminal");
        Group group = new Group(text);
        Scene scene = new Scene(group,500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}