package gui.screens.options.popupwindows;

import files.io.FileType;
import files.io.IO;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.awt.*;

public class OptionsScreenPopUps {

    // can't instantiate me!
    private OptionsScreenPopUps() {}

    private static class Window extends Stage {

        private Window(Text text, String windowType) {

            BorderPane container = new BorderPane();
            container.setBackground(new Background(
                    new BackgroundFill(Color.rgb(30, 30, 30), CornerRadii.EMPTY, Insets.EMPTY)
            ));

            Text windowTextType = new Text(windowType);
            windowTextType.setFont(new Font(50));
            windowTextType.setFill(Color.WHITE);
            windowTextType.setTextAlignment(TextAlignment.CENTER);
            container.setTop(windowTextType);

            text.setFont(new Font(35));
            text.setFill(Color.WHITE);
            text.setTextAlignment(TextAlignment.CENTER);
            container.setBottom(text);

            ScrollPane scrollPane = new ScrollPane(container);
            scrollPane.setMinSize(1080, 720);
            scrollPane.setPrefSize(1080, 720);
            scrollPane.setMaxSize(1080, 720);
            scrollPane.getStylesheets().addAll(IO.readCSS(FileType.CSS.DARK_SCROLL_PANE_STYLE));
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToWidth(true);

            this.setScene(new Scene(container));
            this.setResizable(false);
            this.setTitle(windowType);
            this.show();
        }
    }

    public static class VerifierToggleWindow extends Window {
        public VerifierToggleWindow() {
            super(new Text("Toggles the Verifier on or off. The Verifier is responsible for checking that tables and " +
                            "users referenced in a query or DML statement make sense with respect to what's " +
                            "available on the system. Turning off the Verifier will prevent you from viewing result " +
                            "set data and query costs. However, this will allow you to make references to tables or " +
                            "columns that don't otherwise exist on the system. It should be noted that a query tree " +
                            "and recommended file structures will still be produced."
                    ),
                    "Verifier Toggle Info"
            );
        }
    }

    public static class SecurityCheckerToggleWindow extends Window {
        public SecurityCheckerToggleWindow() {
            super(new Text("Toggles the Security Checker on or off. The Security Checker is responsible for making " +
                            "sure the current user has the correct privileges on a table referenced in the given " +
                            "input. Turning off the Security Checker allows any user to execute a query or DML " +
                            "statement without worrying about whether they have the correct privileges."
                    ),
                    "Security checker Toggle Info"
            );
        }
    }

    public static class JoinOptimizationToggleWindow extends Window {
        public JoinOptimizationToggleWindow() {
            super(new Text("Toggles Join Optimization on or off. When on, the joins will be ordered in such a way " +
                            "that the smallest relations will be located in the deepest part of the tree. When " +
                            "pipelining, operations will perform on smaller relations produced as opposed to larger " +
                            "ones which will reduce overall query costs."),
                    "Join Optimization Info"
            );
        }
    }

    public static class SaveDataWindow extends Window {
        public SaveDataWindow() {
            super(new Text("Simply saves the current state of the system. When you re-launch the app, all changes " +
                            "made will be present."),
                    "Save Database Info");
        }
    }

    public static class RestoreDataWindow extends Window {
        public RestoreDataWindow() {
            super(new Text("Restores the database to its original data. This means that alterations made to table or " +
                            "user data will be deleted and the original will be used. Warning! You can't undo this " +
                            "process!"),
                    "Restore Database Info"
            );
        }
    }
}