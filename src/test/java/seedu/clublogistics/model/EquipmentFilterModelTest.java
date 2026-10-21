package seedu.clublogistics.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.clublogistics.model.Model.PREDICATE_SHOW_ALL_EQUIPMENT;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.clublogistics.model.equipment.BorrowerName;
import seedu.clublogistics.model.equipment.Equipment;
import seedu.clublogistics.model.equipment.EquipmentId;
import seedu.clublogistics.model.equipment.EquipmentStatus;

/** Verifies status-filter lifecycle using the real observable equipment model. */
public class EquipmentFilterModelTest {
    private static final Equipment CAMERA = new Equipment(new EquipmentId("CAM001"), "Camera");
    private static final Equipment MICROPHONE = new Equipment(new EquipmentId("MIC001"), "Microphone");
    private static final Equipment ISSUED_CAMERA = CAMERA.issueTo(new BorrowerName("John Tan"));

    private final ModelManager model = new ModelManager();
    private final ObservableList<Equipment> visible = model.getFilteredEquipmentList();

    /** Verifies no selected filter shows both statuses, including later additions. */
    @Test
    public void defaultView_addEquipment_showsBothStatuses() {
        model.addEquipment(CAMERA);
        model.addEquipment(MICROPHONE.issueTo(new BorrowerName("Alice Lim")));
        assertEquals(model.getEquipmentList(), visible);
    }

    /** Verifies adding matching and nonmatching items preserves the available filter. */
    @Test
    public void availableFilter_addEquipment_preservesSelection() {
        model.addEquipment(CAMERA);
        filterBy(EquipmentStatus.AVAILABLE);
        model.addEquipment(MICROPHONE.issueTo(new BorrowerName("Alice Lim")));
        assertEquals(List.of(CAMERA), visible);
        Equipment projector = new Equipment(new EquipmentId("PROJ001"), "Projector");
        model.addEquipment(projector);
        assertEquals(List.of(CAMERA, projector), visible);
        assertEquals(3, model.getEquipmentList().size());
    }

    /** Verifies an initially empty issued filter survives both kinds of addition. */
    @Test
    public void issuedFilter_addEquipment_preservesSelection() {
        filterBy(EquipmentStatus.ISSUED);
        model.addEquipment(CAMERA);
        assertTrue(visible.isEmpty());
        Equipment issuedMicrophone = MICROPHONE.issueTo(new BorrowerName("Alice Lim"));
        model.addEquipment(issuedMicrophone);
        assertEquals(List.of(issuedMicrophone), visible);
        assertEquals(List.of(CAMERA, issuedMicrophone), model.getEquipmentList());
    }

    /** Verifies issuing removes an available item and returning restores it in register order. */
    @Test
    public void availableFilter_issueAndReturn_updatesExistingView() {
        model.addEquipment(CAMERA);
        model.addEquipment(MICROPHONE);
        filterBy(EquipmentStatus.AVAILABLE);
        model.setEquipment(CAMERA, ISSUED_CAMERA);
        assertEquals(List.of(MICROPHONE), visible);
        model.setEquipment(ISSUED_CAMERA, ISSUED_CAMERA.markReturned());
        assertEquals(List.of(CAMERA, MICROPHONE), visible);
        assertSame(visible, model.getFilteredEquipmentList());
    }

    /** Verifies issuing adds a match and returning removes it without clearing the issued filter. */
    @Test
    public void issuedFilter_issueAndReturn_updatesExistingView() {
        model.addEquipment(CAMERA);
        model.addEquipment(MICROPHONE);
        filterBy(EquipmentStatus.ISSUED);
        model.setEquipment(CAMERA, ISSUED_CAMERA);
        assertEquals(List.of(ISSUED_CAMERA), visible);
        model.setEquipment(ISSUED_CAMERA, ISSUED_CAMERA.markReturned());
        assertTrue(visible.isEmpty());
        assertEquals(List.of(CAMERA, MICROPHONE), model.getEquipmentList());
    }

    /** Verifies same-status replacements refresh displayed details while retaining the filter. */
    @Test
    public void issuedFilter_replaceEquipment_updatesDetails() {
        model.addEquipment(ISSUED_CAMERA);
        model.addEquipment(MICROPHONE);
        filterBy(EquipmentStatus.ISSUED);
        Equipment renamed = new Equipment(CAMERA.getId(), "Updated Camera", new BorrowerName("Alice Lim"));
        model.setEquipment(ISSUED_CAMERA, renamed);
        assertEquals(List.of(renamed), visible);
        assertEquals(List.of(renamed, MICROPHONE), model.getEquipmentList());
    }

    /** Verifies removal of matching and hidden items preserves the active filter. */
    @Test
    public void availableFilter_removeEquipment_preservesSelection() {
        model.addEquipment(CAMERA);
        model.addEquipment(MICROPHONE);
        Equipment issuedProjector = new Equipment(new EquipmentId("PROJ001"), "Projector")
                .issueTo(new BorrowerName("John Tan"));
        model.addEquipment(issuedProjector);
        filterBy(EquipmentStatus.AVAILABLE);
        model.removeEquipment(issuedProjector);
        assertEquals(List.of(CAMERA, MICROPHONE), visible);
        model.removeEquipment(CAMERA);
        assertEquals(List.of(MICROPHONE), visible);
        model.setEquipment(MICROPHONE, MICROPHONE.issueTo(new BorrowerName("Alice Lim")));
        assertTrue(visible.isEmpty());
    }

    /** Verifies selecting a different status and the list reset predicate do not mutate records. */
    @Test
    public void updateFilter_switchAndReset_preservesRecords() {
        model.addEquipment(ISSUED_CAMERA);
        model.addEquipment(MICROPHONE);
        filterBy(EquipmentStatus.AVAILABLE);
        assertEquals(List.of(MICROPHONE), visible);
        filterBy(EquipmentStatus.ISSUED);
        assertEquals(List.of(ISSUED_CAMERA), visible);
        model.updateFilteredEquipmentList(PREDICATE_SHOW_ALL_EQUIPMENT);
        assertEquals(List.of(ISSUED_CAMERA, MICROPHONE), visible);
        assertEquals(List.of(ISSUED_CAMERA, MICROPHONE), model.getEquipmentList());
        filterBy(EquipmentStatus.ISSUED);
        ModelManager restarted = new ModelManager(model.getClubLogisticsData(), model.getUserPrefs());
        assertEquals(model.getEquipmentList(), restarted.getFilteredEquipmentList());
    }

    private void filterBy(EquipmentStatus status) {
        model.updateFilteredEquipmentList(equipment -> equipment.getStatus() == status);
    }
}
