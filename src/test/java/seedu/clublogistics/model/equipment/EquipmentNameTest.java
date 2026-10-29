package seedu.clublogistics.model.equipment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.clublogistics.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class EquipmentNameTest {

    /** Names made of letters, digits, spaces and the documented punctuation are accepted. */
    @Test
    public void isValidName_documentedCharacters_accepted() {
        String[] validNames = {"Sony Camera", "HDMI Cable 2m", "Club's Tripod", "Mic-1 / Mic-2", "Cable (3.5mm)",
            "Speaker #2", "Cables & Adapters", "Lens, 50mm", "Kit: Audio", "Canon EOS R6+", "Café Projector"};
        for (String name : validNames) {
            assertTrue(EquipmentName.isValidName(name), name);
            assertEquals(name, new EquipmentName(name).value);
        }
    }

    /** Names containing symbols outside the documented set are rejected with the constraints message. */
    @Test
    public void isValidName_undocumentedCharacters_rejected() {
        String[] invalidNames = {"<script>&%$#@!", "Camera <HD>", "50% Off", "Mic $5", "Mic@Club", "Wow!",
            "Camera 📷", "Tab;Name", "Back\\slash", "Quote\"d"};
        for (String name : invalidNames) {
            assertFalse(EquipmentName.isValidName(name), name);
            assertThrows(IllegalArgumentException.class, EquipmentName.MESSAGE_CONSTRAINTS, ()
                    -> new EquipmentName(name));
        }
    }

    /** The rejection message names both the length limit and the allowed characters. */
    @Test
    public void messageConstraints_statesLengthAndAllowedCharacters() {
        assertTrue(EquipmentName.MESSAGE_CONSTRAINTS.contains("1 to 50 characters"));
        assertTrue(EquipmentName.MESSAGE_CONSTRAINTS.contains("letters, numbers, spaces"));
        assertTrue(EquipmentName.MESSAGE_CONSTRAINTS.contains("- / ' . , & ( ) # + :"));
    }
}
