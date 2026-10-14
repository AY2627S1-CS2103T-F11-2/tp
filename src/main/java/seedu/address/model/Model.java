package seedu.address.model;

import java.util.Optional;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.model.person.Person;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;
    Predicate<Equipment> PREDICATE_SHOW_ALL_EQUIPMENT = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces ClubLogistics data with the data in {@code clubLogisticsData}.
     */
    void setClubLogisticsData(ReadOnlyClubLogisticsData clubLogisticsData);

    /** Returns the ClubLogisticsData */
    ReadOnlyClubLogisticsData getClubLogisticsData();

    /**
     * Returns true if a person with the same identity as {@code person} exists in ClubLogistics data.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in ClubLogistics data.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in ClubLogistics data.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in ClubLogistics data.
     * The person identity of {@code editedPerson} must not be the same as another existing person in
     * ClubLogistics data.
     */
    void setPerson(Person target, Person editedPerson);

    /** Returns true if equipment with the same ID exists in the equipment register. */
    boolean hasEquipment(Equipment equipment);

    /** Adds the given equipment to the equipment register. */
    void addEquipment(Equipment equipment);

    /** Returns an unmodifiable observable view of all registered equipment. */
    ObservableList<Equipment> getEquipmentList();

    /** Returns the equipment currently shown to the user. */
    default ObservableList<Equipment> getFilteredEquipmentList() {
        return getClubLogisticsData().getEquipmentList();
    }

    /** Updates which equipment is shown to the user. */
    default void updateFilteredEquipmentList(Predicate<Equipment> predicate) {
        throw new UnsupportedOperationException("Equipment filtering is not supported by this model");
    }

    /** Returns the equipment with the given case-insensitive ID, if present. */
    Optional<Equipment> findEquipment(EquipmentId id);

    /** Replaces an existing equipment record. */
    void setEquipment(Equipment target, Equipment replacement);

    /** Removes the given equipment from the equipment register. {@code equipment} must exist. */
    void removeEquipment(Equipment equipment);

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);
}
