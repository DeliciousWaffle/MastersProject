package gui.current.scenes.tables;

import datastructure.relation.table.component.Column;
import datastructure.relation.table.component.DataType;
import datastructure.relation.table.component.FileStructure;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.File;

public class ColumnPane {

    private BorderPane columnPane;

    public ColumnPane(Column column) {

        // get data
        String columnName = column.getName();
        DataType dataType = column.getDataType();
        int size = column.size();
        FileStructure fileStructure = column.getFileStructure();

        // formatting
        StringBuilder sb = new StringBuilder();
        sb.append(columnName).append(" ");
        sb.append(dataType).append("(").append(size).append(")");

        // convert to text
        Text columnDataText = new Text(sb.toString());
        columnDataText.setFont(new Font(25.0));
        columnDataText.fillProperty().set(Color.WHITE);

        // used to contain file structure info
        BorderPane fileStructurePane = new BorderPane();

        Text fileStructureText = new Text(fileStructure.toString());
        fileStructureText.setFont(new Font(25.0));
        fileStructureText.setFill(Color.WHITE);

        ChoiceBox<FileStructure> fileStructureChoiceBox = new ChoiceBox<>();
        fileStructureChoiceBox.getItems().addAll(
                FileStructure.SECONDARY_B_TREE,
                FileStructure.CLUSTERED_B_TREE,
                FileStructure.HASH_TABLE,
                FileStructure.NONE
        );

        fileStructureChoiceBox.setValue(fileStructure);

        fileStructurePane.setLeft(fileStructureText);
        fileStructurePane.setRight(fileStructureChoiceBox);

        // add text and choice box to the pane
        columnPane = new BorderPane();
        columnPane.setTop(columnDataText);
        columnPane.setBottom(fileStructureChoiceBox);
        columnPane.setBackground(new Background(
                new BackgroundFill(Color.rgb(60, 60, 60), CornerRadii.EMPTY, Insets.EMPTY)
        ));
        columnPane.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
    }

    public BorderPane getColumnPane() {
        return columnPane;
    }
}
