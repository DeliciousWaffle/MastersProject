package gui.screens.users.components;

import datastructures.user.component.Privilege;
import datastructures.user.component.TablePrivileges;
import javafx.geometry.Insets;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.List;

public class TablePrivilegePane {

    private BorderPane tablePrivilegePane;

    public TablePrivilegePane(List<Privilege> privileges, List<String> updateColumns, List<String> referenceColumns) {

        this.tablePrivilegePane = new BorderPane();

        VBox tablePrivilegesListContainer = new VBox();

        for(Privilege privilege : privileges) {

            String privilegeString = privilege.toString();
            privilegeString = privilegeString.substring(0,1).toUpperCase() + privilegeString.substring(1).toLowerCase();

            if(privilege == Privilege.UPDATE) {

                StringBuilder updateCols = new StringBuilder();

                for(String updateCol : updateColumns) {
                    updateCols.append(updateCol).append(", ");
                }

                // remove ", "
                updateCols.deleteCharAt(updateCols.length() - 1);
                updateCols.deleteCharAt(updateCols.length() - 1);

                // append to the privilege string
                privilegeString += (": " + updateCols.toString());
            }

            if(privilege == Privilege.REFERENCES) {

                StringBuilder refCols = new StringBuilder();

                for(String refCol : referenceColumns) {
                    refCols.append(refCol).append(", ");
                }

                refCols.deleteCharAt(refCols.length() - 1);
                refCols.deleteCharAt(refCols.length() - 1);

                privilegeString += (": " + refCols.toString());
            }

            Text text = new Text(privilegeString);
            text.setFont(new Font(20));
            text.setFill(Color.WHITE);

            // container for the privilege
            BorderPane privilegeContainer = new BorderPane(text);
            privilegeContainer.setStyle("-fx-background-color: rgb(90, 90, 90);");
            privilegeContainer.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 10, 0.2, 3, 3));

            tablePrivilegesListContainer.getChildren().add(privilegeContainer);
        }

        tablePrivilegePane.setCenter(tablePrivilegesListContainer);
    }

    public BorderPane getTablePrivilegePane() {
        return tablePrivilegePane;
    }
}