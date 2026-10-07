package seedu.address.model.equipment;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a unique, case-insensitive equipment ID.
 */
public final class EquipmentId {

    public static final String MESSAGE_CONSTRAINTS =
            "Error: Equipment ID must contain only letters and numbers and cannot contain spaces.";
    public static final String VALIDATION_REGEX = "[A-Za-z0-9]{1,20}";

    private final String value;

    /**
     * Constructs an ID after trimming, validating, and converting it to uppercase.
     */
    public EquipmentId(String id) {
        requireNonNull(id);
        String trimmedId = id.trim();
        checkArgument(isValidEquipmentId(trimmedId), MESSAGE_CONSTRAINTS);
        value = trimmedId.toUpperCase(Locale.ROOT);
    }

    /**
     * Returns true if the trimmed ID contains between 1 and 20 ASCII letters or digits.
     */
    public static boolean isValidEquipmentId(String id) {
        requireNonNull(id);
        return id.trim().matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof EquipmentId otherId)) {
            return false;
        }

        return value.equals(otherId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
