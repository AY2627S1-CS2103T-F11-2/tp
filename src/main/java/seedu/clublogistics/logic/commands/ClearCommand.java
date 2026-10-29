package seedu.clublogistics.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;

import seedu.clublogistics.commons.util.ToStringBuilder;
import seedu.clublogistics.logic.commands.exceptions.CommandException;
import seedu.clublogistics.model.ClubLogisticsData;
import seedu.clublogistics.model.Model;
import seedu.clublogistics.model.equipment.Equipment;

/**
 * Clears the ClubLogistics data. Issued equipment is only discarded when the clear is forced.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String FORCE_KEYWORD = "force";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " [" + FORCE_KEYWORD + "]";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT =
            "Error: Invalid command format. Usage: " + MESSAGE_USAGE;
    public static final String MESSAGE_SUCCESS = "ClubLogistics data has been cleared!";
    public static final String MESSAGE_EQUIPMENT_ISSUED = "Error: Some equipment is still issued: %1$s.\n"
            + "Return it first, or enter \"" + COMMAND_WORD + " " + FORCE_KEYWORD
            + "\" to delete all records anyway.";

    private final boolean isForced;

    /**
     * Creates a clear command. When {@code isForced} is false, the clear is refused while equipment is issued.
     */
    public ClearCommand(boolean isForced) {
        this.isForced = isForced;
    }

    /** {@inheritDoc} */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Equipment> issuedEquipment = findIssuedEquipment(model);
        if (!isForced && !issuedEquipment.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_EQUIPMENT_ISSUED, describe(issuedEquipment)));
        }
        model.setClubLogisticsData(new ClubLogisticsData());
        return new CommandResult(MESSAGE_SUCCESS);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDataMutating() {
        return true;
    }

    private static List<Equipment> findIssuedEquipment(Model model) {
        return model.getClubLogisticsData().getEquipmentList().stream()
                .filter(Equipment::isIssued)
                .toList();
    }

    private static String describe(List<Equipment> issuedEquipment) {
        return issuedEquipment.stream()
                .map(equipment -> equipment.getId() + " (" + equipment.getIssuedTo().orElseThrow() + ")")
                .collect(Collectors.joining(", "));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ClearCommand otherCommand)) {
            return false;
        }
        return isForced == otherCommand.isForced;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(isForced);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("isForced", isForced)
                .toString();
    }
}
