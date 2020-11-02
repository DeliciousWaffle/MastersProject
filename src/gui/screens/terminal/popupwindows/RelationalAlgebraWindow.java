package gui.screens.terminal.popupwindows;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * After successful execution of a query, this window displays the naive relational algebra and
 * optimized relational algebra.
 */
public class RelationalAlgebraWindow extends Stage {

    public RelationalAlgebraWindow(String naiveRelationalAlgebra, String optimizedRelationalAlgebra) {

        BorderPane root = new BorderPane();



        this.setTitle("Relational Algebra");
        this.setScene(new Scene(root));
        this.show();
    }
}