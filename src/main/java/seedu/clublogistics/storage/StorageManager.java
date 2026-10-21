package seedu.clublogistics.storage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.clublogistics.commons.core.LogsCenter;
import seedu.clublogistics.commons.exceptions.DataLoadingException;
import seedu.clublogistics.model.ReadOnlyClubLogisticsData;
import seedu.clublogistics.model.ReadOnlyUserPrefs;
import seedu.clublogistics.model.UserPrefs;

/**
 * Manages storage of ClubLogistics data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private JsonClubLogisticsStorage clubLogisticsStorage;
    private JsonUserPrefsStorage userPrefsStorage;

    /**
     * Creates a {@code StorageManager} with the given ClubLogistics data and user prefs storage.
     */
    public StorageManager(JsonClubLogisticsStorage clubLogisticsStorage, JsonUserPrefsStorage userPrefsStorage) {
        this.clubLogisticsStorage = clubLogisticsStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ ClubLogisticsData methods ==============================

    /** {@inheritDoc} */
    @Override
    public Path getClubLogisticsDataFilePath() {
        return clubLogisticsStorage.getClubLogisticsDataFilePath();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<ReadOnlyClubLogisticsData> readClubLogisticsData() throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + clubLogisticsStorage.getClubLogisticsDataFilePath());
        return clubLogisticsStorage.readClubLogisticsData();
    }

    /** {@inheritDoc} */
    @Override
    public void saveClubLogisticsData(ReadOnlyClubLogisticsData clubLogisticsData) throws IOException {
        logger.fine("Attempting to write to data file: " + clubLogisticsStorage.getClubLogisticsDataFilePath());
        clubLogisticsStorage.saveClubLogisticsData(clubLogisticsData);
    }

}
