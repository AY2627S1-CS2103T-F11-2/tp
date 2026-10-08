package seedu.address.logic.parser;

import seedu.address.logic.commands.AddEquipmentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.model.equipment.EquipmentName;

/** Parses arguments for the equipment form of the add command. */
public class AddEquipmentCommandParser implements Parser<AddEquipmentCommand> {

    public static final String MESSAGE_USAGE =
            "Error: Invalid command format. Usage: add <equipment-id> <equipment-name>";

    @Override
    public AddEquipmentCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        String[] parts = trimmedArgs.split("\\s+", 2);
        if (parts.length != 2 || parts[1].trim().isEmpty()) {
            throw new ParseException(MESSAGE_USAGE);
        }
        if (!EquipmentId.isValidId(parts[0])) {
            throw new ParseException(EquipmentId.MESSAGE_CONSTRAINTS);
        }
        if (!EquipmentName.isValidName(parts[1])) {
            throw new ParseException(EquipmentName.MESSAGE_CONSTRAINTS);
        }
        return new AddEquipmentCommand(new Equipment(new EquipmentId(parts[0]), new EquipmentName(parts[1])));
    }
}
