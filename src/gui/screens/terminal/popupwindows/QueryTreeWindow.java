package gui.screens.terminal.popupwindows;

import datastructures.relation.table.Table;
import datastructures.rulegraph.types.RuleGraphTypes;
import datastructures.trees.querytree.QueryTree;
import files.io.FileType;
import files.io.IO;
import files.io.Serialize;
import gui.screens.terminal.popupwindows.querytreegui.QueryTreeGUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import systemcatalog.components.Optimizer;
import systemcatalog.components.Parser;
import utilities.enums.InputType;

import java.util.Arrays;
import java.util.List;

public class QueryTreeWindow extends Application {

    @Override
    public void start(Stage primaryStage) {

        List<Table> tables = Serialize.unSerializeTables(IO.readCurrentData(FileType.CurrentData.CURRENT_TABLES));
        String blah = "select customername from Customers join Shippers using(col2) where customername = AlfredsFutterkiste;";
        //String blah = "select customername from Customers where customername = AlfredsFutterkiste";
        //String blah = "select customername from Customers join Shippers using(col2) join Orders using(col3) where customername = AlfredsFutterkiste";
        Optimizer o = new Optimizer();
        String[] tokens = new Parser().formatAndTokenizeInput(blah);
        System.out.println(Arrays.toString(tokens));
        QueryTree qr = (o.formJoins(o.cascadeAndPushDownProjections(o.pushDownSelections(o.cascadeSelections(o.createQueryTree(new RuleGraphTypes().getQueryRuleGraph(), tokens, tables))))));
        System.out.println(qr.getTreeStructure());
        primaryStage.setScene(new Scene(new QueryTreeGUI(qr).getGridPane()));
        primaryStage.show();
    }

    public static void main(String... args) {
        launch(args);
    }
}