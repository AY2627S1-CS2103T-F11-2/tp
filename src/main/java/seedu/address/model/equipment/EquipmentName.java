package seedu.address.model.equipment;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the display name of an equipment item.
 */
public class EquipmentName {

    public static final String MESSAGE_CONSTRAINTS =
            "Error: Equipment name cannot be empty and must be 1–50 characters long.";
    public static final int MAX_LENGTH = 50;

    public final String value;

    /**
     * Creates an equipment name after normalising its whitespace.
     */
    public EquipmentName(String name) {
        requireNonNull(name);
        String normalisedName = normalise(name);
        checkArgument(isValidName(normalisedName), MESSAGE_CONSTRAINTS);
        value = normalisedName;
    }

    /** Returns whether {@code test} is a valid equipment name. */
    public static boolean isValidName(String test) {
        if (test == null) {
            return false;
        }
        String normalisedName = normalise(test);
        return !normalisedName.isEmpty() && normalisedName.length() <= MAX_LENGTH;
    }

    private static String normalise(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof EquipmentName otherName && value.equals(otherName.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
