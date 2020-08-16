package gui.screens.terminal.popupwindows;

import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.DataType;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.trees.querytree.QueryTree;
import files.io.FileType;
import files.io.IO;
import gui.screens.terminal.popupwindows.querytreegui.QueryTreeGUI;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import systemcatalog.components.Optimizer;
import systemcatalog.components.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryTreeWindow extends Application {

    private int stateIndex;

    @Override
    public void start(Stage primaryStage) {

        String query = "select col1, col2, col3 from tab1, tab2, tab3 where tab1.col1 = tab2.col1 and tab2.col1 = tab3.col1 and col3 = 7";
        String[] tokens = Parser.formatAndTokenizeInput(query);
        Optimizer optimizer = new Optimizer();
        optimizer.setRuleGraphToUse(RuleGraphTypes.getQueryRuleGraph());
        optimizer.setTokenizedInput(tokens);
        optimizer.setTables(Arrays.asList(
                new Table("tab1", Arrays.asList(
                        new Column("col1", DataType.NUMBER, 1),
                        new Column("col2", DataType.NUMBER, 1),
                        new Column("col3", DataType.NUMBER, 1)),
                        "col1", new ArrayList<>()),
                new Table("tab2", Arrays.asList(
                        new Column("col1", DataType.NUMBER, 1)),
                        "col1", new ArrayList<>()
                ),
                new Table("tab3", Arrays.asList(
                        new Column("col1", DataType.NUMBER, 1)),
                        "col1", new ArrayList<>())
                )
        );
        optimizer.optimize();
        List<QueryTree> queryTreeStates = optimizer.getQueryTreeStates();



        // root container to hold all of our junk, query tree states will be held above while buttons below
        BorderPane root = new BorderPane();

        // user will be able to click left or right buttons to switch between different states of the query tree
        List<QueryTreeGUI> queryTreeGUIStates = new ArrayList<>();
        this.stateIndex = 0;

        for(QueryTree queryTree : queryTreeStates) {
            queryTreeGUIStates.add(new QueryTreeGUI(queryTree));
        }

        Button leftButton = new Button();
        Image leftButtonImage = IO.readAsset(FileType.Asset.LEFT_ARROW);
        ImageView leftButtonImageView = new ImageView(leftButtonImage);
        leftButtonImageView.setFitWidth(100);
        leftButtonImageView.setFitHeight(100);
        leftButtonImageView.setSmooth(true);
        leftButton.setGraphic(leftButtonImageView);
        leftButton.setText("Previous State");
        leftButton.setFont(new Font(25));
        leftButton.setTextAlignment(TextAlignment.RIGHT);

        // on left click, go to previous state in query tree
        leftButton.setOnAction(e -> {
            decrementStateIndex();
            root.setTop(queryTreeGUIStates.get(stateIndex).getContainer());
        });

        Button rightButton = new Button();
        Image rightButtonImage = IO.readAsset(FileType.Asset.RIGHT_ARROW);
        ImageView rightButtonImageView = new ImageView(rightButtonImage);
        rightButtonImageView.setFitWidth(100);
        rightButtonImageView.setFitHeight(100);
        rightButtonImageView.setSmooth(true);
        rightButton.setGraphic(rightButtonImageView);
        rightButton.setText("Next State");
        rightButton.setFont(new Font(25));
        rightButton.setTextAlignment(TextAlignment.LEFT);

        // on right click, go to next state in query tree
        rightButton.setOnAction(e -> {
           incrementStateIndex();
           root.setTop(queryTreeGUIStates.get(stateIndex).getContainer());
        });

        BorderPane buttonContainer = new BorderPane();
        buttonContainer.setLeft(leftButton);
        buttonContainer.setRight(rightButton);

        // add everything to the root
        root.setTop(queryTreeGUIStates.get(0).getContainer());
        root.setBottom(buttonContainer);

        // adjusting the area in which the query tree is displayed upon window resize
        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            double width = (Double) newValue;
            for(QueryTreeGUI queryTreeGUI : queryTreeGUIStates) {
                queryTreeGUI.adjustWidth(width);
            }
        });

        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
            double height = (Double) newValue;
            for(QueryTreeGUI queryTreeGUI : queryTreeGUIStates) {
                queryTreeGUI.adjustHeight(height);
            }
        });

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void decrementStateIndex() {
        stateIndex--;
        if(stateIndex <= 0) {
            stateIndex = 0;
        }
    }

    private void incrementStateIndex() {
        stateIndex++;
    }

    public static void main(String[] args) {
        launch(args);
    }
}