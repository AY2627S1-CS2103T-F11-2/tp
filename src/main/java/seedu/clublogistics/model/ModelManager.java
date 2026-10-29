package seedu.clublogistics.model;

import static java.util.Objects.requireNonNull;
import static seedu.clublogistics.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.clublogistics.commons.core.GuiSettings;
import seedu.clublogistics.commons.core.LogsCenter;
import seedu.clublogistics.model.equipment.Equipment;
import seedu.clublogistics.model.equipment.EquipmentId;

/**
 * Represents the in-memory model of the ClubLogistics data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final ClubLogisticsData clubLogisticsData;
    private final UserPrefs userPrefs;
    private final FilteredList<Equipment> filteredEquipment;
    private final ReadOnlyStringWrapper filterDescription = new ReadOnlyStringWrapper("Showing all equipment");

    /**
     * Initializes a ModelManager with the given clubLogisticsData and userPrefs.
     */
    public ModelManager(ReadOnlyClubLogisticsData clubLogisticsData, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(clubLogisticsData, userPrefs);

        logger.fine("Initializing equipment model and preferences.");

        this.clubLogisticsData = new ClubLogisticsData(clubLogisticsData);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredEquipment = new FilteredList<>(this.clubLogisticsData.getEquipmentList());
    }

    /** Creates a model with an empty register and default preferences. */
    public ModelManager() {
        this(new ClubLogisticsData(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    /** {@inheritDoc} */
    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    /** {@inheritDoc} */
    @Override
    public ObservableList<Equipment> getEquipmentList() {
        return clubLogisticsData.getEquipmentList();
    }

    /** {@inheritDoc} */
    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public boolean hasEquipment(Equipment equipment) {
        requireNonNull(equipment);
        return clubLogisticsData.hasEquipment(equipment);
    }

    /** Adds equipment while preserving the currently selected equipment filter. */
    @Override
    public void addEquipment(Equipment equipment) {
        clubLogisticsData.addEquipment(equipment);
    }

    /** {@inheritDoc} */
    @Override
    public ObservableList<Equipment> getFilteredEquipmentList() {
        return filteredEquipment;
    }

    /** {@inheritDoc} */
    @Override
    public void updateFilteredEquipmentList(Predicate<Equipment> predicate) {
        requireNonNull(predicate);
        updateFilteredEquipmentList(predicate, predicate == PREDICATE_SHOW_ALL_EQUIPMENT
                ? "Showing all equipment" : "Showing a filtered selection — use list to show all");
    }

    /** Applies a view and its description without changing equipment data. */
    @Override
    public void updateFilteredEquipmentList(Predicate<Equipment> predicate, String description) {
        requireAllNonNull(predicate, description);
        filteredEquipment.setPredicate(predicate);
        filterDescription.set(description);
    }

    /** Returns the observable description of the selected view. */
    @Override
    public ReadOnlyStringProperty equipmentFilterDescriptionProperty() {
        return filterDescription.getReadOnlyProperty();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Equipment> findEquipment(EquipmentId id) {
        return clubLogisticsData.findEquipment(id);
    }

    /** {@inheritDoc} */
    @Override
    public void setEquipment(Equipment target, Equipment replacement) {
        clubLogisticsData.setEquipment(target, replacement);
    }

    /** {@inheritDoc} */
    @Override
    public void removeEquipment(Equipment equipment) {
        requireNonNull(equipment);
        clubLogisticsData.removeEquipment(equipment);
    }

    /** {@inheritDoc} */
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
                && filteredEquipment.equals(otherModelManager.filteredEquipment);
    }

}
