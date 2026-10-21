package seedu.clublogistics.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.clublogistics.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.clublogistics.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.clublogistics.logic.commands.HelpCommand;

public class HelpCommandParserTest {
    private final HelpCommandParser parser = new HelpCommandParser();

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_noArguments_success() throws Exception {
        assertTrue(parser.parse("") instanceof HelpCommand);
        assertTrue(parser.parse("   ") instanceof HelpCommand);
    }

    @Test
    public void parse_anyArguments_failure() {
        assertParseFailure(parser, " 3", HelpCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, " extra", HelpCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, " n/camera", HelpCommand.MESSAGE_INVALID_COMMAND_FORMAT);
    }
}
