package seedu.clublogistics.model.equipment;

import static java.util.Objects.requireNonNull;
import static seedu.clublogistics.commons.util.AppUtil.checkArgument;

/**
 * Represents the name of the person currently holding a piece of equipment.
 */
public final class BorrowerName {

    public static final String MESSAGE_CONSTRAINTS =
            "Error: Person name must be 1 to 50 characters long and contain only letters (A-Z, a-z),"
            + " spaces, and apostrophes (') and hyphens (-) between letters.";
    public static final int MAX_LENGTH = 50;
    private static final String WORD_REGEX = "[A-Za-z]+(?:['-][A-Za-z]+)*";
    public static final String VALIDATION_REGEX = WORD_REGEX + "(?: " + WORD_REGEX + ")*";

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
     * Returns true if the normalized name has 1 to 50 characters and consists of space-separated words
     * of letters, where apostrophes and hyphens may only join two letters.
     */
    public static boolean isValidName(String name) {
        requireNonNull(name);
        String normalizedName = normalize(name);
        return normalizedName.length() <= MAX_LENGTH && normalizedName.matches(VALIDATION_REGEX);
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
