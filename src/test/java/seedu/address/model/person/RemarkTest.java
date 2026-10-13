package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RemarkTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }

    @Test
    public void constructor_anyNonNullText_preservesValue() {
        assertEquals("", new Remark("").value);
        assertEquals("  Likes baseball, #7!  ", new Remark("  Likes baseball, #7!  ").toString());
    }

    @Test
    public void equals_sameText_matchesHashCode() {
        Remark first = new Remark("Likes baseball");
        Remark second = new Remark("Likes baseball");
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(new Remark("Likes swimming")));
        assertFalse(first.equals(null));
        assertFalse(first.equals("Likes baseball"));
    }
}
