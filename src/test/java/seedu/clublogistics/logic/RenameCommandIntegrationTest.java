package seedu.clublogistics.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.clublogistics.logic.commands.exceptions.CommandException;
import seedu.clublogistics.logic.parser.exceptions.ParseException;
import seedu.clublogistics.model.ModelManager;
import seedu.clublogistics.model.equipment.Equipment;
import seedu.clublogistics.model.equipment.EquipmentName;
import seedu.clublogistics.storage.JsonClubLogisticsStorage;
import seedu.clublogistics.storage.JsonUserPrefsStorage;
import seedu.clublogistics.storage.StorageManager;

/** Exercises name correction without changing equipment identity or loan state. */
public class RenameCommandIntegrationTest {
    @TempDir
    public Path testFolder;

    private final ModelManager model = new ModelManager();
    private JsonClubLogisticsStorage storage;
    private LogicManager logic;
    private Path dataFile;

    /** Creates an available camera using the real command and storage pipeline. */
    @BeforeEach
    public void setUp() throws Exception {
        dataFile = testFolder.resolve("equipment.json");
        storage = new JsonClubLogisticsStorage(dataFile);
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json"))));
        logic.execute("add CAM001 Sony Camera");
    }

    /** Correcting an active loan updates the observed record and survives restart. */
    @Test
    public void execute_issuedEquipment_preservesLoanAndIdentity() throws Exception {
        logic.execute("issue CAM001 Alice Tan");
        logic.execute("filter issued");
        var view = model.getFilteredEquipmentList();
        Equipment original = view.get(0);

        assertEquals("Equipment CAM001 renamed to Sony Camera Mark II.",
                logic.execute("rename cam001 Sony   Camera Mark II").getFeedbackToUser());

        Equipment renamed = view.get(0);
        assertSame(view, model.getFilteredEquipmentList());
        assertEquals(original.getId(), renamed.getId());
        assertEquals(original.getStatus(), renamed.getStatus());
        assertEquals(original.getIssuedTo(), renamed.getIssuedTo());
        assertEquals(new EquipmentName("Sony Camera Mark II"), renamed.getName());
        assertEquals(new EquipmentName("Sony Camera"), original.getName());
        assertEquals(model.getClubLogisticsData(), storage.readClubLogisticsData().orElseThrow());
        assertThrows(CommandException.class, () -> logic.execute("remove CAM001"));
    }

    /** A hidden available record can be renamed without resetting the filter or inventing a loan. */
    @Test
    public void execute_hiddenAvailableEquipment_preservesViewAndAvailability() throws Exception {
        logic.execute("filter issued");
        logic.execute("rename CAM001 Club Camera");
        assertTrue(model.getFilteredEquipmentList().isEmpty());
        Equipment renamed = model.getEquipmentList().get(0);
        assertTrue(!renamed.isIssued());
        assertTrue(renamed.getIssuedTo().isEmpty());
        assertEquals(new EquipmentName("Club Camera"), renamed.getName());
        assertEquals(model.getClubLogisticsData(), storage.readClubLogisticsData().orElseThrow());
    }

    /** Missing targets and invalid values leave both memory and saved bytes unchanged. */
    @Test
    public void execute_invalidRename_preservesRegister() throws Exception {
        String before = Files.readString(dataFile);
        for (String command : new String[] {"rename", "rename CAM001", "rename BAD-ID Camera",
            "rename CAM001 " + "x".repeat(51)}) {
            assertThrows(ParseException.class, () -> logic.execute(command));
            assertEquals(before, Files.readString(dataFile));
        }
        assertThrows(CommandException.class, () -> logic.execute("rename MISSING New Name"));
        assertEquals(before, Files.readString(dataFile));
        assertEquals(new EquipmentName("Sony Camera"), model.getEquipmentList().get(0).getName());
    }

    /** A failed save restores the old name and borrower instead of leaving a partial correction. */
    @Test
    public void execute_cannotSave_restoresNameAndLoan() throws Exception {
        logic.execute("issue CAM001 Alice Tan");
        Equipment original = model.getEquipmentList().get(0);
        Files.move(dataFile, testFolder.resolve("backup.json"));
        Files.createDirectory(dataFile);

        assertThrows(CommandException.class, () -> logic.execute("rename CAM001 New Name"));

        assertEquals(original, model.getEquipmentList().get(0));
    }
}
