package gui.screens.alertboxes;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;

public class ConfirmationBox {

    private boolean confirmation;

    public ConfirmationBox() {

        Stage window = new Stage();
        window.setTitle("Confirmation Window");
        window.initModality(Modality.APPLICATION_MODAL);
        window.setWidth(400);
        window.setHeight(200);
        window.setResizable(false);

        Label text = new Label("Are you sure about that?");
        text.setFont(new Font(30));

        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");

        yesButton.setPrefWidth(100);
        yesButton.setPrefHeight(35);
        noButton.setPrefWidth(100);
        noButton.setPrefHeight(35);

        yesButton.setOnAction(e -> {
            confirmation = true;
            window.close();
        });

        noButton.setOnAction(e -> {
            confirmation = false;
            window.close();
        });

        VBox vBox = new VBox();
        vBox.getChildren().add(text);
        vBox.setAlignment(Pos.CENTER);

        HBox hBox = new HBox(50);
        hBox.getChildren().addAll(yesButton, noButton);
        hBox.setAlignment(Pos.CENTER);

        VBox vBox1 = new VBox(40);
        vBox1.getChildren().addAll(vBox, hBox);
        vBox1.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vBox1);
        vBox1.setStyle("-fx-background-color: #778899;");
        window.setScene(scene);
        window.showAndWait();
    }

    public boolean getChoice() { return confirmation; }
}
