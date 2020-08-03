package gui.current.scenes.help;

import gui.current.scenes.help.popupwindows.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HelpScene extends Application {

    private double startingWindowWidth, startingWindowHeight;
    private double buttonWidth, buttonHeight;
    private Text queryText, createTableText, alterTableText, dropTableText, insertText, deleteText,
    updateText, grantText, revokeText, buildFileStructureText, removeFileStructureText;
    private Button queryButton, createTableButton, alterTableButton, dropTableButton, insertButton,
    deleteButton, updateButton, grantButton, revokeButton, buildFileStructureButton, removeFileStructureButton;
    // containers

    @Override
    public void start(Stage primaryStage) {

        startingWindowWidth = 1280.0;
        startingWindowHeight = 720.0;

        // adding each container to the vBox so that containers are laid out vertically
        VBox vBox = new VBox(
                10.0,
                getQueryBox()

        );

        vBox.setPrefWidth(startingWindowWidth - 200);

        // adding the vBox to the scroll pane so that we can scroll through everything
        //ScrollPane scrollPane = new ScrollPane();
        //scrollPane.setContent(vBox);
        Scene scene = new Scene(vBox, startingWindowWidth, startingWindowHeight);
        primaryStage.setScene(scene);
        primaryStage.show();
        //new AlterTableDiagram();
        //new BuildFileStructureDiagram();
        //new CreateTableDiagram();
        //new DeleteDiagram();
        //new DropTableDiagram();
        //new GrantDiagram();
        //new InsertDiagram();
        //new QueryDiagram();
        //new RemoveFileStructureDiagram();
        //new RevokeDiagram();
        //new UpdateDiagram();
        //new FirstStage();
    }

    private BorderPane getQueryBox() {

        queryButton = new Button("Query Diagram");
        queryButton.setOnAction(e -> {
            new QueryDiagram();
        });

        /*BorderPane padding = new BorderPane();
        padding.setPrefWidth(buttonWidth);
        padding.setPrefHeight(200);
        padding.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        padding.setCenter(queryButton);*/

        /*BorderPane centerButton = new BorderPane();
        centerButton.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        centerButton.setCenter(queryButton);

        queryText = new Text("This is some text");

        BorderPane container = new BorderPane();
        container.setTop(queryText);
        container.setBottom(centerButton);*/

        return null;
    }

    private void setUpButtons() {



        createTableButton = new Button("Create Table");
        createTableButton.setOnAction(e -> {
            new CreateTableDiagram();
        });

        alterTableButton = new Button("Alter Table");
        alterTableButton.setOnAction(e -> {
            new AlterTableDiagram();
        });

        dropTableButton = new Button("Drop Table");
        dropTableButton.setOnAction(e -> {
            new DropTableDiagram();
        });

        insertButton = new Button("Insert");
        insertButton.setOnAction(e -> {
            new InsertDiagram();
        });

        deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            new DeleteDiagram();
        });

        updateButton = new Button("Update");
        updateButton.setOnAction(e -> {
            new UpdateDiagram();
        });

        grantButton = new Button("Grant");
        grantButton.setOnAction(e -> {
            new GrantDiagram();
        });

        revokeButton = new Button("Revoke");
        revokeButton.setOnAction(e -> {
            new RevokeDiagram();
        });

        buildFileStructureButton = new Button("Build File Structure");
        buildFileStructureButton.setOnAction(e -> {
            new BuildFileStructureDiagram();
        });

        removeFileStructureButton = new Button("Remove File Structure");
        removeFileStructureButton.setOnAction(e -> {
            new RemoveFileStructureDiagram();
        });
    }


    /*public class FirstStage extends Stage{
        Button openOther = new Button("Open other Stage");
        HBox x = new HBox();

        FirstStage(){
            x.getChildren().add(openOther);
            this.setScene(new Scene(x, 300, 300));
            this.show();

            openOther.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent t) {
                    new SecondStage();
                }//end action
            });
        }
    }

    public class SecondStage extends Stage {
        Label x = new Label("Second stage");
        VBox y = new VBox();

        SecondStage(){
            y.getChildren().add(x);
            this.setScene(new Scene(y, 300, 300));
            this.show();
        }
    }*/

    public static void main(String[] args) {
        launch(args);
    }
}
