package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.DamagedCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.equipment.EquipmentId;

/**
 * Parses an equipment ID into a DamagedCommand.
 */
public class DamagedCommandParser implements Parser<DamagedCommand> {

    /**
     * Parses {@code args} as a single equipment ID.
     *
     * @throws ParseException if the command format or ID is invalid.
     */
    @Override
    public DamagedCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty() || trimmedArgs.split("\\s+").length != 1) {
            throw new ParseException(DamagedCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        }

        try {
            return new DamagedCommand(new EquipmentId(trimmedArgs));
        } catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
}
