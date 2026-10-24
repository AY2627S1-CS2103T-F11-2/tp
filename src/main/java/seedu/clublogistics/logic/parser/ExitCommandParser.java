package seedu.clublogistics.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.clublogistics.logic.commands.ExitCommand;
import seedu.clublogistics.logic.parser.exceptions.ParseException;

/**
 * Parses the arguments of an exit command, which takes none.
 */
public class ExitCommandParser implements Parser<ExitCommand> {

    /**
     * Parses {@code args} as the empty argument list of an ExitCommand.
     *
     * @throws ParseException if any argument is supplied.
     */
    @Override
    public ExitCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (!args.isBlank()) {
            throw new ParseException(ExitCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        }
        return new ExitCommand();
    }
}
