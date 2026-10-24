package seedu.clublogistics.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.clublogistics.logic.commands.ClearCommand;
import seedu.clublogistics.logic.parser.exceptions.ParseException;

/**
 * Parses the arguments of a clear command, which takes none.
 */
public class ClearCommandParser implements Parser<ClearCommand> {

    /**
     * Parses {@code args} as the empty argument list of a ClearCommand.
     *
     * @throws ParseException if any argument is supplied.
     */
    @Override
    public ClearCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (!args.isBlank()) {
            throw new ParseException(ClearCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        }
        return new ClearCommand();
    }
}
