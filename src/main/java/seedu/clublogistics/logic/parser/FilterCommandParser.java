package seedu.clublogistics.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.Locale;

import seedu.clublogistics.logic.commands.FilterCommand;
import seedu.clublogistics.logic.parser.exceptions.ParseException;
import seedu.clublogistics.model.equipment.EquipmentStatus;

/**
 * Parses an equipment status into a FilterCommand.
 */
public class FilterCommandParser implements Parser<FilterCommand> {

    /**
     * Parses {@code args} as a single, case-insensitive equipment status.
     *
     * @throws ParseException if the argument is missing, has extra tokens, or is not a supported status.
     */
    @Override
    public FilterCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty() || trimmedArgs.split("\\s+").length != 1) {
            throw new ParseException(FilterCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        }
        return new FilterCommand(parseStatus(trimmedArgs));
    }

    private EquipmentStatus parseStatus(String status) throws ParseException {
        try {
            return EquipmentStatus.valueOf(status.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ParseException(FilterCommand.MESSAGE_INVALID_COMMAND_FORMAT, e);
        }
    }
}
