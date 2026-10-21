package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.util.JsonUtil;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.parser.ClubLogisticsParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.ui.HelpWindow;

/** Guards the equipment-only product boundary. */
public class ClubLogisticsCleanupTest {
    /** Verifies that contact commands are no longer exposed. */
    @Test
    public void parseCommand_contactCommands_rejected() {
        ClubLogisticsParser parser = new ClubLogisticsParser();
        for (String command : new String[] {"delete 1", "edit 1 n/Alice", "find Alice"}) {
            assertThrows(ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand(command));
        }
    }

    /** Verifies that a fresh installation only persists equipment records. */
    @Test
    public void serialize_sampleData_containsOnlyEquipment() throws Exception {
        String json = JsonUtil.toJsonString(
                new JsonSerializableClubLogisticsData(SampleDataUtil.getSampleClubLogisticsData()));
        assertEquals(Set.of("equipment"), JsonUtil.fromJsonString(json, Map.class).keySet());
    }

    /** Verifies that visible help and command messages use ClubLogistics branding. */
    @Test
    public void userFacingText_usesClubLogistics() {
        assertEquals("ClubLogistics data has been cleared!", ClearCommand.MESSAGE_SUCCESS);
        assertEquals("Exiting ClubLogistics as requested ...", ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT);
        assertEquals("https://ay2627s1-cs2103t-f11-2.github.io/tp/UserGuide.html", HelpWindow.USERGUIDE_URL);
    }
}
