package gui.screens.tables.components;

import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.FileStructure;
import files.io.FileType;
import files.io.IO;
import gui.ScreenController;
import gui.screens.Screen;
import gui.screens.tables.components.tabledatawindow.TableDataWindow;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import systemcatalog.SystemCatalog;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TablePane {

    private BorderPane tablePane;
    private Text tableNameText;
    private List<ColumnPane> columnPaneList;
    //private ChoiceBox<String> clusteredFileTableOptions;
    private Button viewTableDataButton;

    public TablePane(Table table, SystemCatalog systemCatalog, ScreenController screenController) {

        // getting the table data
        String tableName = table.getTableName();
        List<String> primaryKeys = table.getPrimaryKeys();
        Map<String, String> foreignKeys = table.getForeignKeys();
        String clusteredWithTableName = table.getClusteredWithTableName();
        List<Column> columns = table.getColumns();

        // used to store all information of this table pane
        VBox tableContent = new VBox(15);
        tableContent.setAlignment(Pos.TOP_CENTER);
        tableContent.setBackground(new Background(
                new BackgroundFill(Color.rgb(50, 50, 50), new CornerRadii(5), Insets.EMPTY)));
        tableContent.setEffect(new DropShadow(
                BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        Text tableNameText = new Text(tableName);
        tableNameText.setFont(new Font(50.0));
        tableNameText.setFill(Color.WHITE);

        tableContent.getChildren().add(tableNameText);


        // used to contain the primary keys button, and foreign keys button; gets added ot the table content later
        BorderPane keyButtonsContainer = new BorderPane();

        Button primaryKeysButton = new Button("Primary Key(s)");
        primaryKeysButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        primaryKeysButton.setFont(new Font(25.0));
        primaryKeysButton.setOnAction(e -> new KeyWindow("Primary Key(s):", primaryKeys));

        BorderPane.setMargin(primaryKeysButton, new Insets(0, 7.5, 0, 15));
        keyButtonsContainer.setLeft(primaryKeysButton);

        Button foreignKeysButton = new Button("Foreign Key(s)");
        foreignKeysButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        foreignKeysButton.setFont(new Font(25.0));

        List<String> formattedForeignKeys = foreignKeys
                .entrySet()
                .stream()
                .map(entry -> entry.getValue() + " is a foreign key to " + entry.getKey())
                .collect(Collectors.toList());
        foreignKeysButton.setOnAction(e -> new KeyWindow("Foreign Key(s):", formattedForeignKeys));

        BorderPane.setMargin(foreignKeysButton, new Insets(0, 15, 0, 7.5));
        keyButtonsContainer.setRight(foreignKeysButton);

        tableContent.getChildren().add(keyButtonsContainer);


        // creating a choice box to show what tables can be clustered with others
        ChoiceBox<String> clusteredFileTableOptions = new ChoiceBox<>();
        clusteredFileTableOptions.getItems().addAll(
                systemCatalog.getTables()
                .stream()
                .map(Table::getTableName)
                        // add all table names except for this one
                        .filter(systemTableName -> ! tableName.equalsIgnoreCase(systemTableName))
                .collect(Collectors.toList()));
        clusteredFileTableOptions.getItems().add("Clustered With No Table");

        if (clusteredWithTableName.equalsIgnoreCase("none")) {
            clusteredFileTableOptions.setValue("Clustered With No Table");
        } else {
            clusteredFileTableOptions.setValue(table.getClusteredWithTableName());
        }

        // if the user clusters this table with another table, reflect that change in the system
        clusteredFileTableOptions.setOnAction(e -> {
            boolean isRemovingClustering =
                    clusteredFileTableOptions.getValue().equalsIgnoreCase("Clustered With No Table");
            // remove the clustering from this table along with the other table that is clustered with this one
            if (isRemovingClustering) {
                table.setClusteredWith("none");
                systemCatalog.getTables().forEach(t -> {
                    if (t.getClusteredWithTableName().equalsIgnoreCase(tableName)) {
                        t.setClusteredWith("none");
                    }
                });
            // cluster both tables
            } else {
                String otherClusterTable = clusteredFileTableOptions.getValue();
                // if this table was previously clustered with another table, remove the clustering from the other table
                systemCatalog.getTables().forEach(t -> {
                    if (t.getClusteredWithTableName().equalsIgnoreCase(tableName) ||
                            t.getClusteredWithTableName().equalsIgnoreCase(otherClusterTable)) {
                        t.setClusteredWith("none");
                        // remove any file structures built
                        t.getColumns().forEach(c -> c.setFileStructure(FileStructure.NONE));
                    }
                });
                // set this table as clustered
                systemCatalog.getTables().forEach(t -> {
                    if (t.getTableName().equalsIgnoreCase(tableName)) {
                        t.setClusteredWith(otherClusterTable);
                        t.getColumns().forEach(c -> c.setFileStructure(FileStructure.NONE));
                    }
                });
                // set the other table clustered with this table
                systemCatalog.getTables().forEach(t -> {
                    if (t.getTableName().equalsIgnoreCase(otherClusterTable)) {
                        t.setClusteredWith(tableName);
                        t.getColumns().forEach(c -> c.setFileStructure(FileStructure.NONE));
                    }
                });
            }
            // hack to force refresh of the screen
            screenController.refresh(systemCatalog);
            screenController.setScreen(Screen.Type.TABLES_SCREEN);
        });

        clusteredFileTableOptions.getStylesheets().add(IO.readCSS(FileType.CSS.DARK_CHOICE_BOX_STYLE));
        clusteredFileTableOptions.setStyle("-fx-font-size: 25; -fx-pref-width: 360;");
        clusteredFileTableOptions.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        tableContent.getChildren().add(clusteredFileTableOptions);


        // create and add each column pane to the table pane
        tableContent.getChildren().addAll(table
                .getColumns()
                .stream()
                .map(column -> new ColumnPane(column, table, systemCatalog, screenController))
                .map(ColumnPane::getColumnPane)
                .collect(Collectors.toList()));

        // add a button to view the table data
        Button viewTableDataButton = new Button("View Table Data");
        viewTableDataButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        viewTableDataButton.setFont(new Font(25.0));

        viewTableDataButton.setOnAction(e -> {
            new TableDataWindow(tableName,
                    columns.stream()
                            .map(Column::getColumnName)
                            .collect(Collectors.toList()),
                    table.getTableData().getData());
        });

        VBox.setMargin(viewTableDataButton, new Insets(0, 0, 15, 0));
        tableContent.getChildren().add(viewTableDataButton);

        // add the table content to the table pane
        tablePane = new BorderPane(tableContent);
    }

    public BorderPane getTablePane() {
        return tablePane;
    }

    public void setToLightMode() {
        this.tableNameText.setFill(Color.BLACK);
        this.columnPaneList.forEach(ColumnPane::setToLightMode);
        this.viewTableDataButton.getStylesheets().setAll(IO.readCSS(FileType.CSS.LIGHT_BUTTON_STYLE));
        this.tablePane.setBackground(new Background(
                new BackgroundFill(Color.rgb(150, 150, 150), new CornerRadii(5), Insets.EMPTY)));;
    }

    public void setToDarkMode() {
        this.tableNameText.setFill(Color.WHITE);
        this.columnPaneList.forEach(ColumnPane::setToDarkMode);
        this.viewTableDataButton.getStylesheets().setAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        this.tablePane.setBackground(new Background(
                new BackgroundFill(Color.rgb(60, 60, 60), new CornerRadii(5), Insets.EMPTY)));;
    }
}