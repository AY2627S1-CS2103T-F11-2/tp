package seedu.clublogistics.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.clublogistics.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.clublogistics.commons.core.GuiSettings;
import seedu.clublogistics.commons.exceptions.DataLoadingException;
import seedu.clublogistics.model.ClubLogisticsData;
import seedu.clublogistics.model.ModelManager;
import seedu.clublogistics.model.UserPrefs;

public class JsonUserPrefsStorageTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonUserPrefsStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readUserPrefs_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readUserPrefs(null));
    }

    private Optional<UserPrefs> readUserPrefs(String userPrefsFileInTestDataFolder) throws DataLoadingException {
        Path prefsFilePath = addToTestDataPathIfNotNull(userPrefsFileInTestDataFolder);
        return new JsonUserPrefsStorage(prefsFilePath).readUserPrefs(prefsFilePath);
    }

    @Test
    public void readUserPrefs_missingFile_emptyResult() throws DataLoadingException {
        assertFalse(readUserPrefs("NonExistentFile.json").isPresent());
    }

    @Test
    public void readUserPrefs_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readUserPrefs("NotJsonFormatUserPrefs.json"));
    }

    @Test
    public void readUserPrefs_nullJsonDocument_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readUserPrefs("NullUserPrefs.json"));
    }

    private Path addToTestDataPathIfNotNull(String userPrefsFileInTestDataFolder) {
        return userPrefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(userPrefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void readUserPrefs_fileInOrder_successfullyRead() throws DataLoadingException {
        UserPrefs expected = getTypicalUserPrefs();
        UserPrefs actual = readUserPrefs("TypicalUserPref.json").get();
        assertEquals(expected, actual);
    }

    @Test
    public void readUserPrefs_valuesMissingFromFile_defaultValuesUsed() throws DataLoadingException {
        UserPrefs actual = readUserPrefs("EmptyUserPrefs.json").get();
        assertEquals(new UserPrefs(), actual);
    }

    @Test
    public void readUserPrefs_extraValuesInFile_extraValuesIgnored() throws DataLoadingException {
        UserPrefs expected = getTypicalUserPrefs();
        UserPrefs actual = readUserPrefs("ExtraValuesUserPref.json").get();

        assertEquals(expected, actual);
    }

    private UserPrefs getTypicalUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1000, 500, 300, 100));
        return userPrefs;
    }

    @Test
    public void savePrefs_nullPrefs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveUserPrefs(null, "SomeFile.json"));
    }

    @Test
    public void saveUserPrefs_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveUserPrefs(new UserPrefs(), null));
    }

    /**
     * Saves {@code userPrefs} at the specified {@code prefsFileInTestDataFolder} filepath.
     */
    private void saveUserPrefs(UserPrefs userPrefs, String prefsFileInTestDataFolder) {
        try {
            new JsonUserPrefsStorage(addToTestDataPathIfNotNull(prefsFileInTestDataFolder))
                    .saveUserPrefs(userPrefs);
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file", ioe);
        }
    }

    @Test
    public void saveUserPrefs_allInOrder_success() throws DataLoadingException, IOException {

        UserPrefs original = new UserPrefs();
        original.setGuiSettings(new GuiSettings(1200, 200, 0, 2));

        Path pefsFilePath = testFolder.resolve("TempPrefs.json");
        JsonUserPrefsStorage jsonUserPrefsStorage = new JsonUserPrefsStorage(pefsFilePath);

        //Try writing when the file doesn't exist
        jsonUserPrefsStorage.saveUserPrefs(original);
        UserPrefs readBack = jsonUserPrefsStorage.readUserPrefs().get();
        assertEquals(original, readBack);

        //Try saving when the file exists
        original.setGuiSettings(new GuiSettings(5, 5, 5, 5));
        jsonUserPrefsStorage.saveUserPrefs(original);
        readBack = jsonUserPrefsStorage.readUserPrefs().get();
        assertEquals(original, readBack);
    }


    /** Verifies an explicit null GUI setting recovers before model initialization. */
    @Test
    public void readUserPrefs_nullGuiSettings_usesDefaultsAndInitializesModel() throws Exception {
        Path prefsPath = testFolder.resolve("nullGuiSettings.json");
        Files.writeString(prefsPath, "{\"guiSettings\":null}");
        JsonUserPrefsStorage storage = new JsonUserPrefsStorage(prefsPath);

        UserPrefs loaded = storage.readUserPrefs().orElseThrow();
        assertEquals(new GuiSettings(), loaded.getGuiSettings());
        assertEquals(new UserPrefs(), new UserPrefs(loaded));
        ModelManager model = new ModelManager(new ClubLogisticsData(), loaded);
        assertEquals(new GuiSettings(), model.getGuiSettings());

        storage.saveUserPrefs(model.getUserPrefs());
        assertEquals(new UserPrefs(), storage.readUserPrefs().orElseThrow());
    }

}
