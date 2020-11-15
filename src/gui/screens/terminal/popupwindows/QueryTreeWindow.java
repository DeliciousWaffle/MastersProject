package gui.screens.terminal.popupwindows;

import datastructures.querytree.QueryTree;
import datastructures.querytree.operator.types.PipelinedExpression;
import files.io.FileType;
import files.io.IO;
import gui.screens.terminal.popupwindows.querytreegui.QueryTreeGUI;
import gui.screens.terminal.popupwindows.querytreegui.popupwindows.QueryTreeOptimizationHeuristicWindows;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import utilities.OptimizerUtilities;
import utilities.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueryTreeWindow extends Stage {

    private List<QueryTreeGUI> queryTreeGUIStates;
    private List<Button> optimizationInfoButtons;
    private int stateIndex;
    private Button rightButton, leftButton;

    public QueryTreeWindow(List<QueryTree> queryTreeStates, String productionCost, String writeToDiskCost) {

        // root container to hold all of our junk, query tree states will be held above while buttons below
        BorderPane root = new BorderPane();

        // user will be able to click left or right buttons to switch between different states of the query tree
        queryTreeGUIStates = queryTreeStates.stream()
                .map(queryTree -> {
                    QueryTree copy = new QueryTree(queryTree);
                    OptimizerUtilities.removePrefixedColumnNamesFromQueryTrees(copy);
                    return copy;
                })
                .map(queryTree -> new QueryTreeGUI(queryTree, productionCost, writeToDiskCost))
                .collect(Collectors.toList());
        stateIndex = 0;

        // labels mapped to the current query tree state that give info about the optimization taking place
        getOptimizationInfoLabels();

        leftButton = new Button();
        leftButton.setText("Previous State");
        leftButton.setFont(new Font(25));
        leftButton.setVisible(false);
        leftButton.getStylesheets().setAll(IO.readCSS(FileType.CSS.BUTTON_STYLE));

        rightButton = new Button();
        rightButton.setText("Next State");
        rightButton.setFont(new Font(25));
        rightButton.getStylesheets().setAll(IO.readCSS(FileType.CSS.BUTTON_STYLE));

        BorderPane infoContainer = new BorderPane();
        infoContainer.setLeft(leftButton);
        infoContainer.setCenter(optimizationInfoButtons.get(0));
        infoContainer.setRight(rightButton);
        BorderPane.setMargin(leftButton, new Insets(10, 5, 10, 10));
        optimizationInfoButtons.forEach(e -> BorderPane.setMargin(e, new Insets(10, 5, 10, 5)));
        BorderPane.setMargin(rightButton, new Insets(10, 10, 10, 5));
        infoContainer.setStyle("-fx-background-color: rgb(60, 60, 60);");

        // add everything to the root
        root.setTop(queryTreeGUIStates.get(0).getContainer());
        root.setBottom(infoContainer);

        // handling actions

        // on left click, go to previous state in query tree
        leftButton.setOnAction(e -> {

            decrementStateIndex();
            root.setTop(queryTreeGUIStates.get(stateIndex).getContainer());

            if (stateIndex >= optimizationInfoButtons.size()) {
                infoContainer.setCenter(optimizationInfoButtons.get(optimizationInfoButtons.size() - 1));
            } else {
                infoContainer.setCenter(optimizationInfoButtons.get(stateIndex));
            }
        });

        // on right click, go to next state in query tree
        rightButton.setOnAction(e -> {

            incrementStateIndex();
            root.setTop(queryTreeGUIStates.get(stateIndex).getContainer());

            if (stateIndex >= optimizationInfoButtons.size()) {
                infoContainer.setCenter(optimizationInfoButtons.get(optimizationInfoButtons.size() - 1));
            } else {
                infoContainer.setCenter(optimizationInfoButtons.get(stateIndex));
            }
        });

        // adjusting the area in which the query tree is displayed upon window resize
        this.widthProperty().addListener((observable, oldValue, newValue) -> {
            double width = (Double) newValue;
            for(QueryTreeGUI queryTreeGUI : queryTreeGUIStates) {
                queryTreeGUI.adjustWidth(width);
            }
        });

        this.heightProperty().addListener((observable, oldValue, newValue) -> {

            double height = (Double) newValue;

            // will need to adjust the actual display of the query tree states along with the container itself
            for(QueryTreeGUI queryTreeGUI : queryTreeGUIStates) {
                queryTreeGUI.adjustHeight(height);
                queryTreeGUI.getContainer().setPrefHeight(height - 113); // bogus value that works
            }
        });

        this.setTitle("Query Tree States");
        this.setScene(new Scene(root));
        this.show();
    }

    // helper method for keeping the constructor somewhat clean
    private void getOptimizationInfoLabels() {

        // initial state
        Button initialState = new Button("Initial Query Tree State");
        initialState.setOnAction(e -> new QueryTreeOptimizationHeuristicWindows.InitialState());

        // cascaded selections
        Button cascadedSelections = new Button("Cascaded Selections");
        cascadedSelections.setOnAction(e -> new QueryTreeOptimizationHeuristicWindows.CascadedSelections());

        // moving each select as far down the query tree as possible
        Button movedDownSelections = new Button("Moved Down Selections");
        movedDownSelections.setOnAction(e -> new QueryTreeOptimizationHeuristicWindows.MovedDownSelections());

        // combining cartesian products and selections to form joins
        Button formedJoins = new Button("Formed Joins");
        formedJoins.setOnAction(e -> new QueryTreeOptimizationHeuristicWindows.FormedJoins());

        // moving projections as far down the query tree as possible
        Button movedDownProjections = new Button("Moved Down Projections");
        movedDownProjections.setOnAction(e -> new QueryTreeOptimizationHeuristicWindows.MovedDownProjections());

        // rearrangement of joins to reduce query tree cost
        Button rearrangedJoins = new Button("Rearranged Joins");
        rearrangedJoins.setOnAction(e -> new QueryTreeOptimizationHeuristicWindows.RearrangedJoins());

        // identifying subtrees that can be pipelined
        Button pipeliningSubtrees = new Button("Pipelining Subtrees (Click on a " + PipelinedExpression.SYMBOL +
                " Node)");
        pipeliningSubtrees.setOnAction(e -> new QueryTreeOptimizationHeuristicWindows.PipeliningSubtrees());

        // making the list and adding all the labels
        this.optimizationInfoButtons = new ArrayList<>();

        optimizationInfoButtons.addAll(new ArrayList<>(Arrays.asList(initialState, cascadedSelections,
                movedDownSelections, formedJoins, movedDownProjections, rearrangedJoins, pipeliningSubtrees)));

        // adjusting the labels and tooltips
        for (Button button : optimizationInfoButtons) {
            button.setFont(new Font(25));
            button.setTextFill(Color.WHITE);
            button.setAlignment(Pos.CENTER);
            button.getStylesheets().setAll(IO.readCSS(FileType.CSS.BUTTON_STYLE));
        }
    }

    private void decrementStateIndex() {

        stateIndex--;
        leftButton.setVisible(true);
        rightButton.setVisible(true);

        if (stateIndex <= 0) {
            stateIndex = 0;
            leftButton.setVisible(false);
        }
    }

    private void incrementStateIndex() {

        stateIndex++;
        rightButton.setVisible(true);
        leftButton.setVisible(true);

        if (stateIndex >= queryTreeGUIStates.size() - 1) {
            stateIndex = queryTreeGUIStates.size() - 1;
            rightButton.setVisible(false);
        }
    }
}