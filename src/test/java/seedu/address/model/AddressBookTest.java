package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.testutil.PersonBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(List.of(), addressBook.getPersonList());
        assertEquals(List.of(), addressBook.getEquipmentList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Person> newPersons = List.of(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newPersons);

        assertThrows(DuplicatePersonException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        assertTrue(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(addressBook.hasPerson(editedAlice));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getPersonList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{persons=" + addressBook.getPersonList()
                + ", equipment=" + addressBook.getEquipmentList() + "}";
        assertEquals(expected, addressBook.toString());
    }

    @Test
    public void addEquipment_duplicateId_rejectsWithoutMutation() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        addressBook.addEquipment(camera);
        Equipment duplicate = new Equipment(new EquipmentId("cam001"), "Different Camera");
        assertThrows(IllegalArgumentException.class, () -> addressBook.addEquipment(duplicate));
        assertEquals(List.of(camera), addressBook.getEquipmentList());
    }

    @Test
    public void addEquipment_sameNameDifferentIds_acceptsBothItems() {
        Equipment firstCamera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        Equipment secondCamera = new Equipment(new EquipmentId("CAM002"), "Sony Camera");
        addressBook.addEquipment(firstCamera);
        addressBook.addEquipment(secondCamera);
        assertEquals(List.of(firstCamera, secondCamera), addressBook.getEquipmentList());
    }

    @Test
    public void findEquipment_caseInsensitiveId_findsOriginalRecord() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        addressBook.addEquipment(camera);
        assertTrue(addressBook.hasEquipment(new EquipmentId("cam001")));
        assertEquals(Optional.of(camera), addressBook.findEquipment(new EquipmentId("cam001")));
        assertEquals(Optional.empty(), addressBook.findEquipment(new EquipmentId("MIC001")));
    }

    @Test
    public void equipmentOperations_nullInputs_throwsNullPointerException() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        assertThrows(NullPointerException.class, () -> addressBook.addEquipment(null));
        assertThrows(NullPointerException.class, () -> addressBook.findEquipment(null));
        assertThrows(NullPointerException.class, () -> addressBook.hasEquipment((Equipment) null));
        assertThrows(NullPointerException.class, () -> addressBook.setEquipment(null, camera));
        assertThrows(NullPointerException.class, () -> addressBook.setEquipment(camera, null));
        assertThrows(NullPointerException.class, () -> addressBook.setEquipments(null));
    }

    @Test
    public void setEquipment_issuedReplacement_updatesStoredRecord() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        Equipment issuedCamera = camera.issueTo(new BorrowerName("John Tan"));
        addressBook.addEquipment(camera);
        addressBook.setEquipment(camera, issuedCamera);
        assertEquals(Optional.of(issuedCamera), addressBook.findEquipment(camera.getId()));
        assertFalse(camera.isIssued());
    }

    @Test
    public void setEquipment_unusedReplacementId_updatesIdentityAndPreservesOtherRecords() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        Equipment microphone = new Equipment(new EquipmentId("MIC001"), "Wireless Microphone");
        Equipment replacement = new Equipment(new EquipmentId("CAM002"), "Sony Camera");
        addressBook.setEquipments(List.of(camera, microphone));

        addressBook.setEquipment(camera, replacement);

        assertEquals(Optional.empty(), addressBook.findEquipment(new EquipmentId("cam001")));
        assertEquals(Optional.of(replacement), addressBook.findEquipment(new EquipmentId("cam002")));
        assertEquals(Optional.of(microphone), addressBook.findEquipment(new EquipmentId("mic001")));
        assertEquals(List.of(replacement, microphone), addressBook.getEquipmentList());
    }

    @Test
    public void setEquipment_duplicateOrMissingTarget_rejectsWithoutMutation() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        Equipment microphone = new Equipment(new EquipmentId("MIC001"), "Wireless Microphone");
        addressBook.setEquipments(List.of(camera, microphone));
        assertThrows(IllegalArgumentException.class, () -> addressBook.setEquipment(camera, microphone));
        Equipment missing = new Equipment(new EquipmentId("PROJ001"), "Projector");
        assertThrows(IllegalArgumentException.class, () -> addressBook.setEquipment(missing, camera));
        assertEquals(List.of(camera, microphone), addressBook.getEquipmentList());
    }

    @Test
    public void setEquipments_invalidReplacement_rejectsWithoutMutation() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        addressBook.addEquipment(camera);
        Equipment duplicate = new Equipment(new EquipmentId("cam001"), "Different Camera");
        assertThrows(IllegalArgumentException.class, () -> addressBook.setEquipments(List.of(camera, duplicate)));
        assertThrows(NullPointerException.class, ()
                -> addressBook.setEquipments(java.util.Arrays.asList(camera, null)));
        assertEquals(List.of(camera), addressBook.getEquipmentList());
    }

    @Test
    public void resetData_duplicateEquipment_preservesPersonsAndEquipment() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        addressBook.addPerson(ALICE);
        addressBook.addEquipment(camera);
        AddressBook original = new AddressBook(addressBook);
        Equipment duplicate = new Equipment(new EquipmentId("cam001"), "Different Camera");
        AddressBookStub invalidData = new AddressBookStub(List.of(), List.of(camera, duplicate));
        assertThrows(IllegalArgumentException.class, () -> addressBook.resetData(invalidData));
        assertEquals(original, addressBook);
    }

    @Test
    public void constructorAndResetData_equipment_copiesListsAndPreservesBorrower() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera", new BorrowerName("John Tan"));
        addressBook.addEquipment(camera);
        AddressBook copied = new AddressBook(addressBook);
        assertEquals(addressBook, copied);
        assertEquals(addressBook.hashCode(), copied.hashCode());
        addressBook.setEquipments(List.of());
        assertEquals(List.of(camera), copied.getEquipmentList());
        assertFalse(copied.equals(addressBook));

        addressBook.resetData(copied);
        assertEquals(List.of(camera), addressBook.getEquipmentList());
        addressBook.resetData(addressBook);
        assertEquals(List.of(camera), addressBook.getEquipmentList());
    }

    @Test
    public void getEquipmentList_unmodifiableView_reflectsRecordChanges() {
        ObservableList<Equipment> view = addressBook.getEquipmentList();
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        assertThrows(UnsupportedOperationException.class, () -> view.add(camera));
        addressBook.addEquipment(camera);
        assertEquals(List.of(camera), view);
        assertThrows(UnsupportedOperationException.class, () -> view.remove(camera));

        Equipment issuedCamera = camera.issueTo(new BorrowerName("John Tan"));
        addressBook.setEquipment(camera, issuedCamera);
        assertEquals(List.of(issuedCamera), view);
    }

    /**
     * A stub ReadOnlyAddressBook whose persons list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();
        private final ObservableList<Equipment> equipment = FXCollections.observableArrayList();

        AddressBookStub(Collection<Person> persons) {
            this(persons, List.of());
        }

        AddressBookStub(Collection<Person> persons, Collection<Equipment> equipment) {
            this.persons.setAll(persons);
            this.equipment.setAll(equipment);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }

        @Override
        public ObservableList<Equipment> getEquipmentList() {
            return equipment;
        }
    }

}
