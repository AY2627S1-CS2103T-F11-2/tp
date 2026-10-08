package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.model.equipment.EquipmentName;

public class RemoveCommandTest {
    private static final EquipmentId CAMERA_ID = new EquipmentId("CAM001");
    private static final Equipment CAMERA = new Equipment(CAMERA_ID, new EquipmentName("Sony Camera"));
    private static final Equipment MICROPHONE =
            new Equipment(new EquipmentId("MIC001"), new EquipmentName("Wireless Microphone"));

    @Test
    public void constructor_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RemoveCommand(null));
    }

    @Test
    public void execute_existingEquipment_removesOnlyTarget() {
        Model model = new ModelManager();
        model.addEquipment(CAMERA);
        model.addEquipment(MICROPHONE);
        Model expectedModel = new ModelManager();
        expectedModel.addEquipment(MICROPHONE);

        assertCommandSuccess(new RemoveCommand(new EquipmentId("cam001")), model,
                "Equipment CAM001 removed successfully.", expectedModel);
        assertEquals(List.of(MICROPHONE), model.getAddressBook().getEquipmentList());
    }

    @Test
    public void execute_missingEquipment_throwsCommandException() {
        Model model = new ModelManager();
        model.addEquipment(CAMERA);

        assertCommandFailure(new RemoveCommand(new EquipmentId("MIC001")), model,
                "Error: Equipment with ID MIC001 does not exist.");
        assertEquals(List.of(CAMERA), model.getAddressBook().getEquipmentList());
    }

    @Test
    public void equalsAndToString() {
        RemoveCommand command = new RemoveCommand(CAMERA_ID);
        RemoveCommand equivalent = new RemoveCommand(new EquipmentId("cam001"));

        assertTrue(command.equals(command));
        assertTrue(command.equals(equivalent));
        assertEquals(command.hashCode(), equivalent.hashCode());
        assertFalse(command.equals(null));
        assertFalse(command.equals("remove"));
        assertFalse(command.equals(new RemoveCommand(new EquipmentId("MIC001"))));
        assertEquals(RemoveCommand.class.getCanonicalName() + "{equipmentId=CAM001}", command.toString());
    }
}
