package seedu.clublogistics.model.equipment;

import static java.util.Objects.requireNonNull;
import static seedu.clublogistics.commons.util.AppUtil.checkArgument;

/**
 * Represents the display name of an equipment item.
 */
public class EquipmentName {

    public static final String ALLOWED_PUNCTUATION = "- / ' . , & ( ) # + :";
    public static final String MESSAGE_CONSTRAINTS =
            "Error: Equipment name must be 1 to 50 characters long and contain only letters, numbers, spaces"
            + " and these symbols: " + ALLOWED_PUNCTUATION;
    public static final int MAX_LENGTH = 50;
    public static final String VALIDATION_REGEX = "[\\p{L}\\p{N} \\-/'.,&()#+:]+";

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
        return normalisedName.length() <= MAX_LENGTH && normalisedName.matches(VALIDATION_REGEX);
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
