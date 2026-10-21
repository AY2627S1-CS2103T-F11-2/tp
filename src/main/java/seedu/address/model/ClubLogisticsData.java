package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.model.equipment.UniqueEquipmentList;

/**
 * Stores the equipment register for ClubLogistics.
 * Duplicate equipment IDs are not allowed.
 */
public class ClubLogisticsData implements ReadOnlyClubLogisticsData {

    private final UniqueEquipmentList equipment = new UniqueEquipmentList();

    /** Creates an empty ClubLogistics data set. */
    public ClubLogisticsData() {}

    /**
     * Copies the equipment from the {@code toBeCopied}
     */
    public ClubLogisticsData(ReadOnlyClubLogisticsData toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /** Replaces the equipment register after checking for duplicate IDs. */
    public void setEquipments(List<Equipment> equipment) {
        this.equipment.setEquipment(equipment);
    }

    /**
     * Resets the existing data of this {@code ClubLogisticsData} with {@code newData}.
     */
    public void resetData(ReadOnlyClubLogisticsData newData) {
        requireNonNull(newData);

        setEquipments(newData.getEquipmentList());
    }

    //// equipment-level operations

    /** Returns true if an equipment item with the same ID exists in the register. */
    public boolean hasEquipment(Equipment item) {
        requireNonNull(item);
        return equipment.contains(item);
    }

    /** Returns whether an equipment ID is present, ignoring letter case. */
    public boolean hasEquipment(EquipmentId id) {
        requireNonNull(id);
        return equipment.find(id).isPresent();
    }

    /** Adds an equipment item to the register. */
    public void addEquipment(Equipment item) {
        equipment.add(item);
    }

    /** Replaces an existing equipment record. */
    public void setEquipment(Equipment target, Equipment replacement) {
        equipment.set(target, replacement);
    }

    /** Returns the equipment item with the given case-insensitive ID, if present. */
    public Optional<Equipment> findEquipment(EquipmentId id) {
        return equipment.find(id);
    }

    /**
     * Removes the equipment item {@code key} from the register.
     * {@code key} must exist in the register.
     */
    public void removeEquipment(Equipment key) {
        equipment.remove(key);
    }

    //// util methods

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("equipment", equipment)
                .toString();
    }

    /** {@inheritDoc} */
    @Override
    public ObservableList<Equipment> getEquipmentList() {
        return equipment.asUnmodifiableObservableList();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ClubLogisticsData otherClubLogisticsData)) {
            return false;
        }

        return equipment.equals(otherClubLogisticsData.equipment);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return equipment.hashCode();
    }
}
