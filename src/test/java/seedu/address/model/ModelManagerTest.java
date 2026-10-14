package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.model.equipment.EquipmentName;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.testutil.ClubLogisticsDataBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new ClubLogisticsData(), new ClubLogisticsData(modelManager.getClubLogisticsData()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new ClubLogisticsData(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInClubLogisticsData_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInClubLogisticsData_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void equipmentOperations_issueEquipment_updatesModelAndPreservesOtherItems() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
        Equipment microphone = new Equipment(new EquipmentId("MIC001"), "Wireless Microphone");
        modelManager.addEquipment(camera);
        modelManager.addEquipment(microphone);
        Equipment issuedCamera = camera.issueTo(new BorrowerName("John Tan"));
        modelManager.setEquipment(camera, issuedCamera);
        assertEquals(Optional.of(issuedCamera), modelManager.findEquipment(new EquipmentId("cam001")));
        assertEquals(Optional.of(microphone), modelManager.findEquipment(microphone.getId()));
        assertEquals(List.of(issuedCamera, microphone), modelManager.getClubLogisticsData().getEquipmentList());
    }

    @Test
    public void constructorAndSetClubLogisticsData_equipment_copiesDataWithoutSharingLists() {
        Equipment camera = new Equipment(new EquipmentId("CAM001"), "Sony Camera", new BorrowerName("John Tan"));
        ClubLogisticsData original = new ClubLogisticsData();
        original.addEquipment(camera);
        modelManager = new ModelManager(original, new UserPrefs());
        original.setEquipments(List.of());
        assertEquals(Optional.of(camera), modelManager.findEquipment(camera.getId()));

        modelManager.setClubLogisticsData(original);
        assertEquals(Optional.empty(), modelManager.findEquipment(camera.getId()));
        original.addEquipment(camera);
        modelManager.setClubLogisticsData(original);
        assertEquals(Optional.of(camera), modelManager.findEquipment(camera.getId()));
    }

    @Test
    public void getEquipmentList_modelChanges_updatesExistingView() {
        ObservableList<Equipment> equipment = modelManager.getEquipmentList();
        Equipment camera = new Equipment(new EquipmentId("CAM001"), new EquipmentName("Sony Camera"));
        modelManager.addEquipment(camera);
        assertEquals(List.of(camera), equipment);
        assertThrows(UnsupportedOperationException.class, () -> equipment.remove(camera));
        modelManager.removeEquipment(camera);
        assertTrue(equipment.isEmpty());
        modelManager.addEquipment(camera);
        modelManager.setClubLogisticsData(new ClubLogisticsData());
        assertTrue(equipment.isEmpty());
    }

    @Test
    public void equals() {
        ClubLogisticsData clubLogisticsData =
                new ClubLogisticsDataBuilder().withPerson(ALICE).withPerson(BENSON).build();
        ClubLogisticsData differentClubLogisticsData = new ClubLogisticsData();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(clubLogisticsData, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(clubLogisticsData, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different clubLogisticsData -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentClubLogisticsData, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of(keywords)));
        assertFalse(modelManager.equals(new ModelManager(clubLogisticsData, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(clubLogisticsData, differentUserPrefs)));
    }
}
