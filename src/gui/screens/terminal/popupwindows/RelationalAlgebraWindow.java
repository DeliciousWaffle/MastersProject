package gui.screens.terminal.popupwindows;

import files.io.FileType;
import files.io.IO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * After successful execution of a query, this window displays the naive relational algebra and
 * optimized relational algebra.
 */
public class RelationalAlgebraWindow extends Stage {

    public RelationalAlgebraWindow(String naiveRelationalAlgebra, String optimizedRelationalAlgebra) {

        // vBox container to hold the title, naive relational algebra, and optimized relational algebra
        VBox overallContainer = new VBox();


        // title text
        Text titleText = new Text("Relational Algebra");
        titleText.setFont(new Font(100.0));
        titleText.setFill(Color.WHITE);
        titleText.setSmooth(true);

        overallContainer.getChildren().add(titleText);


        // naive relational algebra area
        BorderPane naiveRelationalAlgebraArea = new BorderPane();
        naiveRelationalAlgebraArea.setBackground(new Background(
                new BackgroundFill(Color.rgb(60, 60, 60), new CornerRadii(5), Insets.EMPTY))); // TODO change insets
        naiveRelationalAlgebraArea.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        Text naiveRelationalAlgebraText = new Text("Unoptimized Relational Algebra");
        naiveRelationalAlgebraText.setFont(new Font(75.0));
        naiveRelationalAlgebraText.setFill(Color.WHITE);
        naiveRelationalAlgebraText.setSmooth(true);

        naiveRelationalAlgebraArea.setCenter(naiveRelationalAlgebraText);

        overallContainer.getChildren().add(naiveRelationalAlgebraArea);


        // optimized relational algebra area
        BorderPane optimizedRelationalAlgebraArea = new BorderPane();
        optimizedRelationalAlgebraArea.setBackground(new Background(
                new BackgroundFill(Color.rgb(60, 60, 60), new CornerRadii(5), Insets.EMPTY))); // TODO change insets
        optimizedRelationalAlgebraArea.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        Text optimizedRelationalAlgebraText = new Text("Optimized Relational Algebra");
        titleText.setFont(new Font(75.0));
        titleText.setFill(Color.WHITE);
        titleText.setSmooth(true);

        optimizedRelationalAlgebraArea.setCenter(optimizedRelationalAlgebraText);

        overallContainer.getChildren().add(optimizedRelationalAlgebraArea);


        // scroll pane to hold the vBox container
        ScrollPane scrollPane = new ScrollPane(overallContainer);
        scrollPane.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_SCROLL_PANE_STYLE));
        scrollPane.setHvalue(0.5);


        // add the scroll pane to the scene
        Scene scene = new Scene(scrollPane);

        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = (double) newValue;
            overallContainer.setPrefWidth(newWidth);
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newHeight = (double) newValue;
            overallContainer.setPrefHeight(newHeight);
        });

        this.setTitle("Relational Algebra");
        this.setScene(scene);
        this.show();
    }
}