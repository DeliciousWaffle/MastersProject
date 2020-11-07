package gui.screens.tables.components;

import files.io.FileType;
import files.io.IO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.List;

public class KeyWindow extends Stage {

    public KeyWindow(String keyType, List<String> keys) {

        // create the top part of the screen
        Text keyText = new Text(keyType);
        keyText.setFont(new Font(100.0));
        keyText.setFill(Color.WHITE);
        keyText.setSmooth(true);

        Text keysText = new Text();
        StringBuilder formattedKeys = new StringBuilder();
        keys.forEach(key -> formattedKeys.append(key).append(",\n"));
        if (keys.isEmpty()) {
            formattedKeys.append("No Keys Exist");
        } else {
            formattedKeys.delete(formattedKeys.length() - 2, formattedKeys.length());
        }
        keysText.setText(formattedKeys.toString());
        keysText.setFont(new Font("Ariel", 75.0));
        keysText.setFill(Color.WHITE);
        keysText.setSmooth(true);

        BorderPane container = new BorderPane();
        container.setTop(keyText);
        BorderPane.setAlignment(keyText, Pos.CENTER);
        container.setCenter(keysText);
        BorderPane.setMargin(keysText, new Insets(30, 0, 0, 0));
        BorderPane.setAlignment(keysText, Pos.TOP_CENTER);
        container.setPrefSize(1080, 720);
        container.setStyle("-fx-background-color: rgb(30, 30, 30);");

        // allow the user to scroll around the image
        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStylesheets().add(IO.readCSS(FileType.CSS.SCROLL_PANE_STYLE));
        scrollPane.setHvalue(0.5);

        Scene scene = new Scene(scrollPane);
        scene.setFill(Color.rgb(30, 30, 30));

        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = (double) newValue;
            container.setPrefWidth(newWidth);
        });

        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newHeight = (double) newValue;
            container.setPrefHeight(newHeight);
        });

        this.setTitle(keyType);
        this.setScene(scene);
        this.show();
    }
}