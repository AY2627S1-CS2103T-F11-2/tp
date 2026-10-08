package seedu.address.model.equipment;

import static java.util.Objects.requireNonNull;

/**
 * Represents a registered equipment item.
 */
public class Equipment {

    private final EquipmentId id;
    private final EquipmentName name;
    private final EquipmentStatus status;

    /** Creates an available equipment item. */
    public Equipment(EquipmentId id, EquipmentName name) {
        this(id, name, EquipmentStatus.AVAILABLE);
    }

    /** Creates an equipment item with its stored status. */
    public Equipment(EquipmentId id, EquipmentName name, EquipmentStatus status) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        this.status = requireNonNull(status);
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

    /** Returns whether this equipment is marked as damaged. */
    public boolean isDamaged() {
        return status == EquipmentStatus.DAMAGED;
    }

    /**
     * Returns a damaged copy of this equipment, leaving this record unchanged.
     *
     * @throws IllegalStateException if the equipment is already damaged.
     */
    public Equipment markDamaged() {
        if (isDamaged()) {
            throw new IllegalStateException("Equipment " + id + " is already damaged.");
        }
        return new Equipment(id, name, EquipmentStatus.DAMAGED);
    }

    /** Returns whether this equipment has the same ID as {@code other}. */
    public boolean isSameEquipment(Equipment other) {
        return other != null && id.isSameId(other.id);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Equipment otherEquipment)) {
            return false;
        }
        return id.equals(otherEquipment.id) && name.equals(otherEquipment.name) && status == otherEquipment.status;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, name, status);
    }
}
