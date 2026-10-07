package seedu.address.model.equipment;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Objects;
import java.util.Optional;

/**
 * An immutable equipment record containing its ID, display name, and current borrower.
 */
public final class Equipment {

    public static final String MESSAGE_NAME_CONSTRAINTS =
            "Error: Equipment name cannot be empty and must be 1–50 characters long.";

    private final EquipmentId id;
    private final String name;
    private final BorrowerName issuedTo;

    /**
     * Constructs an available equipment record.
     */
    public Equipment(EquipmentId id, String name) {
        this(id, name, null);
    }

    /**
     * Constructs an equipment record. A null borrower denotes available equipment.
     */
    public Equipment(EquipmentId id, String name, BorrowerName issuedTo) {
        this.id = requireNonNull(id);
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_NAME_CONSTRAINTS);
        this.name = normalizeName(name);
        this.issuedTo = issuedTo;
    }

    /**
     * Returns true if the normalized display name has 1–50 characters and no control characters.
     */
    public static boolean isValidName(String name) {
        requireNonNull(name);
        String normalizedName = normalizeName(name);
        return !normalizedName.isEmpty() && normalizedName.length() <= 50
                && normalizedName.chars().noneMatch(Character::isISOControl);
    }

    private static String normalizeName(String name) {
        return name.trim().replaceAll(" +", " ");
    }

    public EquipmentId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Optional<BorrowerName> getIssuedTo() {
        return Optional.ofNullable(issuedTo);
    }

    /**
     * Returns whether the equipment currently has a borrower.
     */
    public boolean isIssued() {
        return issuedTo != null;
    }

    /**
     * Returns an issued copy of this record without modifying the available record.
     *
     * @throws IllegalStateException if the equipment already has a borrower.
     */
    public Equipment issueTo(BorrowerName borrower) {
        requireNonNull(borrower);
        if (isIssued()) {
            throw new IllegalStateException("Equipment " + id + " is already issued to " + issuedTo + ".");
        }
        return new Equipment(id, name, borrower);
    }

    @Override
    public String toString() {
        String status = isIssued() ? "Issued to: " + issuedTo : "Available";
        return id + " | " + name + " | " + status;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Equipment otherEquipment)) {
            return false;
        }

        return id.equals(otherEquipment.id) && name.equals(otherEquipment.name)
                && Objects.equals(issuedTo, otherEquipment.issuedTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, issuedTo);
    }
}
