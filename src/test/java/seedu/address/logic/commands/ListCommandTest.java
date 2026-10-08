package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;
import seedu.address.model.equipment.EquipmentName;

/** Tests that list shows the complete equipment register. */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_emptyRegister_showsEmptyList() {
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(0, model.getFilteredEquipmentList().size());
    }

    @Test
    public void execute_equipmentRegister_showsEveryItem() {
        model.addEquipment(equipment("CAM001", "Sony Camera"));
        model.addEquipment(equipment("MIC001", "Wireless Microphone"));
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(2, model.getFilteredEquipmentList().size());
    }

    @Test
    public void execute_filteredRegister_showsEveryItem() {
        Equipment camera = equipment("CAM001", "Sony Camera");
        Equipment microphone = equipment("MIC001", "Wireless Microphone");
        model.addEquipment(camera);
        model.addEquipment(microphone);
        model.updateFilteredEquipmentList(item -> item.equals(camera));

        new ListCommand().execute(model);

        assertEquals(2, model.getFilteredEquipmentList().size());
    }

    private Equipment equipment(String id, String name) {
        return new Equipment(new EquipmentId(id), new EquipmentName(name));
    }
}
