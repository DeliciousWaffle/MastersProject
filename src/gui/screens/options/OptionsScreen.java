package gui.screens.options;

import gui.ScreenController;
import gui.screens.Screen;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import systemcatalog.SystemCatalog;

public class OptionsScreen extends Screen {

    private Scene optionsScreen;

    public OptionsScreen(ScreenController screenController, SystemCatalog systemCatalog) {

        HBox buttonLayout = super.getButtonLayout(screenController);

        BorderPane optionsScreenLayout = new BorderPane();
        optionsScreenLayout.setTop(buttonLayout);
        optionsScreenLayout.setPrefSize(defaultWidth, defaultHeight);
        optionsScreenLayout.setStyle("-fx-background-color: rgb(30, 30, 30);");

        optionsScreen = new Scene(optionsScreenLayout);
    }

    @Override
    public Scene getScreen() {
        return optionsScreen;
    }
}