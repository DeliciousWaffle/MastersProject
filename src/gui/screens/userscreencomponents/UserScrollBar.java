package gui.screens.userscreencomponents;

import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.ArrayList;

/**
 * Component for the UserScreen class that rests in the left corner
 * of the screen. Returns a scrollable list of people in the system.
 */

public class UserScrollBar {

    private ScrollPane mainLayout;
    private VBox userLayout;
    private ArrayList<UserButton> userButtons;

    public UserScrollBar() {

        userLayout = new VBox(10);
        userButtons = new ArrayList<>();

        // add all the users
        userButtons.add(new UserButton("Blah"));
        userLayout.getChildren().add(userButtons.get(0).getButtonLayout());

        mainLayout = new ScrollPane();
        mainLayout.setContent(userLayout);
    }

    public ScrollPane getMainLayout() { return mainLayout; }

    private class UserButton {

        private String userName;
        private HBox buttonLayout;
        private Button userNameButton, playAsButton, deleteButton;

        public UserButton(String userName) {

            this.userName = userName;

            userNameButton = new Button(userName);
            playAsButton = new Button("P"); // use some kind of image
            deleteButton = new Button("X"); // use some kind of image

            // set action listeners


            buttonLayout = new HBox(10);
            buttonLayout.getChildren().addAll(userNameButton, playAsButton, deleteButton);
        }

        public HBox getButtonLayout() { return buttonLayout; }
    }
}
