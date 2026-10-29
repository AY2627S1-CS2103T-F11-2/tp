package seedu.clublogistics.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.clublogistics.logic.commands.FindCommand;
import seedu.clublogistics.logic.parser.exceptions.ParseException;
import seedu.clublogistics.model.equipment.BorrowerName;

/** Parses a full borrower name or a name fragment. */
public class FindCommandParser implements Parser<FindCommand> {
    /** Rejects missing or invalid names before changing the current view. */
    @Override
    public FindCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (args.isBlank()) {
            throw new ParseException("Error: Invalid command format. Usage: " + FindCommand.MESSAGE_USAGE);
        }
        try {
            return new FindCommand(new BorrowerName(args));
        } catch (IllegalArgumentException e) {
            throw new ParseException(BorrowerName.MESSAGE_CONSTRAINTS, e);
        }
    }
}
