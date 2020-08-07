package gui.current.scenes.tables;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import datastructure.relation.table.component.Column;
import datastructure.relation.table.component.TableData;
import gui.current.scenes.Screen;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import javax.security.auth.callback.Callback;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TableDataWindow extends Stage {

    double fontSize = 25.0;

    public TableDataWindow(String tableName, List<String> columnNames, TableData tableData) {

        Font font = new Font(fontSize);
        String columnNameStyle = "-fx-background-color: rgb(30, 30, 30);";
        String oddRowStyle = "-fx-background-color: rgb(60, 60, 60);";
        String evenRowStyle = "-fx-background-color: rgb(65, 65, 65);";

        // will place each column and its data into a vbox, and place each vbox in an hbox
        // set up this way so formatting doesn't get weird
        HBox columnDataLayout = new HBox();
        columnDataLayout.setMinSize(0, 0);

        int numCols = tableData.getData().isEmpty() ? 0 : tableData.getData().get(0).size();

        // might have 0 data returned, if that's the case, still need to show the column names
        boolean hasNoData = numCols == 0;

        if(hasNoData) {

            for (String columnName : columnNames) {

                Text columnNameText = new Text(columnName);
                columnNameText.setFont(font);

                BorderPane columnNameContainer = new BorderPane();
                columnNameContainer.setLeft(columnNameText);
                columnNameContainer.setStyle(columnNameStyle);
                columnNameContainer.setPadding(new Insets(0, 10, 0, 10));

                columnDataLayout.getChildren().add(columnNameContainer);
            }

        } else {

            for (int col = 0; col < numCols; col++) {

                // getting the column name and the data at the current column
                String columnName = columnNames.get(col);
                List<String> columnsCellData = tableData.getRowsAt(col);

                // for each cell in the current column, create corresponding text and add it the vBox
                VBox columnsCellDataContainer = new VBox();
                columnsCellDataContainer.setMinSize(0, 0);

                // adding the column name to the vBox
                Text columnNameText = new Text(columnName);
                columnNameText.setFont(font);
                columnNameText.setFill(Color.WHITE);

                BorderPane columnNameContainer = new BorderPane();
                columnNameContainer.setMinSize(0, 0);
                columnNameContainer.setLeft(columnNameText);
                columnNameContainer.setStyle(columnNameStyle);
                columnNameContainer.setPadding(new Insets(0, 10, 0, 10));

                columnsCellDataContainer.getChildren().add(columnNameContainer);

                // adding the cell data to the vBox
                for (int row = 0; row < columnsCellData.size(); row++) {

                    String cellData = columnsCellData.get(row);
                    Text cellDataText = new Text(cellData);
                    cellDataText.setFont(font);
                    cellDataText.setFill(Color.WHITE);

                    BorderPane cellDataContainer = new BorderPane();
                    cellDataContainer.setLeft(cellDataText);
                    cellDataContainer.setPadding(new Insets(0, 10, 0, 10));

                    boolean isEven = row % 2 == 0;

                    if(isEven) {
                        cellDataContainer.setStyle(evenRowStyle);
                    } else {
                        cellDataContainer.setStyle(oddRowStyle);
                    }

                    columnsCellDataContainer.getChildren().add(cellDataContainer);
                }

                // add the rows of the current column to the hBox
                columnDataLayout.getChildren().add(columnsCellDataContainer);
            }
        }

        // add all the table data to a scroll pane, so we can scroll yo
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(columnDataLayout);
        scrollPane.setMinSize(0, 0);
        scrollPane.setPrefSize(Screen.defaultWidth, Screen.defaultHeight);
        scrollPane.getStylesheets().add("gui/current/scenes/help/scrollpane.css");
        scrollPane.setStyle("-fx-background-color: rgb(30, 30, 30);");

        Scene scene = new Scene(scrollPane);

        this.setTitle(tableName);
        this.setScene(scene);
        this.show();
    }
}