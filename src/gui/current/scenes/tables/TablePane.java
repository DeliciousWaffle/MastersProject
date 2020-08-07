package gui.current.scenes.tables;

import datastructure.relation.table.Table;
import datastructure.relation.table.component.Column;
import gui.current.scenes.Screen;
import gui.current.scenes.help.popupwindows.*;
import gui.current.scenes.help.popupwindows.diagrams.SchemaDiagram;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

public class TablePane {

    private BorderPane tablePane;

    public TablePane(Table table) {

        // properties that this pane will have
        //double buttonWidth = Screen.defaultWidth - 100.0;
        //double buttonHeight = 50.0;
        double fontSize = 50.0;
        double textWrappingSize = Screen.defaultWidth - 250.0;

        // setting the name
        String tableName = table.getTableName();
        //Text text = new Text(buttonWidth, buttonHeight, tableName);
        Text text = new Text(tableName);
        text.setFont(new Font(fontSize));
        text.setTextAlignment(TextAlignment.CENTER);
        //text.setWrappingWidth(textWrappingSize);
        text.setFill(Color.WHITE);

        // setting the columns
        List<BorderPane> columnPanes = new ArrayList<>();

        for(Column column : table.getColumns()) {
            columnPanes.add(new ColumnPane(column).getColumnPane());
        }

        VBox columnPanesLayout = new VBox();
        columnPanesLayout.getChildren().addAll(columnPanes);
        columnPanesLayout.setSpacing(10);

        // adding a button that allows the user to view the table data
        Button button = new Button("View Table Data");
        button.setMinSize(0, 0);
        //button.setPrefSize(buttonWidth, buttonHeight);
        button.setFont(new Font(25));

        // spicing up the button
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

        // create a new window containing the table's data
        List<String> columnNames = new ArrayList<>();

        for(Column column : table.getColumns()) {
            String columnName = column.getName();
            columnNames.add(columnName);
        }

        button.setOnAction(e -> {
            new TableDataWindow(tableName, columnNames, table.getTableData());
        });

        // adding everything to the main pane
        tablePane = new BorderPane();
        tablePane.setMinSize(0, 0);
        BorderPane.setAlignment(text, Pos.CENTER);
        BorderPane.setAlignment(columnPanesLayout, Pos.CENTER);
        BorderPane.setAlignment(button, Pos.CENTER);
        tablePane.setTop(text);
        tablePane.setCenter(columnPanesLayout);
        tablePane.setBottom(button);
        BorderPane.setMargin(text, new Insets(15));
        BorderPane.setMargin(columnPanesLayout, new Insets(15));
        BorderPane.setMargin(button, new Insets(15));
        tablePane.setBackground(new Background(
                new BackgroundFill(Color.rgb(60, 60, 60), CornerRadii.EMPTY, Insets.EMPTY)));
        tablePane.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
    }

    public BorderPane getTablePane() {
        return tablePane;
    }
}