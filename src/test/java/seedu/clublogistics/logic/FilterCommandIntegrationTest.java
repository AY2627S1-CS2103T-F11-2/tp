package seedu.clublogistics.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.collections.ObservableList;
import seedu.clublogistics.model.ModelManager;
import seedu.clublogistics.model.equipment.BorrowerName;
import seedu.clublogistics.model.equipment.Equipment;
import seedu.clublogistics.model.equipment.EquipmentId;
import seedu.clublogistics.storage.JsonClubLogisticsStorage;
import seedu.clublogistics.storage.JsonUserPrefsStorage;
import seedu.clublogistics.storage.StorageManager;

/** Tests filter command sequences through the real parser, model, and JSON storage. */
public class FilterCommandIntegrationTest {
    private static final Equipment CAMERA = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
    private static final Equipment MICROPHONE = new Equipment(new EquipmentId("MIC001"), "Wireless Microphone");
    private static final Equipment ISSUED_MICROPHONE = MICROPHONE.issueTo(new BorrowerName("Alice Lim"));

    @TempDir
    public Path temporaryFolder;

    private final ModelManager model = new ModelManager();
    private JsonClubLogisticsStorage clubLogisticsStorage;
    private StorageManager storage;
    private Logic logic;

    /** Creates a real command pipeline with available and issued equipment. */
    @BeforeEach
    public void setUp() {
        clubLogisticsStorage = new JsonClubLogisticsStorage(temporaryFolder.resolve("equipment.json"));
        storage = new StorageManager(clubLogisticsStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")));
        logic = new LogicManager(model, storage);
        model.addEquipment(CAMERA);
        model.addEquipment(ISSUED_MICROPHONE);
    }

    /** Verifies issuing removes a match and returning restores it in the same observable view. */
    @Test
    public void execute_filterAvailableThenIssueAndReturn_updatesVisibleEquipment() throws Exception {
        ObservableList<Equipment> visible = logic.getFilteredEquipmentList();
        assertEquals("Showing available equipment.", logic.execute("filter AVAILABLE").getFeedbackToUser());
        assertEquals(List.of(CAMERA), visible);

        logic.execute("issue CAM001 John Tan");

        assertTrue(visible.isEmpty());
        Equipment issuedCamera = CAMERA.issueTo(new BorrowerName("John Tan"));
        assertEquals(List.of(issuedCamera, ISSUED_MICROPHONE), logic.getEquipmentList());

        logic.execute("return CAM001");

        assertEquals(List.of(CAMERA), visible);
        assertSame(visible, logic.getFilteredEquipmentList());
    }

    /** Verifies returning removes a match and issuing restores it without reapplying the filter. */
    @Test
    public void execute_filterIssuedThenReturnAndIssue_updatesVisibleEquipment() throws Exception {
        ObservableList<Equipment> visible = logic.getFilteredEquipmentList();
        assertEquals("Showing issued equipment.", logic.execute("filter IsSuEd").getFeedbackToUser());
        assertEquals(List.of(ISSUED_MICROPHONE), visible);

        logic.execute("return MIC001");

        assertTrue(visible.isEmpty());
        assertEquals(List.of(CAMERA, MICROPHONE), logic.getEquipmentList());

        logic.execute("issue MIC001 Alice Lim");

        assertEquals(List.of(ISSUED_MICROPHONE), visible);
        assertSame(visible, logic.getFilteredEquipmentList());
    }

    /** Verifies list clears an available filter and subsequent changes remain visible. */
    @Test
    public void execute_filterAvailableThenList_showsAllEquipment() throws Exception {
        logic.execute("filter available");
        assertEquals(List.of(CAMERA), logic.getFilteredEquipmentList());

        assertEquals("Listed all equipment.", logic.execute("list").getFeedbackToUser());

        assertEquals(List.of(CAMERA, ISSUED_MICROPHONE), logic.getFilteredEquipmentList());
        logic.execute("issue CAM001 John Tan");
        assertEquals(logic.getEquipmentList(), logic.getFilteredEquipmentList());
    }

    /** Verifies list clears an issued filter and subsequent changes remain visible. */
    @Test
    public void execute_filterIssuedThenList_showsAllEquipment() throws Exception {
        logic.execute("filter issued");
        assertEquals(List.of(ISSUED_MICROPHONE), logic.getFilteredEquipmentList());

        assertEquals("Listed all equipment.", logic.execute("list").getFeedbackToUser());

        assertEquals(List.of(CAMERA, ISSUED_MICROPHONE), logic.getFilteredEquipmentList());
        logic.execute("return MIC001");
        assertEquals(List.of(CAMERA, MICROPHONE), logic.getFilteredEquipmentList());
    }

    /** Verifies the available no-match message and list reset after an empty result. */
    @Test
    public void execute_filterAvailableWithoutMatches_showsEmptyResult() throws Exception {
        logic.execute("issue CAM001 John Tan");

        assertEquals("No available equipment found.", logic.execute("filter available").getFeedbackToUser());

        assertTrue(logic.getFilteredEquipmentList().isEmpty());
        logic.execute("list");
        assertEquals(2, logic.getFilteredEquipmentList().size());
        assertEquals(logic.getEquipmentList(), logic.getFilteredEquipmentList());
    }

    /** Verifies the issued no-match message and list reset after an empty result. */
    @Test
    public void execute_filterIssuedWithoutMatches_showsEmptyResult() throws Exception {
        logic.execute("return MIC001");

        assertEquals("No issued equipment found.", logic.execute("filter issued").getFeedbackToUser());

        assertTrue(logic.getFilteredEquipmentList().isEmpty());
        logic.execute("list");
        assertEquals(List.of(CAMERA, MICROPHONE), logic.getFilteredEquipmentList());
    }

    /** Verifies adding and removing hidden items preserves an issued filter, including when empty. */
    @Test
    public void execute_filterIssuedThenAddIssueReturnAndRemove_preservesFilter() throws Exception {
        logic.execute("filter issued");
        logic.execute("add NEW001 Club Camera");
        assertEquals(List.of(ISSUED_MICROPHONE), logic.getFilteredEquipmentList());

        logic.execute("issue NEW001 John Tan");
        Equipment newCamera = new Equipment(new EquipmentId("NEW001"), "Club Camera");
        Equipment issuedNewCamera = newCamera.issueTo(new BorrowerName("John Tan"));
        assertEquals(List.of(ISSUED_MICROPHONE, issuedNewCamera), logic.getFilteredEquipmentList());

        logic.execute("return NEW001");
        logic.execute("remove NEW001");
        assertEquals(List.of(ISSUED_MICROPHONE), logic.getFilteredEquipmentList());
        assertEquals(List.of(CAMERA, ISSUED_MICROPHONE), logic.getEquipmentList());

        logic.execute("return MIC001");
        logic.execute("add NEW002 Tripod");
        logic.execute("remove CAM001");
        assertTrue(logic.getFilteredEquipmentList().isEmpty());
        assertEquals(List.of(MICROPHONE, new Equipment(new EquipmentId("NEW002"), "Tripod")),
                logic.getEquipmentList());
    }

    /** Verifies switching filters preserves records and restarting restores the unfiltered view. */
    @Test
    public void execute_switchFiltersAndRestart_preservesRecordsWithoutSavingFilter() throws Exception {
        clubLogisticsStorage.saveClubLogisticsData(model.getClubLogisticsData());
        logic.execute("filter available");
        assertEquals(List.of(CAMERA), logic.getFilteredEquipmentList());
        logic.execute("filter issued");
        assertEquals(List.of(ISSUED_MICROPHONE), logic.getFilteredEquipmentList());
        assertEquals(List.of(CAMERA, ISSUED_MICROPHONE), logic.getEquipmentList());

        ModelManager restartedModel = new ModelManager(clubLogisticsStorage.readClubLogisticsData().orElseThrow(),
                model.getUserPrefs());
        Logic restartedLogic = new LogicManager(restartedModel, storage);

        assertEquals(List.of(CAMERA, ISSUED_MICROPHONE), restartedLogic.getEquipmentList());
        assertEquals(restartedLogic.getEquipmentList(), restartedLogic.getFilteredEquipmentList());
    }
}
