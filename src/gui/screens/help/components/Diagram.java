package gui.screens.help.components;

import javafx.geometry.Insets;
import javafx.scene.Scene;
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
        SCHEMA, QUERY, CREATE_TABLE, ALTER_TABLE, DROP_TABLE, INSERT, UPDATE, DELETE, GRANT, REVOKE,
        BUILD_FILE_STRUCTURE, REMOVE_FILE_STRUCTURE
    }

    public Diagram(String diagramTitle, String diagramFilename, double windowWidth, double windowHeight) {

        // setting the title
        Text text = new Text(diagramTitle);
        text.setFont(new Font("Ariel", 50.0));
        text.setFontSmoothingType(FontSmoothingType.LCD);

        // centering the text
        BorderPane centerText = new BorderPane();
        centerText.setCenter(text);

        // setting the image in the center of the screen
        Image image = new Image("files/assets/helpscreen/" + diagramFilename);
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);

        // used to give the image view extra whitespace padding
        BorderPane imageWhiteSpacePadding = new BorderPane();
        imageWhiteSpacePadding.setPrefWidth(windowWidth);
        imageWhiteSpacePadding.setPrefHeight(windowHeight);
        imageWhiteSpacePadding.setCenter(imageView);
        imageWhiteSpacePadding.setBackground(
                new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        // used to contain the title and image
        BorderPane containsEverything = new BorderPane();
        containsEverything.setTop(centerText);
        containsEverything.setCenter(imageWhiteSpacePadding);

        Scene scene = new Scene(containsEverything, windowWidth, windowHeight);

        this.setTitle(diagramTitle);
        this.setScene(scene);
        this.setResizable(false);
        this.show();
    }
}