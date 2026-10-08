package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;

/**
 * Marks an equipment item as damaged so that it cannot be issued.
 */
public class DamagedCommand extends Command {

    public static final String COMMAND_WORD = "damaged";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " <equipment-id>";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT =
            "Error: Invalid command format. Usage: " + MESSAGE_USAGE;
    public static final String MESSAGE_SUCCESS = "Equipment %1$s marked as damaged.";
    public static final String MESSAGE_EQUIPMENT_NOT_FOUND = "Error: Equipment with ID %1$s does not exist.";
    public static final String MESSAGE_ALREADY_DAMAGED = "Error: Equipment %1$s is already marked as damaged.";

    private final EquipmentId equipmentId;

    /**
     * Creates a command to mark the equipment with {@code equipmentId} as damaged.
     */
    public DamagedCommand(EquipmentId equipmentId) {
        this.equipmentId = requireNonNull(equipmentId);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Equipment equipment = model.findEquipment(equipmentId)
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_EQUIPMENT_NOT_FOUND, equipmentId)));

        if (equipment.isDamaged()) {
            throw new CommandException(String.format(MESSAGE_ALREADY_DAMAGED, equipment.getId()));
        }

        Equipment damagedEquipment = equipment.markDamaged();
        model.setEquipment(equipment, damagedEquipment);
        return new CommandResult(String.format(MESSAGE_SUCCESS, damagedEquipment.getId()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DamagedCommand otherCommand)) {
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
