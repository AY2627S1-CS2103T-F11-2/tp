package seedu.address.model.equipment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class BorrowerNameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new BorrowerName(null));
        assertThrows(NullPointerException.class, () -> BorrowerName.isValidName(null));
    }

    @Test
    public void constructor_invalidNames_throwsIllegalArgumentException() {
        String[] invalidNames = {"", " ", "John1", "John-Tan", "John/Tan", "John\tTan", "é", "A".repeat(51)};
        for (String name : invalidNames) {
            assertFalse(BorrowerName.isValidName(name));
            assertThrows(IllegalArgumentException.class, BorrowerName.MESSAGE_CONSTRAINTS, ()
                    -> new BorrowerName(name));
        }
    }

    @Test
    public void constructor_validNames_normalizesSpacesAndPreservesCase() {
        assertTrue(BorrowerName.isValidName("A"));
        assertTrue(BorrowerName.isValidName("A".repeat(50)));
        assertEquals("John Tan", new BorrowerName("  John   Tan  ").toString());

        // Length limits apply after spaces have been normalized.
        String longInput = "A".repeat(24) + "   " + "B".repeat(25);
        assertTrue(BorrowerName.isValidName(longInput));
        assertEquals(50, new BorrowerName(longInput).toString().length());
    }

    @Test
    public void equals_normalizedNames_comparesValues() {
        BorrowerName name = new BorrowerName(" John   Tan ");
        BorrowerName sameName = new BorrowerName("John Tan");
        assertEquals(sameName, name);
        assertEquals(sameName.hashCode(), name.hashCode());
        assertTrue(name.equals(name));
        assertFalse(name.equals(new BorrowerName("john tan")));
        assertFalse(name.equals(null));
        assertFalse(name.equals("John Tan"));
    }
}
