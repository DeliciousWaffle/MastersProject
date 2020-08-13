package gui.screens.tables;

import datastructures.relation.table.Table;
import files.io.FileType;
import files.io.IO;
import files.io.Serialize;
import gui.ScreenController;
import gui.screens.Screen;
import gui.screens.tables.components.TablePane;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import systemcatalog.SystemCatalog;

import java.util.ArrayList;
import java.util.List;

public class TablesScreen extends Screen {

    private Scene tablesScreen;

    public TablesScreen(ScreenController screenController, SystemCatalog systemCatalog) {

        // button layout for top part of screen
        HBox buttonLayout = super.getButtonLayout(screenController);

        // convert the tables that we have in system to a gui representation
        String blah = IO.readCurrentData(FileType.CurrentData.CURRENT_TABLES);
        List<Table> tables = Serialize.unSerializeTables(blah);

        List<BorderPane> tablePanes = new ArrayList<>();

        for(Table table : tables) {

            List<String> otherTableNames = new ArrayList<>();

            for(Table otherTable : tables) {
                if(! table.getTableName().equalsIgnoreCase(otherTable.getTableName())) {
                    otherTableNames.add("Clustered With: " + otherTable.getTableName());
                }
            }

            otherTableNames.add(0, "Clustered With No Table");
            tablePanes.add(new TablePane(table, otherTableNames).getTablePane());
        }

        // add the table panes to a vertical layout
        HBox tablePanesLayout = new HBox();
        tablePanesLayout.setMinSize(0, 0);
        tablePanesLayout.setSpacing(20);
        tablePanesLayout.setBackground(new Background(
                new BackgroundFill(Color.rgb(30, 30, 30), CornerRadii.EMPTY, Insets.EMPTY)));
        tablePanesLayout.getChildren().addAll(tablePanes);

        // adding the centered layout to a scroll pane because the data may go off screen
        ScrollPane tablePanesScrollLayout = new ScrollPane(tablePanesLayout);
        tablePanesScrollLayout.getStylesheets().add("files/css/ScrollPaneStyle.css");
        tablePanesLayout.setPadding(new Insets(10, 20, 20, 20));

        // increase scroll speed
        // credit: https://stackoverflow.com/questions/32739269/how-do-i-change-the-amount-by-which-scrollpane-scrolls
        tablePanesLayout.setOnScroll(e -> {
            double deltaY = e.getDeltaY() * 6;
            double width = tablePanesScrollLayout.getContent().getBoundsInLocal().getWidth();
            double vValue = tablePanesScrollLayout.getVvalue();
            tablePanesScrollLayout.setVvalue(vValue + -(deltaY / width));
        });

        // add the button layout and content layout to overall screen
        BorderPane overallLayout = new BorderPane();
        overallLayout.setTop(buttonLayout);
        overallLayout.setBottom(tablePanesScrollLayout);
        overallLayout.setMinSize(0, 0);
        overallLayout.setPrefSize(defaultWidth, defaultHeight);
        overallLayout.setStyle("-fx-background-color: rgb(30, 30, 30);");

        tablesScreen = new Scene(overallLayout);

        tablesScreen.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = (double) newValue;
            tablePanesScrollLayout.setPrefWidth(newWidth);
        });

        tablesScreen.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newHeight = (double) newValue;
            tablePanesScrollLayout.setPrefHeight(newHeight);
        });
    }

    @Override
    public Scene getScreen() {
        return tablesScreen;
    }
}