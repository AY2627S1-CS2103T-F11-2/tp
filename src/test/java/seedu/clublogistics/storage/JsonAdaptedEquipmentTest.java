package seedu.clublogistics.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.clublogistics.storage.JsonAdaptedEquipment.MISSING_FIELD_MESSAGE_FORMAT;
import static seedu.clublogistics.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.clublogistics.commons.exceptions.IllegalValueException;
import seedu.clublogistics.model.equipment.BorrowerName;
import seedu.clublogistics.model.equipment.Equipment;
import seedu.clublogistics.model.equipment.EquipmentId;
import seedu.clublogistics.model.equipment.EquipmentName;
import seedu.clublogistics.model.equipment.EquipmentStatus;

public class JsonAdaptedEquipmentTest {

    private static final String VALID_ID = "CAM001";
    private static final String VALID_NAME = "Sony Camera";
    private static final String VALID_BORROWER = "John Tan";

    @Test
    public void toModelType_legacyStatusOnlyAvailableRecord_success() throws Exception {
        Equipment expected = new Equipment(new EquipmentId(VALID_ID), new EquipmentName(VALID_NAME),
                EquipmentStatus.AVAILABLE);
        assertEquals(expected, new JsonAdaptedEquipment(VALID_ID, VALID_NAME, "AVAILABLE").toModelType());
    }

    @Test
    public void toModelType_invalidStatus_rejectsRecord() {
        assertThrows(IllegalValueException.class, "Equipment status is invalid.", ()
                -> new JsonAdaptedEquipment(VALID_ID, VALID_NAME, "UNKNOWN", null).toModelType());
    }

    @Test
    public void toModelType_statusBorrowerMismatch_rejectsRecord() {
        String message = "Equipment status and borrower do not match.";
        assertThrows(IllegalValueException.class, message, ()
                -> new JsonAdaptedEquipment(VALID_ID, VALID_NAME, "ISSUED", null).toModelType());
        assertThrows(IllegalValueException.class, message, ()
                -> new JsonAdaptedEquipment(VALID_ID, VALID_NAME, "ISSUED", "").toModelType());
        assertThrows(IllegalValueException.class, message, ()
                -> new JsonAdaptedEquipment(VALID_ID, VALID_NAME, "AVAILABLE", VALID_BORROWER).toModelType());
    }

    @Test
    public void toModelType_availableEquipment_returnsEquipment() throws Exception {
        Equipment equipment = new Equipment(new EquipmentId(VALID_ID), new EquipmentName(VALID_NAME));
        assertEquals(equipment, new JsonAdaptedEquipment(equipment).toModelType());
    }

    @Test
    public void toModelType_issuedEquipment_returnsEquipment() throws Exception {
        Equipment equipment = new Equipment(new EquipmentId(VALID_ID), new EquipmentName(VALID_NAME),
                new BorrowerName(VALID_BORROWER));
        assertEquals(equipment, new JsonAdaptedEquipment(equipment).toModelType());
    }

    @Test
    public void toModelType_validDetails_normalizesEquipment() throws Exception {
        JsonAdaptedEquipment adapted = new JsonAdaptedEquipment(" cam001 ", " Sony   Camera ", null,
                " John   Tan ");
        Equipment expected = new Equipment(new EquipmentId(VALID_ID), new EquipmentName(VALID_NAME),
                new BorrowerName(VALID_BORROWER));
        assertEquals(expected, adapted.toModelType());
    }

    @Test
    public void toModelType_emptyBorrower_returnsAvailableEquipment() throws Exception {
        Equipment equipment = new JsonAdaptedEquipment(VALID_ID, VALID_NAME, null, "").toModelType();
        assertFalse(equipment.isIssued());
    }

    @Test
    public void toModelType_missingId_throwsIllegalValueException() {
        JsonAdaptedEquipment equipment = new JsonAdaptedEquipment(null, VALID_NAME, null, null);
        assertThrows(IllegalValueException.class, String.format(MISSING_FIELD_MESSAGE_FORMAT, "id"),
                equipment::toModelType);
    }

    @Test
    public void toModelType_invalidId_throwsIllegalValueException() {
        for (String id : new String[] {"", "CAM-001", "CAM 001", "A".repeat(21)}) {
            JsonAdaptedEquipment equipment = new JsonAdaptedEquipment(id, VALID_NAME, null, null);
            assertThrows(IllegalValueException.class, EquipmentId.MESSAGE_CONSTRAINTS, equipment::toModelType);
        }
    }

    @Test
    public void toModelType_missingName_throwsIllegalValueException() {
        JsonAdaptedEquipment equipment = new JsonAdaptedEquipment(VALID_ID, null, null, null);
        assertThrows(IllegalValueException.class, String.format(MISSING_FIELD_MESSAGE_FORMAT, "name"),
                equipment::toModelType);
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        for (String name : new String[] {"", " ", "A".repeat(51)}) {
            JsonAdaptedEquipment equipment = new JsonAdaptedEquipment(VALID_ID, name, null, null);
            assertThrows(IllegalValueException.class, EquipmentName.MESSAGE_CONSTRAINTS, equipment::toModelType);
        }
    }

    @Test
    public void toModelType_invalidBorrower_throwsIllegalValueException() {
        for (String borrower : new String[] {" ", "John3", "John-Tan", "A".repeat(51)}) {
            JsonAdaptedEquipment equipment = new JsonAdaptedEquipment(VALID_ID, VALID_NAME, null, borrower);
            assertThrows(IllegalValueException.class, BorrowerName.MESSAGE_CONSTRAINTS, equipment::toModelType);
        }
    }
}
