package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.address.testutil.TypicalPersons.getTypicalClubLogisticsData;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.ClubLogisticsData;
import seedu.address.model.ReadOnlyClubLogisticsData;
import seedu.address.model.UserPrefs;

public class StorageManagerTest {

    @TempDir
    public Path testFolder;

    private StorageManager storageManager;

    @BeforeEach
    public void setUp() {
        JsonClubLogisticsStorage clubLogisticsStorage =
                new JsonClubLogisticsStorage(getTempFilePath("clubLogisticsData"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(getTempFilePath("prefs"));
        storageManager = new StorageManager(clubLogisticsStorage, userPrefsStorage);
    }

    private Path getTempFilePath(String fileName) {
        return testFolder.resolve(fileName);
    }

    @Test
    public void prefsReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonUserPrefsStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonUserPrefsStorageTest} class.
         */
        UserPrefs original = new UserPrefs();
        original.setGuiSettings(new GuiSettings(300, 600, 4, 6));
        storageManager.saveUserPrefs(original);
        UserPrefs retrieved = storageManager.readUserPrefs().get();
        assertEquals(original, retrieved);
    }

    @Test
    public void clubLogisticsDataReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonClubLogisticsStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonClubLogisticsStorageTest} class.
         */
        ClubLogisticsData original = getTypicalClubLogisticsData();
        storageManager.saveClubLogisticsData(original);
        ReadOnlyClubLogisticsData retrieved = storageManager.readClubLogisticsData().get();
        assertEquals(original, new ClubLogisticsData(retrieved));
    }

    @Test
    public void getClubLogisticsDataFilePath() {
        assertNotNull(storageManager.getClubLogisticsDataFilePath());
    }

}
