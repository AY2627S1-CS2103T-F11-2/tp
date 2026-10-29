package seedu.clublogistics.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.clublogistics.model.Model.PREDICATE_SHOW_ALL_EQUIPMENT;

import seedu.clublogistics.logic.Messages;
import seedu.clublogistics.model.Model;

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

    /** Restores the full view and reports the current register size. */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredEquipmentList(PREDICATE_SHOW_ALL_EQUIPMENT);
        int total = model.getEquipmentList().size();
        String message = total == 0 ? MESSAGE_EMPTY_REGISTER : MESSAGE_SUCCESS;
        return new CommandResult(message + "\n" + Messages.formatEquipmentCount(total, total));
    }
}
