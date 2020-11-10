package gui.screens.terminal.popupwindows;

import datastructures.misc.Quadruple;
import files.io.FileType;
import files.io.IO;
import gui.screens.Screen;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class QueryCostWindow extends Stage {

    public QueryCostWindow(Quadruple<Integer, Integer, String, String> costAnalysis) {

        // unwrapping the values that we need
        int totalProductionCost = costAnalysis.getFirst();
        int totalWriteToDiskCost = costAnalysis.getSecond();
        String productionCostWork = costAnalysis.getThird();
        String writeToDiskCostWork = costAnalysis.getFourth();

        // vBox container to hold the title, a panel containing the production cost/work, and a panel
        // containing the write to disk cost/work
        VBox overallContainer = new VBox(30.0);
        overallContainer.setPrefSize(Screen.defaultWidth, Screen.defaultHeight);
        overallContainer.setAlignment(Pos.CENTER);
        overallContainer.setPadding(new Insets(0, 30, 0, 30));
        overallContainer.setBackground(new Background(
                new BackgroundFill(Color.rgb(30, 30, 30), CornerRadii.EMPTY, Insets.EMPTY)));


        // title text
        Text titleText = new Text("Query Costs");
        titleText.setFont(new Font(75.0));
        titleText.setFill(Color.WHITE);
        titleText.setSmooth(true);

        VBox.setMargin(titleText, new Insets(20, 20, 10, 20));
        overallContainer.getChildren().add(titleText);


        // area that holds production cost title, production cost work, and total production cost
        VBox productionCostArea = new VBox();
        productionCostArea.setAlignment(Pos.CENTER);
        productionCostArea.setBackground(new Background(new BackgroundFill(Color.rgb(60, 60, 60),
                new CornerRadii(5), Insets.EMPTY)));
        productionCostArea.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        Text productionCostTitleText = new Text("Production Cost:");
        productionCostTitleText.setFont(new Font(40));
        productionCostTitleText.setFill(Color.WHITE);

        VBox.setMargin(productionCostTitleText, new Insets(10, 0, 5, 0));
        productionCostArea.getChildren().add(productionCostTitleText);

        Text productionCostWorkText = new Text(productionCostWork);
        productionCostWorkText.setFont(new Font(40));
        productionCostWorkText.setFill(Color.WHITE);

        VBox.setMargin(productionCostWorkText, new Insets(5, 0, 5, 0));
        productionCostArea.getChildren().add(productionCostWorkText);

        Text totalProductionCostText = new Text("Total Production Cost: " + totalProductionCost);
        totalProductionCostText.setFont(new Font(40));
        totalProductionCostText.setFill(Color.WHITE);

        VBox.setMargin(totalProductionCostText, new Insets(5, 0, 10, 0));
        productionCostArea.getChildren().add(totalProductionCostText);

        VBox.setMargin(productionCostArea, new Insets(10, 20, 10, 20));
        overallContainer.getChildren().add(productionCostArea);


        // area that holds write to disk cost title, wtdc work, and total wtdc
        VBox writeToDiskCostArea = new VBox();
        writeToDiskCostArea.setAlignment(Pos.CENTER);
        writeToDiskCostArea.setBackground(new Background(new BackgroundFill(Color.rgb(60, 60, 60),
                new CornerRadii(5), Insets.EMPTY)));
        writeToDiskCostArea.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        Text writeToDiskCostTitleText = new Text("Write To Disk Cost:");
        writeToDiskCostTitleText.setFont(new Font(40));
        writeToDiskCostTitleText.setFill(Color.WHITE);

        VBox.setMargin(writeToDiskCostTitleText, new Insets(10, 0, 5, 0));
        writeToDiskCostArea.getChildren().add(writeToDiskCostTitleText);

        Text writeToDiskCostWorkText = new Text(writeToDiskCostWork);
        writeToDiskCostWorkText.setFont(new Font(40));
        writeToDiskCostWorkText.setFill(Color.WHITE);

        VBox.setMargin(writeToDiskCostWorkText, new Insets(5, 0, 5, 0));
        writeToDiskCostArea.getChildren().add(writeToDiskCostWorkText);

        Text totalWriteToDiskCostText = new Text("Total Write To Disk Cost: " + totalWriteToDiskCost);
        totalWriteToDiskCostText.setFont(new Font(40));
        totalWriteToDiskCostText.setFill(Color.WHITE);

        VBox.setMargin(totalWriteToDiskCostText, new Insets(5, 0, 10, 0));
        writeToDiskCostArea.getChildren().add(totalWriteToDiskCostText);

        VBox.setMargin(writeToDiskCostArea, new Insets(10, 20, 50, 20));
        overallContainer.getChildren().add(writeToDiskCostArea);


        // scroll pane to hold the vBox container
        ScrollPane scrollPane = new ScrollPane(overallContainer);
        scrollPane.getStylesheets().addAll(IO.readCSS(FileType.CSS.SCROLL_PANE_STYLE));
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // add the scroll pane to the scene
        Scene scene = new Scene(scrollPane);
        scene.setFill(Color.rgb(30, 30, 30));

        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = (double) newValue;
            overallContainer.setPrefWidth(newWidth);
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newHeight = (double) newValue;
            overallContainer.setPrefHeight(newHeight);
        });

        this.setTitle("Query Costs:");
        this.setScene(scene);
        this.show();
    }
}