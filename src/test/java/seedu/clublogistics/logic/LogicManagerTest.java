package seedu.clublogistics.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.clublogistics.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.clublogistics.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ObservableList;
import seedu.clublogistics.logic.commands.AddEquipmentCommand;
import seedu.clublogistics.logic.commands.CommandResult;
import seedu.clublogistics.logic.commands.ListCommand;
import seedu.clublogistics.logic.commands.exceptions.CommandException;
import seedu.clublogistics.logic.parser.exceptions.ParseException;
import seedu.clublogistics.model.ClubLogisticsData;
import seedu.clublogistics.model.Model;
import seedu.clublogistics.model.ModelManager;
import seedu.clublogistics.model.ReadOnlyClubLogisticsData;
import seedu.clublogistics.model.UserPrefs;
import seedu.clublogistics.model.equipment.Equipment;
import seedu.clublogistics.model.equipment.EquipmentId;
import seedu.clublogistics.model.equipment.EquipmentName;
import seedu.clublogistics.model.equipment.EquipmentStatus;
import seedu.clublogistics.storage.JsonClubLogisticsStorage;
import seedu.clublogistics.storage.JsonUserPrefsStorage;
import seedu.clublogistics.storage.StorageManager;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    /** Creates isolated storage for each test. */
    @BeforeEach
    public void setUp() {
        JsonClubLogisticsStorage clubLogisticsStorage =
                new JsonClubLogisticsStorage(temporaryFolder.resolve("clubLogisticsData.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(clubLogisticsStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    /** Rejects unknown commands without changing model state. */
    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    /** Reports failure when removing unregistered equipment. */
    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        assertCommandException("remove CAM999", "Error: Equipment with ID CAM999 does not exist.");
    }

    /** Executes a valid list command. */
    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_EMPTY_REGISTER, model);
    }

    /** Rejects extra list arguments. */
    @Test
    public void execute_listWithExtraArguments_throwsParseException() {
        assertParseException(ListCommand.COMMAND_WORD + " 3", ListCommand.MESSAGE_INVALID_COMMAND_FORMAT);
    }

    /** Verifies observable updates, persistence, duplicate rejection, and clearing. */
    @Test
    public void execute_addEquipment_updatesViewAndPersists() throws Exception {
        ObservableList<Equipment> equipment = logic.getEquipmentList();
        CommandResult result = logic.execute("add CAM001 Sony Camera");
        Equipment camera = new Equipment(new EquipmentId("CAM001"), new EquipmentName("Sony Camera"));
        assertEquals("Equipment CAM001 added successfully.", result.getFeedbackToUser());
        assertEquals(List.of(camera), equipment);
        assertEquals(EquipmentStatus.AVAILABLE, equipment.get(0).getStatus());
        assertThrows(UnsupportedOperationException.class, () -> equipment.remove(camera));

        JsonClubLogisticsStorage storage =
                new JsonClubLogisticsStorage(temporaryFolder.resolve("clubLogisticsData.json"));
        ClubLogisticsData saved = new ClubLogisticsData(storage.readClubLogisticsData().orElseThrow());
        assertEquals(model.getClubLogisticsData(), saved);

        assertCommandException("add cam001 Other Camera",
                String.format(AddEquipmentCommand.MESSAGE_DUPLICATE_EQUIPMENT, "cam001"));
        assertEquals(saved, new ClubLogisticsData(storage.readClubLogisticsData().orElseThrow()));

        logic.execute("clear");
        assertEquals(List.of(), equipment);
        assertEquals(new ClubLogisticsData(), new ClubLogisticsData(storage.readClubLogisticsData().orElseThrow()));
    }

    /** Reports a storage failure as a command error. */
    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    /** Reports a storage permission failure as a command error. */
    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getClubLogisticsData(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonClubLogisticsStorage that throws the IOException e when saving
        JsonClubLogisticsStorage clubLogisticsStorage = new JsonClubLogisticsStorage(prefPath) {
            @Override
            public void saveClubLogisticsData(ReadOnlyClubLogisticsData clubLogisticsData) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(clubLogisticsStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveClubLogisticsData method by executing an add command
        String addCommand = "add CAM001 Sony Camera";
        ModelManager expectedModel = new ModelManager();
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
