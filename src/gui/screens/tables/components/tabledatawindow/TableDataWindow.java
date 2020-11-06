package gui.screens.tables.components.tabledatawindow;

import datastructures.relation.table.component.TableData;
import files.io.FileType;
import files.io.IO;
import gui.screens.Screen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TableDataWindow extends Stage {

    public TableDataWindow(String tableName, List<String> columnNames, List<List<String>> data) {

        // will place each column and its rows into a vBox, and place each VBox into the result set layout
        HBox resultSetLayout = new HBox();
        resultSetLayout.setAlignment(Pos.CENTER);
        resultSetLayout.setPrefSize(Screen.defaultWidth, Screen.defaultHeight);
        resultSetLayout.setBackground(new Background(new BackgroundFill(Color.rgb(30, 30, 30),
                CornerRadii.EMPTY, Insets.EMPTY)));

        int numCols = data.isEmpty() ? 0 : data.get(0).size();

        boolean hasNoData = numCols == 0;

        if (hasNoData) {

            for (String columnName : columnNames) {

                // column name text
                Text columnNameText = new Text(columnName);
                columnNameText.setFont(new Font(25));
                columnNameText.setFill(Color.WHITE);
                BorderPane columnNameTextContainer = new BorderPane();
                columnNameTextContainer.setLeft(columnNameText);
                BorderPane.setMargin(columnNameText, new Insets(10, 10, 0, 10));
                columnNameTextContainer.setBackground(new Background(
                        new BackgroundFill(Color.rgb(40, 40, 40), CornerRadii.EMPTY, Insets.EMPTY)));

                resultSetLayout.getChildren().add(columnNameTextContainer);
            }

        } else {

            for (int col = 0; col < numCols; col++) {

                // container for the column name and its rows
                VBox columnContainer = new VBox();

                // column name text
                Text columnNameText = new Text(columnNames.get(col));
                columnNameText.setFont(new Font(25));
                columnNameText.setFill(Color.WHITE);
                BorderPane columnNameTextContainer = new BorderPane();
                columnNameTextContainer.setLeft(columnNameText);
                BorderPane.setMargin(columnNameText, new Insets(10, 10, 0, 10));
                columnNameTextContainer.setBackground(new Background(
                        new BackgroundFill(Color.rgb(40, 40, 40), CornerRadii.EMPTY, Insets.EMPTY)));

                columnContainer.getChildren().add(columnNameTextContainer);

                // getting all the rows of this column
                List<String> columnsRows = new ArrayList<>();

                for (List<String> row : data) {
                    columnsRows.add(row.get(col));
                }

                // columns rows text
                List<BorderPane> rowTextContainerList = new ArrayList<>();

                for (int i = 0; i < columnsRows.size(); i++) {

                    Text rowText = new Text(columnsRows.get(i));
                    rowText.setFont(new Font(25));
                    rowText.setFill(Color.WHITE);
                    BorderPane rowTextContainer = new BorderPane();
                    rowTextContainer.setLeft(rowText);
                    BorderPane.setMargin(rowText, new Insets(0, 10, 0, 10));

                    boolean isEven = i % 2 == 0;

                    if (isEven) {
                        rowTextContainer.setBackground(new Background(
                                new BackgroundFill(Color.rgb(65, 65, 65),
                                        CornerRadii.EMPTY, Insets.EMPTY)));
                    } else {
                        rowTextContainer.setBackground(new Background(
                                new BackgroundFill(Color.rgb(60, 60, 60),
                                        CornerRadii.EMPTY, Insets.EMPTY)));
                    }

                    rowTextContainerList.add(rowTextContainer);
                }

                columnContainer.getChildren().addAll(rowTextContainerList);

                resultSetLayout.getChildren().add(columnContainer);
            }
        }

        // add all the table data to a scroll pane, so we can scroll yo
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(resultSetLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStylesheets().add(IO.readCSS(FileType.CSS.DARK_SCROLL_PANE_STYLE));

        // increase scroll speed
        resultSetLayout.setOnScroll(e -> {
            double deltaY = e.getDeltaY() * 1.1;
            double width = scrollPane.getContent().getBoundsInLocal().getWidth();
            double vValue = scrollPane.getVvalue();
            scrollPane.setVvalue(vValue + -(deltaY / width));
        });

        Scene scene = new Scene(scrollPane);
        scene.setFill(Color.rgb(30, 30, 30));

        this.setTitle(tableName);
        this.setScene(scene);
        this.show();
    }
}