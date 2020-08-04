package gui.current.scenes;

import gui.current.ScreenController;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;

public class OptionsScreen extends Screen {

    private Scene optionsScreen;

    public OptionsScreen(ScreenController screenController) {

        HBox buttonLayout = super.getButtonLayout(screenController);

        optionsScreen = new Scene(buttonLayout);
    }

    @Override
    public Scene getScreen() {
        return optionsScreen;
    }
}