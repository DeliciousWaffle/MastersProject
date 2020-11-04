package gui.screens.tables.components;

import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.DataType;
import datastructures.relation.table.component.FileStructure;
import files.io.FileType;
import files.io.IO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ColumnPane {

    private BorderPane columnPane;
    private Text columnText;
    private ChoiceBox<String> fileStructureChoiceBox;

    public ColumnPane(Column column) {

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

        columnText = new Text(columnName + ": " + dataTypeFormatted + "(" + size + decimalSizeFormatted + ")");
        columnText.setFont(new Font(25.0));
        columnText.fillProperty().set(Color.WHITE);

        // creating a choice box for the data structure to build on for this column
        fileStructureChoiceBox = new ChoiceBox<>();

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

    /*public ColumnPane(Column column, String keyType) {

        // get data
        String columnName = column.getColumnName();
        DataType dataType = column.getDataType();
        int size = column.size();
        FileStructure fileStructure = column.getFileStructure();

        // convert column name to text
        this.columnNameText = new Text(columnName);
        columnNameText.setFont(new Font(25.0));
        columnNameText.fillProperty().set(Color.WHITE);

        // convert data type and size to text
        String dataTypeString = "";

        switch(dataType) {
            case NUMBER:
                dataTypeString = "Number";
                break;
            case CHAR:
                dataTypeString = "Character";
                break;
            case DATE:
                dataTypeString = "Date";
                break;
        }

        this.dataTypeAndSizeText = new Text(dataTypeString + " (" + size + ")");
        dataTypeAndSizeText.setFont(new Font(25.0));
        dataTypeAndSizeText.fillProperty().set(Color.WHITE);

        // store the column name and data into a border pane for formatting
        BorderPane formatColumnData = new BorderPane();
        formatColumnData.setLeft(columnNameText);
        formatColumnData.setRight(dataTypeAndSizeText);

        BorderPane.setMargin(columnNameText, new Insets(0, 5, 0, 0));
        BorderPane.setMargin(dataTypeAndSizeText, new Insets(0, 0, 0, 5));
        formatColumnData.setPadding(new Insets(0, 0, 10, 0));

        // creating a choice box for the data structure to build on
        this.fileStructureChoiceBox = new ChoiceBox<>();
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

        switch(fileStructure) {
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

        // creating a label and a tooltip telling the user what kind of key this is and other info
        Image image = null;

        Tooltip tooltip = new Tooltip();
        tooltip.setFont(new Font(15));

        switch(keyType) {
            case "Primary":
                image = new Image("files/images/tablesscreen/PrimaryKey.png",
                        75, 45, false, true);
                tooltip.setText("Primary Key");
                break;
            case "Foreign":
                image = new Image("files/images/tablesscreen/ForeignKey.png",
                        75, 45, false, true);
                tooltip.setText("Foreign Key");
                break;
            case "None":
                image = new Image("files/images/tablesscreen/NoKey.png",
                        75, 45, false, true);
                tooltip.setText("No Key");
                break;
            default:
                image = new Image("files/images/tablesscreen/NoKey.png",
                        75, 45, false, true);
                break;
        }

        ImageView imageView = new ImageView(image);
        Label keyLabel = new Label("", imageView);
        keyLabel.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        // show the tooltip fast
        keyLabel.setOnMouseMoved(event -> {
            tooltip.show(imageView, event.getScreenX(), event.getScreenY() + 15);
        });
        keyLabel.setOnMouseExited(event -> {
            tooltip.hide();
        });

        // combining the key label and tooltip
        keyLabel.setTooltip(tooltip);

        BorderPane fileStructureAndKeyLayout = new BorderPane();
        fileStructureAndKeyLayout.setLeft(fileStructureChoiceBox);
        fileStructureAndKeyLayout.setRight(keyLabel);

        BorderPane.setMargin(fileStructureChoiceBox, new Insets(0, 5, 0, 0));
        BorderPane.setMargin(keyLabel, new Insets(0, 0, 0, 5));

        // add text and choice box to the pane
        this.columnPane = new BorderPane();
        columnPane.setTop(formatColumnData);
        columnPane.setBottom(fileStructureAndKeyLayout);

        BorderPane.setAlignment(formatColumnData, Pos.CENTER);
        BorderPane.setAlignment(fileStructureAndKeyLayout, Pos.CENTER);

        columnPane.setPadding(new Insets(10));
        columnPane.setBackground(new Background(
                new BackgroundFill(Color.rgb(60, 60, 60), new CornerRadii(5), Insets.EMPTY)
        ));
        columnPane.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
    }*/

    public BorderPane getColumnPane() {
        return columnPane;
    }

    public void setToLightMode() {
        this.columnText.setFill(Color.BLACK);
        //this.dataTypeAndSizeText.setFill(Color.BLACK);
        this.fileStructureChoiceBox.getStylesheets().setAll(IO.readCSS(FileType.CSS.LIGHT_CHOICE_BOX_STYLE));
        this.columnPane.setBackground(new Background(
                new BackgroundFill(Color.rgb(150, 150, 150), new CornerRadii(5), Insets.EMPTY)));
    }

    public void setToDarkMode() {
        this.columnText.setFill(Color.WHITE);
        //this.dataTypeAndSizeText.setFill(Color.WHITE);
        this.fileStructureChoiceBox.getStylesheets().setAll(IO.readCSS(FileType.CSS.DARK_CHOICE_BOX_STYLE));
        this.columnPane.setBackground(new Background(
                new BackgroundFill(Color.rgb(60, 60, 60), new CornerRadii(5), Insets.EMPTY)));
    }
}