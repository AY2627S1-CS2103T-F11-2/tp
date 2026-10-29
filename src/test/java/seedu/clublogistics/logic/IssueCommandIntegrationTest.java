package seedu.clublogistics.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.clublogistics.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ListChangeListener;
import seedu.clublogistics.logic.commands.CommandResult;
import seedu.clublogistics.logic.commands.exceptions.CommandException;
import seedu.clublogistics.logic.parser.exceptions.ParseException;
import seedu.clublogistics.model.ModelManager;
import seedu.clublogistics.model.ReadOnlyClubLogisticsData;
import seedu.clublogistics.model.equipment.BorrowerName;
import seedu.clublogistics.model.equipment.Equipment;
import seedu.clublogistics.model.equipment.EquipmentId;
import seedu.clublogistics.storage.JsonClubLogisticsStorage;
import seedu.clublogistics.storage.JsonUserPrefsStorage;
import seedu.clublogistics.storage.StorageManager;

public class IssueCommandIntegrationTest {
    private static final Equipment CAMERA = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
    private static final Equipment MICROPHONE = new Equipment(new EquipmentId("MIC001"), "Wireless Microphone");

    @TempDir
    public Path temporaryFolder;

    private final ModelManager model = new ModelManager();
    private JsonClubLogisticsStorage clubLogisticsStorage;
    private Logic logic;
    private int saveCount;

    @BeforeEach
    public void setUp() {
        clubLogisticsStorage = new JsonClubLogisticsStorage(temporaryFolder.resolve("equipment.json")) {
            @Override
            public void saveClubLogisticsData(ReadOnlyClubLogisticsData clubLogisticsData) throws IOException {
                saveCount++;
                super.saveClubLogisticsData(clubLogisticsData);
            }
        };
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        logic = new LogicManager(model, new StorageManager(clubLogisticsStorage, userPrefsStorage));
        model.addEquipment(CAMERA);
        model.addEquipment(MICROPHONE);
    }

    @Test
    public void execute_issueCommand_persistsBorrowerAndPreservesOtherEquipment() throws Exception {
        CommandResult result = logic.execute("issue cam001 John   Tan");

        assertEquals("Equipment CAM001 issued to John Tan.\nCAM001 | Sony Camera | Issued to: John Tan",
                result.getFeedbackToUser());
        Equipment expectedCamera = new Equipment(CAMERA.getId(), CAMERA.getName(), new BorrowerName("John Tan"));
        ReadOnlyClubLogisticsData reloadedClubLogisticsData =
                clubLogisticsStorage.readClubLogisticsData().orElseThrow();
        assertEquals(List.of(expectedCamera, MICROPHONE), reloadedClubLogisticsData.getEquipmentList());
        assertEquals(model.getClubLogisticsData(), reloadedClubLogisticsData);
        assertEquals(1, saveCount);
    }

    @Test
    public void execute_missingEquipment_preservesModelAndDoesNotCreateSaveFile() {
        assertThrows(CommandException.class, "Error: Equipment with ID UNKNOWN does not exist.", ()
                -> logic.execute("issue UNKNOWN John Tan"));

        assertEquals(List.of(CAMERA, MICROPHONE), model.getClubLogisticsData().getEquipmentList());
        assertFalse(Files.exists(clubLogisticsStorage.getClubLogisticsDataFilePath()));
        assertEquals(0, saveCount);
    }

    @Test
    public void execute_repeatedIssue_preservesOriginalBorrowerAndSavedData() throws Exception {
        logic.execute("issue CAM001 John Tan");
        String savedData = Files.readString(clubLogisticsStorage.getClubLogisticsDataFilePath());
        List<Equipment> expectedEquipment = List.copyOf(model.getClubLogisticsData().getEquipmentList());

        assertThrows(CommandException.class, "Error: Equipment CAM001 is already issued to John Tan.", ()
                -> logic.execute("issue CAM001 Alice Lim"));

        assertEquals(expectedEquipment, model.getClubLogisticsData().getEquipmentList());
        assertEquals(savedData, Files.readString(clubLogisticsStorage.getClubLogisticsDataFilePath()));
        assertEquals(1, saveCount);
    }

    @Test
    public void execute_invalidArguments_preservesModelAndSavedData() throws Exception {
        clubLogisticsStorage.saveClubLogisticsData(model.getClubLogisticsData());
        String savedData = Files.readString(clubLogisticsStorage.getClubLogisticsDataFilePath());

        assertThrows(ParseException.class,
                "Error: Person name must be 1 to 50 characters long and contain only letters (A-Z, a-z),"
                        + " spaces, and apostrophes (') and hyphens (-) between letters.", ()
                -> logic.execute("issue CAM001 John123"));
        assertThrows(ParseException.class,
                "Error: Invalid command format. Usage: issue <equipment-id> <person-name>", ()
                -> logic.execute("issue CAM001"));

        assertEquals(List.of(CAMERA, MICROPHONE), model.getClubLogisticsData().getEquipmentList());
        assertEquals(savedData, Files.readString(clubLogisticsStorage.getClubLogisticsDataFilePath()));
        assertEquals(1, saveCount);
    }

    @Test
    public void execute_addIssueListAndRestart_preservesBorrowerAndObservableUpdates() throws Exception {
        boolean[] listUpdated = {false};
        model.getFilteredEquipmentList().addListener((ListChangeListener<Equipment>) change -> listUpdated[0] = true);
        assertEquals("Equipment NEW001 added successfully.",
                logic.execute("add NEW001 Club Camera").getFeedbackToUser());
        listUpdated[0] = false;
        assertEquals("Equipment NEW001 issued to John Tan.\nNEW001 | Club Camera | Issued to: John Tan",
                logic.execute("issue new001 John   Tan").getFeedbackToUser());
        assertTrue(listUpdated[0]);
        model.updateFilteredEquipmentList(equipment -> !equipment.isIssued());
        assertFalse(model.getFilteredEquipmentList().stream().anyMatch(Equipment::isIssued));
        logic.execute("list");
        assertEquals(3, logic.getFilteredEquipmentList().size());
        Equipment issued = model.findEquipment(new EquipmentId("new001")).orElseThrow();
        assertEquals("Issued to: John Tan", issued.getDisplayStatus());

        ModelManager restarted = new ModelManager(clubLogisticsStorage.readClubLogisticsData().orElseThrow(),
                model.getUserPrefs());
        Logic restartedLogic = new LogicManager(restarted,
                new StorageManager(clubLogisticsStorage,
                        new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));
        assertEquals(issued, restarted.findEquipment(new EquipmentId("NEW001")).orElseThrow());
        assertThrows(CommandException.class, "Error: Equipment NEW001 is already issued to John Tan.", ()
                -> restartedLogic.execute("issue new001 Alice Lim"));
    }

    @Test
    public void execute_removeIssuedItem_rejectsWithoutLosingBorrower() throws Exception {
        logic.execute("issue CAM001 John Tan");
        String savedData = Files.readString(clubLogisticsStorage.getClubLogisticsDataFilePath());
        assertThrows(CommandException.class, "Error: Equipment CAM001 is issued to John Tan and cannot be removed.", ()
                -> logic.execute("remove cam001"));
        assertEquals(new BorrowerName("John Tan"), model.findEquipment(CAMERA.getId()).orElseThrow()
                .getIssuedTo().orElseThrow());
        assertEquals(savedData, Files.readString(clubLogisticsStorage.getClubLogisticsDataFilePath()));
    }

    @Test
    public void execute_sameBorrowerForDifferentItems_persistsIndependentAssignments() throws Exception {
        logic.execute("issue CAM001 John Tan");
        logic.execute("issue MIC001 John Tan");
        BorrowerName john = new BorrowerName("John Tan");
        assertEquals(2, clubLogisticsStorage.readClubLogisticsData().orElseThrow().getEquipmentList().stream()
                .filter(equipment -> equipment.getIssuedTo().orElseThrow().equals(john)).count());
    }
}
