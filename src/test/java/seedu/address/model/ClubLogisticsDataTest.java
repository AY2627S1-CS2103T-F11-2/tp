package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalClubLogisticsData;

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

public class ClubLogisticsDataTest {

    private final ClubLogisticsData clubLogisticsData = new ClubLogisticsData();

    @Test
    public void constructor() {
        assertEquals(List.of(), clubLogisticsData.getPersonList());
        assertEquals(List.of(), clubLogisticsData.getEquipmentList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> clubLogisticsData.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyClubLogisticsData_replacesData() {
        ClubLogisticsData newData = getTypicalClubLogisticsData();
        clubLogisticsData.resetData(newData);
        assertEquals(newData, clubLogisticsData);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Person> newPersons = List.of(ALICE, editedAlice);
        ClubLogisticsDataStub newData = new ClubLogisticsDataStub(newPersons);

        assertThrows(DuplicatePersonException.class, () -> clubLogisticsData.resetData(newData));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> clubLogisticsData.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInClubLogisticsData_returnsFalse() {
        assertFalse(clubLogisticsData.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInClubLogisticsData_returnsTrue() {
        clubLogisticsData.addPerson(ALICE);
        assertTrue(clubLogisticsData.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameIdentityFieldsInClubLogisticsData_returnsTrue() {
        clubLogisticsData.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(clubLogisticsData.hasPerson(editedAlice));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> clubLogisticsData.getPersonList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = ClubLogisticsData.class.getCanonicalName() + "{persons=" + clubLogisticsData.getPersonList()
                + ", equipment=" + clubLogisticsData.getEquipmentList() + "}";
        assertEquals(expected, clubLogisticsData.toString());
    }

    @Test
    public void addEquipment_duplicateId_rejectsWithoutMutation() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        clubLogisticsData.addEquipment(camera);
        Equipment duplicate = new Equipment(new EquipmentId("cam001"), "Different Camera");
        assertThrows(IllegalArgumentException.class, () -> clubLogisticsData.addEquipment(duplicate));
        assertEquals(List.of(camera), clubLogisticsData.getEquipmentList());
    }

    @Test
    public void addEquipment_sameNameDifferentIds_acceptsBothItems() {
        Equipment firstCamera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        Equipment secondCamera = new Equipment(new EquipmentId("CAM002"), "Sony Camera");
        clubLogisticsData.addEquipment(firstCamera);
        clubLogisticsData.addEquipment(secondCamera);
        assertEquals(List.of(firstCamera, secondCamera), clubLogisticsData.getEquipmentList());
    }

    @Test
    public void findEquipment_caseInsensitiveId_findsOriginalRecord() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        clubLogisticsData.addEquipment(camera);
        assertTrue(clubLogisticsData.hasEquipment(new EquipmentId("cam001")));
        assertEquals(Optional.of(camera), clubLogisticsData.findEquipment(new EquipmentId("cam001")));
        assertEquals(Optional.empty(), clubLogisticsData.findEquipment(new EquipmentId("MIC001")));
    }

    @Test
    public void equipmentOperations_nullInputs_throwsNullPointerException() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        assertThrows(NullPointerException.class, () -> clubLogisticsData.addEquipment(null));
        assertThrows(NullPointerException.class, () -> clubLogisticsData.findEquipment(null));
        assertThrows(NullPointerException.class, () -> clubLogisticsData.hasEquipment((Equipment) null));
        assertThrows(NullPointerException.class, () -> clubLogisticsData.setEquipment(null, camera));
        assertThrows(NullPointerException.class, () -> clubLogisticsData.setEquipment(camera, null));
        assertThrows(NullPointerException.class, () -> clubLogisticsData.setEquipments(null));
    }

    @Test
    public void setEquipment_issuedReplacement_updatesStoredRecord() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        Equipment issuedCamera = camera.issueTo(new BorrowerName("John Tan"));
        clubLogisticsData.addEquipment(camera);
        clubLogisticsData.setEquipment(camera, issuedCamera);
        assertEquals(Optional.of(issuedCamera), clubLogisticsData.findEquipment(camera.getId()));
        assertFalse(camera.isIssued());
    }

    @Test
    public void setEquipment_unusedReplacementId_updatesIdentityAndPreservesOtherRecords() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        Equipment microphone = new Equipment(new EquipmentId("MIC001"), "Wireless Microphone");
        Equipment replacement = new Equipment(new EquipmentId("CAM002"), "Sony Camera");
        clubLogisticsData.setEquipments(List.of(camera, microphone));

        clubLogisticsData.setEquipment(camera, replacement);

        assertEquals(Optional.empty(), clubLogisticsData.findEquipment(new EquipmentId("cam001")));
        assertEquals(Optional.of(replacement), clubLogisticsData.findEquipment(new EquipmentId("cam002")));
        assertEquals(Optional.of(microphone), clubLogisticsData.findEquipment(new EquipmentId("mic001")));
        assertEquals(List.of(replacement, microphone), clubLogisticsData.getEquipmentList());
    }

    @Test
    public void setEquipment_duplicateOrMissingTarget_rejectsWithoutMutation() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        Equipment microphone = new Equipment(new EquipmentId("MIC001"), "Wireless Microphone");
        clubLogisticsData.setEquipments(List.of(camera, microphone));
        assertThrows(IllegalArgumentException.class, () -> clubLogisticsData.setEquipment(camera, microphone));
        Equipment missing = new Equipment(new EquipmentId("PROJ001"), "Projector");
        assertThrows(IllegalArgumentException.class, () -> clubLogisticsData.setEquipment(missing, camera));
        assertEquals(List.of(camera, microphone), clubLogisticsData.getEquipmentList());
    }

    @Test
    public void setEquipments_invalidReplacement_rejectsWithoutMutation() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        clubLogisticsData.addEquipment(camera);
        Equipment duplicate = new Equipment(new EquipmentId("cam001"), "Different Camera");
        assertThrows(IllegalArgumentException.class, () -> clubLogisticsData.setEquipments(List.of(camera, duplicate)));
        assertThrows(NullPointerException.class, ()
                -> clubLogisticsData.setEquipments(java.util.Arrays.asList(camera, null)));
        assertEquals(List.of(camera), clubLogisticsData.getEquipmentList());
    }

    @Test
    public void resetData_duplicateEquipment_preservesPersonsAndEquipment() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        clubLogisticsData.addPerson(ALICE);
        clubLogisticsData.addEquipment(camera);
        ClubLogisticsData original = new ClubLogisticsData(clubLogisticsData);
        Equipment duplicate = new Equipment(new EquipmentId("cam001"), "Different Camera");
        ClubLogisticsDataStub invalidData = new ClubLogisticsDataStub(List.of(), List.of(camera, duplicate));
        assertThrows(IllegalArgumentException.class, () -> clubLogisticsData.resetData(invalidData));
        assertEquals(original, clubLogisticsData);
    }

    @Test
    public void constructorAndResetData_equipment_copiesListsAndPreservesBorrower() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera", new BorrowerName("John Tan"));
        clubLogisticsData.addEquipment(camera);
        ClubLogisticsData copied = new ClubLogisticsData(clubLogisticsData);
        assertEquals(clubLogisticsData, copied);
        assertEquals(clubLogisticsData.hashCode(), copied.hashCode());
        clubLogisticsData.setEquipments(List.of());
        assertEquals(List.of(camera), copied.getEquipmentList());
        assertFalse(copied.equals(clubLogisticsData));

        clubLogisticsData.resetData(copied);
        assertEquals(List.of(camera), clubLogisticsData.getEquipmentList());
        clubLogisticsData.resetData(clubLogisticsData);
        assertEquals(List.of(camera), clubLogisticsData.getEquipmentList());
    }

    @Test
    public void getEquipmentList_unmodifiableView_reflectsRecordChanges() {
        ObservableList<Equipment> view = clubLogisticsData.getEquipmentList();
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        assertThrows(UnsupportedOperationException.class, () -> view.add(camera));
        clubLogisticsData.addEquipment(camera);
        assertEquals(List.of(camera), view);
        assertThrows(UnsupportedOperationException.class, () -> view.remove(camera));

        Equipment issuedCamera = camera.issueTo(new BorrowerName("John Tan"));
        clubLogisticsData.setEquipment(camera, issuedCamera);
        assertEquals(List.of(issuedCamera), view);
    }

    /**
     * A stub ReadOnlyClubLogisticsData whose persons list can violate interface constraints.
     */
    private static class ClubLogisticsDataStub implements ReadOnlyClubLogisticsData {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();
        private final ObservableList<Equipment> equipment = FXCollections.observableArrayList();

        ClubLogisticsDataStub(Collection<Person> persons) {
            this(persons, List.of());
        }

        ClubLogisticsDataStub(Collection<Person> persons, Collection<Equipment> equipment) {
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
