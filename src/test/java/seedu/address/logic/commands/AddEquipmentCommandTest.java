package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.model.equipment.EquipmentName;

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

    @Test
    public void execute_duplicateIdIgnoringCase_throwsCommandException() {
        Model model = new ModelManager();
        model.addEquipment(CAMERA);
        Equipment sameId = new Equipment(new EquipmentId("cam001"), new EquipmentName("Other Camera"));

        assertCommandFailure(new AddEquipmentCommand(sameId), model,
                "Error: Equipment with ID cam001 already exists.");
    }

    @Test
    public void equals_sameEquipment_returnsTrue() {
        assertEquals(new AddEquipmentCommand(CAMERA), new AddEquipmentCommand(CAMERA));
    }
}
