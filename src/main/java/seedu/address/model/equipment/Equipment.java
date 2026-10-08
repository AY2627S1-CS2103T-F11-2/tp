package seedu.address.model.equipment;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Objects;
import java.util.Optional;

/** An immutable equipment record with its register status and optional borrower. */
public class Equipment {

    private final EquipmentId id;
    private final EquipmentName name;
    private final EquipmentStatus status;
    private final BorrowerName issuedTo;

    /** Creates an available equipment item. */
    public Equipment(EquipmentId id, EquipmentName name) {
        this(id, name, EquipmentStatus.AVAILABLE, null);
    }

    /** Convenience constructor for callers that have not yet migrated to {@link EquipmentName}. */
    public Equipment(EquipmentId id, String name) {
        this(id, new EquipmentName(name));
    }

    /** Creates an equipment item with its stored status and no borrower. */
    public Equipment(EquipmentId id, EquipmentName name, EquipmentStatus status) {
        this(id, name, status, null);
    }

    /** Creates an issued equipment item for the given borrower. */
    public Equipment(EquipmentId id, EquipmentName name, BorrowerName borrower) {
        this(id, name, EquipmentStatus.ISSUED, borrower);
    }

    /** Convenience constructor retained for the issue workflow's existing call sites. */
    public Equipment(EquipmentId id, String name, BorrowerName borrower) {
        this(id, new EquipmentName(name), borrower);
    }

    /** Creates an equipment item from its persisted status and optional borrower. */
    public Equipment(EquipmentId id, EquipmentName name, EquipmentStatus status, BorrowerName issuedTo) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        this.status = requireNonNull(status);
        this.issuedTo = issuedTo;
        checkArgument((status == EquipmentStatus.ISSUED) == (issuedTo != null),
                "Issued equipment must have a borrower, and available equipment must not.");
    }

    public EquipmentId getId() {
        return id;
    }

    public EquipmentName getName() {
        return name;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public Optional<BorrowerName> getIssuedTo() {
        return Optional.ofNullable(issuedTo);
    }

    public boolean isIssued() {
        return status == EquipmentStatus.ISSUED;
    }

    /** Returns whether this equipment has the same ID as {@code other}. */
    public boolean isSameEquipment(Equipment other) {
        return other != null && id.isSameId(other.id);
    }

    /** Returns an issued copy, leaving this record unchanged. */
    public Equipment issueTo(BorrowerName borrower) {
        requireNonNull(borrower);
        if (isIssued()) {
            throw new IllegalStateException("Equipment " + id + " is already issued to " + issuedTo + ".");
        }
        return new Equipment(id, name, EquipmentStatus.ISSUED, borrower);
    }

    @Override
    public String toString() {
        String displayStatus = isIssued() ? "Issued to: " + issuedTo : "Available";
        return id + " | " + name + " | " + displayStatus;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Equipment otherEquipment)) {
            return false;
        }
        return id.equals(otherEquipment.id) && name.equals(otherEquipment.name) && status == otherEquipment.status
                && Objects.equals(issuedTo, otherEquipment.issuedTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, status, issuedTo);
    }
}
