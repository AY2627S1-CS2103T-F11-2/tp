package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;

/**
 * Wraps all data at the address-book level.
 * Duplicate persons and case-insensitive equipment IDs are not allowed.
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons = new UniquePersonList();
    private final ObservableList<Equipment> equipment = FXCollections.observableArrayList();
    private final ObservableList<Equipment> unmodifiableEquipment = FXCollections.unmodifiableObservableList(equipment);

    public AddressBook() {}

    /**
     * Creates an AddressBook using the persons and equipment in {@code toBeCopied}.
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
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

    /**
     * Replaces the equipment list. Duplicate IDs are rejected before changing the existing data.
     */
    public void setEquipments(List<Equipment> equipment) {
        this.equipment.setAll(validatedEquipmentCopy(equipment));
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        UniquePersonList replacementPersons = new UniquePersonList();
        replacementPersons.setPersons(newData.getPersonList());
        List<Equipment> replacementEquipment = validatedEquipmentCopy(newData.getEquipmentList());
        persons.setPersons(replacementPersons);
        equipment.setAll(replacementEquipment);
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
    }

    //// equipment-level operations

    /**
     * Returns the equipment with the given case-insensitive ID, if present.
     */
    public Optional<Equipment> findEquipment(EquipmentId id) {
        requireNonNull(id);
        return equipment.stream().filter(item -> item.getId().equals(id)).findFirst();
    }

    /**
     * Returns whether an equipment record with the given ID exists.
     */
    public boolean hasEquipment(EquipmentId id) {
        return findEquipment(id).isPresent();
    }

    /**
     * Adds equipment. The ID must not already exist in the address book.
     */
    public void addEquipment(Equipment toAdd) {
        requireNonNull(toAdd);
        checkArgument(!hasEquipment(toAdd.getId()), "Equipment ID already exists: " + toAdd.getId());
        equipment.add(toAdd);
    }

    /**
     * Replaces an existing record. The replacement ID must not conflict with another item.
     */
    public void setEquipment(Equipment target, Equipment replacement) {
        requireAllNonNull(target, replacement);
        int index = equipment.indexOf(target);
        checkArgument(index != -1, "Equipment does not exist: " + target.getId());
        checkArgument(target.getId().equals(replacement.getId()) || !hasEquipment(replacement.getId()),
                "Equipment ID already exists: " + replacement.getId());
        equipment.set(index, replacement);
    }

    private static List<Equipment> validatedEquipmentCopy(List<Equipment> equipment) {
        requireAllNonNull(equipment);
        Set<EquipmentId> ids = new HashSet<>();
        for (Equipment item : equipment) {
            checkArgument(ids.add(item.getId()), "Equipment ID already exists: " + item.getId());
        }
        return List.copyOf(equipment);
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
        return unmodifiableEquipment;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return persons.equals(otherAddressBook.persons) && equipment.equals(otherAddressBook.equipment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, equipment);
    }
}
