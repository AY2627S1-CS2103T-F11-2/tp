package seedu.clublogistics;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Shows why ClubLogistics could not start, then exits.
 * Used when the app is launched without a console, where a log message alone would go unseen.
 */
public class StartupProblemApp extends Application {

    /** Shows the problem passed as the first launch parameter and exits after it is dismissed. */
    @Override
    public void start(Stage primaryStage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ClubLogistics");
        alert.setHeaderText("ClubLogistics cannot start");
        alert.setContentText(getParameters().getRaw().get(0));
        alert.showAndWait();
        Platform.exit();
    }
}
