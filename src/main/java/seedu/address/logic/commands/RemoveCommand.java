package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;

/**
 * Removes an equipment item from the equipment register.
 */
public class RemoveCommand extends Command {

    public static final String COMMAND_WORD = "remove";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " <equipment-id>";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT =
            "Error: Invalid command format. Usage: " + MESSAGE_USAGE;
    public static final String MESSAGE_SUCCESS = "Equipment %1$s removed successfully.";
    public static final String MESSAGE_EQUIPMENT_NOT_FOUND = "Error: Equipment with ID %1$s does not exist.";

    private final EquipmentId equipmentId;

    /**
     * Creates a command to remove the equipment with {@code equipmentId}.
     */
    public RemoveCommand(EquipmentId equipmentId) {
        this.equipmentId = requireNonNull(equipmentId);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Equipment equipment = model.findEquipment(equipmentId)
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_EQUIPMENT_NOT_FOUND, equipmentId)));

        // TODO: reject removal of issued equipment once the borrower field from the issue command is on master.
        model.removeEquipment(equipment);
        return new CommandResult(String.format(MESSAGE_SUCCESS, equipment.getId()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof RemoveCommand otherCommand)) {
            return false;
        }
        return equipmentId.equals(otherCommand.equipmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentId);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("equipmentId", equipmentId)
                .toString();
    }
}
