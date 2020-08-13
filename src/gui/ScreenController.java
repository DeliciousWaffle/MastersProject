package gui;

import gui.screens.options.OptionsScreen;
import gui.screens.Screen;
import gui.screens.help.HelpScreen;
import gui.screens.tables.TablesScreen;
import gui.screens.terminal.TerminalScreen;
import gui.screens.users.UsersScreen;
import javafx.stage.Stage;
import systemcatalog.SystemCatalog;

public class ScreenController {

    private final Stage primaryStage;
    private final Screen terminalScreen, tablesScreen, usersScreen, optionsScreen, helpScreen;

    public ScreenController(Stage primaryStage, SystemCatalog systemCatalog) {

        this.primaryStage = primaryStage;

        // starting width and height of the screen is 1080 x 720
        primaryStage.setWidth(Screen.defaultWidth);
        primaryStage.setHeight(Screen.defaultHeight);

        terminalScreen = new TerminalScreen(this, systemCatalog);
        tablesScreen   = new TablesScreen(this, systemCatalog);
        usersScreen    = new UsersScreen(this, systemCatalog);
        optionsScreen  = new OptionsScreen(this, systemCatalog);
        helpScreen     = new HelpScreen(this, systemCatalog);

        setScreen(Screen.Type.TERMINAL_SCREEN);
    }

    /**
     * Called from either from the terminal, tables, users, options, or help screens.
     * Simply changes the screen to the one provided.
     * @param screen is the screen to display
     */
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
}