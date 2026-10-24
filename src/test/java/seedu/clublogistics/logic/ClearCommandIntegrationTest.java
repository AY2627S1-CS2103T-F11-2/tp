package seedu.clublogistics.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.clublogistics.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.clublogistics.logic.commands.ClearCommand;
import seedu.clublogistics.logic.parser.exceptions.ParseException;
import seedu.clublogistics.model.ModelManager;
import seedu.clublogistics.model.equipment.BorrowerName;
import seedu.clublogistics.model.equipment.Equipment;
import seedu.clublogistics.model.equipment.EquipmentId;
import seedu.clublogistics.storage.JsonClubLogisticsStorage;
import seedu.clublogistics.storage.JsonUserPrefsStorage;
import seedu.clublogistics.storage.StorageManager;

/** Tests clear through the real parser, model, and JSON storage. */
public class ClearCommandIntegrationTest {
    private static final Equipment CAMERA = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
    private static final Equipment ISSUED_MICROPHONE = new Equipment(new EquipmentId("MIC001"),
            "Wireless Microphone").issueTo(new BorrowerName("Alice Lim"));

    @TempDir
    public Path temporaryFolder;

    private final ModelManager model = new ModelManager();
    private JsonClubLogisticsStorage clubLogisticsStorage;
    private Logic logic;

    /** Creates a real command pipeline holding two saved items, one of them issued. */
    @BeforeEach
    public void setUp() throws Exception {
        clubLogisticsStorage = new JsonClubLogisticsStorage(temporaryFolder.resolve("equipment.json"));
        StorageManager storage = new StorageManager(clubLogisticsStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")));
        logic = new LogicManager(model, storage);
        model.addEquipment(CAMERA);
        model.addEquipment(ISSUED_MICROPHONE);
        clubLogisticsStorage.saveClubLogisticsData(model.getClubLogisticsData());
    }

    /** Verifies a rejected clear leaves both the in-memory register and the saved file untouched. */
    @Test
    public void execute_clearWithExtraArgument_rejectsAndPreservesRecords() throws Exception {
        assertThrows(ParseException.class, ClearCommand.MESSAGE_INVALID_COMMAND_FORMAT, ()
                -> logic.execute("clear CAM001"));

        assertEquals(List.of(CAMERA, ISSUED_MICROPHONE), logic.getEquipmentList());
        assertEquals(List.of(CAMERA, ISSUED_MICROPHONE),
                clubLogisticsStorage.readClubLogisticsData().orElseThrow().getEquipmentList());
    }

    /** Verifies clear without arguments still empties the register and the saved file. */
    @Test
    public void execute_clearWithoutArguments_clearsMemoryAndStorage() throws Exception {
        assertEquals(ClearCommand.MESSAGE_SUCCESS, logic.execute("clear").getFeedbackToUser());

        assertTrue(logic.getEquipmentList().isEmpty());
        assertTrue(clubLogisticsStorage.readClubLogisticsData().orElseThrow().getEquipmentList().isEmpty());
    }
}
