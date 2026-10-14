package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ReturnCommand;
import seedu.address.model.equipment.EquipmentId;

public class ReturnCommandParserTest {
    private final ReturnCommandParser parser = new ReturnCommandParser();

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validId_success() {
        ReturnCommand expected = new ReturnCommand(new EquipmentId("CAM001"));

        assertParseSuccess(parser, "CAM001", expected);
        assertParseSuccess(parser, "  cam001  ", expected);
    }

    @Test
    public void parse_missingOrExtraArguments_failure() {
        assertParseFailure(parser, "", ReturnCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "   ", ReturnCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "CAM001 CAM002", ReturnCommand.MESSAGE_INVALID_COMMAND_FORMAT);
    }

    @Test
    public void parse_invalidId_failure() {
        assertParseFailure(parser, "CAM-001", EquipmentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "A".repeat(21), EquipmentId.MESSAGE_CONSTRAINTS);
    }
}
