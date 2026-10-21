package seedu.clublogistics.model;

import javafx.collections.ObservableList;
import seedu.clublogistics.model.equipment.Equipment;

/** Unmodifiable view of ClubLogistics equipment data. */
public interface ReadOnlyClubLogisticsData {
    /** Returns an unmodifiable view of the equipment register without duplicate IDs. */
    ObservableList<Equipment> getEquipmentList();
}
