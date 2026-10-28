package seedu.clublogistics.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ObservableList;
import seedu.clublogistics.logic.commands.exceptions.CommandException;
import seedu.clublogistics.model.ClubLogisticsData;
import seedu.clublogistics.model.ModelManager;
import seedu.clublogistics.model.equipment.Equipment;
import seedu.clublogistics.storage.JsonClubLogisticsStorage;
import seedu.clublogistics.storage.JsonUserPrefsStorage;
import seedu.clublogistics.storage.StorageManager;

/** Checks command recovery against a real, unwritable storage target. */
public class LogicManagerStorageFailureTest {
    @TempDir
    public Path testFolder;

    private final ModelManager model = new ModelManager();
    private JsonClubLogisticsStorage storage;
    private LogicManager logic;
    private Path dataFile;
    private Path backup;

    /** Saves an available camera and an issued microphone before blocking the data path. */
    @BeforeEach
    public void setUp() throws Exception {
        dataFile = testFolder.resolve("equipment.json");
        backup = testFolder.resolve("backup.json");
        storage = new JsonClubLogisticsStorage(dataFile);
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json"))));
        logic.execute("add CAM001 Camera");
        logic.execute("add MIC001 Microphone");
        logic.execute("issue MIC001 John Tan");
        logic.execute("filter available");
        Files.move(dataFile, backup);
        Files.createDirectory(dataFile);
    }

    /** A rejected add must not leave a duplicate behind when retried. */
    @Test
    public void execute_addCannotSave_restoresRegisterAndAllowsRetry() throws Exception {
        assertRestoredAndRetrySucceeds("add NEW001 Tripod");
    }

    /** A rejected loan must preserve the available state. */
    @Test
    public void execute_issueCannotSave_restoresRegisterAndAllowsRetry() throws Exception {
        assertRestoredAndRetrySucceeds("issue CAM001 Alice Lim");
    }

    /** A rejected return must retain the original borrower. */
    @Test
    public void execute_returnCannotSave_restoresRegisterAndAllowsRetry() throws Exception {
        assertRestoredAndRetrySucceeds("return MIC001");
    }

    /** A rejected removal must preserve the equipment record. */
    @Test
    public void execute_removeCannotSave_restoresRegisterAndAllowsRetry() throws Exception {
        assertRestoredAndRetrySucceeds("remove CAM001");
    }

    /** A rejected clear must retain both available and issued records. */
    @Test
    public void execute_clearCannotSave_restoresRegisterAndAllowsRetry() throws Exception {
        assertRestoredAndRetrySucceeds("clear");
    }

    /** Help remains accessible when writing equipment data is impossible. */
    @Test
    public void execute_helpCannotSave_stillRequestsHelp() throws Exception {
        assertTrue(logic.execute("help").isShowHelp());
        assertTrue(Files.isDirectory(dataFile));
        assertEquals(2, model.getEquipmentList().size());
    }

    /** Exit remains accessible when writing equipment data is impossible. */
    @Test
    public void execute_exitCannotSave_stillRequestsExit() throws Exception {
        assertTrue(logic.execute("exit").isExit());
        assertTrue(Files.isDirectory(dataFile));
        assertEquals(2, model.getEquipmentList().size());
    }

    private void assertRestoredAndRetrySucceeds(String command) throws Exception {
        ClubLogisticsData before = new ClubLogisticsData(model.getClubLogisticsData());
        ObservableList<Equipment> view = model.getFilteredEquipmentList();
        List<Equipment> visibleBefore = List.copyOf(view);
        String savedBefore = Files.readString(backup);

        assertThrows(CommandException.class, () -> logic.execute(command));

        assertEquals(before, model.getClubLogisticsData());
        assertSame(view, model.getFilteredEquipmentList());
        assertEquals(visibleBefore, view);
        assertEquals(savedBefore, Files.readString(backup));
        Files.delete(dataFile);
        Files.move(backup, dataFile);
        assertEquals(before, storage.readClubLogisticsData().orElseThrow());

        logic.execute(command);
        assertEquals(model.getClubLogisticsData(), storage.readClubLogisticsData().orElseThrow());
        logic.execute("add CHECK001 Filter Check");
        assertTrue(view.stream().allMatch(item -> !item.isIssued()));
        assertTrue(view.stream().anyMatch(item -> "CHECK001".equals(item.getId().value)));
    }
}
