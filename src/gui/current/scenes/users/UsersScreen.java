package gui.current.scenes.users;

import datastructure.user.User;
import file.io.Filename;
import file.io.IO;
import file.io.Serialize;
import gui.current.ScreenController;
import gui.current.scenes.Screen;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class UsersScreen extends Screen {

    private Scene usersScreen;

    public UsersScreen(ScreenController screenController) {

        List<User> users = Serialize.unSerializeUsers(IO.read(Filename.CURRENT_USERS));

        // top row button for transitioning between screens
        HBox buttonLayout = super.getButtonLayout(screenController);

        // used to store all the user buttons
        VBox userButtonsContainer = new VBox();

        // list of user buttons
        List<Button> userButtonList = new ArrayList<>();

        // creating and adding to the user button list
        for(User user : users) {
            String username = user.getUsername();
            Button userButton = new Button(username);
            userButton.setFont(new Font(25));
            //userButton.setTextFill(Color.WHITE);
            //userButton.getStylesheets().add("current/scenes/Button.css");
            userButtonList.add(userButton);
            userButtonsContainer.getChildren().add(userButton);
        }

        // each user will have their own content mapped to them
        List<BorderPane> usersContentList = new ArrayList<>(); // TODO

        for(int i = 0; i < users.size(); i++) {

            User user = users.get(i);

            BorderPane userContentPane = new UserContentPane(user).getUserContentPane();
            usersContentList.add(userContentPane);

            // map the user button to the user content pane
            Button userButton = userButtonList.get(i);
        }

        // users will be stored on left side of screen, on click, displays their information on right side
        BorderPane userContentContainer = new BorderPane();

        userContentContainer.setLeft(userButtonsContainer);
        userContentContainer.setRight(usersContentList);

        BorderPane usersScreenLayout = new BorderPane();
        usersScreenLayout.setTop(buttonLayout);
        usersScreenLayout.setPrefSize(Screen.defaultWidth, Screen.defaultHeight);
        usersScreenLayout.setStyle("-fx-background-color: rgb(30, 30, 30);");

        usersScreen = new Scene(usersScreenLayout);
    }

    @Override
    public Scene getScreen() {
        return usersScreen;
    }
}