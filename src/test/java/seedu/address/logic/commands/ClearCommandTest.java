package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalEquipment.getTypicalClubLogisticsData;

import org.junit.jupiter.api.Test;

import seedu.address.model.ClubLogisticsData;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class ClearCommandTest {

    /** Clears an already empty register. */
    @Test
    public void execute_emptyClubLogisticsData_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    /** Clears a register containing equipment. */
    @Test
    public void execute_nonEmptyClubLogisticsData_success() {
        Model model = new ModelManager(getTypicalClubLogisticsData(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalClubLogisticsData(), new UserPrefs());
        expectedModel.setClubLogisticsData(new ClubLogisticsData());

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

}
