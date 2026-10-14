package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;

/**
 * Records that an issued equipment item has been returned.
 */
public class ReturnCommand extends Command {

    public static final String COMMAND_WORD = "return";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " <equipment-id>";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT =
            "Error: Invalid command format. Usage: " + MESSAGE_USAGE;
    public static final String MESSAGE_SUCCESS = "Equipment %1$s returned successfully.\n%2$s";
    public static final String MESSAGE_EQUIPMENT_NOT_FOUND = "Error: Equipment with ID %1$s does not exist.";
    public static final String MESSAGE_NOT_ISSUED = "Error: Equipment %1$s is not currently issued.";

    private final EquipmentId equipmentId;

    /**
     * Creates a command to return the equipment with {@code equipmentId}.
     */
    public ReturnCommand(EquipmentId equipmentId) {
        this.equipmentId = requireNonNull(equipmentId);
    }

    /**
     * Marks the issued equipment as available and clears its borrower.
     *
     * @throws CommandException if the equipment does not exist or is not currently issued.
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Equipment equipment = findIssuedEquipment(model);
        Equipment returnedEquipment = equipment.markReturned();
        model.setEquipment(equipment, returnedEquipment);
        return new CommandResult(String.format(MESSAGE_SUCCESS, returnedEquipment.getId(), returnedEquipment));
    }

    /**
     * Returns the equipment matching {@code equipmentId}, which must exist and be currently issued.
     */
    private Equipment findIssuedEquipment(Model model) throws CommandException {
        Equipment equipment = model.findEquipment(equipmentId)
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_EQUIPMENT_NOT_FOUND, equipmentId)));
        if (!equipment.isIssued()) {
            throw new CommandException(String.format(MESSAGE_NOT_ISSUED, equipment.getId()));
        }
        return equipment;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ReturnCommand otherCommand)) {
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
