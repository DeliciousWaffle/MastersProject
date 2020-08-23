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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
        List<VBox> tablePrivilegePaneListLayoutList = new ArrayList<>();

        ScrollPane tablePrivilegeListLayoutScrollPane = new ScrollPane();

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
            }

            // current user's passable table privileges ----------------------------------------------------------------
            Text passableTablePrivilegesText = new Text("Passable Table Privileges:");
            passableTablePrivilegesText.setFont(new Font(50));
            passableTablePrivilegesText.setFill(Color.WHITE);
            BorderPane.setAlignment(passableTablePrivilegesText, Pos.CENTER);

            List<BorderPane> passableTablePrivilegePaneList = new ArrayList<>();
            for(TablePrivileges passableTablePrivilege : user.getPassableTablePrivilegesList()) {
                BorderPane passableTablePrivilegePane = new TablePrivilegePane(passableTablePrivilege.getTableName(),
                        passableTablePrivilege.getPrivileges(), passableTablePrivilege.getUpdateColumns(),
                        passableTablePrivilege.getReferenceColumns()).getRoot();
                passableTablePrivilegePaneList.add(passableTablePrivilegePane);
                VBox.setMargin(passableTablePrivilegePane, new Insets(10, 20, 10, 20));
            }

            // adding the aforementioned content in a vertical layout --------------------------------------------------
            VBox tablePrivilegePaneListLayout = new VBox();
            tablePrivilegePaneListLayout.setStyle("-fx-background-color: rgb(30, 30, 30);");
            tablePrivilegePaneListLayout.setSpacing(10);
           // VBox.setMargin(tablePrivilegePaneListLayout, new Insets(10));
            tablePrivilegePaneListLayout.getChildren().addAll(tablePrivilegePaneList);
            tablePrivilegePaneListLayout.getChildren().add(passableTablePrivilegesText);
            tablePrivilegePaneListLayout.getChildren().addAll(passableTablePrivilegePaneList);

            tablePrivilegePaneListLayoutList.add(tablePrivilegePaneListLayout);

            // left side of the screen =================================================================================

            // user button ---------------------------------------------------------------------------------------------
            String username = user.getUsername();
            Button userButton = new Button(username + "'s Privileges");
            userButton.setFont(new Font(30));
            userButton.setTextFill(Color.WHITE);
            userButton.setStyle(buttonStyle);
            userButton.setPrefWidth(Screen.defaultWidth / 2 - 40 - 120);
            BorderPane.setMargin(userButton, new Insets(10, 5, 10, 10));
            userButton.setEffect(
                    new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
            userButton.setOnMouseEntered(e -> userButton.setStyle(buttonEnteredStyle));
            userButton.setOnMouseExited(e -> userButton.setStyle(buttonStyle));

            // on click, display this user's table privileges and passable table privileges
            userButton.setOnAction(e -> {
                tablePrivilegeListLayoutScrollPane.setContent(tablePrivilegePaneListLayout);
            });

            // play as button ------------------------------------------------------------------------------------------
            Button playAsButton = new Button("Play");
            playAsButton.setFont(new Font(30));
            playAsButton.setTextFill(Color.WHITE);
            playAsButton.setStyle(buttonStyle);
            playAsButton.setPrefWidth(Screen.defaultWidth / 2 - 40 - 400);
            BorderPane.setMargin(playAsButton, new Insets(10, 10, 10, 5));
            playAsButton.setEffect(
                    new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));
            playAsButton.setOnMouseEntered(e -> playAsButton.setStyle(buttonEnteredStyle));
            playAsButton.setOnMouseExited(e -> playAsButton.setStyle(buttonStyle));
            playAsButton.setOnAction(e -> {
                systemCatalog.setCurrentUser(user);

            });

            // container for user and play as buttons ------------------------------------------------------------------
            BorderPane userContentContainer = new BorderPane();
            userContentContainer.setStyle("-fx-background-color: rgb(30, 30, 30);");
            userContentContainer.setLeft(userButton);
            userContentContainer.setRight(playAsButton);

            // adding everything to the containers ---------------------------------------------------------------------
            userContentContainerLayout.getChildren().add(userContentContainer);
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

        BorderPane leftSideContainer = new BorderPane();
        leftSideContainer.setStyle("-fx-background-color: blue;");
        leftSideContainer.setTop(usersText);
        leftSideContainer.setBottom(userContentScrollPane);

        // container for everything on right side of screen ------------------------------------------------------------
        Text tablePrivilegesText = new Text("Table Privileges:");
        tablePrivilegesText.setFont(new Font(50));
        tablePrivilegesText.setFill(Color.WHITE);
        BorderPane.setAlignment(tablePrivilegesText, Pos.CENTER);

        // DBA's table privileges shown first
        tablePrivilegeListLayoutScrollPane.setContent(tablePrivilegePaneListLayoutList.get(0));
        tablePrivilegeListLayoutScrollPane.getStylesheets().add(IO.readCSS(FileType.CSS.SCROLL_PANE_STYLE));
        tablePrivilegeListLayoutScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        BorderPane rightSideContainer = new BorderPane();
        rightSideContainer.setStyle("-fx-background-color: rgb(30, 30, 30);");
        rightSideContainer.setTop(tablePrivilegesText);
        rightSideContainer.setBottom(tablePrivilegeListLayoutScrollPane);

        // top row button for transitioning between screens ------------------------------------------------------------
        HBox topRowButtonLayout = super.getButtonLayout(screenController);

        // overall layout for the user screen --------------------------------------------------------------------------
        BorderPane usersScreenLayout = new BorderPane();
        usersScreenLayout.setTop(topRowButtonLayout);
        usersScreenLayout.setLeft(leftSideContainer);
        usersScreenLayout.setRight(rightSideContainer);
        usersScreenLayout.setPrefSize(Screen.defaultWidth, Screen.defaultHeight);
        usersScreenLayout.setStyle("-fx-background-color: rgb(30, 30, 30);");

        usersScreen = new Scene(usersScreenLayout);

        // adjust components when the screen is resized ----------------------------------------------------------------
        usersScreen.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = (double) newValue;
            topRowButtonLayout.setMaxWidth(newWidth);
            super.adjustButtonWidth(newWidth);
            userContentScrollPane.setPrefWidth(newWidth / 2);
            tablePrivilegeListLayoutScrollPane.setPrefWidth(newWidth / 2);
            /*for(List<BorderPane> tablePrivilegePaneList : tablePrivilegePaneListLayoutList) {
                for(BorderPane currentPrivilegePane : tablePrivilegePaneList) {
                    currentPrivilegePane.setPrefWidth(newWidth / 2);
                }
            }*/
        });

        usersScreen.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newHeight = (double) newValue;
            userContentScrollPane.setPrefHeight(newHeight - 135);
            tablePrivilegeListLayoutScrollPane.setPrefHeight(newHeight - 135);
        });
    }

    @Override
    public Scene getScreen() {
        return usersScreen;
    }
}