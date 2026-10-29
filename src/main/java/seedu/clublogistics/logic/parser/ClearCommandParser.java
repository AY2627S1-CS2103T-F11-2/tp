package seedu.clublogistics.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.clublogistics.logic.commands.ClearCommand;
import seedu.clublogistics.logic.parser.exceptions.ParseException;

/**
 * Parses the arguments of a clear command, which takes either nothing or the force keyword.
 */
public class ClearCommandParser implements Parser<ClearCommand> {

    /**
     * Parses {@code args} as an unforced clear when blank, or a forced clear when it is the force keyword.
     *
     * @throws ParseException if any other argument is supplied.
     */
    @Override
    public ClearCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            return new ClearCommand(false);
        }
        if (trimmedArgs.equalsIgnoreCase(ClearCommand.FORCE_KEYWORD)) {
            return new ClearCommand(true);
        }
        throw new ParseException(ClearCommand.MESSAGE_INVALID_COMMAND_FORMAT);
    }
}
