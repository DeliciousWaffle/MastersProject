package gui.current.scenes.tables;

import gui.current.ScreenController;
import gui.current.scenes.Screen;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class TablesScreen extends Screen {

    private Scene tablesScreen;

    public TablesScreen(ScreenController screenController) {

        HBox buttonLayout = super.getButtonLayout(screenController);

        BorderPane tablesScreenLayout = new BorderPane();
        tablesScreenLayout.setTop(buttonLayout);
        tablesScreenLayout.setPrefSize(Screen.defaultWidth, Screen.defaultHeight);
        tablesScreenLayout.setStyle("-fx-background-color: rgb(30, 30, 30);");

        tablesScreen = new Scene(tablesScreenLayout);
    }

    @Override
    public Scene getScreen() {
        return tablesScreen;
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