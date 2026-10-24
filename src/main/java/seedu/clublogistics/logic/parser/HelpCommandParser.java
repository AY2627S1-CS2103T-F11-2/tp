package seedu.clublogistics.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.clublogistics.logic.commands.HelpCommand;
import seedu.clublogistics.logic.parser.exceptions.ParseException;

/**
 * Parses the arguments of a help command, which takes none.
 */
public class HelpCommandParser implements Parser<HelpCommand> {

    /**
     * Parses {@code args} as the empty argument list of a HelpCommand.
     *
     * @throws ParseException if any argument is supplied.
     */
    @Override
    public HelpCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (!args.isBlank()) {
            throw new ParseException(HelpCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        }
        return new HelpCommand();
    }
}
