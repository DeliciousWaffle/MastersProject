package gui.screens.help.components;

import files.io.FileType;
import files.io.IO;
import gui.screens.Screen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class HelpPane {

    private BorderPane helpPane;

    public HelpPane(String helpText, String buttonText, Diagram.Type diagramType) {

        // properties that this help pane will have
        double buttonWidth = Screen.defaultWidth - 100.0;
        double buttonHeight = 50.0;
        double fontSize = 25.0;
        double textWrappingSize = Screen.defaultWidth - 250.0;

        // creating text
        Text text = new Text(buttonWidth, buttonHeight, helpText);
        text.setFont(new Font(fontSize));
        text.setTextAlignment(TextAlignment.CENTER);
        text.setWrappingWidth(textWrappingSize);
        text.setFill(Color.WHITE);

        // creating the button
        Button button = new Button(buttonText);
        button.setMinSize(0, 0);
        button.setPrefSize(buttonWidth, buttonHeight);
        button.setFont(new Font(fontSize));
        button.getStylesheets().addAll(IO.readCSS(FileType.CSS.BUTTON_STYLE));

        // if the button is clicked, what kind of window does it launch?
        switch(diagramType) {
            case ER_DIAGRAM:
                button.setOnAction(e -> new Diagram("ER Diagram",
                        "ERDiagram.png"));
                break;
            case SCHEMA:
                button.setOnAction(e -> new Diagram("Database Schema",
                        "DatabaseSchema.png"));
                break;
            case QUERY:
                button.setOnAction(e -> new Diagram("Query Diagram",
                        "QueryDiagram.png"));
                break;
            case CREATE_TABLE:
                button.setOnAction(e -> new Diagram("Create Table Diagram",
                        "CreateTableDiagram.png"));
                break;
            case ALTER_TABLE:
                button.setOnAction(e -> new Diagram("Alter Table Diagram",
                        "AlterTableDiagram.png"));
                break;
            case DROP_TABLE:
                button.setOnAction(e -> new Diagram("Drop Table Diagram",
                        "DropTableDiagram.png"));
                break;
            case INSERT:
                button.setOnAction(e -> new Diagram("Insert Diagram",
                        "InsertDiagram.png"));
                break;
            case UPDATE:
                button.setOnAction(e -> new Diagram("Update Diagram",
                        "UpdateDiagram.png"));
                break;
            case DELETE:
                button.setOnAction(e -> new Diagram("Delete Diagram",
                        "DeleteDiagram.png"));
                break;
            case GRANT:
                button.setOnAction(e -> new Diagram("Grant Diagram",
                        "GrantDiagram.png"));
                break;
            case REVOKE:
                button.setOnAction(e -> new Diagram("Revoke Diagram",
                        "RevokeDiagram.png"));
                break;
            case BUILD_FILE_STRUCTURE:
                button.setOnAction(e -> new Diagram("Build File Structure Diagram",
                        "BuildFileStructureDiagram.png"));
                break;
            case REMOVE_FILE_STRUCTURE:
                button.setOnAction(e -> new Diagram("Remove File Structure Diagram",
                        "RemoveFileStructureDiagram.png"));
                break;
        }

        // creating the help pane
        this.helpPane = new BorderPane();
        helpPane.setMinSize(Screen.defaultWidth - 100.0, 0);
        helpPane.setMaxWidth(Screen.defaultWidth - 100.0);
        BorderPane.setAlignment(text, Pos.CENTER);
        BorderPane.setAlignment(button, Pos.CENTER);
        helpPane.setTop(text);
        helpPane.setBottom(button);
        BorderPane.setMargin(text, new Insets(15));
        BorderPane.setMargin(button, new Insets(15));
        helpPane.setBackground(new Background(new BackgroundFill(Color.rgb(50, 50, 50),
                new CornerRadii(5), Insets.EMPTY)));
        helpPane.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
    }

    public BorderPane getHelpPane() {
        return helpPane;
    }
}