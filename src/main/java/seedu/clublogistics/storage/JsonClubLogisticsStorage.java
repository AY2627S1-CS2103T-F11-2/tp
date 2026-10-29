package seedu.clublogistics.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.clublogistics.commons.core.LogsCenter;
import seedu.clublogistics.commons.exceptions.DataLoadingException;
import seedu.clublogistics.commons.exceptions.IllegalValueException;
import seedu.clublogistics.commons.util.JsonUtil;
import seedu.clublogistics.model.ReadOnlyClubLogisticsData;

/**
 * A class to access ClubLogistics data stored as a JSON file on the hard disk.
 */
public class JsonClubLogisticsStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonClubLogisticsStorage.class);

    private Path filePath;

    /** Creates storage for the ClubLogistics data file at {@code filePath}. */
    public JsonClubLogisticsStorage(Path filePath) {
        this.filePath = filePath;
    }

    /** Returns the location of the ClubLogistics data file. */
    public Path getClubLogisticsDataFilePath() {
        return filePath;
    }

    /**
     * Returns ClubLogistics data as a {@link ReadOnlyClubLogisticsData}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyClubLogisticsData> readClubLogisticsData() throws DataLoadingException {
        return readClubLogisticsData(filePath);
    }

    /**
     * Similar to {@link #readClubLogisticsData()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyClubLogisticsData> readClubLogisticsData(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableClubLogisticsData> jsonClubLogisticsData = JsonUtil.readJsonFile(
                filePath, JsonSerializableClubLogisticsData.class);
        if (!jsonClubLogisticsData.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonClubLogisticsData.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    /**
     * Saves the given {@link ReadOnlyClubLogisticsData} to the storage.
     * @param clubLogisticsData cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    public void saveClubLogisticsData(ReadOnlyClubLogisticsData clubLogisticsData) throws IOException {
        saveClubLogisticsData(clubLogisticsData, filePath);
    }

    /**
     * Similar to {@link #saveClubLogisticsData(ReadOnlyClubLogisticsData)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveClubLogisticsData(ReadOnlyClubLogisticsData clubLogisticsData, Path filePath) throws IOException {
        requireNonNull(clubLogisticsData);
        requireNonNull(filePath);

        JsonUtil.saveJsonFile(new JsonSerializableClubLogisticsData(clubLogisticsData), filePath);
    }

}
