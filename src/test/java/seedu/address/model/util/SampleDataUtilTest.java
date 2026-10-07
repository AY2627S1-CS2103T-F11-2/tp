package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.equipment.EquipmentId;

public class SampleDataUtilTest {

    @Test
    public void getSampleAddressBook_containsAvailableEquipment() {
        ReadOnlyAddressBook sample = SampleDataUtil.getSampleAddressBook();

        assertEquals(3, sample.getEquipmentList().size());
        assertTrue(sample.getEquipmentList().stream().noneMatch(equipment -> equipment.isIssued()));
        assertEquals(new EquipmentId("CAM001"), sample.getEquipmentList().get(0).getId());
        assertEquals("Sony Camera", sample.getEquipmentList().get(0).getName());
    }
}
