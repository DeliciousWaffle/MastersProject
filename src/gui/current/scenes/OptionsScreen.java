package gui.current.scenes;

import gui.current.ScreenController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class OptionsScreen extends Screen {

    private Scene optionsScreen;

    public OptionsScreen(ScreenController screenController) {

        HBox buttonLayout = super.getButtonLayout(screenController);

        BorderPane optionsScreenLayout = new BorderPane();
        optionsScreenLayout.setTop(buttonLayout);
        optionsScreenLayout.setPrefSize(Screen.defaultWidth, Screen.defaultHeight);
        optionsScreenLayout.setStyle("-fx-background-color: rgb(30, 30, 30);");

        optionsScreen = new Scene(optionsScreenLayout);
    }

    @Override
    public Scene getScreen() {
        return optionsScreen;
    }

    @Override
    public void scaleWidth(double scaleWidth) {
        super.scaleButtonWidth(scaleWidth);
    }

    @Override
    public void scaleHeight(double scaleHeight) {
        super.scaleButtonHeight(scaleHeight);
    }
}