package gui.screens.help;

import files.io.FileType;
import files.io.IO;
import gui.ScreenController;
import gui.screens.Screen;
import gui.screens.help.components.Diagram;
import gui.screens.help.components.HelpPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import systemcatalog.SystemCatalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class HelpScreen extends Screen {

    private Scene helpScreen;

    private VBox helpPanesVBox;
    private BorderPane helpScreenLayout;
    private List<HelpPane> helpPaneList;
    private ScrollPane scrollHelpPanes;

    public HelpScreen(ScreenController screenController, SystemCatalog systemCatalog) {

        // button layout for top part of screen
        HBox topRowButtonLayout = super.getButtonLayout(screenController);

        // following is for the content of the help screen
        this.helpPanesVBox = new VBox();
        helpPanesVBox.setMinSize(0, 0);
        helpPanesVBox.setSpacing(30);
        helpPanesVBox.setStyle(Screen.DARK_HI);

        this.helpPaneList = new ArrayList<>(Arrays.asList(
                new HelpPane(getERDiagramText(), "View ER Diagram",
                        Diagram.Type.ER_DIAGRAM),
                new HelpPane(getSchemaText(), "View Schema Diagram",
                        Diagram.Type.SCHEMA),
                new HelpPane(getQueryText(), "View Query Diagram",
                        Diagram.Type.QUERY),
                new HelpPane(getCreateTableText(), "View Create Table Diagram",
                        Diagram.Type.CREATE_TABLE),
                new HelpPane(getAlterTableText(), "View Alter Table Diagram",
                        Diagram.Type.ALTER_TABLE),
                new HelpPane(getDropTableText(), "View Drop Table Diagram",
                        Diagram.Type.DROP_TABLE),
                new HelpPane(getInsertText(), "View Insert Diagram",
                        Diagram.Type.INSERT),
                new HelpPane(getUpdateText(), "View Update Diagram",
                        Diagram.Type.UPDATE),
                new HelpPane(getDeleteText(), "View Delete Diagram",
                        Diagram.Type.DELETE),
                new HelpPane(getGrantText(), "View Grant Diagram",
                        Diagram.Type.GRANT),
                new HelpPane(getRevokeText(), "View Revoke Diagram",
                        Diagram.Type.REVOKE),
                new HelpPane(getBuildFileStructureText(), "View Build File Structure Diagram",
                        Diagram.Type.BUILD_FILE_STRUCTURE),
                new HelpPane(getRemoveFileStructureText(), "View Remove Rile Structure Diagram",
                        Diagram.Type.REMOVE_FILE_STRUCTURE)
        ));

        helpPanesVBox.getChildren().addAll(
                helpPaneList
                        .stream()
                        .map(HelpPane::getHelpPane)
                        .collect(Collectors.toList())
        );

        helpPanesVBox.setAlignment(Pos.CENTER);
        VBox.setMargin(helpPanesVBox, new Insets(0, 30, 0, 30));

        // add the centered help panels to the scroll pane
        this.scrollHelpPanes = new ScrollPane(helpPanesVBox);
        scrollHelpPanes.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollHelpPanes.setFitToWidth(true);
        scrollHelpPanes.getStylesheets().add(IO.readCSS(FileType.CSS.DARK_SCROLL_PANE_STYLE));

        // add the button layout and content layout to overall screen
        this.helpScreenLayout = new BorderPane();
        helpScreenLayout.setTop(topRowButtonLayout);
        helpScreenLayout.setBottom(scrollHelpPanes);

        BorderPane.setAlignment(topRowButtonLayout, Pos.CENTER);
        BorderPane.setAlignment(scrollHelpPanes, Pos.CENTER);
        BorderPane.setMargin(scrollHelpPanes, new Insets(30, 0, 0, 0));

        helpScreenLayout.setMinSize(0, 0);
        helpScreenLayout.setPrefSize(defaultWidth, defaultHeight);
        helpScreenLayout.setStyle(DARK_HI);

        helpScreen = new Scene(helpScreenLayout);

        helpScreen.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = (double) newValue;
            super.adjustButtonWidth(newWidth);
        });
    }

    @Override
    public Scene getScreen() {
        return helpScreen;
    }

    public void setToLightMode() {
        this.helpPanesVBox.setStyle(Screen.LIGHT_LOW);
        this.helpScreenLayout.setStyle(Screen.LIGHT_LOW);
        this.helpPaneList.forEach(HelpPane::setToLightMode);
        this.scrollHelpPanes.getStylesheets().setAll(IO.readCSS(FileType.CSS.LIGHT_SCROLL_PANE_STYLE));
    }

    public void setToDarkMode() {
        this.helpPanesVBox.setStyle(Screen.DARK_HI);
        this.helpScreenLayout.setStyle(Screen.DARK_HI);
        this.helpPaneList.forEach(HelpPane::setToDarkMode);
        this.scrollHelpPanes.getStylesheets().setAll(IO.readCSS(FileType.CSS.DARK_SCROLL_PANE_STYLE));
    }

    private String getERDiagramText() {
        return "Displays the relationship of each table in the system. The database just represents a simple " +
                "business that sells a variety of products. Note: Changes made to the tables of the " +
                "system will not be reflected in this diagram.";
    }

    private String getSchemaText() {
        return "Displays information about each table within the system. This includes primary key/foreign key" +
                "relationships along with general column information. Note: Changes made to the tables of the " +
                "system will not be reflected in this diagram.";
    }

    private String getQueryText() {
        return "Simply queries the system. The diagram contains information about the syntax accepted. Green " +
                "nodes can accept any kind of value while white ones require an exact match. All queries (and " +
                "all other types of input for that matter) must end with a semicolon. Numeric values should not " +
                "be enclosed in quotes while string and date values must be enclosed in quotes. Date values " +
                "should take the form of YYYY-MM-DD. Columns that are ambiguous (referencing a column name that " +
                "belongs to two or more tables referenced) must be prefixed with their respective table name like " +
                "this \"<TableName>.<ColumnName>\".";
    }

    private String getCreateTableText() {
        return "Creates a new table within the system. Will auto create a primary key on the first column supplied. " +
                "If you wish to make changes with respect to the created table, use the ALTER TABLE command";
    }

    private String getAlterTableText() {
        return "Alters a table within the system to either add, modify, or delete a column. Can also choose " +
                "to add a primary key to a table, add a foreign key to a table, remove a primary key from a table, " +
                "or remove a foreign key from a table. If adding a foreign key to the table, the column name " +
                "must be prefixed the foreign table name like this \"<foreign table>.<column name>\".";
    }

    private String getDropTableText() {
        return "Drops the table from the database, basically removing it from the system.";
    }

    private String getInsertText() {
        return "Inserts a new row of data into the supplied table. If the new row has less columns than the table " +
                "supplied, the rest of the columns will be populated with null values.";
    }

    private String getUpdateText() {
        return "Performs an update to the table supplied. Essentially changing the values encountered at the " +
                "supplied column name that match the given condition";
    }

    private String getDeleteText() {
        return "Deletes one or more rows of the supplied table based on the condition to satisfy.";
    }

    private String getGrantText() {
        return "Grants one or more privileges to one or more users in the system on a single table. Can also add " +
                "the \"WITH GRANT OPTION\" option to allow the user that's getting the privileges to grant those " +
                "to other users.";
    }

    private String getRevokeText() {
        return "Revokes one or more privileges from one or more users in the system on a single table. This also " +
                "removes any privileges that they can grant to other users. However, this process is not recursive. " +
                "Assume user A granted privileges to user B and user B granted those privileges to user C. User A " +
                "doesn't like user B anymore, so he removes user B's privileges. User C will still have what was " +
                "given to him.";
    }

    private String getBuildFileStructureText() {
        return "Builds a file structure on a column of a table. You typically want to build on columns that are " +
                "referenced in conditions of a query or columns that you're joining on. Secondary B-Trees are " +
                "a jack of all trades, but don't excel at a particular query type. Clustered B-Trees work best " +
                "when dealing with range queries (using >, <, >=, or <= in an expression). Hash tables work best " +
                "with simple queries (using = or != in an expression). They should not be built if performing a " +
                "range query. Clustered files work best for joins, very expensive to store and you can't build " +
                "other file structures in the clustered tables.";
    }

    private String getRemoveFileStructureText() {
        return "Removes a file structure on the column and table supplied. If the tables are clustered, removes " +
                "the clustering.";
    }
}