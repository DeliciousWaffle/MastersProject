package gui.screens.tables.components;

import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import files.io.FileType;
import files.io.IO;
import gui.screens.Screen;
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
import java.util.stream.Collectors;

public class TablePane {

    private BorderPane tablePane;
    private Text tableNameText;
    private List<ColumnPane> columnPaneList;
    private ChoiceBox<String> clusteredFileTableOptions;
    private Button viewTableDataButton;

    public TablePane(Table table, List<String> otherTableNames) {

        double fontSize = 50.0;

        // setting the name
        String tableName = table.getTableName();
        this.tableNameText = new Text(tableName);
        tableNameText.setFont(new Font(fontSize));
        tableNameText.setTextAlignment(TextAlignment.CENTER);
        tableNameText.setFill(Color.WHITE);

        // setting the columns
        this.columnPaneList = new ArrayList<>();

        for(Column column : table.getColumns()) {
            columnPaneList.add(new ColumnPane(column, "None"));
        }

        VBox columnPanesLayout = new VBox();
        columnPanesLayout.setSpacing(20);
        columnPanesLayout.getChildren().addAll(columnPaneList.stream()
                .map(ColumnPane::getColumnPane)
                .collect(Collectors.toList())
        );

        // adding a choice box for choosing a table to build a clustered file with
        this.clusteredFileTableOptions = new ChoiceBox<>();
        clusteredFileTableOptions.getItems().addAll(otherTableNames);

        if(table.getClusteredWith().equalsIgnoreCase("none")) {
            clusteredFileTableOptions.setValue("Clustered With No Table");
        } else {
            clusteredFileTableOptions.setValue(table.getClusteredWith());
        }

        clusteredFileTableOptions.getStylesheets().add(IO.readCSS(FileType.CSS.DARK_CHOICE_BOX_STYLE));
        clusteredFileTableOptions.setStyle("-fx-font-size: 25; -fx-pref-width: 360;");
        clusteredFileTableOptions.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        // adding a button that allows the user to view the table data
        this.viewTableDataButton = new Button("View Table Data");
        viewTableDataButton.setMinSize(0, 0);
        viewTableDataButton.setPrefWidth(360);
        viewTableDataButton.setFont(new Font(25));
        viewTableDataButton.getStylesheets().setAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));

        // create a new window containing the table's data
        List<String> columnNames = new ArrayList<>();

        for(Column column : table.getColumns()) {
            String columnName = column.getName();
            columnNames.add(columnName);
        }

        viewTableDataButton.setOnAction(e -> {
            new TableDataWindow(tableName, columnNames, table.getTableData());
        });

        // adding everything to the main pane
        BorderPane topContainer = new BorderPane();
        topContainer.setTop(tableNameText);
        topContainer.setBottom(columnPanesLayout);

        BorderPane bottomContainer = new BorderPane();
        bottomContainer.setTop(clusteredFileTableOptions);
        bottomContainer.setBottom(viewTableDataButton);

        this.tablePane = new BorderPane();
        tablePane.setTop(topContainer);
        tablePane.setBottom(bottomContainer);

        BorderPane.setAlignment(tableNameText, Pos.CENTER);
        BorderPane.setAlignment(columnPanesLayout, Pos.CENTER);
        BorderPane.setAlignment(clusteredFileTableOptions, Pos.CENTER);
        BorderPane.setAlignment(viewTableDataButton, Pos.CENTER);

        BorderPane.setMargin(tableNameText, new Insets(20, 20, 10, 20));
        BorderPane.setMargin(columnPanesLayout, new Insets(10, 20, 10, 20));
        BorderPane.setMargin(clusteredFileTableOptions, new Insets(10 + 20, 20, 10 + 20, 20));
        BorderPane.setMargin(viewTableDataButton, new Insets(10, 20, 20, 20));

        tablePane.setBackground(new Background(
                new BackgroundFill(Color.rgb(60, 60, 60), new CornerRadii(5), Insets.EMPTY)));
        tablePane.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
    }

    public BorderPane getTablePane() {
        return tablePane;
    }

    public void setToLightMode() {
        this.tableNameText.setFill(Color.BLACK);
        this.columnPaneList.forEach(ColumnPane::setToLightMode);
        this.clusteredFileTableOptions.getStylesheets().setAll(IO.readCSS(FileType.CSS.LIGHT_CHOICE_BOX_STYLE));
        this.viewTableDataButton.getStylesheets().setAll(IO.readCSS(FileType.CSS.LIGHT_BUTTON_STYLE));
        this.tablePane.setBackground(new Background(
                new BackgroundFill(Color.rgb(150, 150, 150), new CornerRadii(5), Insets.EMPTY)));;
    }

    public void setToDarkMode() {
        this.tableNameText.setFill(Color.WHITE);
        this.columnPaneList.forEach(ColumnPane::setToDarkMode);
        this.clusteredFileTableOptions.getStylesheets().setAll(IO.readCSS(FileType.CSS.DARK_CHOICE_BOX_STYLE));
        this.viewTableDataButton.getStylesheets().setAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        this.tablePane.setBackground(new Background(
                new BackgroundFill(Color.rgb(60, 60, 60), new CornerRadii(5), Insets.EMPTY)));;
    }
}