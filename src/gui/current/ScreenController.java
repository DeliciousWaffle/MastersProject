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

        terminalScreen = new TerminalScreen(this);
        tablesScreen   = new TablesScreen(this);
        usersScreen    = new UsersScreen(this);
        optionsScreen  = new OptionsScreen(this);
        helpScreen     = new HelpScreen(this);

        setScreen(Screen.Type.TERMINAL_SCREEN);
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
        terminalScreen.scaleWidth();
    }

    public void scaleHeight(double scaleHeight) {

    }
}