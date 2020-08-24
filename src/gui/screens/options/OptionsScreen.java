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
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import systemcatalog.SystemCatalog;

public class OptionsScreen extends Screen {

    private Scene optionsScreen;

    public OptionsScreen(ScreenController screenController, SystemCatalog systemCatalog) {

        // lightDarkModeContent ----------------------------------------------------------------------------------------
        Text lightDarkModeText = new Text("Light or Dark Mode:");
        lightDarkModeText.setFont(new Font(50));
        lightDarkModeText.setFill(Color.WHITE);

        Image sunImage = IO.readAsset(FileType.Asset.SUN_IMAGE);
        ImageView sunImageView = new ImageView(sunImage);
        sunImageView.setFitWidth(100);
        sunImageView.setFitHeight(100);
        sunImageView.setSmooth(true);
        Button lightModeButton = new Button();
        lightModeButton.setGraphic(sunImageView);
        lightModeButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.BUTTON_STYLE));
        lightModeButton.setOnAction(e -> screenController.setToLightMode());

        Image moonImage = IO.readAsset(FileType.Asset.MOON_IMAGE);
        ImageView moonImageView = new ImageView(moonImage);
        moonImageView.setFitWidth(100);
        moonImageView.setFitHeight(100);
        moonImageView.setSmooth(true);
        Button darkModeButton = new Button();
        darkModeButton.setGraphic(moonImageView);
        darkModeButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.BUTTON_STYLE));
        darkModeButton.setOnAction(e -> screenController.setToDarkMode());

        HBox lightDarkModeContent = new HBox();
        lightDarkModeContent.getChildren().addAll(lightDarkModeText, lightModeButton, darkModeButton);
        lightDarkModeContent.setBackground(new Background(
                new BackgroundFill(Color.rgb(50, 50, 50), new CornerRadii(5), Insets.EMPTY)));
        lightDarkModeContent.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
        HBox.setMargin(lightDarkModeText, new Insets(20));
        HBox.setMargin(lightModeButton, new Insets(20));
        HBox.setMargin(darkModeButton, new Insets(20));


        // verifierContent ---------------------------------------------------------------------------------------------
        Text verifierToggleText = new Text("Toggle Verifier:");
        verifierToggleText.setFont(new Font(50));
        verifierToggleText.setFill(Color.WHITE);

        Button verifierToggleButton = new Button("On");
        verifierToggleButton.setFont(new Font(50));
        verifierToggleButton.setTextFill(Color.WHITE);
        verifierToggleButton.setPrefSize(140, 100);
        verifierToggleButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.BUTTON_STYLE));
        verifierToggleButton.setOnAction(e -> {
            systemCatalog.toggleVerifier();
            verifierToggleButton.setText(verifierToggleButton.getText().equalsIgnoreCase("On") ? "Off" : "On");
        });

        Image questionImage = IO.readAsset(FileType.Asset.QUESTION_MARK);
        ImageView questionImageView = new ImageView(questionImage);
        questionImageView.setFitWidth(100);
        questionImageView.setFitHeight(100);
        questionImageView.setSmooth(true);
        Button verifierToggleInfoButton = new Button();
        verifierToggleInfoButton.setGraphic(questionImageView);
        verifierToggleInfoButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.BUTTON_STYLE));
        verifierToggleInfoButton.setOnAction(e -> new OptionsScreenPopUps.VerifierToggleInfoWindow()); // window stuff

        HBox verifierContent = new HBox();
        verifierContent.getChildren().addAll(verifierToggleText, verifierToggleButton, verifierToggleInfoButton);
        verifierContent.setBackground(new Background(
                new BackgroundFill(Color.rgb(50, 50, 50), new CornerRadii(5), Insets.EMPTY)));
        verifierContent.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
        HBox.setMargin(verifierToggleText, new Insets(20));
        HBox.setMargin(verifierToggleButton, new Insets(20));
        HBox.setMargin(verifierToggleInfoButton, new Insets(20));

        // joinOptimizationContent -------------------------------------------------------------------------------------
        Text joinOptimizationText = new Text("Join Optimization:");
        joinOptimizationText.setFont(new Font(50));
        joinOptimizationText.setFill(Color.WHITE);

        Button joinOptimizationToggleButton = new Button("On");
        joinOptimizationToggleButton.setFont(new Font(50));
        joinOptimizationToggleButton.setTextFill(Color.WHITE);
        joinOptimizationToggleButton.setPrefSize(140, 100);
        joinOptimizationToggleButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.BUTTON_STYLE));
        joinOptimizationToggleButton.setOnAction(e -> {
            systemCatalog.toggleJoinOptimization();
            joinOptimizationToggleButton.setText(joinOptimizationToggleButton
                    .getText().equalsIgnoreCase("On") ? "Off" : "On");
        });

        Button joinOptimizationToggleInfoButton = new Button();
        joinOptimizationToggleButton.setGraphic(questionImageView);
        joinOptimizationToggleButton.getStylesheets().addAll(IO.readCSS(FileType.CSS.BUTTON_STYLE));
        joinOptimizationToggleButton.setOnAction(e -> new OptionsScreenPopUps.());

        HBox joinOptimizationContent = new HBox();

        // restoreDatabaseContent --------------------------------------------------------------------------------------
        HBox restoreDatabaseContent = new HBox();

        // options content layout for storing option panes -------------------------------------------------------------
        VBox optionsContentLayout = new VBox();
        optionsContentLayout.getChildren().addAll(lightDarkModeContent, verifierContent, joinOptimizationContent,
                restoreDatabaseContent);
        optionsContentLayout.setAlignment(Pos.CENTER);

        // top row of buttons for switching between screens ------------------------------------------------------------
        HBox buttonLayout = super.getButtonLayout(screenController);

        // container for everything ------------------------------------------------------------------------------------
        BorderPane optionsScreenLayout = new BorderPane();
        optionsScreenLayout.setTop(buttonLayout);
        optionsScreenLayout.setBottom(optionsContentLayout);
        optionsScreenLayout.setPrefSize(defaultWidth, defaultHeight);
        optionsScreenLayout.setStyle("-fx-background-color: rgb(30, 30, 30);");

        this.optionsScreen = new Scene(optionsScreenLayout);

        // scaling components with screen size -------------------------------------------------------------------------
        optionsScreen.widthProperty().addListener((observable, oldValue, newValue) -> {

            double newWidth = (double) newValue;

            lightDarkModeContent.setMaxWidth(newWidth - 100);
            verifierContent.setMaxWidth(newWidth - 100);
        });
    }

    @Override
    public Scene getScreen() {
        return optionsScreen;
    }
}