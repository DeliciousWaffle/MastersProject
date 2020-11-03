package gui.screens.tables;

import datastructures.relation.table.Table;
import files.io.FileType;
import files.io.IO;
import files.io.Serializer;
import gui.ScreenController;
import gui.screens.Screen;
import gui.screens.tables.components.TablePane;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import systemcatalog.SystemCatalog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TablesScreen extends Screen {

    private Scene tablesScreen;
    private HBox tablePanesLayout;
    private ScrollPane tablePanesScrollLayout;
    private BorderPane overallLayout;
    private List<TablePane> tablePaneList;

    public TablesScreen(ScreenController screenController, SystemCatalog systemCatalog) {

        // button layout for top part of screen
        HBox buttonLayout = super.getButtonLayout(screenController);

        // convert the tables that we have in system to a gui representation
        List<Table> tables = systemCatalog.getTables();
        this.tablePaneList = new ArrayList<>();

        for(Table table : tables) {

            List<String> otherTableNames = new ArrayList<>();

            for(Table otherTable : tables) {
                if(! table.getTableName().equalsIgnoreCase(otherTable.getTableName())) {
                    otherTableNames.add("Clustered With: " + otherTable.getTableName());
                }
            }

            otherTableNames.add(0, "Clustered With No Table");
            tablePaneList.add(new TablePane(table, otherTableNames));
        }

        // add the table panes to a vertical layout
        this.tablePanesLayout = new HBox();
        tablePanesLayout.setMinSize(0, 0);
        tablePanesLayout.setSpacing(20);
        tablePanesLayout.setStyle(Screen.DARK_HI);

        tablePanesLayout.getChildren().addAll(tablePaneList
                .stream()
                .map(TablePane::getTablePane)
                .collect(Collectors.toList())
        );

        // adding the centered layout to a scroll pane because the data may go off screen
        this.tablePanesScrollLayout = new ScrollPane(tablePanesLayout);
        tablePanesScrollLayout.getStylesheets().add(IO.readCSS(FileType.CSS.DARK_SCROLL_PANE_STYLE));
        tablePanesLayout.setPadding(new Insets(10, 20, 20, 20));

        // add the button layout and content layout to overall screen
        this.overallLayout = new BorderPane();
        overallLayout.setTop(buttonLayout);
        overallLayout.setBottom(tablePanesScrollLayout);
        overallLayout.setMinSize(0, 0);
        overallLayout.setPrefSize(defaultWidth, defaultHeight);
        overallLayout.setStyle(Screen.DARK_HI);

        tablesScreen = new Scene(overallLayout);

        tablesScreen.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = (double) newValue;
            super.adjustButtonWidth(newWidth);
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

    public void setToLightMode() {
        super.setToLightMode();
        this.tablePanesLayout.setStyle(Screen.LIGHT_LOW);
        this.overallLayout.setStyle(Screen.LIGHT_LOW);
        this.tablePanesScrollLayout.getStylesheets().setAll(IO.readCSS(FileType.CSS.LIGHT_SCROLL_PANE_STYLE));
        this.tablePaneList.forEach(TablePane::setToLightMode);
    }

    public void setToDarkMode() {
        super.setToDarkMode();
        this.tablePanesLayout.setStyle(Screen.DARK_HI);
        this.overallLayout.setStyle(Screen.DARK_HI);
        this.tablePanesScrollLayout.getStylesheets().setAll(IO.readCSS(FileType.CSS.DARK_SCROLL_PANE_STYLE));
        this.tablePaneList.forEach(TablePane::setToDarkMode);
    }
}