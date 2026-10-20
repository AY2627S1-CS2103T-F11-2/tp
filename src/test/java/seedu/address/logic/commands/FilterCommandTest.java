package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.model.equipment.EquipmentName;
import seedu.address.model.equipment.EquipmentStatus;

/** Tests that filter shows only equipment with the requested status. */
public class FilterCommandTest {

    private Model model;
    private Equipment camera;
    private Equipment microphone;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        camera = new Equipment(new EquipmentId("CAM001"), new EquipmentName("Sony Camera"));
        microphone = new Equipment(new EquipmentId("MIC001"), new EquipmentName("Wireless Microphone"))
                .issueTo(new BorrowerName("Alice"));
        model.addEquipment(camera);
        model.addEquipment(microphone);
    }

    @Test
    public void execute_availableWithMatches_showsOnlyAvailable() {
        assertCommandSuccess(new FilterCommand(EquipmentStatus.AVAILABLE), model,
                FilterCommand.MESSAGE_SHOWING_AVAILABLE, expectedModelFilteredBy(EquipmentStatus.AVAILABLE));
        assertEquals(List.of(camera), model.getFilteredEquipmentList());
    }

    @Test
    public void execute_issuedWithMatches_showsOnlyIssued() {
        assertCommandSuccess(new FilterCommand(EquipmentStatus.ISSUED), model,
                FilterCommand.MESSAGE_SHOWING_ISSUED, expectedModelFilteredBy(EquipmentStatus.ISSUED));
        assertEquals(List.of(microphone), model.getFilteredEquipmentList());
    }

    @Test
    public void execute_availableWithoutMatches_showsNoAvailableMessage() {
        model.setEquipment(camera, camera.issueTo(new BorrowerName("Bob")));

        CommandResult result = new FilterCommand(EquipmentStatus.AVAILABLE).execute(model);

        assertEquals(FilterCommand.MESSAGE_NO_AVAILABLE, result.getFeedbackToUser());
        assertTrue(model.getFilteredEquipmentList().isEmpty());
    }

    @Test
    public void execute_issuedWithoutMatches_showsNoIssuedMessage() {
        model.setEquipment(microphone, microphone.markReturned());

        CommandResult result = new FilterCommand(EquipmentStatus.ISSUED).execute(model);

        assertEquals(FilterCommand.MESSAGE_NO_ISSUED, result.getFeedbackToUser());
        assertTrue(model.getFilteredEquipmentList().isEmpty());
    }

    @Test
    public void equals() {
        FilterCommand availableCommand = new FilterCommand(EquipmentStatus.AVAILABLE);

        assertTrue(availableCommand.equals(availableCommand));
        assertTrue(availableCommand.equals(new FilterCommand(EquipmentStatus.AVAILABLE)));
        assertFalse(availableCommand.equals(new FilterCommand(EquipmentStatus.ISSUED)));
        assertFalse(availableCommand.equals(null));
        assertFalse(availableCommand.equals(1));
        assertNotEquals(availableCommand.hashCode(), new FilterCommand(EquipmentStatus.ISSUED).hashCode());
    }

    private Model expectedModelFilteredBy(EquipmentStatus status) {
        Model expectedModel = new ModelManager(model.getClubLogisticsData(), new UserPrefs());
        expectedModel.updateFilteredEquipmentList(item -> item.getStatus() == status);
        return expectedModel;
    }
}
