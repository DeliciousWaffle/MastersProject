package gui.screens.terminal.popupwindows;

import datastructures.relation.table.Table;
import datastructures.relation.table.component.Column;
import datastructures.relation.table.component.DataType;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.trees.querytree.QueryTree;
import gui.screens.terminal.popupwindows.querytreegui.QueryTreeGUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import systemcatalog.components.Optimizer;
import systemcatalog.components.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryTreeWindow extends Application {

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
        //for(QueryTree queryTree : queryTreeStates) {
        //    System.out.println(queryTree.getTreeStructure());
        //    System.out.println();
        //}
        System.out.println(queryTreeStates.get(0).getTreeStructure());
        primaryStage.setScene(new Scene(new QueryTreeGUI(queryTreeStates.get(0)).getContainer()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}