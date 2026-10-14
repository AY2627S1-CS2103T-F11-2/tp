package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.model.equipment.EquipmentName;

public class EquipmentCardTest extends JavaFxTest {
    @Test
    public void constructor_equipment_displaysIdNameAndStatus() throws Exception {
        runOnFxThread(() -> {
            Equipment equipment = new Equipment(new EquipmentId("CAM001"), new EquipmentName("Sony Camera"));
            EquipmentCard card = new EquipmentCard(equipment);
            assertEquals("CAM001", ((Label) card.getRoot().lookup("#equipmentId")).getText());
            assertEquals("Sony Camera", ((Label) card.getRoot().lookup("#name")).getText());
            assertEquals("Status: Available", ((Label) card.getRoot().lookup("#status")).getText());
        });
    }
    @Test
    public void constructor_issuedEquipment_displaysIssuedStatus() throws Exception {
        runOnFxThread(() -> {
            Equipment camera = new Equipment(new EquipmentId("CAM001"), new EquipmentName("Sony Camera"));
            EquipmentCard card = new EquipmentCard(camera.issueTo(new BorrowerName("John Tan")));
            assertEquals("Status: Issued", ((Label) card.getRoot().lookup("#status")).getText());
        });
    }
}
