package seedu.address.model.equipment;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the unique identifier of an equipment item.
 */
public class EquipmentId {

    public static final String MESSAGE_CONSTRAINTS =
            "Error: Equipment ID must contain only letters and numbers and cannot contain spaces.";
    public static final String VALIDATION_REGEX = "[A-Za-z0-9]{1,20}";

    public final String value;

    /**
     * Creates an equipment ID from a valid identifier.
     */
    public EquipmentId(String id) {
        requireNonNull(id);
        String trimmedId = id.trim();
        checkArgument(isValidId(trimmedId), MESSAGE_CONSTRAINTS);
        value = trimmedId;
    }

    /** Returns whether {@code test} is a valid equipment ID. */
    public static boolean isValidId(String test) {
        return test != null && test.trim().matches(VALIDATION_REGEX);
    }

    /** Returns whether this ID represents the same identifier as {@code other}, ignoring case. */
    public boolean isSameId(EquipmentId other) {
        requireNonNull(other);
        return value.equalsIgnoreCase(other.value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof EquipmentId otherId && isSameId(otherId));
    }

    @Override
    public int hashCode() {
        return value.toLowerCase(java.util.Locale.ROOT).hashCode();
    }
}
