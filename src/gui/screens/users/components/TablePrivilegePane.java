package gui.screens.users.components;

import datastructures.user.component.Privilege;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the privileges that a user has on a single table.
 */
public class TablePrivilegePane {

    private BorderPane tablePrivilegePane;

    public TablePrivilegePane(String tableName, List<Privilege> privileges, List<String> updateColumns,
                              List<String> referenceColumns) {

        // will contain everything
        this.tablePrivilegePane = new BorderPane();

        Text tableNameText = new Text("Privileges on " + tableName + ":");
        tableNameText.setFont(new Font(35));
        tableNameText.setFill(Color.WHITE);

        // list of privilege containers (privilege container is text within a border pane)
        VBox privilegeListContainer = new VBox();
        privilegeListContainer.setStyle("-fx-background-color: rgb(60, 60, 60);");

        List<Text> privilegeTextList = new ArrayList<>();
        List<BorderPane> privilegeTextContainerList = new ArrayList<>();

        for(int i = 0; i < privileges.size(); i++) {

            Privilege privilege = privileges.get(i);

            // adding this is redundant
            if (privilege == Privilege.ALL_PRIVILEGES) {
                continue;
            }

            // converting privilege to string and capitalizing first letter
            String privilegeString = privilege.toString();
            privilegeString = privilegeString.substring(0, 1).toUpperCase() +
                    privilegeString.substring(1).toLowerCase();

            if(privilege == Privilege.UPDATE) {

                StringBuilder updateCols = new StringBuilder();

                for(String updateCol : updateColumns) {
                    updateCols.append(updateCol).append(",\n");
                }

                // remove ",\n"
                updateCols.deleteCharAt(updateCols.length() - 1);
                updateCols.deleteCharAt(updateCols.length() - 1);

                // append to the privilege string
                privilegeString += (":\n" + updateCols.toString());
            }

            if(privilege == Privilege.REFERENCES) {

                StringBuilder refCols = new StringBuilder();

                for(String refCol : referenceColumns) {
                    refCols.append(refCol).append(",\n");
                }

                refCols.deleteCharAt(refCols.length() - 1);
                refCols.deleteCharAt(refCols.length() - 1);

                privilegeString += (":\n" + refCols.toString());
            }

            // convert the current privilege into text
            Text privilegeText = new Text(privilegeString);
            privilegeText.setFont(new Font(25));
            privilegeText.setFill(Color.WHITE);
            privilegeText.setTextAlignment(TextAlignment.CENTER);

            privilegeTextList.add(privilegeText);

            // container for the privilege text, used to add a background
            BorderPane privilegeTextContainer = new BorderPane(privilegeText);
            privilegeTextContainer.setBackground(new Background(new BackgroundFill(
                    Color.rgb(90, 90, 90), new CornerRadii(5), Insets.EMPTY)));
            privilegeTextContainer.setEffect(
                    new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

            boolean firstPrivilege = i == 0;
            boolean lastPrivilege = i == privileges.size() - 1;

            if(firstPrivilege) {
                VBox.setMargin(privilegeTextContainer, new Insets(10, 10, 5, 10));
            } else if(lastPrivilege) {
                VBox.setMargin(privilegeTextContainer, new Insets(5, 10, 10, 10));
            } else {
                VBox.setMargin(privilegeTextContainer, new Insets(10, 10, 10, 10));
            }

            privilegeTextContainerList.add(privilegeTextContainer);
        }

        // add the privilege container to the list of privilege containers
        privilegeListContainer.getChildren().addAll(privilegeTextContainerList);

        tablePrivilegePane.setTop(tableNameText);
        tablePrivilegePane.setBottom(privilegeListContainer);
        tablePrivilegePane.setBackground(new Background(
                new BackgroundFill(Color.rgb(60, 60, 60), new CornerRadii(5), Insets.EMPTY)));
        tablePrivilegePane.setEffect(
                new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

        BorderPane.setAlignment(tableNameText, Pos.CENTER);
        BorderPane.setMargin(tableNameText, new Insets(10, 0, 10, 0));
        BorderPane.setMargin(privilegeListContainer, new Insets(0, 10, 10, 10));
    }

    public BorderPane getTablePrivilegePane() {
        return tablePrivilegePane;
    }
}