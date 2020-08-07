package gui.current.scenes.help;

import gui.current.scenes.Screen;
import gui.current.scenes.help.popupwindows.*;
import gui.current.scenes.help.popupwindows.diagrams.*;
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

        // spicing up button
        String buttonStyle = "-fx-background-color: #666666; -fx-text-fill: white;";
        button.setStyle(buttonStyle);
        button.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        String buttonEnteredStyle = "-fx-background-color: #999999; -fx-text-fill: white;";
        String buttonExitedStyle = buttonStyle;

        button.setOnMouseEntered(e -> {
            button.setStyle(buttonEnteredStyle);
        });

        button.setOnMouseExited(e -> {
            button.setStyle(buttonExitedStyle);
        });

        // if the button is clicked, what kind of window does it launch?
        switch(diagramType) {
            case SCHEMA:
                button.setOnAction(e -> new SchemaDiagram());
                break;
            case QUERY:
                button.setOnAction(e -> new QueryDiagram());
                break;
            case CREATE_TABLE:
                button.setOnAction(e -> new CreateTableDiagram());
                break;
            case ALTER_TABLE:
                button.setOnAction(e -> new AlterTableDiagram());
                break;
            case DROP_TABLE:
                button.setOnAction(e -> new DropTableDiagram());
                break;
            case INSERT:
                button.setOnAction(e -> new InsertDiagram());
                break;
            case UPDATE:
                button.setOnAction(e -> new UpdateDiagram());
                break;
            case DELETE:
                button.setOnAction(e -> new DeleteDiagram());
                break;
            case GRANT:
                button.setOnAction(e -> new GrantDiagram());
                break;
            case REVOKE:
                button.setOnAction(e -> new RevokeDiagram());
                break;
            case BUILD_FILE_STRUCTURE:
                button.setOnAction(e -> new BuildFileStructureDiagram());
                break;
            case REMOVE_FILE_STRUCTURE:
                button.setOnAction(e -> new RemoveFileStructureDiagram());
                break;
        }

        // creating the help pane
        helpPane = new BorderPane();
        helpPane.setMinSize(0, 0);
        BorderPane.setAlignment(text, Pos.CENTER);
        BorderPane.setAlignment(button, Pos.CENTER);
        helpPane.setTop(text);
        helpPane.setBottom(button);
        BorderPane.setMargin(text, new Insets(15));
        BorderPane.setMargin(button, new Insets(15));
        helpPane.setBackground(new Background(
                new BackgroundFill(Color.rgb(60, 60, 60), CornerRadii.EMPTY, Insets.EMPTY)));
        helpPane.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
    }

    public BorderPane getHelpPane() {
        return helpPane;
    }
}