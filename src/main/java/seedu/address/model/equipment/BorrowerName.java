package seedu.address.model.equipment;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the name of the person currently holding a piece of equipment.
 */
public final class BorrowerName {

    public static final String MESSAGE_CONSTRAINTS =
            "Error: Person name must contain only letters and spaces and be 1–50 characters long.";
    public static final String VALIDATION_REGEX = "[A-Za-z][A-Za-z ]{0,49}";

    private final String value;

    /**
     * Constructs a name after trimming and collapsing consecutive spaces.
     */
    public BorrowerName(String name) {
        requireNonNull(name);
        String normalizedName = normalize(name);
        checkArgument(isValidName(normalizedName), MESSAGE_CONSTRAINTS);
        value = normalizedName;
    }

    /**
     * Returns true if the normalized name contains between 1 and 50 letters and spaces.
     */
    public static boolean isValidName(String name) {
        requireNonNull(name);
        return normalize(name).matches(VALIDATION_REGEX);
    }

    private static String normalize(String name) {
        return name.trim().replaceAll(" +", " ");
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

        if (!(other instanceof BorrowerName otherName)) {
            return false;
        }

        return value.equals(otherName.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
