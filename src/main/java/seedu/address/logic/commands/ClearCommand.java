package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.ClubLogisticsData;
import seedu.address.model.Model;

/**
 * Clears the ClubLogistics data.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "ClubLogistics data has been cleared!";

    /** {@inheritDoc} */
    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setClubLogisticsData(new ClubLogisticsData());
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
