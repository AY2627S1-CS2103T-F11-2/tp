package seedu.clublogistics.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.clublogistics.logic.commands.RenameCommand;
import seedu.clublogistics.logic.parser.exceptions.ParseException;
import seedu.clublogistics.model.equipment.EquipmentId;
import seedu.clublogistics.model.equipment.EquipmentName;

/** Parses a full equipment name following the target ID. */
public class RenameCommandParser implements Parser<RenameCommand> {
    /** Applies the same ID and name validation as equipment registration. */
    @Override
    public RenameCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String[] parts = args.trim().split("\\s+", 2);
        if (parts.length != 2 || parts[1].trim().isEmpty()) {
            throw new ParseException(RenameCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        }
        return createCommand(parts[0], parts[1]);
    }

    private RenameCommand createCommand(String id, String name) throws ParseException {
        try {
            return new RenameCommand(new EquipmentId(id), new EquipmentName(name));
        } catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
}
