package gui.screens.options;

import files.io.FileType;
import files.io.IO;
import gui.ScreenController;
import gui.screens.Screen;
import gui.screens.options.popupwindows.OptionsScreenPopUps;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import systemcatalog.SystemCatalog;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class OptionsScreen extends Screen {

    private Scene optionsScreen;

    public OptionsScreen(ScreenController screenController, SystemCatalog systemCatalog) {

        HBox topRowButtonLayout = super.getButtonLayout(screenController);

        // container for almost everything
        VBox vBoxContainer = new VBox(30.0);
        vBoxContainer.setAlignment(Pos.TOP_CENTER);
        vBoxContainer.setBackground(new Background(
                new BackgroundFill(Color.rgb(30, 30, 30), CornerRadii.EMPTY, Insets.EMPTY)));


        // title text
        Text titleText = new Text("Options");
        titleText.setFill(Color.WHITE);
        titleText.setFont(new Font(100.0));
        titleText.setSmooth(true);
        vBoxContainer.getChildren().add(titleText);


        // toggle the verifier area
        BorderPane verifierContentArea = new BorderPane();
        verifierContentArea.setBackground(new Background(
                new BackgroundFill(Color.rgb(50, 50, 50), new CornerRadii(5), Insets.EMPTY)));
        verifierContentArea.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        Text verifierToggleText = new Text("Toggle Verifier:");
        verifierToggleText.setFont(new Font(50));
        verifierToggleText.setFill(Color.WHITE);

        BorderPane.setAlignment(verifierToggleText, Pos.CENTER);
        BorderPane.setMargin(verifierToggleText, new Insets(0, 0, 0, 15));
        verifierContentArea.setLeft(verifierToggleText);

        BorderPane verifierToggleButtonAndInfoButtons = new BorderPane();

        Button verifierToggleButton = new Button("On");
        verifierToggleButton.setFont(new Font(40));
        verifierToggleButton.setTextFill(Color.WHITE);
        verifierToggleButton.setPrefSize(120, 80);
        verifierToggleButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        verifierToggleButton.setOnAction(e -> {
            if (systemCatalog.isVerifierOn()) {
                systemCatalog.turnOffVerifier();
                verifierToggleButton.setText("Off");
            } else {
                systemCatalog.turnOnVerifier();
                verifierToggleButton.setText("On");
            }
        });

        BorderPane.setMargin(verifierToggleButton, new Insets(10, 5, 10, 10));
        verifierToggleButtonAndInfoButtons.setLeft(verifierToggleButton);

        Button verifierToggleInfoButton = new Button();
        Image questionImage = IO.readAsset(FileType.Asset.QUESTION_MARK);
        ImageView questionImageView = new ImageView(questionImage);
        questionImageView.setFitWidth(100);
        questionImageView.setFitHeight(80);
        questionImageView.setSmooth(true);
        verifierToggleInfoButton.setGraphic(questionImageView);
        verifierToggleInfoButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        verifierToggleInfoButton.setOnAction(e -> new OptionsScreenPopUps.VerifierToggleWindow());

        verifierToggleButtonAndInfoButtons.setRight(verifierToggleInfoButton);
        BorderPane.setMargin(verifierToggleInfoButton, new Insets(10, 10, 10, 5));

        verifierContentArea.setRight(verifierToggleButtonAndInfoButtons);

        VBox.setMargin(verifierContentArea, new Insets(0, 30, 0, 30));
        vBoxContainer.getChildren().add(verifierContentArea);


        // security checker toggle area
        BorderPane securityCheckerContentArea = new BorderPane();
        securityCheckerContentArea.setBackground(new Background(
                new BackgroundFill(Color.rgb(50, 50, 50), new CornerRadii(5), Insets.EMPTY)));
        securityCheckerContentArea.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        Text securityCheckerToggleText = new Text("Toggle Security Checker:");
        securityCheckerToggleText.setFont(new Font(50));
        securityCheckerToggleText.setFill(Color.WHITE);

        BorderPane.setAlignment(securityCheckerToggleText, Pos.CENTER);
        BorderPane.setMargin(securityCheckerToggleText, new Insets(0, 0, 0, 15));
        securityCheckerContentArea.setLeft(securityCheckerToggleText);

        BorderPane securityCheckerToggleAndInfoButtons = new BorderPane();

        Button securityCheckerToggleButton = new Button("On");
        securityCheckerToggleButton.setFont(new Font(40));
        securityCheckerToggleButton.setTextFill(Color.WHITE);
        securityCheckerToggleButton.setPrefSize(120, 80);
        securityCheckerToggleButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        securityCheckerToggleButton.setOnAction(e -> {
            if (systemCatalog.isSecurityCheckerOn()) {
                systemCatalog.turnOffSecurityChecker();
                securityCheckerToggleButton.setText("Off");
            } else {
                systemCatalog.turnOnSecurityChecker();
                securityCheckerToggleButton.setText("On");
            }
        });

        BorderPane.setMargin(securityCheckerToggleButton, new Insets(10, 5, 10, 10));
        securityCheckerToggleAndInfoButtons.setLeft(securityCheckerToggleButton);

        Button securityCheckerToggleInfoButton = new Button();
        questionImage = IO.readAsset(FileType.Asset.QUESTION_MARK);
        questionImageView = new ImageView(questionImage);
        questionImageView.setFitWidth(100);
        questionImageView.setFitHeight(80);
        questionImageView.setSmooth(true);
        securityCheckerToggleInfoButton.setGraphic(questionImageView);
        securityCheckerToggleInfoButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        securityCheckerToggleInfoButton.setOnAction(e -> new OptionsScreenPopUps.SecurityCheckerToggleWindow());

        securityCheckerToggleAndInfoButtons.setRight(securityCheckerToggleInfoButton);
        BorderPane.setMargin(securityCheckerToggleInfoButton, new Insets(10, 10, 10, 5));

        securityCheckerContentArea.setRight(securityCheckerToggleAndInfoButtons);

        VBox.setMargin(securityCheckerContentArea, new Insets(0, 30, 0, 30));
        vBoxContainer.getChildren().add(securityCheckerContentArea);


        // join optimization toggle area
        BorderPane joinOptimizationToggleArea = new BorderPane();
        joinOptimizationToggleArea.setBackground(new Background(
                new BackgroundFill(Color.rgb(50, 50, 50), new CornerRadii(5), Insets.EMPTY)));
        joinOptimizationToggleArea.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        Text joinOptimizationToggleText = new Text("Toggle Join Optimization:");
        joinOptimizationToggleText.setFont(new Font(50));
        joinOptimizationToggleText.setFill(Color.WHITE);

        BorderPane.setAlignment(joinOptimizationToggleText, Pos.CENTER);
        BorderPane.setMargin(joinOptimizationToggleText, new Insets(0, 0, 0, 15));
        joinOptimizationToggleArea.setLeft(joinOptimizationToggleText);

        BorderPane joinOptimizationToggleAndInfoButtons = new BorderPane();

        Button joinOptimizationToggleButton = new Button("On");
        joinOptimizationToggleButton.setFont(new Font(40));
        joinOptimizationToggleButton.setTextFill(Color.WHITE);
        joinOptimizationToggleButton.setPrefSize(120, 80);
        joinOptimizationToggleButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        joinOptimizationToggleButton.setOnAction(e -> {
            if (systemCatalog.isJoinOptimizationOn()) {
                systemCatalog.turnOffJoinOptimization();
                joinOptimizationToggleButton.setText("Off");
            } else {
                systemCatalog.turnOnJoinOptimization();
                joinOptimizationToggleButton.setText("On");
            }
        });

        BorderPane.setMargin(joinOptimizationToggleButton, new Insets(10, 5, 10, 10));
        joinOptimizationToggleAndInfoButtons.setLeft(joinOptimizationToggleButton);

        Button joinOptimizationToggleInfoButton = new Button();
        questionImage = IO.readAsset(FileType.Asset.QUESTION_MARK);
        questionImageView = new ImageView(questionImage);
        questionImageView.setFitWidth(100);
        questionImageView.setFitHeight(80);
        questionImageView.setSmooth(true);
        joinOptimizationToggleInfoButton.setGraphic(questionImageView);
        joinOptimizationToggleInfoButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        joinOptimizationToggleInfoButton.setOnAction(e -> new OptionsScreenPopUps.JoinOptimizationToggleWindow());

        joinOptimizationToggleAndInfoButtons.setRight(joinOptimizationToggleInfoButton);
        BorderPane.setMargin(joinOptimizationToggleInfoButton, new Insets(10, 10, 10, 5));

        joinOptimizationToggleArea.setRight(joinOptimizationToggleAndInfoButtons);

        VBox.setMargin(joinOptimizationToggleArea, new Insets(0, 30, 0, 30));
        vBoxContainer.getChildren().add(joinOptimizationToggleArea);


        // save content
        BorderPane saveDataArea = new BorderPane();
        saveDataArea.setBackground(new Background(
                new BackgroundFill(Color.rgb(50, 50, 50), new CornerRadii(5), Insets.EMPTY)));
        saveDataArea.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        Text saveDataText = new Text("Save Data:");
        saveDataText.setFont(new Font(50));
        saveDataText.setFill(Color.WHITE);

        BorderPane.setAlignment(saveDataText, Pos.CENTER);
        BorderPane.setMargin(saveDataText, new Insets(0, 0, 0, 15));
        saveDataArea.setLeft(saveDataText);

        BorderPane saveDatabaseAndInfoButtons = new BorderPane();

        Button saveDataButton = new Button();
        Image saveImage = IO.readAsset(FileType.Asset.SAVE_IMAGE);
        ImageView saveImageView = new ImageView(saveImage);
        saveImageView.setFitWidth(100);
        saveImageView.setFitHeight(80);
        saveImageView.setSmooth(true);
        saveDataButton.setGraphic(saveImageView);
        saveDataButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        saveDataButton.setOnAction(e -> systemCatalog.saveChanges());

        BorderPane.setMargin(saveDataButton, new Insets(10, 5, 10, 10));
        saveDatabaseAndInfoButtons.setLeft(saveDataButton);

        Button saveDataInfoButton = new Button();
        questionImage = IO.readAsset(FileType.Asset.QUESTION_MARK);
        questionImageView = new ImageView(questionImage);
        questionImageView.setFitWidth(100);
        questionImageView.setFitHeight(80);
        questionImageView.setSmooth(true);
        saveDataInfoButton.setGraphic(questionImageView);
        saveDataInfoButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        saveDataInfoButton.setOnAction(e -> new OptionsScreenPopUps.SaveDataWindow());

        saveDatabaseAndInfoButtons.setRight(saveDataInfoButton);
        BorderPane.setMargin(saveDataInfoButton, new Insets(10, 10, 10, 5));

        saveDataArea.setRight(saveDatabaseAndInfoButtons);

        VBox.setMargin(saveDataArea, new Insets(0, 30, 0, 30));
        vBoxContainer.getChildren().add(saveDataArea);


        // restore data area
        BorderPane restoreDataArea = new BorderPane();
        restoreDataArea.setBackground(new Background(
                new BackgroundFill(Color.rgb(50, 50, 50), new CornerRadii(5), Insets.EMPTY)));
        restoreDataArea.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        Text restoreDataText = new Text("Restore Data:");
        restoreDataText.setFont(new Font(50));
        restoreDataText.setFill(Color.WHITE);

        BorderPane.setAlignment(restoreDataText, Pos.CENTER);
        BorderPane.setMargin(restoreDataText, new Insets(0, 0, 0, 15));
        restoreDataArea.setLeft(restoreDataText);

        BorderPane restoreDataAndInfoButtons = new BorderPane();

        Button restoreDataButton = new Button();
        Image refreshImage = IO.readAsset(FileType.Asset.REFRESH_IMAGE);
        ImageView refreshImageView = new ImageView(refreshImage);
        refreshImageView.setFitWidth(100);
        refreshImageView.setFitHeight(80);
        refreshImageView.setSmooth(true);
        restoreDataButton.setGraphic(refreshImageView);
        restoreDataButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        restoreDataButton.setOnAction(e -> systemCatalog.restoreDatabase());

        BorderPane.setMargin(restoreDataButton, new Insets(10, 5, 10, 10));
        restoreDataAndInfoButtons.setLeft(restoreDataButton);

        Button restoreDataInfoButton = new Button();
        questionImage = IO.readAsset(FileType.Asset.QUESTION_MARK);
        questionImageView = new ImageView(questionImage);
        questionImageView.setFitWidth(100);
        questionImageView.setFitHeight(80);
        questionImageView.setSmooth(true);
        restoreDataInfoButton.setGraphic(questionImageView);
        restoreDataInfoButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        restoreDataInfoButton.setOnAction(e -> new OptionsScreenPopUps.RestoreDataWindow());

        restoreDataAndInfoButtons.setRight(restoreDataInfoButton);
        BorderPane.setMargin(restoreDataInfoButton, new Insets(10, 10, 10, 5));

        restoreDataArea.setRight(restoreDataAndInfoButtons);

        VBox.setMargin(restoreDataArea, new Insets(0, 30, 30, 30));
        vBoxContainer.getChildren().add(restoreDataArea);


        // scroll pane
        ScrollPane scrollPane = new ScrollPane(vBoxContainer);
        scrollPane.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_SCROLL_PANE_STYLE));
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        BorderPane overallContainer = new BorderPane();
        overallContainer.setMinSize(Screen.defaultWidth, Screen.defaultHeight);
        overallContainer.setTop(topRowButtonLayout);
        overallContainer.setBottom(scrollPane);
        overallContainer.setBackground(new Background(new BackgroundFill(Color.rgb(30, 30, 30),
                CornerRadii.EMPTY, Insets.EMPTY)));

        optionsScreen = new Scene(overallContainer);

        optionsScreen.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = (double) newValue;
            topRowButtonLayout.setMaxWidth(newWidth);
            super.adjustButtonWidth(newWidth);
            overallContainer.setMinWidth(newWidth);
        });

        optionsScreen.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newHeight = (double) newValue;
            overallContainer.setMinHeight(newHeight);
        });
    }

    @Override
    public Scene getScreen()  {
        return optionsScreen;
    }
}