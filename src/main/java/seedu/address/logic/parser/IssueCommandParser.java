package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.IssueCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.EquipmentId;

/**
 * Parses an equipment ID and the complete borrower name into an IssueCommand.
 */
public class IssueCommandParser implements Parser<IssueCommand> {

    /**
     * Parses {@code args}, preserving all words after the ID as the borrower name.
     *
     * @throws ParseException if the command format, ID, or borrower name is invalid.
     */
    @Override
    public IssueCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String[] arguments = args.trim().split("\\s+", 2);
        if (arguments.length != 2) {
            throw new ParseException(IssueCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        }

        try {
            EquipmentId equipmentId = new EquipmentId(arguments[0]);
            BorrowerName borrower = new BorrowerName(arguments[1]);
            return new IssueCommand(equipmentId, borrower);
        } catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
}
