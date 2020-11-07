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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import systemcatalog.SystemCatalog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UsersScreen extends Screen {

    private Scene usersScreen;

    public UsersScreen(ScreenController screenController, SystemCatalog systemCatalog) {

        List<User> users = systemCatalog.getUsers();

        VBox userContentContainerLayout = new VBox();
        userContentContainerLayout.setStyle("-fx-background-color: rgb(30, 30, 30);");
        userContentContainerLayout.setMinSize(0, 0);

        List<VBox> listOfTablePrivilegePaneListLayouts = new ArrayList<>();
        ScrollPane tablePrivilegeListLayoutScrollPane = new ScrollPane();
        tablePrivilegeListLayoutScrollPane.setMinSize(0, 0);

        List<BorderPane> userContentContainerList = new ArrayList<>();

        // used for displaying who the user is currently playing as (defaults to the DBA)
        Text playingAsDBAText = new Text("Currently Playing As: DBA");
        playingAsDBAText.setFont(new Font(50));
        playingAsDBAText.setFill(Color.WHITE);
        BorderPane playingAsContainer = new BorderPane(playingAsDBAText);
        playingAsContainer.setStyle("-fx-background-color: rgb(30, 30, 30);");
        playingAsContainer.setMinSize(0, 0);

        // show's whose privileges are displayed (defaults to the DBA)
        Text tablePrivilegesText = new Text("DBA's Privileges:");

        // adding all the content for the users screen in the most god awful way imaginable
        for(User user : users) {

            // right side of the screen ================================================================================

            // current user's table privileges -------------------------------------------------------------------------
            List<TablePrivilegePane> tablePrivilegePaneList = new ArrayList<>();

            for (TablePrivileges tablePrivilege : user.getTablePrivilegesList()) {
                TablePrivilegePane tablePrivilegePane = new TablePrivilegePane(tablePrivilege.getTableName(),
                        tablePrivilege.getPrivileges(), tablePrivilege.getUpdateColumns(),
                        tablePrivilege.getReferenceColumns());
                tablePrivilegePaneList.add(tablePrivilegePane);
            }

            // current user's passable table privileges ----------------------------------------------------------------
            Text passableTablePrivilegesText = new Text("Passable Privileges:");
            passableTablePrivilegesText.setFont(new Font(50));
            passableTablePrivilegesText.setFill(Color.WHITE);
            BorderPane.setAlignment(passableTablePrivilegesText, Pos.CENTER);
            VBox.setMargin(passableTablePrivilegesText, new Insets(10, 20, 10, 20));

            List<TablePrivilegePane> passableTablePrivilegePaneList = new ArrayList<>();

            for(TablePrivileges passableTablePrivilege : user.getGrantedTablePrivilegesList()) {
                TablePrivilegePane passableTablePrivilegePane =
                        new TablePrivilegePane(passableTablePrivilege.getTableName(),
                        passableTablePrivilege.getPrivileges(), passableTablePrivilege.getUpdateColumns(),
                        passableTablePrivilege.getReferenceColumns());
                passableTablePrivilegePaneList.add(passableTablePrivilegePane);
            }

            // adding the aforementioned content in a vertical layout --------------------------------------------------
            VBox tablePrivilegePaneListLayout = new VBox();
            tablePrivilegePaneListLayout.setStyle("-fx-background-color: rgb(30, 30, 30);");
            tablePrivilegePaneListLayout.setSpacing(10);

            List<BorderPane> blah1 = tablePrivilegePaneList
                    .stream()
                    .map(TablePrivilegePane::getRoot)
                    .collect(Collectors.toList());

            List<BorderPane> blah2 = passableTablePrivilegePaneList
                    .stream()
                    .map(TablePrivilegePane::getRoot)
                    .collect(Collectors.toList());

            blah1.forEach(e -> VBox.setMargin(e, new Insets(10, 20, 10, 20)));
            blah2.forEach(e -> VBox.setMargin(e, new Insets(10, 20, 10, 20)));

            tablePrivilegePaneListLayout.getChildren().addAll(blah1);
            tablePrivilegePaneListLayout.getChildren().add(passableTablePrivilegesText);
            tablePrivilegePaneListLayout.getChildren().addAll(blah2);
            tablePrivilegePaneListLayout.setMinSize(Screen.defaultWidth / 2 - 60, 0);

            listOfTablePrivilegePaneListLayouts.add(tablePrivilegePaneListLayout);

            // left side of the screen =================================================================================

            // user button ---------------------------------------------------------------------------------------------
            String username = user.getUsername();
            Button userButton = new Button(username + "'s Privileges");
            userButton.setFont(new Font(30));
            userButton.setTextFill(Color.WHITE);
            userButton.getStylesheets().setAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
            BorderPane.setMargin(userButton, new Insets(10, 5, 10, 10));
            userButton.setEffect(
                    new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

            // on click, display this user's table privileges and passable table privileges
            userButton.setOnAction(e -> {
                tablePrivilegeListLayoutScrollPane.setContent(tablePrivilegePaneListLayout);
                tablePrivilegesText.setText(username + "'s Privileges:");
            });

            // play as button ------------------------------------------------------------------------------------------
            Button playAsButton = new Button("Play");
            playAsButton.setFont(new Font(30));
            playAsButton.setTextFill(Color.WHITE);
            playAsButton.getStylesheets().setAll(IO.readCSS(FileType.CSS.DARK_BUTTON_STYLE));
            BorderPane.setMargin(playAsButton, new Insets(10, 10, 10, 5));
            playAsButton.setEffect(
                    new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
            playAsButton.setOnAction(e -> {
                systemCatalog.setCurrentUser(user);
                Text playingAsUserText = new Text("Currently Playing As: " + username);
                playingAsUserText.setFont(new Font(50));
                playingAsUserText.setFill(Color.WHITE);
                playingAsContainer.setCenter(playingAsUserText);
            });

            // container for user and play as buttons ------------------------------------------------------------------
            BorderPane userContentContainer = new BorderPane();
            userContentContainer.setLeft(userButton);
            userContentContainer.setRight(playAsButton);
            userContentContainer.setBackground(new Background(
                    new BackgroundFill(Color.rgb(50, 50, 50), new CornerRadii(5), Insets.EMPTY)));
            userContentContainer.setEffect(
                    new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
            VBox.setMargin(userContentContainer, new Insets(5, 10, 5, 10));
            userContentContainerList.add(userContentContainer);
        }

        userContentContainerLayout.getChildren().addAll(userContentContainerList);

        // container for everything on left side of screen -------------------------------------------------------------
        Text usersText = new Text("Users:");
        usersText.setFont(new Font(50));
        usersText.setFill(Color.WHITE);
        BorderPane.setAlignment(usersText, Pos.CENTER);

        ScrollPane userContentScrollPane = new ScrollPane();
        userContentScrollPane.setContent(userContentContainerLayout);
        userContentScrollPane.getStylesheets().add(IO.readCSS(FileType.CSS.DARK_SCROLL_PANE_STYLE));

        BorderPane leftSideContainer = new BorderPane();
        leftSideContainer.setStyle("-fx-background-color: rgb(30, 30, 30);");
        leftSideContainer.setTop(usersText);
        leftSideContainer.setBottom(userContentScrollPane);

        // container for everything on right side of screen ------------------------------------------------------------
        tablePrivilegesText.setFont(new Font(50));
        tablePrivilegesText.setFill(Color.WHITE);
        BorderPane.setAlignment(tablePrivilegesText, Pos.CENTER);

        // DBA's table privileges shown first
        tablePrivilegeListLayoutScrollPane.setContent(listOfTablePrivilegePaneListLayouts.get(0));
        tablePrivilegeListLayoutScrollPane.getStylesheets().add(IO.readCSS(FileType.CSS.DARK_SCROLL_PANE_STYLE));

        BorderPane rightSideContainer = new BorderPane();
        rightSideContainer.setStyle("-fx-background-color: rgb(30, 30, 30);");
        rightSideContainer.setTop(tablePrivilegesText);
        rightSideContainer.setBottom(tablePrivilegeListLayoutScrollPane);

        // top row button for transitioning between screens ------------------------------------------------------------
        HBox topRowButtonLayout = super.getButtonLayout(screenController);

        // container for top row buttons and playing as container ------------------------------------------------------
        BorderPane topContainer = new BorderPane();
        topContainer.setTop(topRowButtonLayout);
        topContainer.setBottom(playingAsContainer);

        // overall layout for the user screen --------------------------------------------------------------------------
        BorderPane usersScreenLayout = new BorderPane();
        usersScreenLayout.setTop(topContainer);
        usersScreenLayout.setLeft(leftSideContainer);
        usersScreenLayout.setRight(rightSideContainer);
        usersScreenLayout.setStyle("-fx-background-color: rgb(30, 30, 30);");

        this.usersScreen = new Scene(usersScreenLayout);

        // adjust components when the screen is resized ----------------------------------------------------------------
        usersScreen.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = (double) newValue;
            topRowButtonLayout.setPrefWidth(newWidth);
            playingAsContainer.setPrefWidth(newWidth);
            super.adjustButtonWidth(newWidth);
            userContentScrollPane.setPrefWidth(newWidth / 2);
            tablePrivilegeListLayoutScrollPane.setPrefWidth(newWidth / 2);
            listOfTablePrivilegePaneListLayouts.forEach(e -> e.setPrefWidth(newWidth / 2 - 22));
            usersScreenLayout.setPrefWidth(newWidth);
            userContentContainerLayout.setPrefWidth(newWidth / 2 - 22); // 22 is slack for scroll bar
        });

        usersScreen.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newHeight = (double) newValue;
            userContentScrollPane.setPrefHeight(newHeight - 200);
            tablePrivilegeListLayoutScrollPane.setPrefHeight(newHeight - 200);
            userContentContainerLayout.setPrefHeight(newHeight);
        });
    }

    @Override
    public Scene getScreen() {
        return usersScreen;
    }
}