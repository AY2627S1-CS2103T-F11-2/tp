package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListCommand;

public class ListCommandParserTest {
    private final ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_noArguments_success() throws Exception {
        assertTrue(parser.parse("") instanceof ListCommand);
        assertTrue(parser.parse("   ") instanceof ListCommand);
    }

    @Test
    public void parse_anyArguments_failure() {
        assertParseFailure(parser, " 3", ListCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, " CAM001", ListCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, " n/camera", ListCommand.MESSAGE_INVALID_COMMAND_FORMAT);
    }
}
