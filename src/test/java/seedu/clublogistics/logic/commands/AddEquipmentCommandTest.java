package seedu.clublogistics.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.clublogistics.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.clublogistics.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import seedu.clublogistics.model.Model;
import seedu.clublogistics.model.ModelManager;
import seedu.clublogistics.model.equipment.Equipment;
import seedu.clublogistics.model.equipment.EquipmentId;
import seedu.clublogistics.model.equipment.EquipmentName;

public class AddEquipmentCommandTest {

    private static final Equipment CAMERA = new Equipment(new EquipmentId("CAM001"), new EquipmentName("Sony Camera"));

    @Test
    public void execute_newEquipment_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();
        expectedModel.addEquipment(CAMERA);

        assertCommandSuccess(new AddEquipmentCommand(CAMERA), model,
                "Equipment CAM001 added successfully.", expectedModel);
    }

    /** A duplicate typed in different case is reported with the ID as stored, matching the list. */
    @Test
    public void execute_duplicateIdIgnoringCase_reportsStoredId() {
        Model model = new ModelManager();
        model.addEquipment(CAMERA);
        Equipment sameId = new Equipment(new EquipmentId("cam001"), new EquipmentName("Other Camera"));

        assertCommandFailure(new AddEquipmentCommand(sameId), model,
                "Error: Equipment with ID CAM001 already exists.");
    }

    /** The stored lowercase ID is reported when the duplicate is typed in uppercase. */
    @Test
    public void execute_uppercaseDuplicateOfLowercaseId_reportsStoredId() {
        Model model = new ModelManager();
        model.addEquipment(new Equipment(new EquipmentId("cam002"), new EquipmentName("Tripod")));
        Equipment sameId = new Equipment(new EquipmentId("CAM002"), new EquipmentName("Tripod"));

        assertCommandFailure(new AddEquipmentCommand(sameId), model,
                "Error: Equipment with ID cam002 already exists.");
    }

    @Test
    public void equals_sameEquipment_returnsTrue() {
        assertEquals(new AddEquipmentCommand(CAMERA), new AddEquipmentCommand(CAMERA));
    }
}
