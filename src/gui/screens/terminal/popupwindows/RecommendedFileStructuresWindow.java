package gui.screens.terminal.popupwindows;

import datastructures.misc.Pair;
import datastructures.misc.Triple;
import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.FileStructure;
import files.io.FileType;
import files.io.IO;
import gui.ScreenController;
import gui.screens.Screen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import systemcatalog.SystemCatalog;
import utilities.OptimizerUtilities;
import utilities.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecommendedFileStructuresWindow extends Stage {

    // recommended file structures -> column name, table name, file structure
    // clustered tables -> table name, table name
    public RecommendedFileStructuresWindow(List<Triple<String, String, String>> recommendedFileStructures,
                                           List<Pair<String, String>> clusteredTables, SystemCatalog systemCatalog,
                                           ScreenController screenController) {

        // vBox container to hold the title, a list of recommended file structure panes, and a button for building them
        VBox overallContainer = new VBox(30.0);
        overallContainer.setPrefSize(Screen.defaultWidth, Screen.defaultHeight);
        overallContainer.setAlignment(Pos.CENTER);
        overallContainer.setPadding(new Insets(0, 30, 0, 30));
        overallContainer.setBackground(new Background(
                new BackgroundFill(Color.rgb(30, 30, 30), CornerRadii.EMPTY, Insets.EMPTY)));


        // title text
        Text titleText = new Text("Recommended FileStructures");
        titleText.setFont(new Font(75.0));
        titleText.setFill(Color.WHITE);
        titleText.setSmooth(true);

        overallContainer.getChildren().add(titleText);


        // getting all distinct table names from recommended file structures
        List<String> distinctTableNames = new ArrayList<>();

        for (Triple<String, String, String> recommendedFileStructure : recommendedFileStructures) {
            String tableName = recommendedFileStructure.getSecond();
            boolean foundDuplicate = false;
            for (String distinctTableName : distinctTableNames) {
                if (tableName.equalsIgnoreCase(distinctTableName)) {
                    foundDuplicate = true;
                    break;
                }
            }
            if (! foundDuplicate) {
                distinctTableNames.add(tableName);
            }
        }

        // for each table, create a pane that contains the file structures to build within that table (exclude
        // clustered files), the table pane contains table name text and file structures text
        for (String tableName : distinctTableNames) {

            BorderPane tablePaneArea = new BorderPane();
            tablePaneArea.setBackground(new Background(new BackgroundFill(
                    Color.rgb(60, 60, 60), new CornerRadii(5), Insets.EMPTY)));
            tablePaneArea.setEffect(
                    new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

            Text tableNameText = new Text("In " + tableName + ":");
            tableNameText.setFont(new Font(50.0));
            tableNameText.setFill(Color.WHITE);
            tableNameText.setSmooth(true);

            BorderPane.setAlignment(tableNameText, Pos.CENTER);
            tablePaneArea.setTop(tableNameText);

            // get the recommended file structures for the current table
            StringBuilder stringBuilder = new StringBuilder();

            for (Triple<String, String, String> recommendedFileStructure : recommendedFileStructures) {
                if (recommendedFileStructure.getSecond().equalsIgnoreCase(tableName)) {
                    String columnName = recommendedFileStructure.getFirst();
                    String fileStructure = recommendedFileStructure.getThird();
                    stringBuilder.append(fileStructure).append(" on column ").append(columnName).append("\n");
                }
            }

            // remove "\n"
            assert stringBuilder.length() != 0;
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

            Text fileStructuresToBuildText = new Text(stringBuilder.toString());
            fileStructuresToBuildText.setFont(new Font(35.0));
            fileStructuresToBuildText.setFill(Color.WHITE);
            fileStructuresToBuildText.setSmooth(true);

            BorderPane.setAlignment(fileStructuresToBuildText, Pos.CENTER);
            BorderPane.setMargin(fileStructuresToBuildText, new Insets(15));
            tablePaneArea.setBottom(fileStructuresToBuildText);

            overallContainer.getChildren().add(tablePaneArea);
        }


        // clustered tables area
        for (Pair<String, String> clusteredTable : clusteredTables) {

            BorderPane clusteredTablePaneArea = new BorderPane();
            clusteredTablePaneArea.setBackground(new Background(new BackgroundFill(
                    Color.rgb(60, 60, 60), new CornerRadii(5), Insets.EMPTY)));
            clusteredTablePaneArea.setEffect(
                    new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

            String firstTableName = clusteredTable.getFirst();
            String secondTableName = clusteredTable.getSecond();

            Text clusteredTableText = new Text("Clustered File on table " + firstTableName + " and " + secondTableName);
            clusteredTableText.setFont(new Font(50.0));
            clusteredTableText.setFill(Color.WHITE);
            clusteredTableText.setSmooth(true);

            BorderPane.setAlignment(clusteredTableText, Pos.CENTER);
            clusteredTablePaneArea.setCenter(clusteredTableText);

            overallContainer.getChildren().add(clusteredTablePaneArea);
        }


        // if the user wishes to build these recommended file structures
        Button buildRecommendedFileStructuresButton = new Button("Build Recommended File Structures");
        buildRecommendedFileStructuresButton.setFont(new Font(35.0));
        buildRecommendedFileStructuresButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));

        buildRecommendedFileStructuresButton.setOnAction(e -> {

            for (Triple<String, String, String> recommendedFileStructure : recommendedFileStructures) {

                String columnName = recommendedFileStructure.getFirst();
                String tableName = recommendedFileStructure.getSecond();
                String fileStructure = recommendedFileStructure.getThird();

                Table referencedTable = Utilities.getReferencedTable(tableName, systemCatalog.getTables());
                assert referencedTable != null;
                Column referencedColumn = referencedTable.getColumn(columnName);
                assert referencedColumn != null;

                switch (fileStructure) {
                    case "Hash Table":
                        referencedColumn.setFileStructure(FileStructure.HASH_TABLE);
                        break;
                    case "Clustered B-Tree":
                        referencedColumn.setFileStructure(FileStructure.CLUSTERED_B_TREE);
                        break;
                    case "Secondary B-Tree":
                        referencedColumn.setFileStructure(FileStructure.SECONDARY_B_TREE);
                        break;
                }
            }

            for (Pair<String, String> clusteredFile : clusteredTables) {

                String firstTableName = clusteredFile.getFirst();
                String secondTableName = clusteredFile.getSecond();

                systemCatalog.getTables().forEach(table -> {
                    if (table.getClusteredWithTableName().equalsIgnoreCase(firstTableName) ||
                            table.getClusteredWithTableName().equalsIgnoreCase(secondTableName)) {
                        table.setClusteredWith("none");
                    }
                });

                Table firstTableReference =
                        Utilities.getReferencedTable(clusteredFile.getFirst(), systemCatalog.getTables());
                assert firstTableReference != null;
                Table secondTableReference =
                        Utilities.getReferencedTable(clusteredFile.getSecond(), systemCatalog.getTables());
                assert secondTableReference != null;

                firstTableReference.setClusteredWith(secondTableName);
                firstTableReference.getColumns().forEach(c -> c.setFileStructure(FileStructure.NONE));
                secondTableReference.setClusteredWith(firstTableName);
                secondTableReference.getColumns().forEach(c -> c.setFileStructure(FileStructure.NONE));
            }

            // force refresh to display changes
            screenController.refresh(systemCatalog);
        });

        VBox.setMargin(buildRecommendedFileStructuresButton, new Insets(0, 0, 30, 0));
        overallContainer.getChildren().add(buildRecommendedFileStructuresButton);

        // scroll pane to hold the vBox container
        ScrollPane scrollPane = new ScrollPane(overallContainer);
        scrollPane.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_SCROLL_PANE_STYLE));
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // add the scroll pane to the scene
        Scene scene = new Scene(scrollPane);
        scene.setFill(Color.rgb(30, 30, 30));

        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = (double) newValue;
            overallContainer.setPrefWidth(newWidth - 22);
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newHeight = (double) newValue;
            overallContainer.setPrefHeight(newHeight - 22);
        });

        this.setTitle("Recommended File Structures");
        this.setScene(scene);
        this.show();
    }
}