package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddEquipmentCommand;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.model.equipment.EquipmentName;

public class AddEquipmentCommandParserTest {

    private final AddEquipmentCommandParser parser = new AddEquipmentCommandParser();

    @Test
    public void parse_validArguments_success() {
        Equipment expectedEquipment = new Equipment(new EquipmentId("CAM001"), new EquipmentName("Sony Camera"));

        assertParseSuccess(parser, " CAM001   Sony   Camera ", new AddEquipmentCommand(expectedEquipment));
    }

    @Test
    public void parse_missingRequiredArgument_failure() {
        assertParseFailure(parser, "", AddEquipmentCommandParser.MESSAGE_USAGE);
        assertParseFailure(parser, "CAM001", AddEquipmentCommandParser.MESSAGE_USAGE);
    }

    @Test
    public void parse_invalidValues_failure() {
        assertParseFailure(parser, "CAM-001 Sony Camera", EquipmentId.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "CAM001 " + "a".repeat(51), EquipmentName.MESSAGE_CONSTRAINTS);
    }
}
