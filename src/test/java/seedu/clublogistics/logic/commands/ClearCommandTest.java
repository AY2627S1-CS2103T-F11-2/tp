package seedu.clublogistics.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.clublogistics.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.clublogistics.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.clublogistics.testutil.TypicalEquipment.CAMERA;
import static seedu.clublogistics.testutil.TypicalEquipment.MICROPHONE;
import static seedu.clublogistics.testutil.TypicalEquipment.getTypicalClubLogisticsData;

import org.junit.jupiter.api.Test;

import seedu.clublogistics.model.ClubLogisticsData;
import seedu.clublogistics.model.Model;
import seedu.clublogistics.model.ModelManager;
import seedu.clublogistics.model.UserPrefs;
import seedu.clublogistics.model.equipment.BorrowerName;
import seedu.clublogistics.model.equipment.Equipment;
import seedu.clublogistics.model.equipment.EquipmentId;

public class ClearCommandTest {

    /** Clears an already empty register. */
    @Test
    public void execute_emptyClubLogisticsData_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(false), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    /** Clears a register whose equipment is all available. */
    @Test
    public void execute_noIssuedEquipment_success() {
        ClubLogisticsData data = new ClubLogisticsData();
        data.addEquipment(CAMERA);
        Model model = new ModelManager(data, new UserPrefs());
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(false), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    /** Refuses to clear while equipment is issued, naming each item and its holder, and keeps the register. */
    @Test
    public void execute_issuedEquipmentNotForced_failure() {
        ClubLogisticsData data = getTypicalClubLogisticsData();
        data.addEquipment(new Equipment(new EquipmentId("PROJ001"), "Epson Projector", new BorrowerName("Alice Lim")));
        Model model = new ModelManager(data, new UserPrefs());
        String expectedMessage = String.format(ClearCommand.MESSAGE_EQUIPMENT_ISSUED,
                "MIC001 (John Tan), PROJ001 (Alice Lim)");

        assertCommandFailure(new ClearCommand(false), model, expectedMessage);
    }

    /** Clears issued equipment when the user has explicitly forced it. */
    @Test
    public void execute_issuedEquipmentForced_success() {
        Model model = new ModelManager(getTypicalClubLogisticsData(), new UserPrefs());
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(true), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void equals() {
        ClearCommand clear = new ClearCommand(false);
        ClearCommand forcedClear = new ClearCommand(true);

        assertEquals(clear, clear);
        assertEquals(clear, new ClearCommand(false));
        assertEquals(forcedClear, new ClearCommand(true));
        assertNotEquals(clear, forcedClear);
        assertNotEquals(clear, null);
        assertNotEquals(clear, MICROPHONE);
    }
}
