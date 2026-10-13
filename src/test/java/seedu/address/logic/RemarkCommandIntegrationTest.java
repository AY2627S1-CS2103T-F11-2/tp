package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class RemarkCommandIntegrationTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    public void execute_remarkAndEditThenClear_persistsAcrossReloads() throws Exception {
        ModelManager model = new ModelManager();
        model.addPerson(ALICE);
        model.addPerson(BOB);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressbook.json"));
        Logic logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));

        String result = logic.execute("remark 1 r/Likes baseball, #7!").getFeedbackToUser();
        assertTrue(result.contains("Remark: Likes baseball, #7!"));
        Person expected = new PersonBuilder(ALICE).withRemark("Likes baseball, #7!").build();
        assertEquals(expected, storage.readAddressBook().orElseThrow().getPersonList().getFirst());
        assertEquals(BOB, storage.readAddressBook().orElseThrow().getPersonList().get(1));

        logic.execute("edit 1 p/12345678");
        expected = new PersonBuilder(expected).withPhone("12345678").build();
        assertEquals(expected, storage.readAddressBook().orElseThrow().getPersonList().getFirst());

        String savedData = Files.readString(storage.getAddressBookFilePath());
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, ()
                -> logic.execute("remark 100 r/Invalid"));
        assertEquals(savedData, Files.readString(storage.getAddressBookFilePath()));

        ModelManager restarted = new ModelManager(storage.readAddressBook().orElseThrow(), model.getUserPrefs());
        Logic restartedLogic = new LogicManager(restarted, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));
        assertTrue(restartedLogic.execute("remark 1 r/").getFeedbackToUser().startsWith("Removed remark"));
        assertEquals("", storage.readAddressBook().orElseThrow().getPersonList().getFirst().getRemark().value);
    }
}
