package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddEquipmentCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.IssueCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ReturnCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.model.equipment.EquipmentName;
import seedu.address.model.equipment.EquipmentStatus;

public class ClubLogisticsParserTest {

    private final ClubLogisticsParser parser = new ClubLogisticsParser();

    /** Parses equipment registration arguments. */
    @Test
    public void parseCommand_addEquipment_success() throws Exception {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), new EquipmentName("Sony Camera"));
        assertEquals(new AddEquipmentCommand(camera), parser.parseCommand("add CAM001 Sony Camera"));
    }

    /** Rejects equipment registration without arguments. */
    @Test
    public void parseCommand_addEquipmentMissingArguments_throwsParseException() {
        assertThrows(ParseException.class, AddEquipmentCommandParser.MESSAGE_USAGE, ()
                -> parser.parseCommand("add"));
    }

    /** Rejects the obsolete equipment command spelling. */
    @Test
    public void parseCommand_oldEquipmentCommand_throwsUnknownCommand() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, ()
                -> parser.parseCommand("add-equipment CAM001 Sony Camera"));
    }

    /** Parses the clear command. */
    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    /** Parses the exit command. */
    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    /** Parses the help command. */
    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    /** Parses list and rejects extra arguments. */
    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertThrows(ParseException.class, ListCommand.MESSAGE_INVALID_COMMAND_FORMAT, ()
                -> parser.parseCommand(ListCommand.COMMAND_WORD + " 3"));
    }

    /** Parses equipment IDs and multiword borrower names. */
    @Test
    public void parseCommand_issue() throws Exception {
        assertEquals(new IssueCommand(new EquipmentId("CAM001"), new BorrowerName("John Tan")),
                parser.parseCommand("  issue cam001 John   Tan  "));
    }

    /** Rejects issuing without a borrower. */
    @Test
    public void parseCommand_issueMissingName_throwsParseException() {
        assertThrows(ParseException.class, IssueCommand.MESSAGE_INVALID_COMMAND_FORMAT, ()
                -> parser.parseCommand("issue CAM001"));
    }

    /** Rejects empty input with usage guidance. */
    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    /** Rejects unknown command words. */
    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }

    /** Parses a case-insensitive equipment ID for return. */
    @Test
    public void parseCommand_return() throws Exception {
        assertEquals(new ReturnCommand(new EquipmentId("CAM001")), parser.parseCommand("  return cam001 "));
    }

    /** Rejects return without an equipment ID. */
    @Test
    public void parseCommand_returnMissingId_throwsParseException() {
        assertThrows(ParseException.class, ReturnCommand.MESSAGE_INVALID_COMMAND_FORMAT, ()
                -> parser.parseCommand("return"));
    }

    /** Parses availability and issued filters without regard to case. */
    @Test
    public void parseCommand_filter() throws Exception {
        assertEquals(new FilterCommand(EquipmentStatus.AVAILABLE), parser.parseCommand("filter AVAILABLE"));
        assertEquals(new FilterCommand(EquipmentStatus.ISSUED), parser.parseCommand("filter issued"));
    }

    /** Rejects a filter without a status. */
    @Test
    public void parseCommand_filterMissingStatus_throwsParseException() {
        assertThrows(ParseException.class, FilterCommand.MESSAGE_INVALID_COMMAND_FORMAT, ()
                -> parser.parseCommand("filter"));
    }
}
