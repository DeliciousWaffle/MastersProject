package gui.screens.users;

import datastructures.user.User;
import datastructures.user.component.TablePrivileges;
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

        // top row button for transitioning between screens
        HBox topRowButtonLayout = super.getButtonLayout(screenController);

        // storing user's table privileges on right side of the screen -------------------------------------------------
        BorderPane completeUserPrivilegesLayout = new BorderPane(); // contains everything on the right side of the screen

        Text userPrivilegesText = new Text("User's Privileges");
        userPrivilegesText.setFont(new Font(50));

        ScrollPane tablePrivilegeListScrollPane = new ScrollPane();
        VBox tablePrivilegesList = new VBox();

        tablePrivilegeListScrollPane.setPrefWidth(Screen.defaultWidth / 2); // remove

        List<List<BorderPane>> userTablePrivilegePanesList = new ArrayList<>();

        for(User user : users) {
            List<TablePrivilegePane> userTablePrivilegePanes = new ArrayList<>();
            for (TablePrivileges tablePrivilege : user.getTablePrivilegesList()) {
                BorderPane tablePrivilegePane = new TablePrivilegePane(tablePrivilege.getPrivileges(), tablePrivilege.getUpdateColumns(), tablePrivilege.getReferenceColumns()).getTablePrivilegePane();
                //userTablePrivilegePanes.add(tablePrivilegePane);
                // TODO this
            }
        }

        //tablePrivilegesList.getChildren().add(
        tablePrivilegeListScrollPane.setContent(tablePrivilegesList);

        completeUserPrivilegesLayout.setTop(userPrivilegesText);
        completeUserPrivilegesLayout.setBottom(tablePrivilegeListScrollPane);

        // passable table privileges
        /*Text passableTablePrivilegesText = new Text("Passable Table Privileges Text");
        passableTablePrivilegesText.setFont(new Font(50));
        passableTablePrivilegesText.setFill(Color.WHITE);
        BorderPane.setAlignment(passableTablePrivilegesText, Pos.CENTER);

        tablePrivilegesList.getChildren().add(passableTablePrivilegesText);

        for(User user : users) {
            tablePrivilegesList.getChildren().add(new TablePrivilegePane(user.getPassableTablePrivilegesList()).getTablePrivilegePane());
        }*/

        Text tablePrivilegesText = new Text("Table Privileges:");
        tablePrivilegesText.setFont(new Font(50));
        tablePrivilegesText.setFill(Color.WHITE);
        BorderPane.setAlignment(tablePrivilegesText, Pos.CENTER);

        completeUserPrivilegesLayout.setTop(tablePrivilegesText);
        completeUserPrivilegesLayout.setBottom(tablePrivilegeListScrollPane);

        // storing user data on left side of screen --------------------------------------------------------------------
        BorderPane completeUserLayout = new BorderPane();
        ScrollPane userContentContainerScrollPane = new ScrollPane();

        VBox userContentContainer = new VBox();
        userContentContainer.setSpacing(20);

        Text usersText = new Text("Users:");
        usersText.setFont(new Font(50));
        usersText.setFill(Color.WHITE);
        BorderPane.setAlignment(usersText, Pos.CENTER);

        String buttonStyle = " -fx-background-color: rgb(100, 100, 100); -fx-text-fill: white;";
        String buttonEnteredStyle = "-fx-background-color: rgb(150, 150, 150); -fx-text-fill: white;";

        // creating user and play as buttons for each user in the system
        for(User user : users) {

            // contains the user and play as buttons
            BorderPane userContentLayout = new BorderPane();

            String username = user.getUsername();

            Button userButton = new Button(username + "'s Privileges");
            userButton.setFont(new Font(30));
            userButton.setTextFill(Color.WHITE);
            userButton.setStyle(buttonStyle);
            userButton.setPrefWidth(Screen.defaultWidth / 2 - 40 - 100); // 40 to account for insets, 400 is actual width
            BorderPane.setMargin(userButton, new Insets(0, 10, 0, 20));
            userButton.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

            userButton.setOnMouseEntered(e -> userButton.setStyle(buttonEnteredStyle));
            userButton.setOnMouseExited(e -> userButton.setStyle(buttonStyle));

            Button playAsButton = new Button("Play");
            playAsButton.setFont(new Font(30));
            playAsButton.setTextFill(Color.WHITE);
            playAsButton.setStyle(buttonStyle);
            playAsButton.setPrefWidth(Screen.defaultWidth / 2 - 40 - 400); // 40 to account for insets, 100 is actual width
            BorderPane.setMargin(playAsButton, new Insets(0, 20, 0, 10));

            playAsButton.setOnMouseEntered(e -> playAsButton.setStyle(buttonEnteredStyle));
            playAsButton.setOnMouseExited(e -> playAsButton.setStyle(buttonStyle));
            playAsButton.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

            playAsButton.setOnAction(e -> {
                systemCatalog.setCurrentUser(user);
                System.out.println("User set " + user);
            });

            userContentLayout.setLeft(userButton);
            userContentLayout.setRight(playAsButton);

            userContentContainer.getChildren().add(userContentLayout);
        }

        userContentContainerScrollPane.setContent(userContentContainer);

        completeUserLayout.setTop(usersText);
        completeUserLayout.setCenter(userContentContainerScrollPane);

        // overall layout for the user screen --------------------------------------------------------------------------
        BorderPane usersScreenLayout = new BorderPane();
        usersScreenLayout.setTop(topRowButtonLayout);
        usersScreenLayout.setLeft(completeUserLayout);
        usersScreenLayout.setRight(completeUserPrivilegesLayout);
        usersScreenLayout.setPrefSize(Screen.defaultWidth, Screen.defaultHeight);
        usersScreenLayout.setStyle("-fx-background-color: rgb(30, 30, 30);");

        usersScreen = new Scene(usersScreenLayout);

        // adjust components when the screen is resized ----------------------------------------------------------------
        usersScreen.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = (double) newValue;
            topRowButtonLayout.setMaxWidth(newWidth);
            super.adjustButtonWidth(newWidth);

        });

        usersScreen.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newHeight = (double) newValue;

        });
    }

    @Override
    public Scene getScreen() {
        return usersScreen;
    }
}