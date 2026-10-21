package seedu.clublogistics.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.clublogistics.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.clublogistics.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.clublogistics.logic.commands.ExitCommand;

public class ExitCommandParserTest {
    private final ExitCommandParser parser = new ExitCommandParser();

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_noArguments_success() throws Exception {
        assertTrue(parser.parse("") instanceof ExitCommand);
        assertTrue(parser.parse("   ") instanceof ExitCommand);
    }

    @Test
    public void parse_anyArguments_failure() {
        assertParseFailure(parser, " 3", ExitCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, " extra", ExitCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, " n/camera", ExitCommand.MESSAGE_INVALID_COMMAND_FORMAT);
    }
}
