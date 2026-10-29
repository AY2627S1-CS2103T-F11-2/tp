package seedu.clublogistics.logic;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;
import seedu.clublogistics.commons.core.GuiSettings;
import seedu.clublogistics.logic.commands.CommandResult;
import seedu.clublogistics.logic.commands.exceptions.CommandException;
import seedu.clublogistics.logic.parser.exceptions.ParseException;
import seedu.clublogistics.model.equipment.Equipment;

/**
 * API of the Logic component
 */
public interface Logic {
    /**
     * Executes the command and returns the result.
     * @param commandText The command as entered by the user.
     * @return the result of the command execution.
     * @throws CommandException If an error occurs during command execution.
     * @throws ParseException If an error occurs during parsing.
     */
    CommandResult execute(String commandText) throws CommandException, ParseException;

    /** Returns an unmodifiable observable view of all registered equipment. */
    ObservableList<Equipment> getEquipmentList();

    /** Returns the current view description for persistent UI feedback. */
    ReadOnlyStringProperty equipmentFilterDescriptionProperty();

    /** Returns the equipment currently shown to the user. */
    ObservableList<Equipment> getFilteredEquipmentList();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Set the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);
}
