package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
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

    /** Verifies available equipment shows a green-coded status indicator. */
    @Test
    public void constructor_availableEquipment_showsAvailableIndicator() throws Exception {
        runOnFxThread(() -> {
            Equipment equipment = new Equipment(new EquipmentId("CAM001"), new EquipmentName("Sony Camera"));
            Circle indicator = getStatusIndicator(new EquipmentCard(equipment));
            assertTrue(indicator.getStyleClass().contains("status-available"));
            assertFalse(indicator.getStyleClass().contains("status-issued"));
        });
    }

    /** Verifies issued equipment shows an orange-coded status indicator. */
    @Test
    public void constructor_issuedEquipment_showsIssuedIndicator() throws Exception {
        runOnFxThread(() -> {
            Equipment camera = new Equipment(new EquipmentId("CAM001"), new EquipmentName("Sony Camera"));
            Circle indicator = getStatusIndicator(new EquipmentCard(camera.issueTo(new BorrowerName("John Tan"))));
            assertTrue(indicator.getStyleClass().contains("status-issued"));
            assertFalse(indicator.getStyleClass().contains("status-available"));
        });
    }

    private static Circle getStatusIndicator(EquipmentCard card) {
        return (Circle) card.getRoot().lookup("#statusIndicator");
    }
}
