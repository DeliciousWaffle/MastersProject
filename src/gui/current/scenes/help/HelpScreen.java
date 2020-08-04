package gui.current.scenes.help;

import gui.current.ScreenController;
import gui.current.scenes.Screen;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class HelpScreen extends Screen {

    private Scene helpScreen;

    public HelpScreen(ScreenController screenController) {

        // button layout
        HBox buttonLayout = super.getButtonLayout(screenController);

        // content layout

        helpScreen = new Scene(buttonLayout);
    }

    @Override
    public Scene getScreen() {
        return helpScreen;
    }

    /*private void setUpButtons() {

        queryButton = new Button("Query Diagram");
        queryButton.setOnAction(e -> {
            new QueryDiagram();
        });

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
    }*/
}