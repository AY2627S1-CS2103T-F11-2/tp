package seedu.clublogistics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.clublogistics.model.ClubLogisticsData;
import seedu.clublogistics.model.Model;
import seedu.clublogistics.model.UserPrefs;
import seedu.clublogistics.storage.JsonClubLogisticsStorage;
import seedu.clublogistics.storage.JsonUserPrefsStorage;
import seedu.clublogistics.storage.StorageManager;

public class MainAppTest {
    private static final String NULL_JSON_DOCUMENT = "null";

    @TempDir
    public Path testFolder;

    /** Verifies that the application uses its own data file. */
    @Test
    public void dataFilePath_usesClubLogisticsFile() throws Exception {
        Field dataFilePath = MainApp.class.getDeclaredField("CLUB_LOGISTICS_FILE_PATH");
        dataFilePath.setAccessible(true);

        assertEquals(Paths.get("data", "clublogistics.json").toAbsolutePath(), dataFilePath.get(null));
    }

    /** Falls back to default preferences when the preferences file contains only {@code null}. */
    @Test
    public void initPrefs_nullJsonDocument_usesDefaultPrefs() throws IOException {
        Path prefsFilePath = writeNullJsonDocument("preferences.json");

        UserPrefs prefs = new MainApp().initPrefs(new JsonUserPrefsStorage(prefsFilePath));

        assertEquals(new UserPrefs(), prefs);
    }

    /** Starts with an empty register when the data file contains only {@code null}. */
    @Test
    public void initModelManager_nullJsonDocument_usesEmptyData() throws IOException {
        Path dataFilePath = writeNullJsonDocument("clublogistics.json");
        StorageManager storage = new StorageManager(new JsonClubLogisticsStorage(dataFilePath),
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json")));

        Model model = new MainApp().initModelManager(storage, new UserPrefs());

        assertEquals(new ClubLogisticsData(), model.getClubLogisticsData());
    }

    private Path writeNullJsonDocument(String fileName) throws IOException {
        Path filePath = testFolder.resolve(fileName);
        Files.writeString(filePath, NULL_JSON_DOCUMENT);
        return filePath;
    }
}
