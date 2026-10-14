package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import seedu.address.model.ReadOnlyClubLogisticsData;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.storage.JsonClubLogisticsStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class ReturnCommandIntegrationTest {
    private static final BorrowerName JOHN = new BorrowerName("John Tan");
    private static final Equipment ISSUED_CAMERA = new Equipment(new EquipmentId("CAM001"), "Sony Camera", JOHN);
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
        model.addEquipment(ISSUED_CAMERA);
        model.addEquipment(MICROPHONE);
    }

    @Test
    public void execute_returnCommand_persistsAvailableStatus() throws Exception {
        CommandResult result = logic.execute("return cam001");

        assertEquals("Equipment CAM001 returned successfully.\nCAM001 | Sony Camera | Available",
                result.getFeedbackToUser());
        Equipment expectedCamera = new Equipment(ISSUED_CAMERA.getId(), ISSUED_CAMERA.getName());
        ReadOnlyClubLogisticsData reloadedData = clubLogisticsStorage.readClubLogisticsData().orElseThrow();
        assertEquals(List.of(expectedCamera, MICROPHONE), reloadedData.getEquipmentList());
        assertEquals(model.getClubLogisticsData(), reloadedData);
        assertEquals(1, saveCount);
    }

    @Test
    public void execute_issueThenReturn_restoresOriginalRecord() throws Exception {
        logic.execute("issue MIC001 Alice Lim");
        logic.execute("return MIC001");

        assertEquals(List.of(ISSUED_CAMERA, MICROPHONE), model.getClubLogisticsData().getEquipmentList());
        assertEquals(2, saveCount);
    }

    @Test
    public void execute_repeatedReturn_preservesSavedData() throws Exception {
        logic.execute("return CAM001");
        String savedData = Files.readString(clubLogisticsStorage.getClubLogisticsDataFilePath());

        assertThrows(CommandException.class, "Error: Equipment CAM001 is not currently issued.", ()
                -> logic.execute("return CAM001"));

        assertEquals(savedData, Files.readString(clubLogisticsStorage.getClubLogisticsDataFilePath()));
        assertEquals(1, saveCount);
    }

    @Test
    public void execute_invalidArguments_preservesModel() {
        assertThrows(ParseException.class, "Error: Invalid command format. Usage: return <equipment-id>", ()
                -> logic.execute("return"));
        assertThrows(ParseException.class,
                "Error: Equipment ID must contain only letters and numbers and cannot contain spaces.", ()
                -> logic.execute("return CAM-001"));
        assertThrows(CommandException.class, "Error: Equipment with ID UNKNOWN does not exist.", ()
                -> logic.execute("return UNKNOWN"));

        assertEquals(List.of(ISSUED_CAMERA, MICROPHONE), model.getClubLogisticsData().getEquipmentList());
        assertEquals(0, saveCount);
    }
}
