package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.testutil.PersonBuilder;

public class RemarkCommandTest {

    private final ModelManager model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RemarkCommand(null, new Remark("note")));
        assertThrows(NullPointerException.class, () -> new RemarkCommand(INDEX_FIRST_PERSON, null));
        assertThrows(NullPointerException.class, ()
                -> new RemarkCommand(INDEX_FIRST_PERSON, new Remark("")).execute(null));
    }

    @Test
    public void execute_addReplaceAndClear_updatesOnlyRemark() throws Exception {
        Person original = model.getFilteredPersonList().getFirst();
        Person unaffected = model.getFilteredPersonList().get(1);
        for (String text : new String[] {"Likes baseball", "Prefers swimming", ""}) {
            CommandResult result = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(text)).execute(model);
            Person expected = new PersonBuilder(original).withRemark(text).build();
            String message = text.isEmpty() ? RemarkCommand.MESSAGE_DELETE_REMARK_SUCCESS
                    : RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS;
            assertEquals(String.format(message, Messages.format(expected)), result.getFeedbackToUser());
            assertEquals(expected, model.getFilteredPersonList().getFirst());
            assertEquals(unaffected, model.getFilteredPersonList().get(1));
        }
        assertEquals("", original.getRemark().value);
    }

    @Test
    public void execute_filteredList_usesDisplayedIndexAndShowsAllPersons() throws Exception {
        Person target = model.getFilteredPersonList().get(1);
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Displayed person")).execute(model);
        assertEquals(new PersonBuilder(target).withRemark("Displayed person").build(),
                model.getAddressBook().getPersonList().get(1));
        assertEquals(model.getAddressBook().getPersonList(), model.getFilteredPersonList());
        assertEquals("", model.getFilteredPersonList().getFirst().getRemark().value);
    }

    @Test
    public void execute_invalidIndex_preservesModelAndFilter() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandFailure(new RemarkCommand(INDEX_SECOND_PERSON, new Remark("Invalid")), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals_comparesIndexAndRemark() {
        RemarkCommand first = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("note"));
        RemarkCommand second = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("note"));
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertFalse(first.equals(null));
        assertFalse(first.equals("remark"));
        assertFalse(first.equals(new RemarkCommand(INDEX_SECOND_PERSON, new Remark("note"))));
        assertFalse(first.equals(new RemarkCommand(INDEX_FIRST_PERSON, new Remark("other"))));
    }
}
