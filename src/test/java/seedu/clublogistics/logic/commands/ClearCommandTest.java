package seedu.clublogistics.logic.commands;

import static seedu.clublogistics.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.clublogistics.testutil.TypicalEquipment.getTypicalClubLogisticsData;

import org.junit.jupiter.api.Test;

import seedu.clublogistics.model.ClubLogisticsData;
import seedu.clublogistics.model.Model;
import seedu.clublogistics.model.ModelManager;
import seedu.clublogistics.model.UserPrefs;

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
