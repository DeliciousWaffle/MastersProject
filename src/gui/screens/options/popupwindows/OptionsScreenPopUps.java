package gui.screens.options.popupwindows;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class OptionsScreenPopUps {

    // can't instantiate me!
    private OptionsScreenPopUps() {}

    private static class Window extends Stage {

        private Window(Text text, String windowTitle) {

            text.setFont(new Font(35));
            text.setFill(Color.WHITE);
            text.setTextAlignment(TextAlignment.CENTER);

            BorderPane root = new BorderPane();
            root.setCenter(text);
            BorderPane.setMargin(text, new Insets(20));
            root.setBackground(new Background(
                    new BackgroundFill(Color.rgb(40, 40, 40), CornerRadii.EMPTY, Insets.EMPTY)
            ));

            this.setScene(new Scene(root));
            this.setTitle(windowTitle);
            this.setResizable(false);
            this.show();
        }
    }

    public static class VerifierToggleWindow extends Window {
        public VerifierToggleWindow() {
            super(new Text("Toggles the Verifier on or off.\n" +
                    "The Verifier is responsible for checking that tables and users referenced in a query or\n" +
                    "DML statement make sense with respect to what's available on the system.\n" +
                    "Turning off the Verifier will make viewing query costs unavailable and prevent any\n" +
                    "DML statements from executing. However, this will allow you to make references to\n" +
                    "tables or columns that don't exist on the system."),
                    "Verifier Toggle Info"
            );
        }
    }

    public static class JoinOptimizationWindow extends Window {
        public JoinOptimizationWindow() {
            super(new Text("Toggles Join Optimization on or off.\n" +
                    "This has an impact on the ordering of joins when a query is executed and its query tree\n" +
                    "is viewed. When on, the joins will be ordered in such a way to reduce write to disk costs\n" +
                    "when the query tree is being pipelined. This means that subtrees that produce a smaller\n" +
                    "write to disk cost will be executed first while those that are larger are executed last.\n" +
                    "When off, the ordering of subtrees will be determined by how a user writes a query."),
                    "Join Optimization Info"
            );
        }
    }

    public static class RestoreDatabaseWindow extends Window {
        public RestoreDatabaseWindow() {
            super(new Text("Restores the database to its original data.\n" +
                    "This means that alterations made to table or user data will be deleted and\n" +
                    "the original will be used. Warning! You can't undo this process!"),
                    "Restore Database Info"
            );
        }
    }
}