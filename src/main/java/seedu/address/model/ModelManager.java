package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.model.person.Person;

/**
 * Represents the in-memory model of the ClubLogistics data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final ClubLogisticsData clubLogisticsData;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final FilteredList<Equipment> filteredEquipment;

    /**
     * Initializes a ModelManager with the given clubLogisticsData and userPrefs.
     */
    public ModelManager(ReadOnlyClubLogisticsData clubLogisticsData, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(clubLogisticsData, userPrefs);

        logger.fine("Initializing with ClubLogistics data: " + clubLogisticsData + " and user prefs " + userPrefs);

        this.clubLogisticsData = new ClubLogisticsData(clubLogisticsData);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.clubLogisticsData.getPersonList());
        filteredEquipment = new FilteredList<>(this.clubLogisticsData.getEquipmentList());
    }

    public ModelManager() {
        this(new ClubLogisticsData(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public ObservableList<Equipment> getEquipmentList() {
        return clubLogisticsData.getEquipmentList();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== ClubLogisticsData ================================================================================

    /** {@inheritDoc} */
    @Override
    public void setClubLogisticsData(ReadOnlyClubLogisticsData clubLogisticsData) {
        this.clubLogisticsData.resetData(clubLogisticsData);
    }

    /** {@inheritDoc} */
    @Override
    public ReadOnlyClubLogisticsData getClubLogisticsData() {
        return clubLogisticsData;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return clubLogisticsData.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        clubLogisticsData.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        clubLogisticsData.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public boolean hasEquipment(Equipment equipment) {
        requireNonNull(equipment);
        return clubLogisticsData.hasEquipment(equipment);
    }

    @Override
    public void addEquipment(Equipment equipment) {
        clubLogisticsData.addEquipment(equipment);
        updateFilteredEquipmentList(PREDICATE_SHOW_ALL_EQUIPMENT);
    }

    @Override
    public ObservableList<Equipment> getFilteredEquipmentList() {
        return filteredEquipment;
    }

    @Override
    public void updateFilteredEquipmentList(Predicate<Equipment> predicate) {
        requireNonNull(predicate);
        filteredEquipment.setPredicate(predicate);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        clubLogisticsData.setPerson(target, editedPerson);
    }

    @Override
    public Optional<Equipment> findEquipment(EquipmentId id) {
        return clubLogisticsData.findEquipment(id);
    }

    @Override
    public void setEquipment(Equipment target, Equipment replacement) {
        clubLogisticsData.setEquipment(target, replacement);
    }

    @Override
    public void removeEquipment(Equipment equipment) {
        requireNonNull(equipment);
        clubLogisticsData.removeEquipment(equipment);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code clubLogisticsData}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return clubLogisticsData.equals(otherModelManager.clubLogisticsData)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons)
                && filteredEquipment.equals(otherModelManager.filteredEquipment);
    }

}
