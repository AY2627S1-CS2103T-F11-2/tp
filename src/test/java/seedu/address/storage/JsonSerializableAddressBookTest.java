package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
        assertTrue(addressBookFromFile.getEquipmentList().isEmpty());
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateEquipmentIds_throwsIllegalValueException() {
        List<JsonAdaptedEquipment> equipment = List.of(
                new JsonAdaptedEquipment("CAM001", "Sony Camera", null, null),
                new JsonAdaptedEquipment("cam001", "Another Camera", null, "John Tan"));
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(null, equipment);
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_EQUIPMENT,
                data::toModelType);
    }

    @Test
    public void toModelType_nullEquipmentRecord_throwsIllegalValueException() {
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(null, Arrays.asList(
                new JsonAdaptedEquipment("CAM001", "Sony Camera", null, null), null));
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_NULL_EQUIPMENT,
                data::toModelType);
    }

    @Test
    public void toModelType_missingLists_returnsEmptyAddressBook() throws Exception {
        JsonSerializableAddressBook data = JsonUtil.fromJsonString("{}", JsonSerializableAddressBook.class);
        assertEquals(new AddressBook(), data.toModelType());
    }

}
