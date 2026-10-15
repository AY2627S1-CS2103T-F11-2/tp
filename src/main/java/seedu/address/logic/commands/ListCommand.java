package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_EQUIPMENT;

import seedu.address.model.Model;

/**
 * Lists all equipment in the equipment register to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_USAGE = COMMAND_WORD;
    public static final String MESSAGE_INVALID_COMMAND_FORMAT =
            "Error: Invalid command format. Usage: " + MESSAGE_USAGE;
    public static final String MESSAGE_SUCCESS = "Listed all equipment.";
    public static final String MESSAGE_EMPTY_REGISTER = "No equipment found.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredEquipmentList(PREDICATE_SHOW_ALL_EQUIPMENT);
        if (model.getFilteredEquipmentList().isEmpty()) {
            return new CommandResult(MESSAGE_EMPTY_REGISTER);
        }
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
