package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;

/**
 * Jackson-friendly version of {@link Equipment}.
 */
class JsonAdaptedEquipment {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Equipment's %s field is missing!";

    private final String id;
    private final String name;
    private final String issuedTo;

    /**
     * Constructs a {@code JsonAdaptedEquipment} with the given equipment details.
     */
    @JsonCreator
    public JsonAdaptedEquipment(@JsonProperty("id") String id, @JsonProperty("name") String name,
            @JsonProperty("issuedTo") String issuedTo) {
        this.id = id;
        this.name = name;
        this.issuedTo = issuedTo;
    }

    /**
     * Converts the given equipment into this class for Jackson use.
     */
    public JsonAdaptedEquipment(Equipment source) {
        id = source.getId().toString();
        name = source.getName();
        issuedTo = source.getIssuedTo().map(BorrowerName::toString).orElse(null);
    }

    /**
     * Converts this adapted equipment object into the model's equipment object.
     *
     * @throws IllegalValueException if any equipment details violate the model's constraints.
     */
    public Equipment toModelType() throws IllegalValueException {
        if (id == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "id"));
        }
        if (!EquipmentId.isValidEquipmentId(id)) {
            throw new IllegalValueException(EquipmentId.MESSAGE_CONSTRAINTS);
        }
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "name"));
        }
        if (!Equipment.isValidName(name)) {
            throw new IllegalValueException(Equipment.MESSAGE_NAME_CONSTRAINTS);
        }

        BorrowerName borrower = null;
        if (issuedTo != null && !issuedTo.isEmpty()) {
            if (!BorrowerName.isValidName(issuedTo)) {
                throw new IllegalValueException(BorrowerName.MESSAGE_CONSTRAINTS);
            }
            borrower = new BorrowerName(issuedTo);
        }

        return new Equipment(new EquipmentId(id), name, borrower);
    }
}
