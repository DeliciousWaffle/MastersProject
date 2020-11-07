package gui.screens.tables;

import files.io.FileType;
import files.io.IO;
import gui.ScreenController;
import gui.screens.Screen;
import gui.screens.tables.components.TablePane;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import systemcatalog.SystemCatalog;
;
import java.util.stream.Collectors;

public class TablesScreen extends Screen {

    private Scene tablesScreen;

    public TablesScreen(ScreenController screenController, SystemCatalog systemCatalog) {

        // button layout for top part of screen
        HBox buttonLayout = super.getButtonLayout(screenController);

        // add the table panes to a horizontal layout
        HBox tablePanesLayout = new HBox();
        tablePanesLayout.setMinSize(0, 0);
        tablePanesLayout.setSpacing(20);
        tablePanesLayout.setStyle(Screen.DARK_HI);

        tablePanesLayout.getChildren().addAll(systemCatalog
                .getTables()
                .stream()
                .map(table -> new TablePane(table, systemCatalog, screenController))
                .map(TablePane::getTablePane)
                .collect(Collectors.toList())
        );

        // adding the centered layout to a scroll pane because the data may go off screen
        ScrollPane tablePanesScrollLayout = new ScrollPane(tablePanesLayout);
        tablePanesScrollLayout.getStylesheets().add(IO.readCSS(FileType.CSS.DARK_SCROLL_PANE_STYLE));
        tablePanesLayout.setPadding(new Insets(10, 20, 20, 20));

        // add the button layout and content layout to overall screen
        BorderPane overallLayout = new BorderPane();
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
}