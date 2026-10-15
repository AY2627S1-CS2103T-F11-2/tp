package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.model.equipment.EquipmentName;

public class EquipmentCardTest extends JavaFxTest {
    /** Verifies available equipment shows its shared display text in the layout. */
    @Test
    public void constructor_equipment_displaysIdNameAndStatus() throws Exception {
        runOnFxThread(() -> {
            Equipment equipment = new Equipment(new EquipmentId("CAM001"), new EquipmentName("Sony Camera"));
            EquipmentCard card = new EquipmentCard(equipment);
            assertEquals("CAM001", ((Label) card.getRoot().lookup("#equipmentId")).getText());
            assertEquals("Sony Camera", ((Label) card.getRoot().lookup("#name")).getText());
            assertEquals(3, card.getRoot().lookupAll(".label").size());
            Label borrower = (Label) card.getRoot().lookup("#borrower");
            assertEquals("Available", borrower.getText());
            assertTrue(borrower.isVisible());
            assertTrue(borrower.isManaged());
        });
    }
    /** Verifies issued equipment shows the current borrower in the layout. */
    @Test
    public void constructor_issuedEquipment_displaysIssuedStatus() throws Exception {
        runOnFxThread(() -> {
            Equipment camera = new Equipment(new EquipmentId("CAM001"), new EquipmentName("Sony Camera"));
            EquipmentCard card = new EquipmentCard(camera.issueTo(new BorrowerName("John Tan")));
            assertEquals(3, card.getRoot().lookupAll(".label").size());
            Label borrower = (Label) card.getRoot().lookup("#borrower");
            assertEquals("Issued to: John Tan", borrower.getText());
            assertTrue(borrower.isVisible());
            assertTrue(borrower.isManaged());
        });
    }
}
