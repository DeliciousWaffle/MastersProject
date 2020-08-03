package gui.old.screens.alertboxes;

public class AddUserBox {

    /*private String userName;

    public AddUserBox() {

        Stage window = new Stage();
        window.setTitle("Add User Window");
        window.initModality(Modality.APPLICATION_MODAL);
        window.setWidth(600);
        window.setHeight(200);
        window.setResizable(false);

        Label text = new Label("User's Name:");
        text.setFont(new Font(25));

        Button addUserButton = new Button("Add User");
        Button cancelButton = new Button("Cancel");

        addUserButton.setPrefWidth(100);
        addUserButton.setPrefHeight(35);
        cancelButton.setPrefWidth(100);
        cancelButton.setPrefHeight(35);

        TextField textField = new TextField();
        textField.setFont(new Font(25));

        addUserButton.setOnAction(e -> {
            userName = textField.getText();
            UserIO.writeUserData(userName, new ArrayList<String>());
            window.close();
        });

        cancelButton.setOnAction(e -> {
            window.close();
        });

        HBox hBox1 = new HBox(40);
        hBox1.getChildren().addAll(text, textField);
        hBox1.setAlignment(Pos.CENTER);

        HBox hBox2 = new HBox(50);
        hBox2.getChildren().addAll(addUserButton, cancelButton);
        hBox2.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(35);
        vBox.getChildren().addAll(hBox1, hBox2);
        vBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vBox);
        vBox.setStyle("-fx-background-color: #778899;");
        window.setScene(scene);
        window.showAndWait();
    }*/
}
