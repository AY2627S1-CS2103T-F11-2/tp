package seedu.clublogistics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import seedu.clublogistics.commons.core.AppPaths;
import seedu.clublogistics.commons.core.LogsCenter;
import seedu.clublogistics.commons.exceptions.DataLoadingException;
import seedu.clublogistics.commons.util.StringUtil;
import seedu.clublogistics.logic.Logic;
import seedu.clublogistics.logic.LogicManager;
import seedu.clublogistics.model.ClubLogisticsData;
import seedu.clublogistics.model.Model;
import seedu.clublogistics.model.ModelManager;
import seedu.clublogistics.model.ReadOnlyClubLogisticsData;
import seedu.clublogistics.model.ReadOnlyUserPrefs;
import seedu.clublogistics.model.UserPrefs;
import seedu.clublogistics.model.util.SampleDataUtil;
import seedu.clublogistics.storage.JsonClubLogisticsStorage;
import seedu.clublogistics.storage.JsonUserPrefsStorage;
import seedu.clublogistics.storage.Storage;
import seedu.clublogistics.storage.StorageManager;
import seedu.clublogistics.ui.Ui;
import seedu.clublogistics.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final String VERSION = "V1.5";

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final Path USER_PREFS_FILE_PATH = AppPaths.getHomeFolder().resolve("preferences.json");
    private static final Path CLUB_LOGISTICS_FILE_PATH = AppPaths.getHomeFolder().resolve("data/clublogistics.json");

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;

    private Optional<String> legacyMigrationNotice = Optional.empty();

    /** Initializes components unless a legacy register needs manual migration first. */
    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing ClubLogistics ]===========================");
        super.init();
        legacyMigrationNotice = getLegacyMigrationNotice(CLUB_LOGISTICS_FILE_PATH);
        if (legacyMigrationNotice.isPresent()) {
            return;
        }

        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(USER_PREFS_FILE_PATH);
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        JsonClubLogisticsStorage clubLogisticsStorage = new JsonClubLogisticsStorage(CLUB_LOGISTICS_FILE_PATH);
        storage = new StorageManager(clubLogisticsStorage, userPrefsStorage);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic, storage.getClubLogisticsDataFilePath());
    }

    /** Returns migration instructions when only the old v1.3 equipment file exists. */
    protected Optional<String> getLegacyMigrationNotice(Path destination) {
        Path absoluteDestination = destination.toAbsolutePath();
        Path legacy = absoluteDestination.resolveSibling("addressbook.json");
        if (Files.exists(absoluteDestination) || !Files.exists(legacy)) {
            return Optional.empty();
        }
        return Optional.of("A v1.3 register was found at:\n" + legacy
                + "\n\nBack up the entire data folder before continuing. With the app closed, copy "
                + "addressbook.json to:\n" + absoluteDestination
                + "\n\nKeep the original file. Restart and verify equipment IDs and current borrowers "
                + "against the original before changing records. See the User Guide’s upgrade instructions."
                + "\n\nClubLogistics will now close without creating sample data or changing either register.");
    }

    /**
     * Loads ClubLogistics data and preferences into a model manager.
     * Uses sample data when the file is missing, or empty data when loading fails.
     */
    protected Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getClubLogisticsDataFilePath());

        Optional<ReadOnlyClubLogisticsData> clubLogisticsDataOptional;
        ReadOnlyClubLogisticsData initialData;
        try {
            clubLogisticsDataOptional = storage.readClubLogisticsData();
            if (clubLogisticsDataOptional.isEmpty()) {
                logger.info("Creating a new data file " + storage.getClubLogisticsDataFilePath()
                        + " populated with a sample ClubLogisticsData.");
            }
            initialData = clubLogisticsDataOptional.orElseGet(SampleDataUtil::getSampleClubLogisticsData);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getClubLogisticsDataFilePath() + " could not be loaded."
                    + " Will be starting with an empty ClubLogisticsData.");
            initialData = new ClubLogisticsData();
        }

        return new ModelManager(initialData, userPrefs);
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (prefsOptional.isEmpty()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save preference file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    /** Shows migration instructions or starts the equipment window. */
    @Override
    public void start(Stage primaryStage) {
        if (legacyMigrationNotice.isPresent()) {
            showLegacyMigrationNotice();
            return;
        }
        logger.info("Starting ClubLogistics " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    private void showLegacyMigrationNotice() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ClubLogistics upgrade");
        alert.setHeaderText("Migrate your v1.3 equipment register before continuing");
        alert.setContentText(legacyMigrationNotice.orElseThrow());
        alert.showAndWait();
        Platform.exit();
    }

    /** Saves preferences after an ordinary session; migration-only startup writes nothing. */
    @Override
    public void stop() {
        if (storage == null) {
            return;
        }
        logger.info("============================ [ Stopping ClubLogistics ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
