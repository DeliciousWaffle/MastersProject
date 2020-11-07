package gui.screens.tables.components;

import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.DataType;
import datastructures.relation.table.component.FileStructure;
import files.io.FileType;
import files.io.IO;
import gui.ScreenController;
import gui.screens.Screen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import systemcatalog.SystemCatalog;

public class ColumnPane {

    private BorderPane columnPane;

    public ColumnPane(Column column, Table columnsTable,
                      SystemCatalog systemCatalog, ScreenController screenController) {

        // get data
        String columnName = column.getColumnName();
        DataType dataType = column.getDataType();
        int size = column.size();
        int decimalSize = column.getDecimalSize();
        FileStructure fileStructure = column.getFileStructure();

        // creating a text representation of the data (except file structure)

        String dataTypeFormatted = "";

        switch(dataType) {
            case NUMBER:
                dataTypeFormatted = "Number";
                break;
            case CHAR:
                dataTypeFormatted = "Character";
                break;
            case DATE:
                dataTypeFormatted = "Date";
                break;
        }

        String decimalSizeFormatted = decimalSize > 0 ? ", " + decimalSize : "";

        Text columnText = new Text(columnName + ": " + dataTypeFormatted + "(" + size + decimalSizeFormatted + ")");
        columnText.setFont(new Font(25.0));
        columnText.fillProperty().set(Color.WHITE);

        // creating a choice box for the data structure to build on for this column
        ChoiceBox<String> fileStructureChoiceBox = new ChoiceBox<>();

        fileStructureChoiceBox.getItems().addAll(
                "Secondary B-Tree",
                "Clustered B-Tree",
                "Hash Table",
                "No File Structure"
        );

        fileStructureChoiceBox.getStylesheets().add(IO.readCSS(FileType.CSS.DARK_CHOICE_BOX_STYLE));
        fileStructureChoiceBox.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        String fileStructureString = "";

        switch (fileStructure) {
            case SECONDARY_B_TREE:
                fileStructureString = "Secondary B-Tree";
                break;
            case CLUSTERED_B_TREE:
                fileStructureString = "Clustered B-Tree";
                break;
            case HASH_TABLE:
                fileStructureString = "Hash Table";
                break;
            case NONE:
                fileStructureString = "No File Structure";
                break;
        }

        fileStructureChoiceBox.setValue(fileStructureString);

        // if the user changes the file structure value, actually make that change to the column
        fileStructureChoiceBox.setOnAction(e -> {
            String value = fileStructureChoiceBox.getValue();
            switch (value) {
                case "Secondary B-Tree":
                    column.setFileStructure(FileStructure.SECONDARY_B_TREE);
                    break;
                case "Clustered B-Tree":
                    column.setFileStructure(FileStructure.CLUSTERED_B_TREE);
                    break;
                case "Hash Table":
                    column.setFileStructure(FileStructure.HASH_TABLE);
                    break;
                case "No File Structure":
                    column.setFileStructure(FileStructure.NONE);
                    break;
            }
            // if building a file structure on a table, remove the clustering
            if (! value.equalsIgnoreCase("No File Structure")) {
                if (columnsTable.isClustered()) {
                    String thisTableName = columnsTable.getTableName();
                    String otherTableName = columnsTable.getClusteredWithTableName();
                    systemCatalog.getTables().forEach(table -> {
                        if (table.getClusteredWithTableName().equalsIgnoreCase(thisTableName) ||
                                table.getClusteredWithTableName().equalsIgnoreCase(otherTableName)) {
                            table.setClusteredWith("none");
                        }
                    });
                    systemCatalog.getTables().forEach(table -> {
                        if (table.getTableName().equalsIgnoreCase(thisTableName) ||
                                table.getTableName().equalsIgnoreCase(otherTableName)) {
                            table.setClusteredWith("none");
                        }
                    });
                }
                // force refresh
                screenController.refresh(systemCatalog);
                screenController.setScreen(Screen.Type.TABLES_SCREEN);
            }
        });

        columnPane = new BorderPane();
        columnPane.setTop(columnText);
        columnPane.setBottom(fileStructureChoiceBox);
        BorderPane.setAlignment(columnText, Pos.CENTER);
        BorderPane.setAlignment(fileStructureChoiceBox, Pos.CENTER);
        BorderPane.setMargin(columnText, new Insets(15, 0, 15, 0));
        BorderPane.setMargin(fileStructureChoiceBox, new Insets(0, 0, 15, 0));
        columnPane.setBackground(new Background(
                new BackgroundFill(Color.rgb(70, 70, 70), new CornerRadii(5),
                        new Insets(0, 15, 0, 15))));
        columnPane.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
    }

    public BorderPane getColumnPane() {
        return columnPane;
    }
}