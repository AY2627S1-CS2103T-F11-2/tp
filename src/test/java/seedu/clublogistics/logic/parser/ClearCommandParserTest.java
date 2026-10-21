package seedu.clublogistics.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.clublogistics.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.clublogistics.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.clublogistics.logic.commands.ClearCommand;

public class ClearCommandParserTest {
    private final ClearCommandParser parser = new ClearCommandParser();

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_noArguments_success() throws Exception {
        assertTrue(parser.parse("") instanceof ClearCommand);
        assertTrue(parser.parse("   ") instanceof ClearCommand);
    }

    @Test
    public void parse_anyArguments_failure() {
        assertParseFailure(parser, " 3", ClearCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, " CAM001", ClearCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, " n/camera", ClearCommand.MESSAGE_INVALID_COMMAND_FORMAT);
    }
}
