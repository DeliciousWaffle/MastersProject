package gui.screens.terminal.popupwindows;

import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.DataType;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.trees.querytree.QueryTree;
import gui.screens.terminal.popupwindows.querytreegui.QueryTreeGUI;
import gui.screens.terminal.popupwindows.querytreegui.popupwindows.QueryTreeOptimizationHeuristicWindows;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import systemcatalog.components.Optimizer;
import systemcatalog.components.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryTreeWindow extends Application {

    private List<QueryTreeGUI> queryTreeGUIStates;
    private List<Button> optimizationInfoButtons;
    private int stateIndex;
    private Button rightButton, leftButton;

    @Override
    public void start(Stage primaryStage) {

        String query = "select col1, col2, col3 from tab1, tab2, tab3 where tab1.col1 = tab2.col1 and tab2.col1 = tab3.col1 and col3 = 7 and tab1.col1 = 3";
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
        this.queryTreeGUIStates = new ArrayList<>();
        this.stateIndex = 0;

        for(QueryTree queryTree : queryTreeStates) {
            queryTreeGUIStates.add(new QueryTreeGUI(queryTree));
        }

        // labels mapped to the current query tree state that give info about the optimization taking place
        getOptimizationInfoLabels();

        // styling for the buttons
        String buttonStyle = " -fx-background-color: rgb(100, 100, 100); -fx-text-fill: white;";
        String buttonEnteredStyle = "-fx-background-color: rgb(150, 150, 150); -fx-text-fill: white;";
        String buttonExitedStyle = buttonStyle;

        this.leftButton = new Button();
        leftButton.setText("Previous State");
        leftButton.setFont(new Font(25));
        leftButton.setVisible(false);
        leftButton.setStyle(buttonStyle);
        leftButton.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        // styling stuff
        leftButton.setOnMouseEntered(e -> {
            leftButton.setStyle(buttonEnteredStyle);
        });

        leftButton.setOnMouseExited(e -> {
            leftButton.setStyle(buttonExitedStyle);
        });

        this.rightButton = new Button();
        rightButton.setText("Next State");
        rightButton.setFont(new Font(25));
        rightButton.setStyle(buttonStyle);
        rightButton.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        rightButton.setOnMouseEntered(e -> {
            rightButton.setStyle(buttonEnteredStyle);
        });

        rightButton.setOnMouseExited(e -> {
            rightButton.setStyle(buttonExitedStyle);
        });

        BorderPane infoContainer = new BorderPane();
        infoContainer.setLeft(leftButton);
        infoContainer.setCenter(optimizationInfoButtons.get(0));
        infoContainer.setRight(rightButton);
        BorderPane.setMargin(leftButton, new Insets(10, 5, 10, 10));
        optimizationInfoButtons.forEach(e -> BorderPane.setMargin(e, new Insets(10, 5, 10, 5)));
        BorderPane.setMargin(rightButton, new Insets(10, 10, 10, 5));
        infoContainer.setBackground(new Background(
                new BackgroundFill(Color.rgb(50, 50, 50), CornerRadii.EMPTY, Insets.EMPTY))
        );

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

            // bad practice, yadda, yadda, yadda
            try {
                infoContainer.setCenter(optimizationInfoButtons.get(stateIndex));
            } catch(ArrayIndexOutOfBoundsException blah) {
                infoContainer.setCenter(optimizationInfoButtons.get(optimizationInfoButtons.size() - 1));
            }
        });

        // adjusting the area in which the query tree is displayed upon window resize
        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            double width = (Double) newValue;
            for(QueryTreeGUI queryTreeGUI : queryTreeGUIStates) {
                queryTreeGUI.adjustWidth(width);
            }
        });

        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {

            double height = (Double) newValue;

            // will need to adjust the actual display of the query tree states along with the container itself
            for(QueryTreeGUI queryTreeGUI : queryTreeGUIStates) {
                queryTreeGUI.adjustHeight(height);
                queryTreeGUI.getContainer().setPrefHeight(height - 113); // bogus value that works
            }
        });

        primaryStage.setTitle("Query Tree States");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    // helper method for keeping the constructor somewhat clean
    private void getOptimizationInfoLabels() {

        // initial state
        Button initialState = new Button("Initial Query Tree State");
        initialState.setOnAction(e -> {
            new QueryTreeOptimizationHeuristicWindows.InitialState();
        });

        // moving each select as far down the query tree as possible
        Button movedDownSelections = new Button("Moved Down Selections");

        // combining cartesian products and selections to form joins
        Button formedJoins = new Button("Formed Joins");

        // rearrangement of leaf nodes to reduce query tree cost
        Button rearrangedLeafNodes = new Button("Rearranged Leaf Nodes");

        // moving projections as far down the query tree as possible
        Button movedDownProjections = new Button("Moved Down Projections");

        // identifying subtrees that can be pipelined
        Button pipeliningSubtrees = new Button("Pipelining Subtrees");

        // making the list and adding all the labels
        this.optimizationInfoButtons = new ArrayList<>();

        optimizationInfoButtons.add(initialState);
        optimizationInfoButtons.add(movedDownSelections);
        optimizationInfoButtons.add(formedJoins);
        optimizationInfoButtons.add(rearrangedLeafNodes);
        optimizationInfoButtons.add(movedDownProjections);
        optimizationInfoButtons.add(pipeliningSubtrees);

        // adjusting the labels and tooltips
        for (Button button : optimizationInfoButtons) {

            button.setFont(new Font(25));
            button.setTextFill(Color.WHITE);
            button.setAlignment(Pos.CENTER);

            String buttonStyle = " -fx-background-color: rgb(100, 100, 100); -fx-text-fill: white;";

            button.setStyle(buttonStyle);

            String buttonEnteredStyle = "-fx-background-color: rgb(150, 150, 150); -fx-text-fill: white;";
            String buttonExitedStyle = buttonStyle;

            button.setOnMouseEntered(e -> {
                button.setStyle(buttonEnteredStyle);
            });

            button.setOnMouseExited(e -> {
                button.setStyle(buttonExitedStyle);
            });

            /*button.setBackground(new Background(
                    new BackgroundFill(Color.rgb(100, 100, 100),
                            new CornerRadii(3), new Insets(-10))
            ));*/
            button.setEffect(
                    new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
        }
    }

    private void decrementStateIndex() {

        stateIndex--;
        leftButton.setVisible(true);
        rightButton.setVisible(true);

        if(stateIndex <= 0) {
            stateIndex = 0;
            leftButton.setVisible(false);
        }
    }

    private void incrementStateIndex() {

        stateIndex++;
        rightButton.setVisible(true);
        leftButton.setVisible(true);

        if(stateIndex >= queryTreeGUIStates.size() - 1) {
            stateIndex = queryTreeGUIStates.size() - 1;
            rightButton.setVisible(false);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}