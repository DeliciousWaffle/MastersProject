package gui.screens.options;

import files.io.FileType;
import files.io.IO;
import gui.ScreenController;
import gui.screens.Screen;
import gui.screens.options.popupwindows.OptionsScreenPopUps;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import systemcatalog.SystemCatalog;

import java.util.ArrayList;
import java.util.List;

public class OptionsScreen extends Screen {

    private Scene optionsScreen;

    // for adjust for later
    private List<Text> textList;
    private List<Button> buttonList;
    private List<BorderPane> contentList;
    private BorderPane optionsContentBackground;
    private BorderPane optionsScreenLayout;

    public OptionsScreen(ScreenController screenController, SystemCatalog systemCatalog) {

        this.textList = new ArrayList<>();
        this.buttonList = new ArrayList<>();
        this.contentList = new ArrayList<>();

        // lightDarkModeContent ----------------------------------------------------------------------------------------
        Text lightDarkModeText = new Text("Light or Dark Mode:");
        lightDarkModeText.setFont(new Font(50));
        lightDarkModeText.setFill(Color.WHITE);
        BorderPane.setMargin(lightDarkModeText, new Insets(20, 0, 0, 20));
        textList.add(lightDarkModeText);

        Image sunImage = IO.readAsset(FileType.Asset.SUN_IMAGE);
        ImageView sunImageView = new ImageView(sunImage);
        sunImageView.setFitWidth(100);
        sunImageView.setFitHeight(80);
        sunImageView.setSmooth(true);
        Button lightModeButton = new Button();
        lightModeButton.setGraphic(sunImageView);
        lightModeButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        lightModeButton.setOnAction(e -> screenController.setToLightMode());
        buttonList.add(lightModeButton);

        Image moonImage = IO.readAsset(FileType.Asset.MOON_IMAGE);
        ImageView moonImageView = new ImageView(moonImage);
        moonImageView.setFitWidth(100);
        moonImageView.setFitHeight(80);
        moonImageView.setSmooth(true);
        Button darkModeButton = new Button();
        darkModeButton.setGraphic(moonImageView);
        darkModeButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        darkModeButton.setOnAction(e -> screenController.setToDarkMode());
        buttonList.add(darkModeButton);

        BorderPane lightAndDarkModeButtons = new BorderPane();
        lightAndDarkModeButtons.setLeft(lightModeButton);
        lightAndDarkModeButtons.setRight(darkModeButton);
        BorderPane.setMargin(lightModeButton, new Insets(10, 5, 10, 10));
        BorderPane.setMargin(darkModeButton, new Insets(10, 10, 10, 5));

        BorderPane lightDarkModeContent = new BorderPane();
        lightDarkModeContent.setLeft(lightDarkModeText);
        lightDarkModeContent.setRight(lightAndDarkModeButtons);
        lightDarkModeContent.setBackground(new Background(
                new BackgroundFill(Color.rgb(50, 50, 50), new CornerRadii(5), Insets.EMPTY)));
        lightDarkModeContent.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
        contentList.add(lightDarkModeContent);

        // verifierContent ---------------------------------------------------------------------------------------------
        Text verifierToggleText = new Text("Toggle Verifier:");
        verifierToggleText.setFont(new Font(50));
        verifierToggleText.setFill(Color.WHITE);
        BorderPane.setMargin(verifierToggleText, new Insets(20, 0, 0, 20));
        textList.add(verifierToggleText);

        Button verifierToggleButton = new Button("On");
        verifierToggleButton.setFont(new Font(40));
        verifierToggleButton.setTextFill(Color.WHITE);
        verifierToggleButton.setPrefSize(120, 80);
        verifierToggleButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        verifierToggleButton.setOnAction(e -> {
            //systemCatalog.toggleVerifier();
            verifierToggleButton.setText(verifierToggleButton.getText()
                    .equalsIgnoreCase("On") ? "Off" : "On");
        });
        buttonList.add(verifierToggleButton);

        Image questionImage1 = IO.readAsset(FileType.Asset.QUESTION_MARK);
        ImageView questionImageView1 = new ImageView(questionImage1);
        questionImageView1.setFitWidth(100);
        questionImageView1.setFitHeight(80);
        questionImageView1.setSmooth(true);
        Button verifierToggleInfoButton = new Button();
        verifierToggleInfoButton.setGraphic(questionImageView1);
        verifierToggleInfoButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        verifierToggleInfoButton.setOnAction(e -> new OptionsScreenPopUps.VerifierToggleWindow());
        buttonList.add(verifierToggleInfoButton);

        BorderPane verifierToggleAndInfoButtons = new BorderPane();
        verifierToggleAndInfoButtons.setLeft(verifierToggleButton);
        verifierToggleAndInfoButtons.setRight(verifierToggleInfoButton);
        BorderPane.setMargin(verifierToggleButton, new Insets(10, 5, 10, 10));
        BorderPane.setMargin(verifierToggleInfoButton, new Insets(10, 10, 10, 5));

        BorderPane verifierContent = new BorderPane();
        verifierContent.setLeft(verifierToggleText);
        verifierContent.setRight(verifierToggleAndInfoButtons);
        verifierContent.setBackground(new Background(
                new BackgroundFill(Color.rgb(50, 50, 50), new CornerRadii(5), Insets.EMPTY)));
        verifierContent.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
        contentList.add(verifierContent);

        // joinOptimizationContent -------------------------------------------------------------------------------------
        Text joinOptimizationText = new Text("Join Optimization:");
        joinOptimizationText.setFont(new Font(50));
        joinOptimizationText.setFill(Color.WHITE);
        BorderPane.setMargin(joinOptimizationText, new Insets(20, 0, 0, 20));
        textList.add(joinOptimizationText);

        Button joinOptimizationToggleButton = new Button("On");
        joinOptimizationToggleButton.setFont(new Font(40));
        joinOptimizationToggleButton.setTextFill(Color.WHITE);
        joinOptimizationToggleButton.setPrefSize(120, 80);
        joinOptimizationToggleButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        joinOptimizationToggleButton.setOnAction(e -> {
            systemCatalog.turnOnJoinOptimization();
            joinOptimizationToggleButton.setText(joinOptimizationToggleButton
                    .getText().equalsIgnoreCase("On") ? "Off" : "On");
        });
        buttonList.add(joinOptimizationToggleButton);

        Image questionImage2 = IO.readAsset(FileType.Asset.QUESTION_MARK);
        ImageView questionImageView2 = new ImageView(questionImage2);
        questionImageView2.setFitWidth(100);
        questionImageView2.setFitHeight(80);
        questionImageView2.setSmooth(true);
        Button joinOptimizationToggleInfoButton = new Button();
        joinOptimizationToggleInfoButton.setGraphic(questionImageView2);
        joinOptimizationToggleInfoButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        joinOptimizationToggleInfoButton.setOnAction(e -> new OptionsScreenPopUps.JoinOptimizationWindow());
        buttonList.add(joinOptimizationToggleInfoButton);

        BorderPane joinOptimizationToggleAndInfoButtons = new BorderPane();
        joinOptimizationToggleAndInfoButtons.setLeft(joinOptimizationToggleButton);
        joinOptimizationToggleAndInfoButtons.setRight(joinOptimizationToggleInfoButton);
        BorderPane.setMargin(joinOptimizationToggleButton, new Insets(10, 5, 10, 10));
        BorderPane.setMargin(joinOptimizationToggleInfoButton, new Insets(10, 10, 10, 5));

        BorderPane joinOptimizationContent = new BorderPane();
        joinOptimizationContent.setLeft(joinOptimizationText);
        joinOptimizationContent.setRight(joinOptimizationToggleAndInfoButtons);
        joinOptimizationContent.setBackground(new Background(
                new BackgroundFill(Color.rgb(50, 50, 50), new CornerRadii(5), Insets.EMPTY)));
        joinOptimizationContent.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
        contentList.add(joinOptimizationContent);

        // restoreDatabaseContent --------------------------------------------------------------------------------------
        Text restoreDatabaseText = new Text("Restore Database:");
        restoreDatabaseText.setFont(new Font(50));
        restoreDatabaseText.setFill(Color.WHITE);
        BorderPane.setMargin(restoreDatabaseText, new Insets(20, 0, 0, 20));
        textList.add(restoreDatabaseText);

        Image refreshImage = IO.readAsset(FileType.Asset.REFRESH_IMAGE);
        ImageView refreshImageView = new ImageView(refreshImage);
        refreshImageView.setFitWidth(100);
        refreshImageView.setFitHeight(80);
        refreshImageView.setSmooth(true);
        Button restoreDatabaseButton = new Button();
        restoreDatabaseButton.setGraphic(refreshImageView);
        restoreDatabaseButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        restoreDatabaseButton.setOnAction(e -> systemCatalog.restoreDatabase());
        buttonList.add(restoreDatabaseButton);

        Image questionImage3 = IO.readAsset(FileType.Asset.QUESTION_MARK);
        ImageView questionImageView3 = new ImageView(questionImage3);
        questionImageView3.setFitWidth(100);
        questionImageView3.setFitHeight(80);
        questionImageView3.setSmooth(true);
        Button restoreDatabaseInfoButton = new Button();
        restoreDatabaseInfoButton.setGraphic(questionImageView3);
        restoreDatabaseInfoButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
        restoreDatabaseInfoButton.setOnAction(e -> new OptionsScreenPopUps.RestoreDatabaseWindow());
        buttonList.add(restoreDatabaseInfoButton);

        BorderPane restoreDatabaseToggleAndInfoButtons = new BorderPane();
        restoreDatabaseToggleAndInfoButtons.setLeft(restoreDatabaseButton);
        restoreDatabaseToggleAndInfoButtons.setRight(restoreDatabaseInfoButton);
        BorderPane.setMargin(restoreDatabaseButton, new Insets(10, 5, 10, 10));
        BorderPane.setMargin(restoreDatabaseInfoButton, new Insets(10, 10, 10, 5));

        BorderPane restoreDatabaseContent = new BorderPane();
        restoreDatabaseContent.setLeft(restoreDatabaseText);
        restoreDatabaseContent.setRight(restoreDatabaseToggleAndInfoButtons);
        restoreDatabaseContent.setBackground(new Background(
                new BackgroundFill(Color.rgb(50, 50, 50), new CornerRadii(5), Insets.EMPTY)));
        restoreDatabaseContent.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
        contentList.add(restoreDatabaseContent);

        // options content layout for storing option panes -------------------------------------------------------------
        VBox optionsContentLayout = new VBox();
        optionsContentLayout.getChildren().addAll(lightDarkModeContent, verifierContent, joinOptimizationContent,
                restoreDatabaseContent);
        VBox.setMargin(lightDarkModeContent, new Insets(0, 20, 10, 20));
        VBox.setMargin(verifierContent, new Insets(10, 20, 10, 20));
        VBox.setMargin(joinOptimizationContent, new Insets(10, 20, 10, 20));
        VBox.setMargin(restoreDatabaseContent, new Insets(10, 20, 10, 20));

        optionsContentBackground = new BorderPane();
        optionsContentBackground.setCenter(optionsContentLayout);
        optionsContentBackground.setBackground(new Background(new BackgroundFill(Color.rgb(40, 40, 40),
                new CornerRadii(5), new Insets(-20, 0, 0, 0))));
        optionsContentBackground.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        // top row of buttons for switching between screens ------------------------------------------------------------
        HBox buttonLayout = super.getButtonLayout(screenController);

        // container for everything ------------------------------------------------------------------------------------
        optionsScreenLayout = new BorderPane();
        optionsScreenLayout.setTop(buttonLayout);
        optionsScreenLayout.setCenter(optionsContentBackground);
        optionsScreenLayout.setPrefSize(defaultWidth, defaultHeight);
        optionsScreenLayout.setStyle("-fx-background-color: rgb(30, 30, 30);");
        BorderPane.setMargin(optionsContentBackground, new Insets(70, 50, 30, 50));

        this.optionsScreen = new Scene(optionsScreenLayout);

        optionsScreen.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = (double) newValue;
            super.adjustButtonWidth(newWidth);
        });
    }

    @Override
    public Scene getScreen() {
        return optionsScreen;
    }

    @Override
    public void setToLightMode() {
        super.setToLightMode();
        textList.forEach(e -> e.setFill(Color.BLACK));
        buttonList.forEach(e -> e.getStylesheets().setAll(IO.readCSS(FileType.CSS.LIGHT_BUTTON_STYLE)));
        contentList.forEach(e -> e.setBackground(new Background(new BackgroundFill(Color.rgb(190, 190, 190),
                new CornerRadii(5), Insets.EMPTY))));
        optionsContentBackground.setBackground(new Background(new BackgroundFill(Color.rgb(150, 150, 150),
                new CornerRadii(5), new Insets(-20, 0, 0, 0))));
        optionsScreenLayout.setStyle(Screen.LIGHT_LOW);
    }

    @Override
    public void setToDarkMode() {
        super.setToDarkMode();
        textList.forEach(e -> e.setFill(Color.WHITE));
        buttonList.forEach(e -> e.getStylesheets().setAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE)));
        contentList.forEach(e -> e.setBackground(new Background(new BackgroundFill(Color.rgb(90, 90, 90),
                new CornerRadii(5), Insets.EMPTY))));
        optionsContentBackground.setBackground(new Background(new BackgroundFill(Color.rgb(60, 60, 60),
                new CornerRadii(5), new Insets(-20, 0, 0, 0))));
        optionsScreenLayout.setStyle(Screen.DARK_HI);
    }
}