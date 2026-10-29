package seedu.clublogistics.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.clublogistics.logic.commands.exceptions.CommandException;
import seedu.clublogistics.model.Model;
import seedu.clublogistics.model.equipment.Equipment;
import seedu.clublogistics.model.equipment.EquipmentId;

/** Adds an equipment item to the equipment register. */
public class AddEquipmentCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_SUCCESS = "Equipment %1$s added successfully.";
    public static final String MESSAGE_DUPLICATE_EQUIPMENT = "Error: Equipment with ID %1$s already exists.";

    private final Equipment equipmentToAdd;

    /** Creates a command to register the given equipment item. */
    public AddEquipmentCommand(Equipment equipment) {
        equipmentToAdd = requireNonNull(equipment);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.hasEquipment(equipmentToAdd)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_EQUIPMENT, findStoredId(model)));
        }
        model.addEquipment(equipmentToAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, equipmentToAdd.getId()));
    }

    /** Returns the existing item's ID as stored, which may differ in case from the ID that was typed. */
    private EquipmentId findStoredId(Model model) {
        return model.findEquipment(equipmentToAdd.getId())
                .map(Equipment::getId)
                .orElse(equipmentToAdd.getId());
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof AddEquipmentCommand otherCommand
                && equipmentToAdd.equals(otherCommand.equipmentToAdd));
    }
}
