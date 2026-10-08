package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.IssueCommand;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.EquipmentId;

public class IssueCommandParserTest {
    private final IssueCommandParser parser = new IssueCommandParser();

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validArguments_success() {
        IssueCommand expected = new IssueCommand(new EquipmentId("CAM001"), new BorrowerName("John Tan"));

        assertParseSuccess(parser, "CAM001 John Tan", expected);
        assertParseSuccess(parser, "  cam001   John   Tan  ", expected);
        assertParseSuccess(parser, "CAM001\tJohn Tan", expected);
        assertParseSuccess(parser, "PROJ001 AliceLim",
                new IssueCommand(new EquipmentId("PROJ001"), new BorrowerName("AliceLim")));
    }

    @Test
    public void parse_boundaryLengths_success() {
        String equipmentId = "A".repeat(20);
        String borrower = "B".repeat(50);

        assertParseSuccess(parser, equipmentId + " " + borrower,
                new IssueCommand(new EquipmentId(equipmentId), new BorrowerName(borrower)));
        assertParseSuccess(parser, "A B", new IssueCommand(new EquipmentId("A"), new BorrowerName("B")));
    }

    @Test
    public void parse_missingArguments_failure() {
        String message = "Error: Invalid command format. Usage: issue <equipment-id> <person-name>";

        assertParseFailure(parser, "", message);
        assertParseFailure(parser, " \t\n ", message);
        assertParseFailure(parser, "CAM001", message);
        assertParseFailure(parser, " CAM001   ", message);
    }

    @Test
    public void parse_invalidEquipmentId_failure() {
        String message = "Error: Equipment ID must contain only letters and numbers and cannot contain spaces.";

        assertParseFailure(parser, "CAM-001 John Tan", message);
        assertParseFailure(parser, "CAM/001 John Tan", message);
        assertParseFailure(parser, "A".repeat(21) + " John Tan", message);
        assertParseFailure(parser, "\u00c9QUIPEMENT John Tan", message);
    }

    @Test
    public void parse_invalidBorrowerName_failure() {
        String message = "Error: Person name must contain only letters and spaces and be 1–50 characters long.";

        assertParseFailure(parser, "CAM001 John1", message);
        assertParseFailure(parser, "CAM001 John-Tan", message);
        assertParseFailure(parser, "CAM001 John/Tan", message);
        assertParseFailure(parser, "CAM001 John\tTan", message);
        assertParseFailure(parser, "CAM001 " + "A".repeat(51), message);
    }

    @Test
    public void parse_bothArgumentsInvalid_reportsEquipmentIdFirst() {
        assertParseFailure(parser, "CAM-001 John1", EquipmentId.MESSAGE_CONSTRAINTS);
    }
}
