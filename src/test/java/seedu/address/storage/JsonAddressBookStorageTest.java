package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.HOON;
import static seedu.address.testutil.TypicalPersons.IDA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;

public class JsonAddressBookStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAddressBookStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readAddressBook(null));
    }

    private java.util.Optional<ReadOnlyAddressBook> readAddressBook(String filePath) throws Exception {
        return new JsonAddressBookStorage(Paths.get(filePath)).readAddressBook(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAddressBook("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("notJsonFormatAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidPersonAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidAndValidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidAndValidPersonAddressBook.json"));
    }

    @Test
    public void readAndSaveAddressBook_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempAddressBook.json");
        AddressBook original = getTypicalAddressBook();
        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);

        // Save in new file and read back
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Modify data, overwrite existing file, and read back
        original.addPerson(HOON);
        original.removePerson(ALICE);
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Save and read without specifying file path
        original.addPerson(IDA);
        jsonAddressBookStorage.saveAddressBook(original); // file path not specified
        readBack = jsonAddressBookStorage.readAddressBook().get(); // file path not specified
        assertEquals(original, new AddressBook(readBack));

    }

    @Test
    public void readAndSaveAddressBook_availableAndIssuedEquipment_success() throws Exception {
        Path filePath = testFolder.resolve("EquipmentAddressBook.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);
        AddressBook original = getTypicalAddressBook();
        original.addEquipment(new Equipment(new EquipmentId("CAM001"), "Sony Camera"));
        original.addEquipment(new Equipment(new EquipmentId("MIC001"), "Wireless Microphone",
                new BorrowerName("John Tan")));

        storage.saveAddressBook(original);
        ReadOnlyAddressBook readBack = storage.readAddressBook().get();
        assertEquals(original, new AddressBook(readBack));
        assertFalse(readBack.getEquipmentList().get(0).isIssued());
        assertEquals(new BorrowerName("John Tan"), readBack.getEquipmentList().get(1).getIssuedTo().get());

        AddressBook returnedEquipment = new AddressBook();
        returnedEquipment.addEquipment(new Equipment(new EquipmentId("MIC001"), "Wireless Microphone"));
        storage.saveAddressBook(returnedEquipment);
        assertEquals(returnedEquipment, new AddressBook(storage.readAddressBook().get()));
    }

    @Test
    public void readAddressBook_equipmentWithoutPersons_success() throws Exception {
        Path filePath = testFolder.resolve("EquipmentOnly.json");
        Files.writeString(filePath, """
                {"equipment": [
                  {"id": "cam001", "name": "Sony Camera"},
                  {"id": "MIC001", "name": "Wireless Microphone", "issuedTo": ""}
                ]}
                """);
        ReadOnlyAddressBook readBack = new JsonAddressBookStorage(filePath).readAddressBook().get();
        assertTrue(readBack.getPersonList().isEmpty());
        assertEquals(2, readBack.getEquipmentList().size());
        assertEquals(new EquipmentId("CAM001"), readBack.getEquipmentList().get(0).getId());
        assertTrue(readBack.getEquipmentList().stream().noneMatch(Equipment::isIssued));
    }

    @Test
    public void readAddressBook_invalidEquipmentAfterValidRecord_throwsDataLoadingException() throws Exception {
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
            JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);
            assertThrows(DataLoadingException.class, storage::readAddressBook);
        }
    }

    @Test
    public void saveAddressBook_nullAddressBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(null, "SomeFile.json"));
    }

    /**
     * Saves {@code addressBook} at the specified {@code filePath}.
     */
    private void saveAddressBook(ReadOnlyAddressBook addressBook, String filePath) {
        try {
            new JsonAddressBookStorage(Paths.get(filePath))
                    .saveAddressBook(addressBook, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(new AddressBook(), null));
    }
}
