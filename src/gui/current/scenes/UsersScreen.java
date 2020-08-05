package gui.current.scenes;

import gui.current.ScreenController;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class UsersScreen extends Screen {

    private Scene usersScreen;

    public UsersScreen(ScreenController screenController) {

        HBox buttonLayout = super.getButtonLayout(screenController);

        BorderPane usersScreenLayout = new BorderPane();
        usersScreenLayout.setTop(buttonLayout);
        usersScreenLayout.setPrefSize(Screen.defaultWidth, Screen.defaultHeight);
        usersScreenLayout.setStyle("-fx-background-color: rgb(30, 30, 30);");

        usersScreen = new Scene(usersScreenLayout);
    }

    @Override
    public Scene getScreen() {
        return usersScreen;
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