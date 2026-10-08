package seedu.address.model.equipment;

/** Status of an equipment item. */
public enum EquipmentStatus {
    AVAILABLE("Available"),
    DAMAGED("Damaged");

    private final String displayName;

    EquipmentStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
