package seedu.clublogistics.model;

import java.util.Optional;
import java.util.function.Predicate;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;
import seedu.clublogistics.commons.core.GuiSettings;
import seedu.clublogistics.model.equipment.Equipment;
import seedu.clublogistics.model.equipment.EquipmentId;

/**
 * The API of the Model component.
 */
public interface Model {
    /** Predicate that shows every equipment item. */
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

    /** Returns true if equipment with the same ID exists in the equipment register. */
    boolean hasEquipment(Equipment equipment);

    /** Adds the given equipment to the equipment register. */
    void addEquipment(Equipment equipment);

    /** Returns an unmodifiable observable view of all registered equipment. */
    ObservableList<Equipment> getEquipmentList();

    /** Returns the current view description for persistent UI feedback. */
    ReadOnlyStringProperty equipmentFilterDescriptionProperty();

    /** Returns the equipment currently shown to the user. */
    default ObservableList<Equipment> getFilteredEquipmentList() {
        return getClubLogisticsData().getEquipmentList();
    }

    /** Updates which equipment is shown to the user. */
    default void updateFilteredEquipmentList(Predicate<Equipment> predicate) {
        throw new UnsupportedOperationException("Equipment filtering is not supported by this model");
    }

    /** Updates the view together with its human-readable description. */
    void updateFilteredEquipmentList(Predicate<Equipment> predicate, String description);

    /** Returns the equipment with the given case-insensitive ID, if present. */
    Optional<Equipment> findEquipment(EquipmentId id);

    /** Replaces an existing equipment record. */
    void setEquipment(Equipment target, Equipment replacement);

    /** Removes the given equipment from the equipment register. {@code equipment} must exist. */
    void removeEquipment(Equipment equipment);

}
