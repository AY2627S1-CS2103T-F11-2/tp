package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.model.equipment.EquipmentName;
import seedu.address.model.equipment.EquipmentStatus;

/** Jackson-friendly version of {@link Equipment}. */
class JsonAdaptedEquipment {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Equipment's %s field is missing!";

    private final String id;
    private final String name;
    private final String status;
    private final String issuedTo;

    @JsonCreator
    JsonAdaptedEquipment(@JsonProperty("id") String id, @JsonProperty("name") String name,
            @JsonProperty("status") String status, @JsonProperty("issuedTo") String issuedTo) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.issuedTo = issuedTo;
    }

    /** Constructs an adapted item using the upstream status-only format. */
    JsonAdaptedEquipment(String id, String name, String status) {
        this(id, name, status, null);
    }

    JsonAdaptedEquipment(Equipment source) {
        id = source.getId().value;
        name = source.getName().value;
        status = source.getStatus().name();
        issuedTo = source.getIssuedTo().map(BorrowerName::toString).orElse(null);
    }

    Equipment toModelType() throws IllegalValueException {
        if (id == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "id"));
        }
        if (!EquipmentId.isValidId(id)) {
            throw new IllegalValueException(EquipmentId.MESSAGE_CONSTRAINTS);
        }
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "name"));
        }
        if (!EquipmentName.isValidName(name)) {
            throw new IllegalValueException(EquipmentName.MESSAGE_CONSTRAINTS);
        }

        BorrowerName borrower = null;
        if (issuedTo != null && !issuedTo.isEmpty()) {
            if (!BorrowerName.isValidName(issuedTo)) {
                throw new IllegalValueException(BorrowerName.MESSAGE_CONSTRAINTS);
            }
            borrower = new BorrowerName(issuedTo);
        }

        EquipmentStatus equipmentStatus;
        try {
            // Missing status is treated as the original, available-record format.
            equipmentStatus = status == null
                    ? (borrower == null ? EquipmentStatus.AVAILABLE : EquipmentStatus.ISSUED)
                    : EquipmentStatus.valueOf(status);
        } catch (IllegalArgumentException exception) {
            throw new IllegalValueException("Equipment status is invalid.");
        }

        if ((equipmentStatus == EquipmentStatus.ISSUED) != (borrower != null)) {
            throw new IllegalValueException("Equipment status and borrower do not match.");
        }
        return new Equipment(new EquipmentId(id), new EquipmentName(name), equipmentStatus, borrower);
    }
}
