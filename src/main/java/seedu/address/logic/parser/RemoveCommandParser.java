package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.RemoveCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.equipment.EquipmentId;

/**
 * Parses an equipment ID into a RemoveCommand.
 */
public class RemoveCommandParser implements Parser<RemoveCommand> {

    /**
     * Parses {@code args} as a single equipment ID.
     *
     * @throws ParseException if the command format or ID is invalid.
     */
    @Override
    public RemoveCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty() || trimmedArgs.split("\\s+").length != 1) {
            throw new ParseException(RemoveCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        }

        try {
            return new RemoveCommand(new EquipmentId(trimmedArgs));
        } catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
}
