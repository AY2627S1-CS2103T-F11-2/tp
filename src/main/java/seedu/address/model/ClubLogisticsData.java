package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.model.equipment.UniqueEquipmentList;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;

/**
 * Stores contacts and equipment for ClubLogistics.
 * Duplicate contacts and equipment IDs are not allowed.
 */
public class ClubLogisticsData implements ReadOnlyClubLogisticsData {

    private final UniquePersonList persons = new UniquePersonList();
    private final UniqueEquipmentList equipment = new UniqueEquipmentList();

    /** Creates an empty ClubLogistics data set. */
    public ClubLogisticsData() {}

    /**
     * Copies the contacts and equipment from the {@code toBeCopied}
     */
    public ClubLogisticsData(ReadOnlyClubLogisticsData toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    /** Replaces the equipment register after checking for duplicate IDs. */
    public void setEquipments(List<Equipment> equipment) {
        this.equipment.setEquipment(equipment);
    }

    /**
     * Resets the existing data of this {@code ClubLogisticsData} with {@code newData}.
     */
    public void resetData(ReadOnlyClubLogisticsData newData) {
        requireNonNull(newData);

        UniquePersonList replacementPersons = new UniquePersonList();
        replacementPersons.setPersons(newData.getPersonList());
        UniqueEquipmentList replacementEquipment = new UniqueEquipmentList();
        replacementEquipment.setEquipment(newData.getEquipmentList());

        setPersons(replacementPersons.asUnmodifiableObservableList());
        equipment.setEquipment(replacementEquipment.asUnmodifiableObservableList());
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in ClubLogistics data.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the ClubLogistics data.
     * The person must not already exist in ClubLogistics data.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in ClubLogistics data.
     * The person identity of {@code editedPerson} must not be the same as another existing person in
     * ClubLogistics data.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
    }

    /**
     * Removes {@code key} from this {@code ClubLogisticsData}.
     * {@code key} must exist in ClubLogistics data.
     */
    public void removePerson(Person key) {
        persons.remove(key);
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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .add("equipment", equipment)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Equipment> getEquipmentList() {
        return equipment.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ClubLogisticsData otherClubLogisticsData)) {
            return false;
        }

        return persons.equals(otherClubLogisticsData.persons) && equipment.equals(otherClubLogisticsData.equipment);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(persons, equipment);
    }
}
