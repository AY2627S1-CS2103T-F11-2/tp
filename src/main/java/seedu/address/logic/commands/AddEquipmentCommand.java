package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.equipment.Equipment;

/** Adds an equipment item to the equipment register. */
public class AddEquipmentCommand extends Command {

    public static final String MESSAGE_SUCCESS = "Equipment %1$s added successfully.";
    public static final String MESSAGE_DUPLICATE_EQUIPMENT = "Error: Equipment with ID %1$s already exists.";

    private final Equipment equipmentToAdd;

    public AddEquipmentCommand(Equipment equipment) {
        equipmentToAdd = requireNonNull(equipment);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.hasEquipment(equipmentToAdd)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_EQUIPMENT, equipmentToAdd.getId()));
        }
        model.addEquipment(equipmentToAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, equipmentToAdd.getId()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof AddEquipmentCommand otherCommand
                && equipmentToAdd.equals(otherCommand.equipmentToAdd));
    }
}
