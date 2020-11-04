package gui.screens.terminal.popupwindows;

import files.io.FileType;
import files.io.IO;
import gui.screens.Screen;
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
        VBox overallContainer = new VBox(30.0);
        overallContainer.setPrefSize(Screen.defaultWidth, Screen.defaultHeight);
        overallContainer.setAlignment(Pos.TOP_CENTER);
        overallContainer.setPadding(new Insets(0, 30, 0, 30));
        overallContainer.setBackground(new Background(
                new BackgroundFill(Color.rgb(30, 30, 30), CornerRadii.EMPTY, Insets.EMPTY)));


        // title text
        Text titleText = new Text("Relational Algebra");
        titleText.setFont(new Font(75.0));
        titleText.setFill(Color.WHITE);
        titleText.setSmooth(true);

        overallContainer.getChildren().add(titleText);


        // naive relational algebra area
        BorderPane naiveRelationalAlgebraArea = new BorderPane();
        naiveRelationalAlgebraArea.setBackground(new Background(new BackgroundFill(
                Color.rgb(60, 60, 60), new CornerRadii(5), Insets.EMPTY)));
        naiveRelationalAlgebraArea.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        Text unoptimizedTitleText = new Text("Unoptimized:");
        unoptimizedTitleText.setFont(new Font(50.0));
        unoptimizedTitleText.setFill(Color.WHITE);
        unoptimizedTitleText.setSmooth(true);

        BorderPane.setAlignment(unoptimizedTitleText, Pos.CENTER);
        naiveRelationalAlgebraArea.setTop(unoptimizedTitleText);

        Text naiveRelationalAlgebraText = new Text(naiveRelationalAlgebra);
        naiveRelationalAlgebraText.setFont(new Font(35.0));
        naiveRelationalAlgebraText.setFill(Color.WHITE);
        naiveRelationalAlgebraText.setSmooth(true);

        BorderPane.setAlignment(naiveRelationalAlgebraText, Pos.CENTER);
        BorderPane.setMargin(naiveRelationalAlgebraText, new Insets(15));
        naiveRelationalAlgebraArea.setBottom(naiveRelationalAlgebraText);

        overallContainer.getChildren().add(naiveRelationalAlgebraArea);


        // optimized relational algebra area
        BorderPane optimizedRelationalAlgebraArea = new BorderPane();
        optimizedRelationalAlgebraArea.setBackground(new Background(new BackgroundFill(
                Color.rgb(60, 60, 60), new CornerRadii(5), Insets.EMPTY)));
        optimizedRelationalAlgebraArea.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        Text optimizedTitleText = new Text("Optimized:");
        optimizedTitleText.setFont(new Font(50.0));
        optimizedTitleText.setFill(Color.WHITE);
        optimizedTitleText.setSmooth(true);

        BorderPane.setAlignment(optimizedTitleText, Pos.CENTER);
        optimizedRelationalAlgebraArea.setTop(optimizedTitleText);

        Text optimizedRelationalAlgebraText = new Text(optimizedRelationalAlgebra);
        optimizedRelationalAlgebraText.setFont(new Font(35.0));
        optimizedRelationalAlgebraText.setFill(Color.WHITE);
        optimizedRelationalAlgebraText.setSmooth(true);

        BorderPane.setAlignment(optimizedRelationalAlgebraText, Pos.CENTER);
        BorderPane.setMargin(optimizedRelationalAlgebraText, new Insets(15));
        optimizedRelationalAlgebraArea.setBottom(optimizedRelationalAlgebraText);

        overallContainer.getChildren().add(optimizedRelationalAlgebraArea);


        // scroll pane to hold the vBox container
        ScrollPane scrollPane = new ScrollPane(overallContainer);
        scrollPane.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_SCROLL_PANE_STYLE));
        scrollPane.setHvalue(0.5);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);


        // add the scroll pane to the scene
        Scene scene = new Scene(scrollPane);
        scene.setFill(Color.rgb(30, 30, 30));

        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = (double) newValue;
            overallContainer.setPrefWidth(newWidth - 22);
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newHeight = (double) newValue;
            overallContainer.setPrefHeight(newHeight - 22);
        });

        this.setTitle("Relational Algebra");
        this.setScene(scene);
        this.show();
    }
}