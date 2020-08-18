package gui.screens.terminal.popupwindows.querytreegui.popupwindows;

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

    private final static String AGGREGATION = "\uD835\uDCA2",
            R = "\uD835\uDCE1", N = "\uD835\uDC5B", L = "\uD835\uDC59", C = "\uD835\uDC50";

    // cant't instantiate me!
    private QueryTreeOptimizationHeuristicWindows() {}

    // all heuristic windows will extend this class, reduces code duplication
    private static class HeuristicWindow extends Stage {

        public HeuristicWindow(Text text, String windowTitle) {

            text.setFont(new Font(35));
            text.setFill(Color.WHITE);
            text.setTextAlignment(TextAlignment.CENTER);

            BorderPane root = new BorderPane();
            root.setCenter(text);
            BorderPane.setMargin(text, new Insets(20));
            root.setBackground(new Background(
                    new BackgroundFill(Color.rgb(40, 40, 40), CornerRadii.EMPTY, Insets.EMPTY)
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
                    "• Columns referenced in the SELECT clause get transformed into a Projection (π)\n" +
                    "• Conditions in WHERE clause get transformed into a Selection (σ)\n" +
                    "• Tables referenced in FROM clause get transformed into sets of Cartesian " +
                        "Products (✕)\n" +
                    "• Aggregate operations in SELECT clause with columns referenced in GROUP BY " +
                        "clause\n get transformed into an Aggregation (" + AGGREGATION + ")" +"\n" +
                    "• Conditions in HAVING clause get transformed into an Aggregate Selection (σ)"
                    ),
                "Initial State Info"
            );
        }
    }

    public static class CascadedSelections extends HeuristicWindow {
        public CascadedSelections() {
            super(new Text(
                    "Relational Algebra Axiom Used:\n" +
                    "• Cascade of Selection:\n" +
                    "σ (" + C + "₁∧" + C + "₂∧...∧" + C + N + " (\uD835\uDCE1)) ≡ σ(" + C + "₁) " + "[σ(" + C + "₂) ... [σ(" + C + N + ") [" + R + "]]]" + "\n" +
                    "Where " + C + "is some condition and \uD835\uDCE1 is some Relation"
                ),
                "Cascaded Selections Info"
            );
        }
    }

    public static class MovedDownSelections extends HeuristicWindow {
        public MovedDownSelections() {
            super(new Text(
                    "Relational Algebra Axioms Used:\n" +
                    "• Commutativity of Selection:\n" +
                    "σ(" + C + "₁) [σ(" + C + "₂) [" + R + "]] ≡ σ(" + C + "₂) [σ(" + C + "₁) [" + R + "]]\n" +
                    "• Commutativity of Selection and Projection:\n" +
                    "π(" + L + "₁) [σ(" +  C + "₁) [" + R + "]] ≡ σ(" + C + "₁) [π(" + L + "₁) [" + R + "]]\n" +
                    "• Commutativity of Selection and Join/Cartesian Product:\n" +
                            "σ(" + C + "₁) [" + R + "₁ ⨝ " + R + "₂] ≡ [σ(" + C + "₁) [" + R + "₁ ⨝ " + R + "₂] \n" +
                            "Where " + C + " is some condition, " + R + " is some relation, and " + L + " is a list of columns"
            ), "Moved Down Selections Info");
        }
    }

    public static class FormedJoins extends HeuristicWindow {
        public FormedJoins() {
            super(new Text(
                    "Combination of a Selection (σ) containing a join criteria as a\ncondition and a Cartesian Product (✕) to form a Join (⨝)"
            ), "Formed Joins Info");
        }
    }

    public static class RearrangedJoins extends HeuristicWindow {
        public RearrangedJoins() {
            super(new Text(
                    "Rearrange the ordering of Joins such that smaller subtrees are executed first.\n" +
                    "This reduces the cost of storing data in intermediary steps,\nthus, reducing overall Write To Disk Cost.\n" +
                    "Relational Algebra Axiom Used:\n" +
                    "• Commutativity of Joins:\n" +
                    R + "₁ ⨝ " + R + "₂ ≡ " + R + "₂ ⨝ " + R + "₁\n" +
                    "Where " + R + " is some Relation"
            ), "Rearranged Joins Info");
        }
    }

    public static class MovedDownProjections extends HeuristicWindow {
        public MovedDownProjections() {
            super(new Text(
                    "Relational Algebra Axioms Used:\n" +
                    "• Cascade Of Projection:\n" +
                    "π(" + L + "₁) [π(" + L + "₂) ... [π(" + L + N + ") [" + R + "]]] ≡ π ("+ L + "₁) [" + R + "]\n" +
                    "• Commutativity of Selection and Projection:\n" +
                    "π(" + L + "₁) [σ(" + C + "₁) [" + R + "]] ≡ σ(" + C + "₁) [π(" + L + "₁) [" + R + "]]\n" +
                            "Where " + C + " is some condition, " + R + " is some Relation,\nand " + L + " is some List of Columns"
            ), "Moved Down Projections Info");
        }
    }

    public static class PipeliningSubtrees extends HeuristicWindow {
        public PipeliningSubtrees() {
            super(new Text(
                    "The result of executing a single node within the Query Tree doesn't\n"+
                    "need to be written out to disk and be reaccessed later. Instead, some\n"+ "" +
                    "operations can be executed all in one step, reducing Write To Disk Cost.\n"+ "" +
                    "This step can be repeated multiple times until only a single Pipelined expression exists."
            ), "Pipelining Subtrees Info");
        }
    }
}