package gui.screens.terminal.popupwindows.querytreegui.popupwindows;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * When the Query Tree GUI is displayed, user can click on some buttons to display
 * additional information about the type of transformation performed. Pretty much
 * just used for displaying the relational algebra axioms and some other useful junk.
 * Usage: new QueryTreeGUIPopUpWindows.<any of the static classes here>() to launch a new window
 */
public class QueryTreeOptimizationHeuristicWindows {

    private final static String BULLET_POINT = "\u2022", PROJECTION = "\u03C0", SELECTION = "\u03C3", JOIN = "\u2A1D",
            CARTESIAN_PRODUCT = "\u2715", AGGREGATION = "\uD835\uDCA2", LOGICALLY_EQUIVALENT = "\u2261",
            LOGICAL_AND = "\u2227", LOGICAL_OR = "\u2228", RELATION = "\uD835\uDCE1";

    // cant't instantiate me!
    private QueryTreeOptimizationHeuristicWindows() {}

    // all heuristic windows will extend this class, reduces code duplication
    private static class HeuristicWindow extends Stage {

        public HeuristicWindow(Text text, String windowTitle) {

            text.setFont(new Font(25));
            text.setFill(Color.WHITE);
            text.setTextAlignment(TextAlignment.CENTER);

            BorderPane root = new BorderPane();
            root.setCenter(text);
            root.setBackground(new Background(
                    new BackgroundFill(Color.rgb(40, 40, 40), CornerRadii.EMPTY, new Insets(-20))
            ));

            this.setScene(new Scene(root));
            this.setTitle(windowTitle);
            this.setResizable(false);
            this.show();
        }
    }

    public static class InitialState extends HeuristicWindow {

        public InitialState() {

            super(new Text(
                    BULLET_POINT + " Columns referenced in the SELECT clause get transformed into a Projection " +
                        "(" + PROJECTION + ")" + "\n" +
                    BULLET_POINT + " Conditions in WHERE clause get transformed into a Selection " +
                        "(" + SELECTION + ")" + "\n" +
                    BULLET_POINT + " Tables referenced in FROM clause get transformed into sets of Cartesian " +
                        "Products (" + CARTESIAN_PRODUCT + ")" + "\n" +
                    BULLET_POINT + " Aggregate operations in SELECT clause with columns referenced in GROUP BY " +
                        "clause\n get transformed into an Aggregation (" + AGGREGATION + ")" +"\n" +
                    BULLET_POINT + " Conditions in HAVING clause get transformed into an Aggregate Selection " +
                        "(" + SELECTION + ")"
                    ),
                "Initial State Info"
            );
        }
    }

    public static class MovedDownSelections extends HeuristicWindow {

        public MovedDownSelections() {

            super(new Text(
                            BULLET_POINT + " Columns referenced in the SELECT clause get transformed into a Projection " +
                                    "(" + PROJECTION + ")" + "\n" +
                                    BULLET_POINT + " Conditions in WHERE clause get transformed into a Selection " +
                                    "(" + SELECTION + ")" + "\n" +
                                    BULLET_POINT + " Tables referenced in FROM clause get transformed into sets of Cartesian " +
                                    "Products (" + CARTESIAN_PRODUCT + ")" + "\n" +
                                    BULLET_POINT + " Aggregate operations in SELECT clause with columns referenced in GROUP BY " +
                                    "clause\n get transformed into an Aggregation (" + AGGREGATION + ")" +"\n" +
                                    BULLET_POINT + " Conditions in HAVING clause get transformed into an Aggregate Selection " +
                                    "(" + SELECTION + ")"
                    ),
                    "Initial State Info"
            );
        }
    }
}