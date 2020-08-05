package gui.current.scenes.help;

import gui.current.ScreenController;
import gui.current.scenes.Screen;
import gui.current.scenes.help.popupwindows.*;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.List;


public class HelpScreen extends Screen {

    private Scene helpScreen;

    public HelpScreen(ScreenController screenController) {

        // button layout for top part of screen
        HBox buttonLayout = super.getButtonLayout(screenController);

        // following is for the content of the help screen
        VBox helpPanes = new VBox();
        helpPanes.setMinSize(0, 0);
        helpPanes.setMaxWidth(Screen.defaultWidth - 200.0);
        helpPanes.setSpacing(30);
        helpPanes.setBackground(new Background(new BackgroundFill(Color.rgb(30, 30, 30), CornerRadii.EMPTY, Insets.EMPTY)));

        helpPanes.getChildren().addAll(
                //getAboutPane(),
                new HelpPane(getSchemaText(), "View Schema Diagram", Diagram.Type.SCHEMA).getHelpPane(),
                new HelpPane(getQueryText(), "View Query Diagram", Diagram.Type.QUERY).getHelpPane(),
                new HelpPane(getCreateTableText(), "View Create Table Diagram", Diagram.Type.CREATE_TABLE).getHelpPane(),
                new HelpPane(getAlterTableText(), "View Alter Table Diagram", Diagram.Type.ALTER_TABLE).getHelpPane(),
                new HelpPane(getDropTableText(), "View Drop Table Diagram", Diagram.Type.DROP_TABLE).getHelpPane(),
                new HelpPane(getInsertText(), "View Insert Diagram", Diagram.Type.INSERT).getHelpPane(),
                new HelpPane(getUpdateText(), "View Update Diagram", Diagram.Type.UPDATE).getHelpPane(),
                new HelpPane(getDeleteText(), "View Delete Diagram", Diagram.Type.DELETE).getHelpPane(),
                new HelpPane(getGrantText(), "View Grant Diagram", Diagram.Type.GRANT).getHelpPane(),
                new HelpPane(getRevokeText(), "View Revoke Diagram", Diagram.Type.REVOKE).getHelpPane(),
                new HelpPane(getBuildFileStructureText(), "View Build File Structure Diagram",
                        Diagram.Type.BUILD_FILE_STRUCTURE).getHelpPane(),
                new HelpPane(getRemoveFileStructureText(), "View Remove Rile Structure Diagram",
                        Diagram.Type.REMOVE_FILE_STRUCTURE).getHelpPane()
        );

        // centering the help panes
        BorderPane centeredHelpPanes = new BorderPane();
        centeredHelpPanes.setMaxWidth(Screen.defaultWidth);
        centeredHelpPanes.setCenter(helpPanes);
        BorderPane.setAlignment(helpPanes, Pos.CENTER);
        centeredHelpPanes.setBackground(new Background(new BackgroundFill(Color.rgb(30, 30, 30), CornerRadii.EMPTY, Insets.EMPTY)));

        // add the centered help panels to the scroll pane
        ScrollPane scrollHelpPanes = new ScrollPane(centeredHelpPanes);

        // -90 is to allow the vertical scroll bar to appear on screen
        scrollHelpPanes.setMinSize(0, 0);
        scrollHelpPanes.setPrefSize(Screen.defaultWidth, Screen.defaultHeight - 90.0);
        scrollHelpPanes.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollHelpPanes.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollHelpPanes.setStyle("-fx-font-size: 15px; -fx-background-color: green; -fx-border-color: black; -fx-focus-color: transparent; -fx-faint-focus-color: rgb(30, 30, 30);");
        scrollHelpPanes.fitToWidthProperty().set(true);

        // add the button layout and content layout to overall screen
        BorderPane helpScreenLayout = new BorderPane();
        helpScreenLayout.setTop(buttonLayout);
        helpScreenLayout.setBottom(scrollHelpPanes);
        helpScreenLayout.setStyle("-fx-background-color: red; -fx-focus-color: transparent; -fx-faint-focus-color: rgb(30, 30, 30);");
        helpScreen = new Scene(helpScreenLayout);
    }

    @Override
    public Scene getScreen() {
        return helpScreen;
    }

    @Override
    public void scaleWidth(double scaleWidth) {
        super.scaleButtonWidth(scaleWidth);
    }

    @Override
    public void scaleHeight(double scaleHeight) {
        super.scaleButtonHeight(scaleHeight);
    }

    private BorderPane getAboutHelpPane() {
        return new BorderPane();
    }

    private String getSchemaText() {
        return "Note that this is the schema of the database prior to any changes. This means that any changes made " +
                "will not be reflected in this diagram.";
    }

    private String getQueryText() {
        return "Queries the database. NOTE: The diagram provided is not 100% accurate of what the system is capable " +
                "of at the moment. Using an OR at the WHERE clause, a GROUP by expression, or a HAVING expression " +
                "are currently not supported. I'm sure there are some fun bugs that I haven't come across yet, so " +
                "stay on your toes. Good luck!";
    }

    private String getCreateTableText() {
        return "Creates a new table within the system. Will auto create a primary key on the first column supplied.";
    }

    private String getAlterTableText() {
        return "Alters a table within the system to either add, modify, or delete a column. Can also choose " +
                "to add a primary key (if one doesn't already exist) to a table, add a foreign key to a table, " +
                "remove a primary key from a table, or remove a foreign key from a table. If removing a primary " +
                "key, care must be taken to ensure that the column is not foreign key of a different table.";
    }

    private String getDropTableText() {
        return "Drops the table from the database. Care must be taken to ensure that the table to drop's primary " +
                "key is not references by another table in the system.";
    }

    private String getInsertText() {
        return "Inserts a new row of data into the supplied table. If the new row has less columns than the table " +
                "supplied, the rest of the columns will be populated with null values.";
    }

    private String getUpdateText() {
        return "Performs an update to the table supplied. Careful! For whatever reason, I give you the ability to " +
                "update entire columns to a value. So watch out for that!";
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
                "referenced in conditions of a query or columns that you're joining on. Secondary B-Trees () are " +
                "a jack of all trades, but don't excel at a particular query type. Clustered B-Trees () work best " +
                "when dealing with range queries (using >, <, >=, or <= in an expression). Don't use for blah. " +
                "Hash tables work best with simple queries (using = or != in an expression). They don't do so hot " +
                "when it comes to range queries. Clustered files work best for joins, very expensive to store and " +
                "you can't build other file structures in the clustered tables.";
    }

    private String getRemoveFileStructureText() {
        return "Removes a file structure on the column and table supplied. If the tables are clustered, removes " +
                "the clustering.";
    }
}