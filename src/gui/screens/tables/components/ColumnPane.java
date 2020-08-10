package gui.screens.tables.components;

import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.DataType;
import datastructures.relation.table.component.FileStructure;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ColumnPane {

    private BorderPane columnPane;

    public ColumnPane(Column column, String keyType) {

        // get data
        String columnName = column.getName();
        DataType dataType = column.getDataType();
        int size = column.size();
        FileStructure fileStructure = column.getFileStructure();

        // convert column name to text
        Text columnNameText = new Text(columnName);
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

        Text dataTypeAndSizeText = new Text(dataTypeString + " (" + size + ")");
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
        ChoiceBox<String> fileStructureChoiceBox = new ChoiceBox<>();
        fileStructureChoiceBox.getItems().addAll(
                "Secondary B-Tree",
                "Clustered B-Tree",
                "Hash Table",
                "No File Structure"
        );

        fileStructureChoiceBox.getStylesheets().add("files/css/ChoiceBoxStyle.css");

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
                image = new Image("files/assets/tablesscreen/PrimaryKey.png",
                        75, 45, false, true);
                tooltip.setText("Primary Key");
                break;
            case "Foreign":
                image = new Image("files/assets/tablesscreen/ForeignKey.png",
                        75, 45, false, true);
                tooltip.setText("Foreign Key");
                break;
            case "None":
                image = new Image("files/assets/tablesscreen/NoKey.png",
                        75, 45, false, true);
                tooltip.setText("No Key");
                break;
            default:
                image = new Image("files/assets/tablesscreen/NoKey.png",
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
        columnPane = new BorderPane();
        columnPane.setTop(formatColumnData);
        columnPane.setBottom(fileStructureAndKeyLayout);

        BorderPane.setAlignment(formatColumnData, Pos.CENTER);
        BorderPane.setAlignment(fileStructureAndKeyLayout, Pos.CENTER);

        columnPane.setPadding(new Insets(10));
        columnPane.setBackground(new Background(
                new BackgroundFill(Color.rgb(70, 70, 70), CornerRadii.EMPTY, Insets.EMPTY)
        ));
        columnPane.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
    }

    public BorderPane getColumnPane() {
        return columnPane;
    }
}
