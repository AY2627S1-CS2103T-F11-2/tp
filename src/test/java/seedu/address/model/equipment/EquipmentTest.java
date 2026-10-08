package seedu.address.model.equipment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class EquipmentTest {

    @Test
    public void equipmentId_caseInsensitiveIdentity_returnsTrue() {
        assertTrue(new EquipmentId("CAM001").isSameId(new EquipmentId("cam001")));
        assertEquals(new EquipmentId("CAM001"), new EquipmentId("cam001"));
    }

    @Test
    public void equipmentName_normalisesWhitespace_preservesCapitalisation() {
        EquipmentName name = new EquipmentName("  Sony   Alpha-7 / Mark II  ");

        assertEquals("Sony Alpha-7 / Mark II", name.value);
        assertTrue(EquipmentName.isValidName("Wireless Microphone's Case"));
        assertFalse(EquipmentName.isValidName(" "));
        assertFalse(EquipmentName.isValidName("a".repeat(51)));
    }

    @Test
    public void newEquipment_isAvailable() {
        Equipment equipment = new Equipment(new EquipmentId("CAM001"), new EquipmentName("Sony Camera"));

        assertEquals(EquipmentStatus.AVAILABLE, equipment.getStatus());
    }

    @Test
    public void markDamaged_availableEquipment_returnsDamagedCopy() {
        Equipment equipment = new Equipment(new EquipmentId("CAM001"), new EquipmentName("Sony Camera"));
        Equipment damaged = equipment.markDamaged();

        assertTrue(damaged.isDamaged());
        assertFalse(equipment.isDamaged());
        assertEquals(EquipmentStatus.DAMAGED, damaged.getStatus());
        assertEquals("Damaged", damaged.getStatus().toString());
        assertTrue(equipment.isSameEquipment(damaged));
        assertFalse(equipment.equals(damaged));
    }

    @Test
    public void markDamaged_alreadyDamaged_throwsIllegalStateException() {
        Equipment damaged = new Equipment(new EquipmentId("CAM001"), new EquipmentName("Sony Camera")).markDamaged();
        assertThrows(IllegalStateException.class, damaged::markDamaged);
    }
}
