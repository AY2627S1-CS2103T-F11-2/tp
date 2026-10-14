package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.HOON;
import static seedu.address.testutil.TypicalPersons.IDA;
import static seedu.address.testutil.TypicalPersons.getTypicalClubLogisticsData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.ClubLogisticsData;
import seedu.address.model.ReadOnlyClubLogisticsData;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;

public class JsonClubLogisticsStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonClubLogisticsStorageTest");

    @TempDir
    public Path testFolder;

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

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readClubLogisticsData("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readClubLogisticsData("notJsonFormatClubLogisticsData.json"));
    }

    @Test
    public void readClubLogisticsData_invalidPersonClubLogisticsData_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readClubLogisticsData("invalidPersonClubLogisticsData.json"));
    }

    @Test
    public void readClubLogisticsData_invalidAndValidPersonClubLogisticsData_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, ()
                -> readClubLogisticsData("invalidAndValidPersonClubLogisticsData.json"));
    }

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
        original.addPerson(HOON);
        original.removePerson(ALICE);
        jsonClubLogisticsStorage.saveClubLogisticsData(original, filePath);
        readBack = jsonClubLogisticsStorage.readClubLogisticsData(filePath).get();
        assertEquals(original, new ClubLogisticsData(readBack));

        // Save and read without specifying file path
        original.addPerson(IDA);
        jsonClubLogisticsStorage.saveClubLogisticsData(original); // file path not specified
        readBack = jsonClubLogisticsStorage.readClubLogisticsData().get(); // file path not specified
        assertEquals(original, new ClubLogisticsData(readBack));

    }

    @Test
    public void readAndSaveClubLogisticsData_availableAndIssuedEquipment_success() throws Exception {
        Path filePath = testFolder.resolve("EquipmentClubLogisticsData.json");
        JsonClubLogisticsStorage storage = new JsonClubLogisticsStorage(filePath);
        ClubLogisticsData original = getTypicalClubLogisticsData();
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

    @Test
    public void readClubLogisticsData_equipmentWithoutPersons_success() throws Exception {
        Path filePath = testFolder.resolve("EquipmentOnly.json");
        Files.writeString(filePath, """
                {"equipment": [
                  {"id": "cam001", "name": "Sony Camera"},
                  {"id": "MIC001", "name": "Wireless Microphone", "issuedTo": ""}
                ]}
                """);
        ReadOnlyClubLogisticsData readBack = new JsonClubLogisticsStorage(filePath).readClubLogisticsData().get();
        assertTrue(readBack.getPersonList().isEmpty());
        assertEquals(2, readBack.getEquipmentList().size());
        assertEquals(new EquipmentId("CAM001"), readBack.getEquipmentList().get(0).getId());
        assertTrue(readBack.getEquipmentList().stream().noneMatch(Equipment::isIssued));
    }

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

    @Test
    public void saveClubLogisticsData_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveClubLogisticsData(new ClubLogisticsData(), null));
    }
}
