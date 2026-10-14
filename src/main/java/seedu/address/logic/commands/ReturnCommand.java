package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.equipment.EquipmentId;

/**
 * Records that an issued equipment item has been returned.
 */
public class ReturnCommand extends Command {

    public static final String COMMAND_WORD = "return";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " <equipment-id>";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT =
            "Error: Invalid command format. Usage: " + MESSAGE_USAGE;
    public static final String MESSAGE_NOT_IMPLEMENTED = "Error: The return command is not yet supported.";

    private final EquipmentId equipmentId;

    /**
     * Creates a command to return the equipment with {@code equipmentId}.
     */
    public ReturnCommand(EquipmentId equipmentId) {
        this.equipmentId = requireNonNull(equipmentId);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        // TODO: mark the equipment as available and clear its borrower once issuing is on master.
        throw new CommandException(MESSAGE_NOT_IMPLEMENTED);
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
