package seedu.clublogistics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    /** A v1.3 register must be detected before samples are offered, without changing either file. */
    @Test
    public void legacyMigrationNotice_onlyLegacyExists_explainsSafeCopy() throws Exception {
        Path legacy = testFolder.resolve("addressbook.json");
        Path destination = testFolder.resolve("clublogistics.json");
        String data = "{\"equipment\":[{\"id\":\"UPG001\",\"name\":\"Tripod\",\"issuedTo\":\"Alice Tan\"}]}";
        Files.writeString(legacy, data);

        String notice = new MainApp().getLegacyMigrationNotice(destination).orElseThrow();

        assertTrue(notice.contains(legacy.toString()));
        assertTrue(notice.contains(destination.toString()));
        assertTrue(notice.contains("Back up"));
        assertTrue(notice.contains("copy"));
        assertEquals(data, Files.readString(legacy));
        assertTrue(Files.notExists(destination));

        Files.copy(legacy, destination);
        var migrated = new JsonClubLogisticsStorage(destination).readClubLogisticsData().orElseThrow();
        assertEquals("UPG001", migrated.getEquipmentList().get(0).getId().toString());
        assertEquals("Alice Tan", migrated.getEquipmentList().get(0).getIssuedTo().orElseThrow().toString());
        assertEquals(data, Files.readString(legacy));
    }

    /** An existing new-format register takes precedence even when the legacy file remains as a backup. */
    @Test
    public void legacyMigrationNotice_destinationExists_doesNotInterruptStartup() throws IOException {
        Files.writeString(testFolder.resolve("addressbook.json"), "legacy backup");
        Path destination = testFolder.resolve("clublogistics.json");
        Files.writeString(destination, "{\"equipment\":[]}");
        assertTrue(new MainApp().getLegacyMigrationNotice(destination).isEmpty());
    }

    /** Fresh installations have no upgrade notice. */
    @Test
    public void legacyMigrationNotice_noLegacy_doesNotInterruptStartup() {
        assertTrue(new MainApp().getLegacyMigrationNotice(testFolder.resolve("clublogistics.json")).isEmpty());
    }

    private Path writeNullJsonDocument(String fileName) throws IOException {
        Path filePath = testFolder.resolve(fileName);
        Files.writeString(filePath, NULL_JSON_DOCUMENT);
        return filePath;
    }
}
