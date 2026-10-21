package seedu.clublogistics.logic.parser;

import static seedu.clublogistics.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.clublogistics.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.clublogistics.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.clublogistics.logic.commands.RemoveCommand;
import seedu.clublogistics.model.equipment.EquipmentId;

public class RemoveCommandParserTest {
    private final RemoveCommandParser parser = new RemoveCommandParser();

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validId_success() {
        RemoveCommand expected = new RemoveCommand(new EquipmentId("CAM001"));

        assertParseSuccess(parser, "CAM001", expected);
        assertParseSuccess(parser, "  cam001  ", expected);
    }

    @Test
    public void parse_missingOrExtraArguments_failure() {
        assertParseFailure(parser, "", RemoveCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "   ", RemoveCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "CAM001 John", RemoveCommand.MESSAGE_INVALID_COMMAND_FORMAT);
    }

    @Test
    public void parse_invalidId_failure() {
        assertParseFailure(parser, "CAM-001", EquipmentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "A".repeat(21), EquipmentId.MESSAGE_CONSTRAINTS);
    }
}
