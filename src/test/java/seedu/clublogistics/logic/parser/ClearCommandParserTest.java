package seedu.clublogistics.logic.parser;

import static seedu.clublogistics.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.clublogistics.logic.parser.CommandParserTestUtil.assertParseSuccess;
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
    public void parse_noArguments_returnsUnforcedClear() {
        assertParseSuccess(parser, "", new ClearCommand(false));
        assertParseSuccess(parser, "   ", new ClearCommand(false));
    }

    @Test
    public void parse_forceKeyword_returnsForcedClear() {
        assertParseSuccess(parser, " force", new ClearCommand(true));
        assertParseSuccess(parser, "  FORCE  ", new ClearCommand(true));
    }

    @Test
    public void parse_otherArguments_failure() {
        assertParseFailure(parser, " 3", ClearCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, " CAM001", ClearCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, " force now", ClearCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, " forced", ClearCommand.MESSAGE_INVALID_COMMAND_FORMAT);
    }
}
