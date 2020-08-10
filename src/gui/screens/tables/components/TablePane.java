package gui.screens.tables.components;

import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import files.io.FileType;
import files.io.IO;
import gui.screens.tables.components.tabledatawindow.TableDataWindow;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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

    public TablePane(Table table, List<String> otherTableNames) {

        double fontSize = 50.0;

        // setting the name
        String tableName = table.getTableName();
        Text text = new Text(tableName);
        text.setFont(new Font(fontSize));
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFill(Color.WHITE);

        // setting the columns
        List<BorderPane> columnPanes = new ArrayList<>();

        for(Column column : table.getColumns()) {
            columnPanes.add(new ColumnPane(column, "None").getColumnPane());
        }

        VBox columnPanesLayout = new VBox();
        columnPanesLayout.getChildren().addAll(columnPanes);
        columnPanesLayout.setSpacing(20);

        // adding a choice box for choosing a table to build a clustered file with
        ChoiceBox<String> clusteredFileTableOptions = new ChoiceBox<>();
        clusteredFileTableOptions.getItems().addAll(otherTableNames);

        if(table.getClusteredWith().equalsIgnoreCase("none")) {
            clusteredFileTableOptions.setValue("Clustered With No Table");
        } else {
            clusteredFileTableOptions.setValue(table.getClusteredWith());
        }

        clusteredFileTableOptions.getStylesheets().add(IO.readCSS(FileType.CSS.CHOICE_BOX_STYLE));
        clusteredFileTableOptions.setStyle("-fx-font-size: 25; -fx-pref-width: 360;");
        clusteredFileTableOptions.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        // adding a button that allows the user to view the table data
        Button button = new Button("View Table Data");
        button.setMinSize(0, 0);
        button.setPrefWidth(360);
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
        BorderPane topContainer = new BorderPane();
        topContainer.setTop(text);
        topContainer.setBottom(columnPanesLayout);

        BorderPane bottomContainer = new BorderPane();
        bottomContainer.setTop(clusteredFileTableOptions);
        bottomContainer.setBottom(button);

        tablePane = new BorderPane();
        tablePane.setTop(topContainer);
        tablePane.setBottom(bottomContainer);

        BorderPane.setAlignment(text, Pos.CENTER);
        BorderPane.setAlignment(columnPanesLayout, Pos.CENTER);
        BorderPane.setAlignment(clusteredFileTableOptions, Pos.CENTER);
        BorderPane.setAlignment(button, Pos.CENTER);

        BorderPane.setMargin(text, new Insets(20, 20, 10, 20));
        BorderPane.setMargin(columnPanesLayout, new Insets(10, 20, 10, 20));
        BorderPane.setMargin(clusteredFileTableOptions, new Insets(10 + 20, 20, 10 + 20, 20));
        BorderPane.setMargin(button, new Insets(10, 20, 20, 20));

        tablePane.setBackground(new Background(
                new BackgroundFill(Color.rgb(60, 60, 60), CornerRadii.EMPTY, Insets.EMPTY)));
        tablePane.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
    }

    public BorderPane getTablePane() {
        return tablePane;
    }
}