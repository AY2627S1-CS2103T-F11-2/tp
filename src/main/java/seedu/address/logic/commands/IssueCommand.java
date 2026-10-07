package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;

/**
 * Issues an available equipment item to a borrower.
 */
public class IssueCommand extends Command {

    public static final String COMMAND_WORD = "issue";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " <equipment-id> <person-name>";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT =
            "Error: Invalid command format. Usage: " + MESSAGE_USAGE;
    public static final String MESSAGE_SUCCESS = "Equipment %1$s issued to %2$s.\n%3$s";
    public static final String MESSAGE_EQUIPMENT_NOT_FOUND = "Error: Equipment with ID %1$s does not exist.";
    public static final String MESSAGE_ALREADY_ISSUED = "Error: Equipment %1$s is already issued to %2$s.";

    private final EquipmentId equipmentId;
    private final BorrowerName borrower;

    /**
     * Creates a command to issue the equipment with {@code equipmentId} to {@code borrower}.
     */
    public IssueCommand(EquipmentId equipmentId, BorrowerName borrower) {
        requireAllNonNull(equipmentId, borrower);
        this.equipmentId = equipmentId;
        this.borrower = borrower;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Equipment equipment = model.findEquipment(equipmentId)
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_EQUIPMENT_NOT_FOUND, equipmentId)));

        if (equipment.isIssued()) {
            throw new CommandException(String.format(MESSAGE_ALREADY_ISSUED,
                    equipment.getId(), equipment.getIssuedTo().orElseThrow()));
        }

        Equipment issuedEquipment = equipment.issueTo(borrower);
        model.setEquipment(equipment, issuedEquipment);
        return new CommandResult(String.format(MESSAGE_SUCCESS, issuedEquipment.getId(), borrower, issuedEquipment));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof IssueCommand otherCommand)) {
            return false;
        }
        return equipmentId.equals(otherCommand.equipmentId) && borrower.equals(otherCommand.borrower);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipmentId, borrower);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("equipmentId", equipmentId)
                .add("borrower", borrower)
                .toString();
    }
}
