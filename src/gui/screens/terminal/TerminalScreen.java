package gui.screens.terminal;

import datastructures.rulegraph.RuleGraph;
import files.io.FileType;
import files.io.IO;
import gui.ScreenController;
import gui.screens.Screen;
import gui.screens.terminal.popupwindows.QueryTreeWindow;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import systemcatalog.SystemCatalog;

public class TerminalScreen extends Screen {

    private Scene terminalScreen;

    public TerminalScreen(ScreenController screenController, SystemCatalog systemCatalog) {

        // top row of buttons for switching between screens
        HBox topRowButtonLayout = super.getButtonLayout(screenController);

        // terminal area -----------------------------------------------------------------------------------------------
        BorderPane terminalAreaLayout = new BorderPane();

        Text inputAreaText = new Text("Input Area:");
        inputAreaText.setFont(new Font(40));
        inputAreaText.setFill(Color.WHITE);

        TextArea terminal = new TextArea();
        //terminal.setPrefSize(Screen.defaultWidth - 210, Screen.defaultHeight - 420);
        terminal.setFont(new Font(40));
        terminal.getStylesheets().add(IO.readCSS(FileType.CSS.DARK_TEXT_AREA_STYLE));
        terminal.getStylesheets().add(IO.readCSS(FileType.CSS.DARK_SCROLL_PANE_STYLE));
        terminal.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        terminalAreaLayout.setTop(inputAreaText);
        terminalAreaLayout.setBottom(terminal);

        BorderPane.setAlignment(inputAreaText, Pos.CENTER);
        BorderPane.setMargin(inputAreaText, new Insets(0, 0, 10, 0));

        // output area -------------------------------------------------------------------------------------------------
        BorderPane outputAreaLayout = new BorderPane();

        Text outputAreaText = new Text("Output Area:");
        outputAreaText.setFont(new Font(40));
        outputAreaText.setFill(Color.WHITE);

        TextArea output = new TextArea();
        //output.setPrefSize(Screen.defaultWidth - 205, Screen.defaultHeight - 550);
        output.setMinHeight(0);
        output.setFont(new Font(40));
        output.setEditable(false);
        output.getStylesheets().add(IO.readCSS(FileType.CSS.DARK_TEXT_AREA_STYLE));
        output.getStylesheets().add(IO.readCSS(FileType.CSS.DARK_SCROLL_PANE_STYLE));
        output.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        outputAreaLayout.setTop(outputAreaText);
        outputAreaLayout.setBottom(output);

        BorderPane.setAlignment(outputAreaText, Pos.CENTER);
        BorderPane.setMargin(outputAreaText, new Insets(0, 0, 10, 0));

        // contains both the terminal and output areas -----------------------------------------------------------------
        BorderPane ioAreaLayout = new BorderPane();
        ioAreaLayout.setTop(terminalAreaLayout);
        ioAreaLayout.setBottom(outputAreaLayout);

        // right column of buttons for executing the input, if the input is a query, shows the
        // relational algebra, query tree states, query cost, and recommended file structures
        // -------------------------------------------------------------------------------------------------------------
        VBox rightColumnButtonLayout = new VBox();
        rightColumnButtonLayout.setSpacing(10);
        rightColumnButtonLayout.setBackground(new Background(new BackgroundFill(Color.rgb(50, 50, 50), new CornerRadii(5), Insets.EMPTY)));
        rightColumnButtonLayout.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        String buttonStyle = " -fx-background-color: rgb(100, 100, 100); -fx-text-fill: white;";
        String buttonEnteredStyle = "-fx-background-color: rgb(150, 150, 150); -fx-text-fill: white;";

        // execute button ..............................................................................................
        Button executeButton = new Button();

        ImageView imageView = new ImageView(IO.readAsset(FileType.Asset.PLAY_IMAGE));

        imageView.setFitWidth(125);
        imageView.setFitHeight(100);
        imageView.setSmooth(true);

        executeButton.setGraphic(imageView);
        executeButton.setStyle(buttonStyle);
        executeButton.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        // tell the system catalog to execute the input
        executeButton.setOnAction(e -> {

            // clear what's in the output area first
            output.clear();

            // get the content on the text area up until the first semicolon is reached
            String input = terminal.getText();

            // pass to the system catalog to execute
            if(input.contains(";")) {

                input = input.split(";")[0];
                systemCatalog.executeInput(input);

            } else {
                output.setText("Error! Input must end with a semicolon!");
            }
        });

        executeButton.setOnMouseEntered(e -> executeButton.setStyle(buttonEnteredStyle));
        executeButton.setOnMouseExited(e -> executeButton.setStyle(buttonStyle));

        Tooltip tooltip = new Tooltip("Execute Input");
        tooltip.setFont(new Font(20));
        executeButton.setTooltip(tooltip);

        // relational algebra button ...................................................................................
        Button relationalAlgebraButton = new Button();

        imageView = new ImageView(IO.readAsset(FileType.Asset.X_IMAGE));

        imageView.setFitWidth(125);
        imageView.setFitHeight(100);
        imageView.setSmooth(true);

        relationalAlgebraButton.setGraphic(imageView);
        relationalAlgebraButton.setStyle(buttonStyle);
        relationalAlgebraButton.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        // launch a new window displaying the relational algebra
        relationalAlgebraButton.setOnAction(e -> {
            if(systemCatalog.getInputType() == RuleGraph.Type.QUERY && systemCatalog.getLogger().wasSuccessfullyExecuted()) {
            }
        });

        relationalAlgebraButton.setOnMouseEntered(e -> relationalAlgebraButton.setStyle(buttonEnteredStyle));
        relationalAlgebraButton.setOnMouseExited(e -> relationalAlgebraButton.setStyle(buttonStyle));

        tooltip = new Tooltip("View Corresponding Relational Algebra");
        tooltip.setFont(new Font(20));
        relationalAlgebraButton.setTooltip(tooltip);

        // query tree states button ...................................................................................
        Button queryTreeStatesButton = new Button();

        imageView = new ImageView(IO.readAsset(FileType.Asset.TREE_IMAGE));
        imageView.setFitWidth(125);
        imageView.setFitHeight(100);
        imageView.setSmooth(true);

        queryTreeStatesButton.setGraphic(imageView);
        queryTreeStatesButton.setStyle(buttonStyle);
        queryTreeStatesButton.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        // launch a new window displaying the query tree states
        queryTreeStatesButton.setOnAction(e -> {
            if(systemCatalog.getInputType() == RuleGraph.Type.QUERY && systemCatalog.getLogger().wasSuccessfullyExecuted()) {
                new QueryTreeWindow(systemCatalog.getQueryTreeStates());
            }
        });

        queryTreeStatesButton.setOnMouseEntered(e -> queryTreeStatesButton.setStyle(buttonEnteredStyle));
        queryTreeStatesButton.setOnMouseExited(e -> queryTreeStatesButton.setStyle(buttonStyle));

        tooltip = new Tooltip("View Query Tree States");
        tooltip.setFont(new Font(20));
        queryTreeStatesButton.setTooltip(tooltip);

        // query cost button ...........................................................................................
        Button queryCostButton = new Button();

        imageView = new ImageView(IO.readAsset(FileType.Asset.DOLLAR_SIGN_IMAGE));
        imageView.setFitWidth(125);
        imageView.setFitHeight(100);
        imageView.setSmooth(true);

        queryCostButton.setGraphic(imageView);
        queryCostButton.setStyle(buttonStyle);
        queryCostButton.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        // launch a new window displaying the query cost
        queryCostButton.setOnAction(e -> {
            if(systemCatalog.getInputType() == RuleGraph.Type.QUERY && systemCatalog.getLogger().wasSuccessfullyExecuted()) {
            }
        });

        queryCostButton.setOnMouseEntered(e -> queryCostButton.setStyle(buttonEnteredStyle));
        queryCostButton.setOnMouseExited(e -> queryCostButton.setStyle(buttonStyle));

        tooltip = new Tooltip("View Query Cost");
        tooltip.setFont(new Font(20));
        queryCostButton.setTooltip(tooltip);

        // query cost button ...........................................................................................
        Button recommendedFileStructuresButton = new Button();

        imageView = new ImageView(IO.readAsset(FileType.Asset.FOLDER_IMAGE));
        imageView.setFitWidth(125);
        imageView.setFitHeight(100);
        imageView.setSmooth(true);

        recommendedFileStructuresButton.setGraphic(imageView);
        recommendedFileStructuresButton.setStyle(buttonStyle);
        recommendedFileStructuresButton.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        // launch a new window displaying the recommended file structures
        recommendedFileStructuresButton.setOnAction(e -> {
            if(systemCatalog.getInputType() == RuleGraph.Type.QUERY && systemCatalog.getLogger().wasSuccessfullyExecuted()) {
            }
        });

        recommendedFileStructuresButton.setOnMouseEntered(e -> recommendedFileStructuresButton.setStyle(buttonEnteredStyle));
        recommendedFileStructuresButton.setOnMouseExited(e -> recommendedFileStructuresButton.setStyle(buttonStyle));

        tooltip = new Tooltip("View Recommended File Structures");
        tooltip.setFont(new Font(20));
        recommendedFileStructuresButton.setTooltip(tooltip);

        rightColumnButtonLayout.getChildren().addAll(
                executeButton, relationalAlgebraButton, queryTreeStatesButton,
                queryCostButton, recommendedFileStructuresButton
        );

        VBox.setMargin(executeButton, new Insets(10, 10, 0, 10));
        VBox.setMargin(relationalAlgebraButton, new Insets(0, 10, 0, 10));
        VBox.setMargin(queryTreeStatesButton, new Insets(0, 10, 0, 10));
        VBox.setMargin(queryCostButton, new Insets(0, 10, 0, 10));
        VBox.setMargin(recommendedFileStructuresButton, new Insets(0, 10, 10, 10));

        // layout container for everything
        BorderPane terminalScreenLayout = new BorderPane();
        terminalScreenLayout.setTop(topRowButtonLayout);
        terminalScreenLayout.setLeft(ioAreaLayout);
        terminalScreenLayout.setRight(rightColumnButtonLayout);

        BorderPane.setMargin(ioAreaLayout, new Insets(0, 5, 10, 10));
        BorderPane.setMargin(rightColumnButtonLayout, new Insets(0, 10, 10, 5));

        terminalScreenLayout.setBackground(new Background(new BackgroundFill(Color.rgb(30, 30, 30), CornerRadii.EMPTY, Insets.EMPTY)));

        this.terminalScreen = new Scene(terminalScreenLayout);

        // adjust components when the screen is resized ----------------------------------------------------------------
        terminalScreen.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = (double) newValue;
            topRowButtonLayout.setMaxWidth(newWidth);
            super.adjustButtonWidth(newWidth);
            terminal.setPrefWidth(newWidth - 190);
            output.setPrefWidth(newWidth - 190);

        });

        terminalScreen.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newHeight = (double) newValue;
            terminal.setMaxHeight(newHeight - 400);
            output.setMaxHeight(newHeight - 500);
        });
    }

    @Override
    public Scene getScreen() {
        return terminalScreen;
    }
}