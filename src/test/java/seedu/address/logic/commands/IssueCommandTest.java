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

public class IssueCommandTest {
    private static final EquipmentId CAMERA_ID = new EquipmentId("CAM001");
    private static final BorrowerName JOHN = new BorrowerName("John Tan");
    private static final Equipment CAMERA = new Equipment(CAMERA_ID, "Sony Camera");

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new IssueCommand(null, JOHN));
        assertThrows(NullPointerException.class, () -> new IssueCommand(CAMERA_ID, null));
    }

    @Test
    public void execute_availableEquipment_updatesOnlyTarget() throws Exception {
        ModelManager model = new ModelManager();
        Equipment microphone = new Equipment(new EquipmentId("MIC001"), "Wireless Microphone");
        model.addEquipment(CAMERA);
        model.addEquipment(microphone);

        CommandResult result = new IssueCommand(new EquipmentId("cam001"), JOHN).execute(model);

        Equipment expectedCamera = new Equipment(CAMERA_ID, "Sony Camera", JOHN);
        assertEquals("Equipment CAM001 issued to John Tan.\nCAM001 | Sony Camera | Issued to: John Tan",
                result.getFeedbackToUser());
        assertEquals(List.of(expectedCamera, microphone), model.getAddressBook().getEquipmentList());
        assertFalse(CAMERA.isIssued());
    }

    @Test
    public void execute_missingEquipment_throwsCommandExceptionAndPreservesModel() {
        ModelManager model = new ModelManager();
        model.addEquipment(CAMERA);
        IssueCommand command = new IssueCommand(new EquipmentId("MIC001"), JOHN);

        assertThrows(CommandException.class, "Error: Equipment with ID MIC001 does not exist.", ()
                -> command.execute(model));
        assertEquals(List.of(CAMERA), model.getAddressBook().getEquipmentList());
    }

    @Test
    public void execute_alreadyIssuedEquipment_throwsCommandExceptionAndPreservesBorrower() {
        ModelManager model = new ModelManager();
        Equipment issuedCamera = new Equipment(CAMERA_ID, "Sony Camera", JOHN);
        model.addEquipment(issuedCamera);
        IssueCommand command = new IssueCommand(CAMERA_ID, new BorrowerName("Alice Lim"));

        assertThrows(CommandException.class, "Error: Equipment CAM001 is already issued to John Tan.", ()
                -> command.execute(model));
        assertEquals(List.of(issuedCamera), model.getAddressBook().getEquipmentList());
    }

    @Test
    public void execute_sameBorrowerForDifferentEquipment_success() throws Exception {
        ModelManager model = new ModelManager();
        Equipment issuedCamera = new Equipment(CAMERA_ID, "Sony Camera", JOHN);
        EquipmentId microphoneId = new EquipmentId("MIC001");
        model.addEquipment(issuedCamera);
        model.addEquipment(new Equipment(microphoneId, "Wireless Microphone"));

        new IssueCommand(microphoneId, JOHN).execute(model);

        assertEquals(List.of(issuedCamera, new Equipment(microphoneId, "Wireless Microphone", JOHN)),
                model.getAddressBook().getEquipmentList());
    }

    @Test
    public void equals() {
        IssueCommand command = new IssueCommand(CAMERA_ID, JOHN);
        IssueCommand equivalentCommand = new IssueCommand(new EquipmentId("cam001"), new BorrowerName("John Tan"));

        assertTrue(command.equals(command));
        assertTrue(command.equals(equivalentCommand));
        assertEquals(command.hashCode(), equivalentCommand.hashCode());
        assertFalse(command.equals(null));
        assertFalse(command.equals("issue"));
        assertFalse(command.equals(new IssueCommand(new EquipmentId("MIC001"), JOHN)));
        assertFalse(command.equals(new IssueCommand(CAMERA_ID, new BorrowerName("Alice Lim"))));
    }

    @Test
    public void toStringMethod() {
        IssueCommand command = new IssueCommand(CAMERA_ID, JOHN);
        String expected = IssueCommand.class.getCanonicalName() + "{equipmentId=CAM001, borrower=John Tan}";

        assertEquals(expected, command.toString());
    }
}
