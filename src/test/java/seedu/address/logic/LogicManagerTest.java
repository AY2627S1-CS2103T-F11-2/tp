package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ObservableList;
import seedu.address.logic.commands.AddEquipmentCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ClubLogisticsData;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyClubLogisticsData;
import seedu.address.model.UserPrefs;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.model.equipment.EquipmentName;
import seedu.address.model.equipment.EquipmentStatus;
import seedu.address.storage.JsonClubLogisticsStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonClubLogisticsStorage clubLogisticsStorage =
                new JsonClubLogisticsStorage(temporaryFolder.resolve("clubLogisticsData.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(clubLogisticsStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_EMPTY_REGISTER, model);
    }

    @Test
    public void execute_listWithExtraArguments_throwsParseException() {
        assertParseException(ListCommand.COMMAND_WORD + " 3", ListCommand.MESSAGE_INVALID_COMMAND_FORMAT);
    }

    @Test
    public void execute_addEquipment_updatesViewAndPersistsWithContacts() throws Exception {
        model.addPerson(AMY);
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

    @Test
    public void execute_contactAddSyntax_preservesModelAndDoesNotSave() {
        model.addPerson(AMY);
        assertParseException("add n/John Doe p/98765432 e/john@example.com a/Main Street",
                EquipmentId.MESSAGE_CONSTRAINTS);
        assertEquals(List.of(AMY), model.getClubLogisticsData().getPersonList());
        assertEquals(List.of(), model.getEquipmentList());
        assertFalse(java.nio.file.Files.exists(temporaryFolder.resolve("clubLogisticsData.json")));
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
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
        Equipment expectedEquipment = new Equipment(new EquipmentId("CAM001"), new EquipmentName("Sony Camera"));
        ModelManager expectedModel = new ModelManager();
        expectedModel.addEquipment(expectedEquipment);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
