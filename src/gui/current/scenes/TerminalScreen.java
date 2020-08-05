package gui.current.scenes;

import gui.current.ScreenController;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;

public class TerminalScreen extends Screen {

    private Scene terminalScreen;

    public TerminalScreen(ScreenController screenController) {

        HBox buttonLayout = super.getButtonLayout(screenController);

        terminalScreen = new Scene(buttonLayout);
    }

    @Override
    public Scene getScreen() {
        return terminalScreen;
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