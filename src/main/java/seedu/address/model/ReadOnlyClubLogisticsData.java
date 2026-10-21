package seedu.address.model;

import javafx.collections.ObservableList;
import seedu.address.model.equipment.Equipment;

/** Unmodifiable view of ClubLogistics equipment data. */
public interface ReadOnlyClubLogisticsData {
    /** Returns an unmodifiable view of the equipment register without duplicate IDs. */
    ObservableList<Equipment> getEquipmentList();
}
