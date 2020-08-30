package gui.screens.terminal.popupwindows;

import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.DataType;
import datastructures.trees.querytree.QueryTree;
import files.io.FileType;
import files.io.IO;
import gui.screens.terminal.popupwindows.querytreegui.QueryTreeGUI;
import gui.screens.terminal.popupwindows.querytreegui.popupwindows.QueryTreeOptimizationHeuristicWindows;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import systemcatalog.components.optimizer.Optimizer;
import systemcatalog.components.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryTreeWindow extends Application { // change back to Stage

    private List<QueryTreeGUI> queryTreeGUIStates;
    private List<Button> optimizationInfoButtons;
    private int stateIndex;
    private Button rightButton, leftButton;

    public static void main(String[] args) {launch(args);}

    @Override public void start(Stage primaryStage) {

        String query = "select col1, col2, col3 from tab1, tab2, tab3 where tab1.col1 = tab2.col1 and tab2.col1 = tab3.col1 and col3 = 7 and tab1.col1 = 3";
        String[] tokens = Parser.formatAndTokenizeInput(query);
        Optimizer optimizer = new Optimizer();
        List<Table> tables = new ArrayList<>();/*(Arrays.asList(
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
        );*/
        List<QueryTree> queryTreeStates = optimizer.getQueryTreeStates(tokens, tables);

        //new QueryTreeWindow(queryTreeStates, primaryStage);
    //}

    //public QueryTreeWindow(List<QueryTree> queryTreeStates, Stage primaryStage) {


        System.out.println(optimizer.getNaiveRelationalAlgebra(queryTreeStates.get(0)));

for(QueryTree queryTree : queryTreeStates) {
    System.out.println(queryTree.getTreeStructure());
    System.out.println("==========================================================================");
}


        // root container to hold all of our junk, query tree states will be held above while buttons below
        BorderPane root = new BorderPane();

        // user will be able to click left or right buttons to switch between different states of the query tree
        this.queryTreeGUIStates = new ArrayList<>();
        this.stateIndex = 0;

        for(QueryTree queryTree : queryTreeStates) {
            queryTreeGUIStates.add(new QueryTreeGUI(queryTree));
        }

        // labels mapped to the current query tree state that give info about the optimization taking place
        getOptimizationInfoLabels();

        this.leftButton = new Button();
        leftButton.setText("Previous State");
        leftButton.setFont(new Font(25));
        leftButton.setVisible(false);
        leftButton.getStylesheets().setAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));

        this.rightButton = new Button();
        rightButton.setText("Next State");
        rightButton.setFont(new Font(25));
        rightButton.getStylesheets().setAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));

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
            infoContainer.setCenter(optimizationInfoButtons.get(stateIndex));
        });

        // on right click, go to next state in query tree
        rightButton.setOnAction(e -> {

            incrementStateIndex();
            root.setTop(queryTreeGUIStates.get(stateIndex).getContainer());

            try {
                infoContainer.setCenter(optimizationInfoButtons.get(stateIndex));
            } catch(ArrayIndexOutOfBoundsException blah) {
                infoContainer.setCenter(optimizationInfoButtons.get(optimizationInfoButtons.size() - 1));
            }
        });

        // adjusting the area in which the query tree is displayed upon window resize
        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> { // change back to this
            double width = (Double) newValue;
            for(QueryTreeGUI queryTreeGUI : queryTreeGUIStates) {
                queryTreeGUI.adjustWidth(width);
            }
        });

        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> { // change back to this

            double height = (Double) newValue;

            // will need to adjust the actual display of the query tree states along with the container itself
            for(QueryTreeGUI queryTreeGUI : queryTreeGUIStates) {
                queryTreeGUI.adjustHeight(height);
                queryTreeGUI.getContainer().setPrefHeight(height - 113); // bogus value that works
            }
        });

        // change back to this
        primaryStage.setTitle("Query Tree States");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
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

        // rearrangement of joins to reduce query tree cost
        Button rearrangedJoins = new Button("Rearranged Joins");
        rearrangedJoins.setOnAction(e -> new QueryTreeOptimizationHeuristicWindows.RearrangedJoins());

        // moving projections as far down the query tree as possible
        Button movedDownProjections = new Button("Moved Down Projections");
        movedDownProjections.setOnAction(e -> new QueryTreeOptimizationHeuristicWindows.MovedDownProjections());

        // identifying subtrees that can be pipelined
        Button pipeliningSubtrees = new Button("Pipelining Subtrees");
        pipeliningSubtrees.setOnAction(e -> new QueryTreeOptimizationHeuristicWindows.PipeliningSubtrees());

        // making the list and adding all the labels
        this.optimizationInfoButtons = new ArrayList<>();

        optimizationInfoButtons.addAll(Arrays.asList(initialState, cascadedSelections, movedDownSelections,
                formedJoins, rearrangedJoins, movedDownProjections, pipeliningSubtrees));

        // adjusting the labels and tooltips
        for (Button button : optimizationInfoButtons) {
            button.setFont(new Font(25));
            button.setTextFill(Color.WHITE);
            button.setAlignment(Pos.CENTER);
            button.getStylesheets().setAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        }
    }

    private void decrementStateIndex() {

        stateIndex--;
        leftButton.setVisible(true);
        rightButton.setVisible(true);

        if(stateIndex < 0) {
            stateIndex = 0;
            leftButton.setVisible(false);
        }
    }

    private void incrementStateIndex() {

        stateIndex++;
        rightButton.setVisible(true);
        leftButton.setVisible(true);

        if(stateIndex > queryTreeGUIStates.size() - 1) {
            stateIndex = queryTreeGUIStates.size() - 1;
            rightButton.setVisible(false);
        }
    }
}