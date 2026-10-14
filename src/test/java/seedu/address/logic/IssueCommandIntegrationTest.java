package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class IssueCommandIntegrationTest {
    private static final Equipment CAMERA = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
    private static final Equipment MICROPHONE = new Equipment(new EquipmentId("MIC001"), "Wireless Microphone");

    @TempDir
    public Path temporaryFolder;

    private final ModelManager model = new ModelManager();
    private JsonAddressBookStorage addressBookStorage;
    private Logic logic;
    private int saveCount;

    @BeforeEach
    public void setUp() {
        addressBookStorage = new JsonAddressBookStorage(temporaryFolder.resolve("equipment.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                saveCount++;
                super.saveAddressBook(addressBook);
            }
        };
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        logic = new LogicManager(model, new StorageManager(addressBookStorage, userPrefsStorage));
        model.addEquipment(CAMERA);
        model.addEquipment(MICROPHONE);
    }

    @Test
    public void execute_issueCommand_persistsBorrowerAndPreservesOtherEquipment() throws Exception {
        CommandResult result = logic.execute("issue cam001 John   Tan");

        assertEquals("Equipment CAM001 issued to John Tan.\nCAM001 | Sony Camera | Issued to: John Tan",
                result.getFeedbackToUser());
        Equipment expectedCamera = new Equipment(CAMERA.getId(), CAMERA.getName(), new BorrowerName("John Tan"));
        ReadOnlyAddressBook reloadedAddressBook = addressBookStorage.readAddressBook().orElseThrow();
        assertEquals(List.of(expectedCamera, MICROPHONE), reloadedAddressBook.getEquipmentList());
        assertEquals(model.getAddressBook(), reloadedAddressBook);
        assertEquals(1, saveCount);
    }

    @Test
    public void execute_missingEquipment_preservesModelAndDoesNotCreateSaveFile() {
        assertThrows(CommandException.class, "Error: Equipment with ID UNKNOWN does not exist.", ()
                -> logic.execute("issue UNKNOWN John Tan"));

        assertEquals(List.of(CAMERA, MICROPHONE), model.getAddressBook().getEquipmentList());
        assertFalse(Files.exists(addressBookStorage.getAddressBookFilePath()));
        assertEquals(0, saveCount);
    }

    @Test
    public void execute_repeatedIssue_preservesOriginalBorrowerAndSavedData() throws Exception {
        logic.execute("issue CAM001 John Tan");
        String savedData = Files.readString(addressBookStorage.getAddressBookFilePath());
        List<Equipment> expectedEquipment = List.copyOf(model.getAddressBook().getEquipmentList());

        assertThrows(CommandException.class, "Error: Equipment CAM001 is already issued to John Tan.", ()
                -> logic.execute("issue CAM001 Alice Lim"));

        assertEquals(expectedEquipment, model.getAddressBook().getEquipmentList());
        assertEquals(savedData, Files.readString(addressBookStorage.getAddressBookFilePath()));
        assertEquals(1, saveCount);
    }

    @Test
    public void execute_invalidArguments_preservesModelAndSavedData() throws Exception {
        addressBookStorage.saveAddressBook(model.getAddressBook());
        String savedData = Files.readString(addressBookStorage.getAddressBookFilePath());

        assertThrows(ParseException.class,
                "Error: Person name must contain only letters and spaces and be 1–50 characters long.", ()
                -> logic.execute("issue CAM001 John123"));
        assertThrows(ParseException.class,
                "Error: Invalid command format. Usage: issue <equipment-id> <person-name>", ()
                -> logic.execute("issue CAM001"));

        assertEquals(List.of(CAMERA, MICROPHONE), model.getAddressBook().getEquipmentList());
        assertEquals(savedData, Files.readString(addressBookStorage.getAddressBookFilePath()));
        assertEquals(1, saveCount);
    }
}
