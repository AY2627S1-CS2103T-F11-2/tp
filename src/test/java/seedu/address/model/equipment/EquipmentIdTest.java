package seedu.address.model.equipment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class EquipmentIdTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EquipmentId(null));
        assertThrows(NullPointerException.class, () -> EquipmentId.isValidEquipmentId(null));
    }

    @Test
    public void constructor_invalidIds_throwsIllegalArgumentException() {
        String[] invalidIds = {"", " ", "CAM 001", "CAM-001", "CAM_001", "é", "A".repeat(21)};
        for (String id : invalidIds) {
            assertFalse(EquipmentId.isValidEquipmentId(id));
            assertThrows(IllegalArgumentException.class, EquipmentId.MESSAGE_CONSTRAINTS, () -> new EquipmentId(id));
        }
    }

    @Test
    public void constructor_validIds_normalizesCaseAndWhitespace() {
        String[] validIds = {"A", "7", "A".repeat(20), "cam001", " CAM001 "};
        for (String id : validIds) {
            assertTrue(EquipmentId.isValidEquipmentId(id));
            assertEquals(id.trim().toUpperCase(java.util.Locale.ROOT), new EquipmentId(id).toString());
        }
    }

    @Test
    public void equals_normalizedIds_comparesCanonicalValues() {
        EquipmentId id = new EquipmentId(" cam001 ");
        EquipmentId sameId = new EquipmentId("CAM001");
        assertEquals(sameId, id);
        assertEquals(sameId.hashCode(), id.hashCode());
        assertTrue(id.equals(id));
        assertFalse(id.equals(new EquipmentId("MIC001")));
        assertFalse(id.equals(null));
        assertFalse(id.equals("CAM001"));
    }
}
