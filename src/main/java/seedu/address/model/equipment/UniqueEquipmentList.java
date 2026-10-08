package seedu.address.model.equipment;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/** A list of equipment which enforces case-insensitive ID uniqueness. */
public class UniqueEquipmentList {

    private final ObservableList<Equipment> internalList = FXCollections.observableArrayList();
    private final ObservableList<Equipment> unmodifiableList = FXCollections.unmodifiableObservableList(internalList);

    /** Returns whether the register contains an item with the same ID. */
    public boolean contains(Equipment equipment) {
        requireNonNull(equipment);
        return internalList.stream().anyMatch(equipment::isSameEquipment);
    }

    /** Adds an item to the register. */
    public void add(Equipment equipment) {
        requireNonNull(equipment);
        if (contains(equipment)) {
            throw new IllegalArgumentException("Duplicate equipment ID");
        }
        internalList.add(equipment);
    }

    /** Returns the item with the same ID as {@code id}, ignoring case, if present. */
    public Optional<Equipment> find(EquipmentId id) {
        requireNonNull(id);
        return internalList.stream().filter(item -> item.getId().isSameId(id)).findFirst();
    }

    /**
     * Removes {@code toRemove} from the register.
     * {@code toRemove} must exist in the register.
     */
    public void remove(Equipment toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new IllegalArgumentException("Equipment does not exist: " + toRemove.getId());
        }
    }

    /** Replaces an item while preserving case-insensitive ID uniqueness. */
    public void set(Equipment target, Equipment replacement) {
        requireNonNull(target);
        requireNonNull(replacement);
        int index = internalList.indexOf(target);
        if (index < 0) {
            throw new IllegalArgumentException("Equipment does not exist: " + target.getId());
        }
        for (int i = 0; i < internalList.size(); i++) {
            if (i != index && internalList.get(i).isSameEquipment(replacement)) {
                throw new IllegalArgumentException("Duplicate equipment ID: " + replacement.getId());
            }
        }
        internalList.set(index, replacement);
    }

    /** Replaces the register contents after checking IDs for uniqueness. */
    public void setEquipment(java.util.List<Equipment> equipment) {
        requireNonNull(equipment);
        UniqueEquipmentList replacement = new UniqueEquipmentList();
        equipment.forEach(replacement::add);
        internalList.setAll(replacement.internalList);
    }

    public ObservableList<Equipment> asUnmodifiableObservableList() {
        return unmodifiableList;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof UniqueEquipmentList otherList
                && internalList.equals(otherList.internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }
}
