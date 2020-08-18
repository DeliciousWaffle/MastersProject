package gui.screens.terminal;

import files.io.FileType;
import files.io.IO;
import gui.ScreenController;
import gui.screens.Screen;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import systemcatalog.SystemCatalog;

public class TerminalScreen extends Screen {

    private Scene terminalScreen;

    public TerminalScreen(ScreenController screenController, SystemCatalog systemCatalog) {

        // top row of buttons for switching between screens
        HBox topRowButtonLayout = super.getButtonLayout(screenController);

        // terminal area, grows/shrinks if window width changes
        TextArea terminal = new TextArea();

        // output area, grows/shrinks if window width changes
        TextArea output = new TextArea();

        // right column of buttons for executing the input, if the input is a query, shows the
        // relational algebra, query tree states, query cost, and recommended file structures
        VBox rightColumnButtonLayout = new VBox();

        String buttonStyle = " -fx-background-color: rgb(100, 100, 100); -fx-text-fill: white; -fx-padding: 0;";
        String buttonEnteredStyle = "-fx-background-color: rgb(150, 150, 150); -fx-text-fill: white; -fx-border-color: transparent;";

        // execute button ..............................................................................................
        Button executeButton = new Button();

        ImageView imageView = new ImageView(IO.readAsset(FileType.Asset.PLAY_IMAGE));
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setSmooth(true);

        executeButton.setGraphic(imageView);
        executeButton.setStyle(buttonStyle);
        executeButton.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        // tell the system catalog to execute the input
        executeButton.setOnAction(e -> {

        });

        executeButton.setOnMouseEntered(e -> executeButton.setStyle(buttonEnteredStyle));
        executeButton.setOnMouseExited(e -> executeButton.setStyle(buttonStyle));

        Tooltip tooltip = new Tooltip("Execute Input");
        tooltip.setFont(new Font(20));
        executeButton.setTooltip(tooltip);

        // relational algebra button ...................................................................................
        Button relationalAlgebraButton = new Button();
        String fancyX = "\uD835\uDC65";
        relationalAlgebraButton.setText(fancyX);
        relationalAlgebraButton.setFont(new Font(20));
        relationalAlgebraButton.setStyle(buttonStyle);
        relationalAlgebraButton.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        // launch a new window displaying the relational algebra
        relationalAlgebraButton.setOnAction(e -> {

        });

        relationalAlgebraButton.setOnMouseEntered(e -> relationalAlgebraButton.setStyle(buttonEnteredStyle));
        relationalAlgebraButton.setOnMouseExited(e -> relationalAlgebraButton.setStyle(buttonStyle));

        tooltip = new Tooltip("View Corresponding Relational Algebra");
        tooltip.setFont(new Font(20));
        relationalAlgebraButton.setTooltip(tooltip);

        // query tree states button ...................................................................................
        Button queryTreeStatesButton = new Button();

        imageView = new ImageView(IO.readAsset(FileType.Asset.TREE_IMAGE));
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setSmooth(true);

        executeButton.setGraphic(imageView);
        executeButton.setStyle(buttonStyle);
        executeButton.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        // launch a new window displaying the query tree states
        executeButton.setOnAction(e -> {

        });

        executeButton.setOnMouseEntered(e -> executeButton.setStyle(buttonEnteredStyle));
        executeButton.setOnMouseExited(e -> executeButton.setStyle(buttonStyle));

        tooltip = new Tooltip("Execute Input");
        tooltip.setFont(new Font(20));
        executeButton.setTooltip(tooltip);

        // layout container for everything

        //TextArea terminal = getTerminal();
        //TextArea output = getOutput();


        BorderPane ioAreaLayout = getIOAreaLayout(terminal, output);
        //VBox rightColumnButtonLayout = getRightColumnButtonLayout();

        BorderPane terminalScreenLayout =
                getTerminalScreenLayout(topRowButtonLayout, ioAreaLayout, rightColumnButtonLayout);

        this.terminalScreen = new Scene(terminalScreenLayout);
    }

    // returns the terminal area
    public TextArea getTerminal() {
        return new TextArea();
    }

    // returns the output area
    public TextArea getOutput() {
        return new TextArea();
    }

    // returns a layout for the terminal and output areas
    public BorderPane getIOAreaLayout(TextArea terminal, TextArea output) {
        return null;
    }

    // returns a layout containing the the buttons on the right side of the screen for executing the input,
    // viewing relational algebra, query tree states, query cost, and recommended file structures
    private VBox getRightColumnButtonLayout() {
        return null;
    }

    // contains all components for the terminal screen
    public BorderPane getTerminalScreenLayout(HBox topRowButtonLayout, BorderPane terminalAndOutputAreaLayout, VBox rightColumnButtonLayout) {

        BorderPane terminalScreenLayout = new BorderPane();

        terminalScreenLayout.setTop(topRowButtonLayout);
        terminalScreenLayout.setBottom(terminalAndOutputAreaLayout);
        terminalScreenLayout.setRight(rightColumnButtonLayout);

        terminalScreenLayout.setPrefSize(Screen.defaultWidth, Screen.defaultHeight);
        terminalScreenLayout.setStyle("-fx-background-color: rgb(30, 30, 30);");

        return terminalScreenLayout;
    }

    @Override
    public Scene getScreen() {
        return terminalScreen;
    }
}