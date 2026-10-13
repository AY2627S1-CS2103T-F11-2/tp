package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * An immutable optional note about a person. An empty value denotes no remark.
 */
public final class Remark {

    public final String value;

    /**
     * Creates a remark with no restrictions on its text other than being non-null.
     */
    public Remark(String remark) {
        value = requireNonNull(remark);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Remark otherRemark && value.equals(otherRemark.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
