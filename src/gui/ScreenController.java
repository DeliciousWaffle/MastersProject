package gui;

import files.io.FileType;
import files.io.IO;
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
    private Screen terminalScreen, tablesScreen, usersScreen, optionsScreen, helpScreen;

    public ScreenController(Stage primaryStage, SystemCatalog systemCatalog) {

        this.primaryStage = primaryStage;

        // starting width and height of the screen is 1080 x 720
        primaryStage.setWidth(Screen.defaultWidth);
        primaryStage.setHeight(Screen.defaultHeight);

        terminalScreen = new TerminalScreen(this, systemCatalog);
        tablesScreen = new TablesScreen(this, systemCatalog);
        usersScreen = new UsersScreen(this, systemCatalog);
        optionsScreen = new OptionsScreen(this, systemCatalog);
        helpScreen = new HelpScreen(this, systemCatalog);

        setScreen(Screen.Type.TERMINAL_SCREEN);

        // icon on the taskbar
        primaryStage.getIcons().add(IO.readAsset(FileType.Asset.PI_IMAGE));
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

    // a bit of a hack to refresh data that might have been modified during execution of the input
    public void refresh(SystemCatalog systemCatalog) {
        tablesScreen = new TablesScreen(this, systemCatalog);
        usersScreen = new UsersScreen(this, systemCatalog);
    }
}