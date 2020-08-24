package gui.screens.users;

import datastructures.user.User;
import datastructures.user.component.TablePrivileges;
import files.io.FileType;
import files.io.IO;
import gui.ScreenController;
import gui.screens.Screen;
import gui.screens.users.components.TablePrivilegePane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Shadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import systemcatalog.SystemCatalog;

import java.util.ArrayList;
import java.util.List;

public class UsersScreen extends Screen {

    private Scene usersScreen;

    public UsersScreen(ScreenController screenController, SystemCatalog systemCatalog) {

        List<User> users = systemCatalog.getUsers();

        VBox userContentContainerLayout = new VBox();
        userContentContainerLayout.setStyle("-fx-background-color: rgb(30, 30, 30);");
        userContentContainerLayout.setMinSize(0, 0);

        List<VBox> tablePrivilegePaneListLayoutList = new ArrayList<>();
        ScrollPane tablePrivilegeListLayoutScrollPane = new ScrollPane();
        tablePrivilegeListLayoutScrollPane.setMinSize(0, 0);

        List<Button> userButtonsToScale = new ArrayList<>();
        List<Button> playButtonsToScale = new ArrayList<>();
        List<BorderPane> privilegePanesToScale = new ArrayList<>();

        // used for displaying who the user is currently playing as (defaults to the DBA)
        Text playingAsDBAText = new Text("Currently Playing As: DBA");
        playingAsDBAText.setFont(new Font(50));
        playingAsDBAText.setFill(Color.WHITE);
        BorderPane playingAsContainer = new BorderPane(playingAsDBAText);
        playingAsContainer.setStyle("-fx-background-color: rgb(30, 30, 30);");
        playingAsContainer.setMinSize(0, 0);

        // show's whose privileges are displayed (defaults to the DBA)
        Text tablePrivilegesText = new Text("DBA's Privileges:");

        // button styles
        String buttonStyle = " -fx-background-color: rgb(100, 100, 100); -fx-text-fill: white;";
        String buttonEnteredStyle = "-fx-background-color: rgb(150, 150, 150); -fx-text-fill: white;";

        // adding all the content for the users screen in the most god awful way imaginable
        for(User user : users) {

            // right side of the screen ================================================================================

            // current user's table privileges -------------------------------------------------------------------------
            List<BorderPane> tablePrivilegePaneList = new ArrayList<>();
            for (TablePrivileges tablePrivilege : user.getTablePrivilegesList()) {
                BorderPane tablePrivilegePane = new TablePrivilegePane(tablePrivilege.getTableName(),
                        tablePrivilege.getPrivileges(), tablePrivilege.getUpdateColumns(),
                        tablePrivilege.getReferenceColumns()).getRoot();
                tablePrivilegePaneList.add(tablePrivilegePane);
                VBox.setMargin(tablePrivilegePane, new Insets(10, 20, 10, 20));
                tablePrivilegePane.setMinSize(0, 0);

                privilegePanesToScale.add(tablePrivilegePane);
            }

            // current user's passable table privileges ----------------------------------------------------------------
            Text passableTablePrivilegesText = new Text("Passable Privileges:");
            passableTablePrivilegesText.setFont(new Font(50));
            passableTablePrivilegesText.setFill(Color.WHITE);
            BorderPane.setAlignment(passableTablePrivilegesText, Pos.CENTER);
            VBox.setMargin(passableTablePrivilegesText, new Insets(10, 20, 10, 20));

            List<BorderPane> passableTablePrivilegePaneList = new ArrayList<>();
            for(TablePrivileges passableTablePrivilege : user.getPassableTablePrivilegesList()) {
                BorderPane passableTablePrivilegePane = new TablePrivilegePane(passableTablePrivilege.getTableName(),
                        passableTablePrivilege.getPrivileges(), passableTablePrivilege.getUpdateColumns(),
                        passableTablePrivilege.getReferenceColumns()).getRoot();
                passableTablePrivilegePaneList.add(passableTablePrivilegePane);
                VBox.setMargin(passableTablePrivilegePane, new Insets(10, 20, 10, 20));

                passableTablePrivilegePane.setMinSize(0, 0);
                privilegePanesToScale.add(passableTablePrivilegePane);
            }

            // adding the aforementioned content in a vertical layout --------------------------------------------------
            VBox tablePrivilegePaneListLayout = new VBox();
            tablePrivilegePaneListLayout.setStyle("-fx-background-color: rgb(30, 30, 30);");//rgb(30, 30, 30);");
            tablePrivilegePaneListLayout.setSpacing(10);
            tablePrivilegePaneListLayout.getChildren().addAll(tablePrivilegePaneList);
            tablePrivilegePaneListLayout.getChildren().add(passableTablePrivilegesText);
            tablePrivilegePaneListLayout.getChildren().addAll(passableTablePrivilegePaneList);
            tablePrivilegePaneListLayout.setMinSize(0, 0);

            tablePrivilegePaneListLayoutList.add(tablePrivilegePaneListLayout);

            // left side of the screen =================================================================================

            // user button ---------------------------------------------------------------------------------------------
            String username = user.getUsername();
            Button userButton = new Button(username + "'s Privileges");
            userButton.setFont(new Font(30));
            userButton.setTextFill(Color.WHITE);
            userButton.setStyle(buttonStyle);
            BorderPane.setMargin(userButton, new Insets(10, 5, 10, 10));
            userButton.setEffect(
                    new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
            userButton.setOnMouseEntered(e -> userButton.setStyle(buttonEnteredStyle));
            userButton.setOnMouseExited(e -> userButton.setStyle(buttonStyle));
            userButton.setMinSize(0, 0);

            // on click, display this user's table privileges and passable table privileges
            userButton.setOnAction(e -> {
                tablePrivilegeListLayoutScrollPane.setContent(tablePrivilegePaneListLayout);
                tablePrivilegesText.setText(username + "'s Privileges:");
            });

            userButtonsToScale.add(userButton);

            // play as button ------------------------------------------------------------------------------------------
            Button playAsButton = new Button("Play");
            playAsButton.setFont(new Font(30));
            playAsButton.setTextFill(Color.WHITE);
            playAsButton.setStyle(buttonStyle);
            BorderPane.setMargin(playAsButton, new Insets(10, 10, 10, 5));
            playAsButton.setEffect(
                    new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
            playAsButton.setOnMouseEntered(e -> playAsButton.setStyle(buttonEnteredStyle));
            playAsButton.setOnMouseExited(e -> playAsButton.setStyle(buttonStyle));
            playAsButton.setOnAction(e -> {
                systemCatalog.setCurrentUser(user);
                Text playingAsUserText = new Text("Currently Playing As: " + username);
                playingAsUserText.setFont(new Font(50));
                playingAsUserText.setFill(Color.WHITE);
                playingAsContainer.setCenter(playingAsUserText);
            });
            playAsButton.setMinSize(0, 0);

            playButtonsToScale.add(playAsButton);

            // container for user and play as buttons ------------------------------------------------------------------
            BorderPane userContentContainer = new BorderPane();
            userContentContainer.setLeft(userButton);
            userContentContainer.setRight(playAsButton);
            userContentContainer.setBackground(new Background(
                    new BackgroundFill(Color.rgb(50, 50, 50), new CornerRadii(5), Insets.EMPTY)));
            userContentContainer.setEffect(
                    new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
            userContentContainer.setMinSize(0, 0);

            // adding everything to the containers ---------------------------------------------------------------------
            userContentContainerLayout.getChildren().add(userContentContainer);
            VBox.setMargin(userContentContainer, new Insets(5, 10, 5, 10));
        }

        // container for everything on left side of screen -------------------------------------------------------------
        Text usersText = new Text("Users:");
        usersText.setFont(new Font(50));
        usersText.setFill(Color.WHITE);
        BorderPane.setAlignment(usersText, Pos.CENTER);

        ScrollPane userContentScrollPane = new ScrollPane();
        userContentScrollPane.setContent(userContentContainerLayout);
        userContentScrollPane.getStylesheets().add(IO.readCSS(FileType.CSS.SCROLL_PANE_STYLE));
        userContentScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        userContentScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        userContentScrollPane.setHvalue(0.5);
        userContentScrollPane.setMinSize(0, 0);

        BorderPane leftSideContainer = new BorderPane();
        leftSideContainer.setStyle("-fx-background-color: rgb(30, 30, 30);");
        leftSideContainer.setTop(usersText);
        leftSideContainer.setBottom(userContentScrollPane);
        leftSideContainer.setMinSize(0, 0);

        // container for everything on right side of screen ------------------------------------------------------------
        tablePrivilegesText.setFont(new Font(50));
        tablePrivilegesText.setFill(Color.WHITE);
        BorderPane.setAlignment(tablePrivilegesText, Pos.CENTER);

        // DBA's table privileges shown first
        tablePrivilegePaneListLayoutList.forEach(e -> e.setAlignment(Pos.CENTER));
        tablePrivilegeListLayoutScrollPane.setContent(tablePrivilegePaneListLayoutList.get(0));
        tablePrivilegeListLayoutScrollPane.getStylesheets().add(IO.readCSS(FileType.CSS.SCROLL_PANE_STYLE));
        tablePrivilegeListLayoutScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        tablePrivilegeListLayoutScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        tablePrivilegeListLayoutScrollPane.setHvalue(0.5);
        tablePrivilegeListLayoutScrollPane.setMinSize(0, 0);

        BorderPane rightSideContainer = new BorderPane();
        rightSideContainer.setStyle("-fx-background-color: rgb(30, 30, 30);");
        rightSideContainer.setTop(tablePrivilegesText);
        rightSideContainer.setBottom(tablePrivilegeListLayoutScrollPane);
        rightSideContainer.setMinSize(0, 0);

        // top row button for transitioning between screens ------------------------------------------------------------
        HBox topRowButtonLayout = super.getButtonLayout(screenController);
        topRowButtonLayout.setMinSize(0, 0);

        // container for top row buttons and playing as container ------------------------------------------------------
        BorderPane topContainer = new BorderPane();
        topContainer.setTop(topRowButtonLayout);
        topContainer.setBottom(playingAsContainer);
        topContainer.setMinSize(0, 0);

        // overall layout for the user screen --------------------------------------------------------------------------
        BorderPane usersScreenLayout = new BorderPane();
        usersScreenLayout.setTop(topContainer);
        usersScreenLayout.setLeft(leftSideContainer);
        usersScreenLayout.setRight(rightSideContainer);
        usersScreenLayout.setStyle("-fx-background-color: rgb(30, 30, 30);");
        usersScreenLayout.setMinSize(0, 0);

        usersScreen = new Scene(usersScreenLayout);

        // adjust components when the screen is resized ----------------------------------------------------------------
        usersScreen.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = (double) newValue;
            topRowButtonLayout.setPrefWidth(newWidth);
            playingAsContainer.setPrefWidth(newWidth);
            super.adjustButtonWidth(newWidth);
            userContentScrollPane.setPrefWidth(newWidth / 2);
            tablePrivilegeListLayoutScrollPane.setPrefWidth(newWidth / 2);
            tablePrivilegePaneListLayoutList.forEach(e -> e.setPrefWidth(newWidth / 2));
            userButtonsToScale.forEach(e -> e.setPrefWidth(newWidth / 2 - 200));
            playButtonsToScale.forEach(e -> e.setPrefWidth(newWidth / 2 - 425));
            privilegePanesToScale.forEach(e -> e.setPrefWidth(newWidth / 2 - 100));
            usersScreenLayout.setPrefWidth(newWidth);
        });

        usersScreen.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newHeight = (double) newValue;
            userContentScrollPane.setPrefHeight(newHeight - 200);
            tablePrivilegeListLayoutScrollPane.setPrefHeight(newHeight - 200);
        });
    }

    @Override
    public Scene getScreen() {
        return usersScreen;
    }
}