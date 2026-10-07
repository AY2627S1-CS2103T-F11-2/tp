package seedu.address.model.equipment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class EquipmentTest {

    private final EquipmentId cameraId = new EquipmentId("CAM001");
    private final BorrowerName borrower = new BorrowerName("John Tan");

    @Test
    public void constructor_nullFields_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Equipment(null, "Sony Camera"));
        assertThrows(NullPointerException.class, () -> new Equipment(cameraId, null));
        assertThrows(NullPointerException.class, () -> Equipment.isValidName(null));
    }

    @Test
    public void constructor_invalidNames_throwsIllegalArgumentException() {
        String[] invalidNames = {"", " ", "A".repeat(51), "Sony\nCamera", "Sony\tCamera"};
        for (String name : invalidNames) {
            assertFalse(Equipment.isValidName(name));
            assertThrows(IllegalArgumentException.class, Equipment.MESSAGE_NAME_CONSTRAINTS, ()
                    -> new Equipment(cameraId, name));
        }
    }

    @Test
    public void constructor_validName_preservesCaseAndPunctuation() {
        Equipment equipment = new Equipment(cameraId, "  Sony   Camera-1 / Club's  ");
        assertEquals("Sony Camera-1 / Club's", equipment.getName());
        assertEquals(cameraId, equipment.getId());
        assertEquals(Optional.empty(), equipment.getIssuedTo());
        assertFalse(equipment.isIssued());
        assertTrue(Equipment.isValidName("A"));
        assertTrue(Equipment.isValidName("A".repeat(50)));
        assertTrue(Equipment.isValidName("A".repeat(24) + "   " + "B".repeat(25)));
    }

    @Test
    public void issueTo_availableEquipment_returnsIssuedCopy() {
        Equipment available = new Equipment(cameraId, "Sony Camera");
        Equipment issued = available.issueTo(borrower);
        assertFalse(available.isIssued());
        assertEquals(Optional.empty(), available.getIssuedTo());
        assertTrue(issued.isIssued());
        assertEquals(Optional.of(borrower), issued.getIssuedTo());
        assertEquals(available.getId(), issued.getId());
        assertEquals(available.getName(), issued.getName());
        assertEquals("CAM001 | Sony Camera | Available", available.toString());
        assertEquals("CAM001 | Sony Camera | Issued to: John Tan", issued.toString());
    }

    @Test
    public void issueTo_invalidTransition_keepsExistingRecord() {
        Equipment available = new Equipment(cameraId, "Sony Camera");
        assertThrows(NullPointerException.class, () -> available.issueTo(null));
        assertFalse(available.isIssued());

        Equipment issued = available.issueTo(borrower);
        assertThrows(IllegalStateException.class, () -> issued.issueTo(new BorrowerName("Alice Lim")));
        assertEquals(Optional.of(borrower), issued.getIssuedTo());
    }

    @Test
    public void equals_allFields_comparesIdNameAndBorrower() {
        Equipment equipment = new Equipment(cameraId, "Sony Camera", borrower);
        Equipment sameEquipment = new Equipment(new EquipmentId("cam001"), "Sony Camera", borrower);
        assertEquals(sameEquipment, equipment);
        assertEquals(sameEquipment.hashCode(), equipment.hashCode());
        assertTrue(equipment.equals(equipment));
        assertFalse(equipment.equals(new Equipment(new EquipmentId("CAM002"), "Sony Camera", borrower)));
        assertFalse(equipment.equals(new Equipment(cameraId, "Different Camera", borrower)));
        assertFalse(equipment.equals(new Equipment(cameraId, "Sony Camera")));
        assertFalse(equipment.equals(new Equipment(cameraId, "Sony Camera", new BorrowerName("Alice Lim"))));
        assertFalse(equipment.equals(null));
        assertFalse(equipment.equals("Sony Camera"));
    }
}
