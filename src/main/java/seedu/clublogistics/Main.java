package seedu.clublogistics;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import javafx.application.Application;
import seedu.clublogistics.commons.core.AppPaths;
import seedu.clublogistics.commons.core.LogsCenter;

/**
 * The main entry point to the application.
 *
 * This is a workaround for the following error when MainApp is made the
 * entry point of the application:
 *
 *     Error: JavaFX runtime components are missing, and are required to run this application
 *
 * The reason is that MainApp extends Application. In that case, the
 * LauncherHelper will check for the javafx.graphics module to be present
 * as a named module. We don't use JavaFX via the module system so it can't
 * find the javafx.graphics module, and so the launch is aborted.
 *
 * By having a separate main class (Main) that doesn't extend Application
 * to be the entry point of the application, we avoid this issue.
 */
public class Main {
    private static Logger logger = LogsCenter.getLogger(Main.class);

    /** Launches one application session per home folder. */
    public static void main(String[] args) {

        logger.warning("The warnings about a 'restricted method in java.lang.System' "
            + "and 'enabling native access' appearing below (if any) can be ignored.");
        try {
            launchWithLock(AppPaths.getHomeFolder(), () -> Application.launch(MainApp.class, args));
        } catch (IOException e) {
            logger.severe("Cannot start ClubLogistics: " + e.getMessage());
        }
    }

    static void launchWithLock(Path home, Runnable launchApplication) throws IOException {
        try (AppInstanceLock instanceLock = AppInstanceLock.acquire(home)) {
            launchApplication.run();
        }
    }
}
