package gui.screens.terminal;

import datastructures.misc.Pair;
import datastructures.misc.Quadruple;
import datastructures.misc.Triple;
import datastructures.querytree.QueryTree;
import datastructures.relation.resultset.ResultSet;
import enums.InputType;
import files.io.FileType;
import files.io.IO;
import gui.ScreenController;
import gui.screens.Screen;
import gui.screens.terminal.popupwindows.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import systemcatalog.SystemCatalog;

import java.util.List;

public class TerminalScreen extends Screen {

    private Scene terminalScreen;

    private int ICON_WIDTH = 120, ICON_HEIGHT = 66;

    public TerminalScreen(ScreenController screenController, SystemCatalog systemCatalog) {

        // top row of buttons for switching between screens
        HBox topRowButtonLayout = super.getButtonLayout(screenController);

        // terminal area -----------------------------------------------------------------------------------------------
        BorderPane terminalAreaLayout = new BorderPane();

        Text inputAreaText = new Text("Input Area:");
        inputAreaText.setFont(new Font(35));
        inputAreaText.setFill(Color.WHITE);

        TextArea terminal = new TextArea();
        terminal.setMaxSize(877.5, 287.5);
        terminal.setFont(new Font(35));
        terminal.getStylesheets().add(IO.readCSS(FileType.CSS.TEXT_AREA_STYLE));
        terminal.getStylesheets().add(IO.readCSS(FileType.CSS.SCROLL_PANE_STYLE));
        terminal.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        terminalAreaLayout.setTop(inputAreaText);
        terminalAreaLayout.setBottom(terminal);

        BorderPane.setAlignment(inputAreaText, Pos.CENTER);
        BorderPane.setMargin(inputAreaText, new Insets(0, 0, 10, 0));

        // output area -------------------------------------------------------------------------------------------------
        BorderPane outputAreaLayout = new BorderPane();

        Text outputAreaText = new Text("Output Area:");
        outputAreaText.setFont(new Font(35));
        outputAreaText.setFill(Color.WHITE);

        TextArea output = new TextArea();
        output.setMaxSize(877.5, 200);
        output.setFont(new Font(35));
        output.setEditable(false);
        output.getStylesheets().add(IO.readCSS(FileType.CSS.TEXT_AREA_STYLE));
        output.getStylesheets().add(IO.readCSS(FileType.CSS.SCROLL_PANE_STYLE));
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
        rightColumnButtonLayout.setBackground(new Background(new BackgroundFill(
                Color.rgb(50, 50, 50), new CornerRadii(5), Insets.EMPTY)));
        rightColumnButtonLayout.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        // execute button ..............................................................................................
        Button executeButton = new Button();

        ImageView imageView = new ImageView(IO.readAsset(FileType.Asset.PLAY_IMAGE));
        imageView.setFitWidth(ICON_WIDTH);
        imageView.setFitHeight(ICON_HEIGHT);
        imageView.setSmooth(true);

        executeButton.setGraphic(imageView);
        executeButton.getStylesheets().setAll(IO.readCSS(FileType.CSS.BUTTON_STYLE));
        executeButton.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        // tell the system catalog to execute the input
        executeButton.setOnAction(e -> {
            output.clear();
            String input = terminal.getText();
            // get the content on the text area up until the first semicolon is reached
            if (! input.contains(";")) {
                output.setText("Input must end with a semicolon");
            } else {
                input = input.split(";")[0];
                systemCatalog.executeInput(input);
                output.setText(systemCatalog.getExecutionMessage());
                // upon successful execution of a dml statement, refresh the tables/users screens to reflect the changes
                boolean wasSuccessfullyExecuted = systemCatalog.wasSuccessfullyExecuted();
                boolean executedDML = systemCatalog.getInputType() != InputType.QUERY;
                if (wasSuccessfullyExecuted && executedDML) {
                    screenController.refresh(systemCatalog);
                }
            }
        });

        Tooltip tooltip = new Tooltip("Execute Input");
        tooltip.setFont(new Font(20));
        executeButton.setTooltip(tooltip);

        // clear input/output button ...................................................................................
        Button clearButton = new Button();

        imageView = new ImageView(IO.readAsset(FileType.Asset.ERASER_IMAGE));
        imageView.setFitWidth(ICON_WIDTH);
        imageView.setFitHeight(ICON_HEIGHT);
        imageView.setSmooth(true);

        clearButton.setGraphic(imageView);
        clearButton.getStylesheets().setAll(IO.readCSS(FileType.CSS.BUTTON_STYLE));
        clearButton.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        clearButton.setOnAction(e -> {
            terminal.clear();
            output.clear();
            systemCatalog.clear();
        });

        tooltip = new Tooltip("Clear Input and Output");
        tooltip.setFont(new Font(20));
        clearButton.setTooltip(tooltip);

        // view query result set .......................................................................................

        Button resultSetButton = new Button();

        imageView = new ImageView(IO.readAsset(FileType.Asset.RESULT_SET_IMAGE));
        imageView.setFitWidth(ICON_WIDTH);
        imageView.setFitHeight(ICON_HEIGHT);
        imageView.setSmooth(true);

        resultSetButton.setGraphic(imageView);
        resultSetButton.getStylesheets().setAll(IO.readCSS(FileType.CSS.BUTTON_STYLE));
        resultSetButton.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        resultSetButton.setOnAction(e -> {
            boolean wasSuccessfullyExecuted = systemCatalog.wasSuccessfullyExecuted();
            boolean executedQuery = systemCatalog.getInputType() == InputType.QUERY;
            boolean isVerifierOn = systemCatalog.isVerifierOn();
            if (wasSuccessfullyExecuted && executedQuery && isVerifierOn) {
                ResultSet resultSet = systemCatalog.getResultSet();
                new ResultSetWindow(resultSet);
            }
        });

        tooltip = new Tooltip("View Result Set");
        tooltip.setFont(new Font(20));
        resultSetButton.setTooltip(tooltip);

        // relational algebra button ...................................................................................
        Button relationalAlgebraButton = new Button();
        imageView = new ImageView(IO.readAsset(FileType.Asset.PI_IMAGE));
        imageView.setFitWidth(ICON_WIDTH);
        imageView.setFitHeight(ICON_HEIGHT);
        imageView.setSmooth(true);

        relationalAlgebraButton.setGraphic(imageView);
        relationalAlgebraButton.getStylesheets().setAll(IO.readCSS(FileType.CSS.BUTTON_STYLE));
        relationalAlgebraButton.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        // launch a new window displaying the relational algebra
        relationalAlgebraButton.setOnAction(e -> {
            boolean wasSuccessfullyExecuted = systemCatalog.wasSuccessfullyExecuted();
            boolean executedQuery = systemCatalog.getInputType() == InputType.QUERY;
            if (wasSuccessfullyExecuted && executedQuery) {
                String naiveRelationalAlgebra = systemCatalog.getNaiveRelationalAlgebra();
                String optimizedRelationalAlgebra = systemCatalog.getOptimizedRelationalAlgebra();
                new RelationalAlgebraWindow(naiveRelationalAlgebra, optimizedRelationalAlgebra);
            }
        });

        tooltip = new Tooltip("View Corresponding Relational Algebra");
        tooltip.setFont(new Font(20));
        relationalAlgebraButton.setTooltip(tooltip);

        // query tree states button ...................................................................................
        Button queryTreeStatesButton = new Button();

        imageView = new ImageView(IO.readAsset(FileType.Asset.TREE_IMAGE));
        imageView.setFitWidth(ICON_WIDTH);
        imageView.setFitHeight(ICON_HEIGHT);
        imageView.setSmooth(true);

        queryTreeStatesButton.setGraphic(imageView);
        queryTreeStatesButton.getStylesheets().setAll(IO.readCSS(FileType.CSS.BUTTON_STYLE));
        queryTreeStatesButton.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        // launch a new window displaying the query tree states
        queryTreeStatesButton.setOnAction(e -> {
            boolean wasSuccessfullyExecuted = systemCatalog.wasSuccessfullyExecuted();
            boolean executedQuery = systemCatalog.getInputType() == InputType.QUERY;
            if (wasSuccessfullyExecuted && executedQuery) {
                List<QueryTree> queryTreeStates = systemCatalog.getQueryTreeStates();
                String productionCost = systemCatalog.getCostAnalysis().getThird();
                String writeToDiskCost = systemCatalog.getCostAnalysis().getFourth();
                new QueryTreeWindow(queryTreeStates, productionCost, writeToDiskCost);
            }
        });

        tooltip = new Tooltip("View Query Tree States");
        tooltip.setFont(new Font(20));
        queryTreeStatesButton.setTooltip(tooltip);

        // query cost button ...........................................................................................
        Button queryCostButton = new Button();

        imageView = new ImageView(IO.readAsset(FileType.Asset.DOLLAR_SIGN_IMAGE));
        imageView.setFitWidth(ICON_WIDTH);
        imageView.setFitHeight(ICON_HEIGHT);
        imageView.setSmooth(true);

        queryCostButton.setGraphic(imageView);
        queryCostButton.getStylesheets().setAll(IO.readCSS(FileType.CSS.BUTTON_STYLE));
        queryCostButton.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        // launch a new window displaying the query cost
        queryCostButton.setOnAction(e -> {
            boolean wasSuccessfullyExecuted = systemCatalog.wasSuccessfullyExecuted();
            boolean executedQuery = systemCatalog.getInputType() == InputType.QUERY;
            boolean isVerifierOn = systemCatalog.isVerifierOn();
            if (wasSuccessfullyExecuted && executedQuery && isVerifierOn) {
                Quadruple<Integer, Integer, String, String> costAnalysis = systemCatalog.getCostAnalysis();
                new QueryCostWindow(costAnalysis);
            }
        });

        tooltip = new Tooltip("View Query Cost");
        tooltip.setFont(new Font(20));
        queryCostButton.setTooltip(tooltip);

        // recommended file structures button ..........................................................................
        Button recommendedFileStructuresButton = new Button();

        imageView = new ImageView(IO.readAsset(FileType.Asset.FOLDER_IMAGE));
        imageView.setFitWidth(ICON_WIDTH);
        imageView.setFitHeight(ICON_HEIGHT);
        imageView.setSmooth(true);

        recommendedFileStructuresButton.setGraphic(imageView);
        recommendedFileStructuresButton.getStylesheets().setAll(IO.readCSS(FileType.CSS.BUTTON_STYLE));
        recommendedFileStructuresButton.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        // launch a new window displaying the recommended file structures
        recommendedFileStructuresButton.setOnAction(e -> {
            boolean wasSuccessfullyExecuted = systemCatalog.wasSuccessfullyExecuted();
            boolean executedQuery = systemCatalog.getInputType() == InputType.QUERY;
            if (wasSuccessfullyExecuted && executedQuery) {
                Pair<List<Triple<String, String, String>>, List<Pair<String, String>>> recommendedFileStructures =
                        systemCatalog.getRecommendedFileStructures();
                List<Triple<String, String, String>> fileStructures = recommendedFileStructures.getFirst();
                List<Pair<String, String>> clusteredTables = recommendedFileStructures.getSecond();
                new RecommendedFileStructuresWindow(fileStructures, clusteredTables, systemCatalog,
                        screenController);
            }
        });

        tooltip = new Tooltip("View Recommended File Structures");
        tooltip.setFont(new Font(20));
        recommendedFileStructuresButton.setTooltip(tooltip);

        rightColumnButtonLayout.getChildren().addAll(executeButton, clearButton, resultSetButton,
                relationalAlgebraButton, queryTreeStatesButton, queryCostButton, recommendedFileStructuresButton);

        VBox.setMargin(executeButton, new Insets(10, 10, 0, 10));
        VBox.setMargin(clearButton, new Insets(0, 10, 0, 10));
        VBox.setMargin(resultSetButton, new Insets(0, 10, 0, 10));
        VBox.setMargin(relationalAlgebraButton, new Insets(0, 10, 0, 10));
        VBox.setMargin(queryTreeStatesButton, new Insets(0, 10, 0, 10));
        VBox.setMargin(queryCostButton, new Insets(0, 10, 0, 10));
        VBox.setMargin(recommendedFileStructuresButton, new Insets(0, 10, 10, 10));

        // contains the input area, output area, and the right column button layout
        BorderPane contentLayout = new BorderPane();
        contentLayout.setLeft(ioAreaLayout);
        contentLayout.setRight(rightColumnButtonLayout);
        contentLayout.setMaxSize(Screen.defaultWidth, Screen.defaultHeight - 107.5);
        BorderPane.setAlignment(contentLayout, Pos.CENTER);

        // layout container for everything
        BorderPane terminalScreenLayout = new BorderPane();
        terminalScreenLayout.setTop(topRowButtonLayout);
        terminalScreenLayout.setCenter(contentLayout);

        BorderPane.setMargin(ioAreaLayout, new Insets(0, 5, 10, 10));
        BorderPane.setMargin(rightColumnButtonLayout, new Insets(0, 10, 10, 5));

        terminalScreenLayout.setStyle(Screen.DARK_HI);

        this.terminalScreen = new Scene(terminalScreenLayout);

        // adjust components when the screen is resized ----------------------------------------------------------------
        terminalScreen.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = (double) newValue;
            topRowButtonLayout.setMaxWidth(newWidth);
            super.adjustButtonWidth(newWidth);
            double scaleX = newWidth / Screen.defaultWidth;
            contentLayout.setScaleX(newWidth > Screen.defaultWidth ? scaleX : 1.0);

        });

        terminalScreen.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newHeight = (double) newValue;
            double scaleY = newHeight / Screen.defaultHeight;
            contentLayout.setScaleY(newHeight > Screen.defaultHeight ? scaleY : 1.0);
        });
    }

    @Override
    public Scene getScreen() {
        return terminalScreen;
    }
}