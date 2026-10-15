package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;

public class ReturnCommandTest {
    private static final EquipmentId CAMERA_ID = new EquipmentId("CAM001");
    private static final BorrowerName JOHN = new BorrowerName("John Tan");
    private static final Equipment AVAILABLE_CAMERA = new Equipment(CAMERA_ID, "Sony Camera");
    private static final Equipment ISSUED_CAMERA = new Equipment(CAMERA_ID, "Sony Camera", JOHN);

    @Test
    public void constructor_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ReturnCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ReturnCommand(CAMERA_ID).execute(null));
    }

    @Test
    public void execute_issuedEquipment_marksOnlyTargetAvailable() throws Exception {
        ModelManager model = new ModelManager();
        Equipment issuedMicrophone = new Equipment(new EquipmentId("MIC001"), "Wireless Microphone", JOHN);
        model.addEquipment(ISSUED_CAMERA);
        model.addEquipment(issuedMicrophone);

        CommandResult result = new ReturnCommand(new EquipmentId("cam001")).execute(model);

        assertEquals("Equipment CAM001 returned successfully.\nCAM001 | Sony Camera | Available",
                result.getFeedbackToUser());
        assertEquals(List.of(AVAILABLE_CAMERA, issuedMicrophone), model.getClubLogisticsData().getEquipmentList());
    }

    @Test
    public void execute_missingEquipment_throwsCommandExceptionAndPreservesModel() {
        ModelManager model = new ModelManager();
        model.addEquipment(ISSUED_CAMERA);
        ReturnCommand command = new ReturnCommand(new EquipmentId("MIC001"));

        assertThrows(CommandException.class, "Error: Equipment with ID MIC001 does not exist.", ()
                -> command.execute(model));
        assertEquals(List.of(ISSUED_CAMERA), model.getClubLogisticsData().getEquipmentList());
    }

    @Test
    public void execute_availableEquipment_throwsCommandExceptionAndPreservesModel() {
        ModelManager model = new ModelManager();
        model.addEquipment(AVAILABLE_CAMERA);
        ReturnCommand command = new ReturnCommand(new EquipmentId("cam001"));

        assertThrows(CommandException.class, "Error: Equipment CAM001 is not currently issued.", ()
                -> command.execute(model));
        assertEquals(List.of(AVAILABLE_CAMERA), model.getClubLogisticsData().getEquipmentList());
    }

    @Test
    public void equals() {
        ReturnCommand command = new ReturnCommand(CAMERA_ID);
        ReturnCommand equivalentCommand = new ReturnCommand(new EquipmentId("cam001"));

        assertTrue(command.equals(command));
        assertTrue(command.equals(equivalentCommand));
        assertEquals(command.hashCode(), equivalentCommand.hashCode());
        assertFalse(command.equals(null));
        assertFalse(command.equals("return"));
        assertFalse(command.equals(new ReturnCommand(new EquipmentId("MIC001"))));
    }

    @Test
    public void toStringMethod() {
        ReturnCommand command = new ReturnCommand(CAMERA_ID);
        String expected = ReturnCommand.class.getCanonicalName() + "{equipmentId=CAM001}";

        assertEquals(expected, command.toString());
    }
}
