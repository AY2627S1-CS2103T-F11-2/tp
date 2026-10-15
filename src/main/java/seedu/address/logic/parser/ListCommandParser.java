package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses the arguments of a list command, which takes none.
 */
public class ListCommandParser implements Parser<ListCommand> {

    /**
     * Parses {@code args} as the empty argument list of a ListCommand.
     *
     * @throws ParseException if any argument is supplied.
     */
    @Override
    public ListCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (!args.isBlank()) {
            throw new ParseException(ListCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        }
        return new ListCommand();
    }
}
