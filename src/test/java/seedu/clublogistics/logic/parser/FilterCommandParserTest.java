package seedu.clublogistics.logic.parser;

import static seedu.clublogistics.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.clublogistics.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.clublogistics.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.clublogistics.logic.commands.FilterCommand;
import seedu.clublogistics.model.equipment.EquipmentStatus;

public class FilterCommandParserTest {
    private final FilterCommandParser parser = new FilterCommandParser();

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validStatus_success() {
        assertParseSuccess(parser, "available", new FilterCommand(EquipmentStatus.AVAILABLE));
        assertParseSuccess(parser, "issued", new FilterCommand(EquipmentStatus.ISSUED));
    }

    @Test
    public void parse_statusInAnyCaseWithWhitespace_success() {
        assertParseSuccess(parser, " AVAILABLE ", new FilterCommand(EquipmentStatus.AVAILABLE));
        assertParseSuccess(parser, "  IsSuEd  ", new FilterCommand(EquipmentStatus.ISSUED));
    }

    @Test
    public void parse_missingArgument_failure() {
        assertParseFailure(parser, "", FilterCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "   ", FilterCommand.MESSAGE_INVALID_COMMAND_FORMAT);
    }

    @Test
    public void parse_extraArguments_failure() {
        assertParseFailure(parser, "available issued", FilterCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "issued extra", FilterCommand.MESSAGE_INVALID_COMMAND_FORMAT);
    }

    @Test
    public void parse_unsupportedStatus_failure() {
        assertParseFailure(parser, "broken", FilterCommand.MESSAGE_INVALID_COMMAND_FORMAT);
        assertParseFailure(parser, "avail", FilterCommand.MESSAGE_INVALID_COMMAND_FORMAT);
    }
}
