package gui.current.scenes;

import gui.current.ScreenController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class TerminalScreen extends Screen {

    private Scene terminalScreen;

    public TerminalScreen(ScreenController screenController) {

        HBox buttonLayout = super.getButtonLayout(screenController);

        BorderPane terminalScreenLayout = new BorderPane();
        terminalScreenLayout.setTop(buttonLayout);
        terminalScreenLayout.setPrefSize(Screen.defaultWidth, Screen.defaultHeight);
        terminalScreenLayout.setStyle("-fx-background-color: rgb(30, 30, 30);");

        terminalScreen = new Scene(terminalScreenLayout);
    }

    @Override
    public Scene getScreen() {
        return terminalScreen;
    }
}