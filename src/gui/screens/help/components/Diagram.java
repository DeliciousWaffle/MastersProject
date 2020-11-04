package gui.screens.help.components;

import files.io.FileType;
import files.io.IO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.stage.Stage;

// diagrams will just extend this to to avoid repeating code
public class Diagram extends Stage {

    public enum Type {
        ER_DIAGRAM, SCHEMA, QUERY, CREATE_TABLE, ALTER_TABLE, DROP_TABLE, INSERT, UPDATE, DELETE, GRANT, REVOKE,
        BUILD_FILE_STRUCTURE, REMOVE_FILE_STRUCTURE
    }

    public Diagram(String diagramTitle, String diagramFilename) {

        // setting the title
        Text text = new Text(diagramTitle);
        text.setFont(new Font("Ariel", 100.0));
        text.setSmooth(true);

        // centering the text
        BorderPane centerText = new BorderPane();
        centerText.setCenter(text);

        // setting the image in the center of the screen
        Image image = new Image("files/images/helpscreen/" + diagramFilename);
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);

        // used to give the image view extra whitespace padding
        BorderPane imageWhiteSpacePadding = new BorderPane();
        imageWhiteSpacePadding.setPrefWidth(1080);
        imageWhiteSpacePadding.setPrefHeight(720);
        imageWhiteSpacePadding.setCenter(imageView);
        imageWhiteSpacePadding.setBackground(
                new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        // used to contain the title and image
        BorderPane containsEverything = new BorderPane();
        containsEverything.setTop(centerText);
        BorderPane.setMargin(centerText, new Insets(0, 0, 50, 0));
        containsEverything.setCenter(imageWhiteSpacePadding);
        containsEverything.setStyle("-fx-background-color: white;");

        // allow the user to scroll around the image
        ScrollPane scrollPane = new ScrollPane(containsEverything);
        scrollPane.getStylesheets().add(IO.readCSS(FileType.CSS.DARK_SCROLL_PANE_STYLE));

        Scene scene = new Scene(scrollPane, 1080, 720);

        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = (double) newValue;
            containsEverything.setPrefWidth(newWidth);
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newHeight = (double) newValue;
            containsEverything.setPrefHeight(newHeight);
        });

        this.setTitle(diagramTitle);
        this.setScene(scene);
        this.show();
    }
}