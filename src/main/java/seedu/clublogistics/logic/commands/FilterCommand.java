package seedu.clublogistics.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import seedu.clublogistics.commons.util.ToStringBuilder;
import seedu.clublogistics.model.Model;
import seedu.clublogistics.model.equipment.EquipmentStatus;

/**
 * Shows only the equipment whose status matches the requested status.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " <available|issued>";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT =
            "Error: Invalid command format. Usage: " + MESSAGE_USAGE;
    public static final String MESSAGE_SHOWING_AVAILABLE = "Showing available equipment.";
    public static final String MESSAGE_SHOWING_ISSUED = "Showing issued equipment.";
    public static final String MESSAGE_NO_AVAILABLE = "No available equipment found.";
    public static final String MESSAGE_NO_ISSUED = "No issued equipment found.";

    private final EquipmentStatus status;

    /**
     * Creates a command to show only equipment with the given {@code status}.
     */
    public FilterCommand(EquipmentStatus status) {
        this.status = requireNonNull(status);
    }

    /**
     * Applies the status filter to the equipment list and reports whether any equipment matched.
     */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredEquipmentList(equipment -> equipment.getStatus() == status,
                "Showing: " + (status == EquipmentStatus.AVAILABLE ? "Available" : "Issued")
                        + " — use list to show all");
        return new CommandResult(selectMessage(model.getFilteredEquipmentList().isEmpty()));
    }

    private String selectMessage(boolean noMatches) {
        boolean isAvailable = status == EquipmentStatus.AVAILABLE;
        if (noMatches) {
            return isAvailable ? MESSAGE_NO_AVAILABLE : MESSAGE_NO_ISSUED;
        }
        return isAvailable ? MESSAGE_SHOWING_AVAILABLE : MESSAGE_SHOWING_ISSUED;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof FilterCommand otherCommand)) {
            return false;
        }
        return status == otherCommand.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("status", status)
                .toString();
    }
}
