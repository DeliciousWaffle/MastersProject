package gui.screens.help.components.diagramwindows;

import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class QueryDiagram extends Stage {

    public QueryDiagram() {

        double windowWidth = 1280.0, windowHeight = 720.0;

        // setting the image in the center of the screen
        Image image = new Image("files/images/helpscreen/QueryDiagram.png");
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);

        // query is kind of long, will need to wrap it in a scroll bar in order to see everything
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(imageView);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-font-size: 15px;");

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(scrollPane);

        Scene scene = new Scene(borderPane, windowWidth - 209, windowHeight);

        this.setTitle("Query Diagram");
        this.setResizable(false);
        this.setScene(scene);
        this.show();
    }
}