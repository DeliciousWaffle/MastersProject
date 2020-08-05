package gui.current;

import gui.current.scenes.OptionsScreen;
import gui.current.scenes.Screen;
import gui.current.scenes.help.HelpScreen;
import gui.current.scenes.TablesScreen;
import gui.current.scenes.TerminalScreen;
import gui.current.scenes.UsersScreen;
import javafx.stage.Stage;

public class ScreenController {

    private Stage primaryStage;
    private Screen terminalScreen, tablesScreen, usersScreen, optionsScreen, helpScreen;

    public ScreenController(Stage primaryStage) {

        this.primaryStage = primaryStage;

        // starting width and height of the screen is 1080 x 720
        primaryStage.setWidth(Screen.defaultWidth);
        primaryStage.setHeight(Screen.defaultHeight);

        terminalScreen = new TerminalScreen(this);
        tablesScreen   = new TablesScreen(this);
        usersScreen    = new UsersScreen(this);
        optionsScreen  = new OptionsScreen(this);
        helpScreen     = new HelpScreen(this);

        setScreen(Screen.Type.TERMINAL_SCREEN);

        // when the window size changes, scale all the objects within the window
        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = (double) newValue;
            double scaleWidth = newWidth / Screen.defaultWidth;
            scaleWidth(scaleWidth);
        });

        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newHeight = (double) newValue;
            double scaleHeight = newHeight / Screen.defaultHeight;
            scaleHeight(scaleHeight);
        });
    }

    public void setScreen(Screen.Type screen) {
        switch(screen) {
            case TERMINAL_SCREEN:
                primaryStage.setScene(terminalScreen.getScreen());
                primaryStage.show();
                break;
            case TABLES_SCREEN:
                primaryStage.setScene(tablesScreen.getScreen());
                primaryStage.show();
                break;
            case USERS_SCREEN:
                primaryStage.setScene(usersScreen.getScreen());
                primaryStage.show();
                break;
            case OPTIONS_SCREEN:
                primaryStage.setScene(optionsScreen.getScreen());
                primaryStage.show();
                break;
            case HELP_SCREEN:
                primaryStage.setScene(helpScreen.getScreen());
                primaryStage.show();
                break;
        }
    }

    public void scaleWidth(double scaleWidth) {
        terminalScreen.scaleWidth(scaleWidth);
        tablesScreen.scaleWidth(scaleWidth);
        usersScreen.scaleWidth(scaleWidth);
        optionsScreen.scaleWidth(scaleWidth);
        helpScreen.scaleWidth(scaleWidth);
    }

    public void scaleHeight(double scaleHeight) {
        terminalScreen.scaleHeight(scaleHeight);
        tablesScreen.scaleHeight(scaleHeight);
        usersScreen.scaleHeight(scaleHeight);
        optionsScreen.scaleHeight(scaleHeight);
        helpScreen.scaleHeight(scaleHeight);
    }
}