package gui.current.scenes;

import gui.current.ScreenController;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;

public class TablesScreen extends Screen {

    private Scene tablesScreen;

    public TablesScreen(ScreenController screenController) {

        HBox buttonLayout = super.getButtonLayout(screenController);

        tablesScreen = new Scene(buttonLayout);
    }

    @Override
    public Scene getScreen() {
        return tablesScreen;
    }
}