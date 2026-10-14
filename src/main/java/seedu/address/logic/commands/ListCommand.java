package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_EQUIPMENT;

import seedu.address.model.Model;

/**
 * Lists all equipment in the equipment register to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS = "Listed all equipment.";


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredEquipmentList(PREDICATE_SHOW_ALL_EQUIPMENT);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
