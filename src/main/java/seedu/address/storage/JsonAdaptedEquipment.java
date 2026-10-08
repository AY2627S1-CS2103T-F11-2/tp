package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.model.equipment.EquipmentName;
import seedu.address.model.equipment.EquipmentStatus;

/** Jackson-friendly version of {@link Equipment}. */
class JsonAdaptedEquipment {

    private final String id;
    private final String name;
    private final String status;

    @JsonCreator
    JsonAdaptedEquipment(@JsonProperty("id") String id, @JsonProperty("name") String name,
            @JsonProperty("status") String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    JsonAdaptedEquipment(Equipment source) {
        id = source.getId().value;
        name = source.getName().value;
        status = source.getStatus().name();
    }

    Equipment toModelType() throws IllegalValueException {
        if (!EquipmentId.isValidId(id)) {
            throw new IllegalValueException(EquipmentId.MESSAGE_CONSTRAINTS);
        }
        if (!EquipmentName.isValidName(name)) {
            throw new IllegalValueException(EquipmentName.MESSAGE_CONSTRAINTS);
        }
        if (status == null) {
            throw new IllegalValueException("Equipment status is missing.");
        }
        try {
            return new Equipment(new EquipmentId(id), new EquipmentName(name), EquipmentStatus.valueOf(status));
        } catch (IllegalArgumentException exception) {
            throw new IllegalValueException("Equipment status is invalid.");
        }
    }
}
