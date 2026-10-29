package seedu.clublogistics.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.clublogistics.testutil.Assert.assertThrows;
import static seedu.clublogistics.testutil.TypicalEquipment.CAMERA;
import static seedu.clublogistics.testutil.TypicalEquipment.PROJECTOR;
import static seedu.clublogistics.testutil.TypicalEquipment.getTypicalClubLogisticsData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.clublogistics.commons.exceptions.DataLoadingException;
import seedu.clublogistics.model.ClubLogisticsData;
import seedu.clublogistics.model.ReadOnlyClubLogisticsData;
import seedu.clublogistics.model.equipment.BorrowerName;
import seedu.clublogistics.model.equipment.Equipment;
import seedu.clublogistics.model.equipment.EquipmentId;

public class JsonClubLogisticsStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonClubLogisticsStorageTest");

    @TempDir
    public Path testFolder;

    /** Rejects a null storage path. */
    @Test
    public void readClubLogisticsData_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readClubLogisticsData(null));
    }

    private java.util.Optional<ReadOnlyClubLogisticsData> readClubLogisticsData(String filePath) throws Exception {
        return new JsonClubLogisticsStorage(Paths.get(filePath))
                .readClubLogisticsData(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    /** Returns an empty result for a missing data file. */
    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readClubLogisticsData("NonExistentFile.json").isPresent());
    }

    /** Reports malformed JSON as a loading error. */
    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readClubLogisticsData("notJsonFormatClubLogisticsData.json"));
    }

    /** Reports a JSON document containing only {@code null} as a loading error. */
    @Test
    public void read_nullJsonDocument_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readClubLogisticsData("nullClubLogisticsData.json"));
    }

    /** Verifies saving, updating, and reloading the equipment register. */
    @Test
    public void readAndSaveClubLogisticsData_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempClubLogisticsData.json");
        ClubLogisticsData original = getTypicalClubLogisticsData();
        JsonClubLogisticsStorage jsonClubLogisticsStorage = new JsonClubLogisticsStorage(filePath);

        // Save in new file and read back
        jsonClubLogisticsStorage.saveClubLogisticsData(original, filePath);
        ReadOnlyClubLogisticsData readBack = jsonClubLogisticsStorage.readClubLogisticsData(filePath).get();
        assertEquals(original, new ClubLogisticsData(readBack));

        // Modify data, overwrite existing file, and read back
        original.addEquipment(PROJECTOR);
        original.removeEquipment(CAMERA);
        jsonClubLogisticsStorage.saveClubLogisticsData(original, filePath);
        readBack = jsonClubLogisticsStorage.readClubLogisticsData(filePath).get();
        assertEquals(original, new ClubLogisticsData(readBack));

        // Save and read without specifying file path
        original.addEquipment(CAMERA);
        jsonClubLogisticsStorage.saveClubLogisticsData(original); // file path not specified
        readBack = jsonClubLogisticsStorage.readClubLogisticsData().get(); // file path not specified
        assertEquals(original, new ClubLogisticsData(readBack));

    }

    /** Preserves equipment availability and borrowers across saves. */
    @Test
    public void readAndSaveClubLogisticsData_availableAndIssuedEquipment_success() throws Exception {
        Path filePath = testFolder.resolve("EquipmentClubLogisticsData.json");
        JsonClubLogisticsStorage storage = new JsonClubLogisticsStorage(filePath);
        ClubLogisticsData original = new ClubLogisticsData();
        original.addEquipment(new Equipment(new EquipmentId("CAM001"), "Sony Camera"));
        original.addEquipment(new Equipment(new EquipmentId("MIC001"), "Wireless Microphone",
                new BorrowerName("John Tan")));

        storage.saveClubLogisticsData(original);
        ReadOnlyClubLogisticsData readBack = storage.readClubLogisticsData().get();
        assertEquals(original, new ClubLogisticsData(readBack));
        assertFalse(readBack.getEquipmentList().get(0).isIssued());
        assertEquals(new BorrowerName("John Tan"), readBack.getEquipmentList().get(1).getIssuedTo().get());

        ClubLogisticsData returnedEquipment = new ClubLogisticsData();
        returnedEquipment.addEquipment(new Equipment(new EquipmentId("MIC001"), "Wireless Microphone"));
        storage.saveClubLogisticsData(returnedEquipment);
        assertEquals(returnedEquipment, new ClubLogisticsData(storage.readClubLogisticsData().get()));
    }

    /** Previously accepted names remain loadable and retain their loans after saving and reloading. */
    @Test
    public void readAndSaveClubLogisticsData_legacyNameSymbols_preservesRecords() throws Exception {
        Path filePath = testFolder.resolve("LegacyNames.json");
        Files.writeString(filePath, """
                {"equipment": [
                  {"id": "CAM001", "name": "Camera <HD>"},
                  {"id": "MIC001", "name": "Mic@Club", "issuedTo": "John Tan"}
                ]}
                """);
        JsonClubLogisticsStorage storage = new JsonClubLogisticsStorage(filePath);
        ReadOnlyClubLogisticsData loaded = storage.readClubLogisticsData().orElseThrow();
        assertEquals("Camera <HD>", loaded.getEquipmentList().get(0).getName().value);
        Equipment microphone = loaded.getEquipmentList().get(1);
        assertEquals("Mic@Club", microphone.getName().value);
        assertEquals(new BorrowerName("John Tan"), microphone.getIssuedTo().orElseThrow());
        assertEquals("Mic@Club", microphone.markReturned().getName().value);
        storage.saveClubLogisticsData(loaded);
        assertEquals(new ClubLogisticsData(loaded),
                new ClubLogisticsData(storage.readClubLogisticsData().orElseThrow()));
    }

    /** Loads available equipment from the equipment-only JSON schema. */
    @Test
    public void readClubLogisticsData_equipmentOnly_success() throws Exception {
        Path filePath = testFolder.resolve("EquipmentOnly.json");
        Files.writeString(filePath, """
                {"equipment": [
                  {"id": "cam001", "name": "Sony Camera"},
                  {"id": "MIC001", "name": "Wireless Microphone", "issuedTo": ""}
                ]}
                """);
        ReadOnlyClubLogisticsData readBack = new JsonClubLogisticsStorage(filePath).readClubLogisticsData().get();
        assertEquals(2, readBack.getEquipmentList().size());
        assertEquals(new EquipmentId("CAM001"), readBack.getEquipmentList().get(0).getId());
        assertTrue(readBack.getEquipmentList().stream().noneMatch(Equipment::isIssued));
    }

    /** Accepts an editor's UTF-8 BOM without changing equipment or active loans. */
    @Test
    public void readClubLogisticsData_utf8Bom_preservesRegister() throws Exception {
        Path filePath = testFolder.resolve("Bom.json");
        JsonClubLogisticsStorage storage = new JsonClubLogisticsStorage(filePath);
        ClubLogisticsData original = getTypicalClubLogisticsData();
        original.setEquipment(CAMERA, CAMERA.issueTo(new BorrowerName("Alice Tan")));
        storage.saveClubLogisticsData(original);
        Files.writeString(filePath, "\uFEFF" + Files.readString(filePath));

        assertEquals(original, new ClubLogisticsData(storage.readClubLogisticsData().orElseThrow()));
    }

    /** Rejects invalid records even when valid records precede them. */
    @Test
    public void readClubLogisticsData_invalidEquipmentAfterValidRecord_throwsDataLoadingException() throws Exception {
        Path filePath = testFolder.resolve("InvalidEquipment.json");
        String[] invalidRecords = {
            "{\"name\": \"Camera\"}",
            "{\"id\": \"CAM-002\", \"name\": \"Camera\"}",
            "{\"id\": \"CAM002\"}",
            "{\"id\": \"CAM002\", \"name\": \" \"}",
            "{\"id\": \"CAM002\", \"name\": \"Camera\", \"issuedTo\": \"John3\"}",
            "{\"id\": \"cam001\", \"name\": \"Duplicate Camera\"}",
            "null"
        };
        for (String invalidRecord : invalidRecords) {
            String json = "{\"equipment\": [{\"id\": \"CAM001\", \"name\": \"Sony Camera\"},"
                    + invalidRecord + "]}";
            Files.writeString(filePath, json);
            JsonClubLogisticsStorage storage = new JsonClubLogisticsStorage(filePath);
            assertThrows(DataLoadingException.class, storage::readClubLogisticsData);
        }
    }

    /** Rejects a null register when saving. */
    @Test
    public void saveClubLogisticsData_nullClubLogisticsData_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveClubLogisticsData(null, "SomeFile.json"));
    }

    /**
     * Saves {@code clubLogisticsData} at the specified {@code filePath}.
     */
    private void saveClubLogisticsData(ReadOnlyClubLogisticsData clubLogisticsData, String filePath) {
        try {
            new JsonClubLogisticsStorage(Paths.get(filePath))
                    .saveClubLogisticsData(clubLogisticsData, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    /** Rejects a null save path. */
    @Test
    public void saveClubLogisticsData_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveClubLogisticsData(new ClubLogisticsData(), null));
    }
}
