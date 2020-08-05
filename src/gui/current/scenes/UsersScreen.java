package gui.current.scenes;

import gui.current.ScreenController;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;

public class UsersScreen extends Screen {

    private Scene usersScreen;

    public UsersScreen(ScreenController screenController) {

        HBox buttonLayout = super.getButtonLayout(screenController);

        usersScreen = new Scene(buttonLayout);
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