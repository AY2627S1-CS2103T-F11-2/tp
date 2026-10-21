package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalEquipment.getTypicalClubLogisticsData;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;

public class ClubLogisticsDataTest {

    private final ClubLogisticsData clubLogisticsData = new ClubLogisticsData();

    /** Verifies the default register and preferences. */
    @Test
    public void constructor() {
        assertEquals(List.of(), clubLogisticsData.getEquipmentList());
    }

    /** Rejects a null replacement register. */
    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> clubLogisticsData.resetData(null));
    }

    /** Replaces the register with valid equipment data. */
    @Test
    public void resetData_withValidReadOnlyClubLogisticsData_replacesData() {
        ClubLogisticsData newData = getTypicalClubLogisticsData();
        clubLogisticsData.resetData(newData);
        assertEquals(newData, clubLogisticsData);
    }

    /** Verifies the equipment register diagnostic text. */
    @Test
    public void toStringMethod() {
        String expected = ClubLogisticsData.class.getCanonicalName() + "{equipment="
                + clubLogisticsData.getEquipmentList() + "}";
        assertEquals(expected, clubLogisticsData.toString());
    }

    /** Rejects duplicate IDs without changing the register. */
    @Test
    public void addEquipment_duplicateId_rejectsWithoutMutation() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        clubLogisticsData.addEquipment(camera);
        Equipment duplicate = new Equipment(new EquipmentId("cam001"), "Different Camera");
        assertThrows(IllegalArgumentException.class, () -> clubLogisticsData.addEquipment(duplicate));
        assertEquals(List.of(camera), clubLogisticsData.getEquipmentList());
    }

    /** Allows equipment with the same name and different IDs. */
    @Test
    public void addEquipment_sameNameDifferentIds_acceptsBothItems() {
        Equipment firstCamera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        Equipment secondCamera = new Equipment(new EquipmentId("CAM002"), "Sony Camera");
        clubLogisticsData.addEquipment(firstCamera);
        clubLogisticsData.addEquipment(secondCamera);
        assertEquals(List.of(firstCamera, secondCamera), clubLogisticsData.getEquipmentList());
    }

    /** Verifies case-insensitive lookup and missing-item handling. */
    @Test
    public void findEquipment_caseInsensitiveId_findsOriginalRecord() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        clubLogisticsData.addEquipment(camera);
        assertTrue(clubLogisticsData.hasEquipment(new EquipmentId("cam001")));
        assertEquals(Optional.of(camera), clubLogisticsData.findEquipment(new EquipmentId("cam001")));
        assertEquals(Optional.empty(), clubLogisticsData.findEquipment(new EquipmentId("MIC001")));
    }

    /** Rejects null arguments to equipment operations. */
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

    /** Replaces equipment with its issued record. */
    @Test
    public void setEquipment_issuedReplacement_updatesStoredRecord() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        Equipment issuedCamera = camera.issueTo(new BorrowerName("John Tan"));
        clubLogisticsData.addEquipment(camera);
        clubLogisticsData.setEquipment(camera, issuedCamera);
        assertEquals(Optional.of(issuedCamera), clubLogisticsData.findEquipment(camera.getId()));
        assertFalse(camera.isIssued());
    }

    /** Verifies identity changes without affecting other equipment. */
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

    /** Rejects invalid replacements without changing the register. */
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

    /** Rejects duplicate or null records before replacing the register. */
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

    /** Keeps the existing register when replacement data contains duplicates. */
    @Test
    public void resetData_duplicateEquipment_preservesEquipment() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        clubLogisticsData.addEquipment(camera);
        ClubLogisticsData original = new ClubLogisticsData(clubLogisticsData);
        Equipment duplicate = new Equipment(new EquipmentId("cam001"), "Different Camera");
        ClubLogisticsDataStub invalidData = new ClubLogisticsDataStub(List.of(camera, duplicate));
        assertThrows(IllegalArgumentException.class, () -> clubLogisticsData.resetData(invalidData));
        assertEquals(original, clubLogisticsData);
    }

    /** Verifies copying and resetting preserve equipment and borrowers. */
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

    /** Verifies an unmodifiable live equipment view. */
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

    /** A data source that can contain duplicate equipment IDs. */
    private static class ClubLogisticsDataStub implements ReadOnlyClubLogisticsData {
        private final ObservableList<Equipment> equipment = FXCollections.observableArrayList();

        ClubLogisticsDataStub(Collection<Equipment> equipment) {
            this.equipment.setAll(equipment);
        }

        /** {@inheritDoc} */
        @Override
        public ObservableList<Equipment> getEquipmentList() {
            return equipment;
        }
    }
}
