package seedu.clublogistics.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import seedu.clublogistics.logic.commands.IssueCommand;
import seedu.clublogistics.model.ClubLogisticsData;
import seedu.clublogistics.model.ModelManager;
import seedu.clublogistics.model.equipment.BorrowerName;
import seedu.clublogistics.model.equipment.Equipment;
import seedu.clublogistics.model.equipment.EquipmentId;
import seedu.clublogistics.model.equipment.EquipmentName;

public class EquipmentListPanelTest extends JavaFxTest {
    private static final Equipment CAMERA = new Equipment(new EquipmentId("CAM001"), new EquipmentName("Sony Camera"));
    private static final Equipment MIC = new Equipment(new EquipmentId("MIC001"), new EquipmentName("Microphone"));

    @Test
    public void modelChanges_updatePanelItems() throws Exception {
        runOnFxThread(() -> {
            ModelManager model = new ModelManager();
            EquipmentListPanel panel = new EquipmentListPanel(model.getEquipmentList());
            ListView<?> list = (ListView<?>) panel.getRoot().lookup("#equipmentListView");
            assertTrue(list.getItems().isEmpty());
            model.addEquipment(CAMERA);
            model.addEquipment(MIC);
            assertEquals(List.of(CAMERA, MIC), list.getItems());
            model.removeEquipment(CAMERA);
            assertEquals(List.of(MIC), list.getItems());
            model.setClubLogisticsData(new ClubLogisticsData());
            assertTrue(list.getItems().isEmpty());
        });
    }

    @Test
    public void updateItem_reusedCell_replacesCardAndClearsEmptyOrNullItems() throws Exception {
        runOnFxThread(() -> {
            EquipmentListPanel panel = new EquipmentListPanel(new ModelManager().getEquipmentList());
            EquipmentListPanel.EquipmentListViewCell cell = panel.new EquipmentListViewCell();
            cell.updateItem(CAMERA, false);
            assertEquals("CAM001", ((Label) cell.getGraphic().lookup("#equipmentId")).getText());
            cell.updateItem(MIC, false);
            assertEquals("MIC001", ((Label) cell.getGraphic().lookup("#equipmentId")).getText());
            assertEquals("Microphone", ((Label) cell.getGraphic().lookup("#name")).getText());
            cell.setText("Stale text");
            cell.updateItem(MIC, true);
            assertNull(cell.getGraphic());
            assertNull(cell.getText());
            cell.updateItem(CAMERA, false);
            cell.updateItem(null, false);
            assertNull(cell.getGraphic());
            assertNull(cell.getText());
        });
    }

    /** Verifies issuing equipment refreshes the current borrower in an existing cell. */
    @Test
    public void issueCommand_updatesRenderedBorrower() throws Exception {
        runOnFxThread(() -> {
            ModelManager model = new ModelManager();
            model.addEquipment(CAMERA);
            EquipmentListPanel panel = new EquipmentListPanel(model.getFilteredEquipmentList());
            @SuppressWarnings("unchecked")
            ListView<Equipment> list = (ListView<Equipment>) panel.getRoot().lookup("#equipmentListView");
            javafx.scene.control.ListCell<Equipment> cell = list.getCellFactory().call(list);
            cell.updateListView(list);
            cell.updateIndex(0);
            new IssueCommand(new EquipmentId("cam001"), new BorrowerName("John Tan")).execute(model);
            assertEquals("Issued to: John Tan", ((Label) cell.getGraphic().lookup("#borrower")).getText());
        });
    }

    /** The displayed counts remain current even when another command replaces the feedback. */
    @Test
    public void counts_followHiddenAndVisibleChanges() throws Exception {
        runOnFxThread(() -> {
            ModelManager model = new ModelManager();
            EquipmentListPanel panel = new EquipmentListPanel(model.getFilteredEquipmentList(),
                    model.equipmentFilterDescriptionProperty(), model.getEquipmentList());
            Label label = (Label) panel.getRoot().lookup("#filterDescription");
            model.updateFilteredEquipmentList(Equipment::isIssued);
            model.addEquipment(CAMERA);
            assertTrue(label.getText().endsWith("Showing 0 of 1 equipment records."));
            model.setEquipment(CAMERA, CAMERA.issueTo(new BorrowerName("Alice Tan")));
            assertTrue(label.getText().endsWith("Showing 1 of 1 equipment records."));
            model.setEquipment(model.getEquipmentList().get(0), CAMERA);
            assertTrue(label.getText().endsWith("Showing 0 of 1 equipment records."));
            model.removeEquipment(CAMERA);
            assertTrue(label.getText().endsWith("Showing 0 of 0 equipment records."));
        });
    }

    /** Keeps the view description visible when another command replaces result feedback. */
    @Test
    public void viewDescription_tracksFiltersAndListReset() throws Exception {
        runOnFxThread(() -> {
            ModelManager model = new ModelManager();
            EquipmentListPanel panel = new EquipmentListPanel(model.getFilteredEquipmentList(),
                    model.equipmentFilterDescriptionProperty());
            Label label = (Label) panel.getRoot().lookup("#filterDescription");
            seedu.clublogistics.logic.parser.ClubLogisticsParser parser =
                    new seedu.clublogistics.logic.parser.ClubLogisticsParser();
            parser.parseCommand("filter issued").execute(model);
            parser.parseCommand("add CAM001 Camera").execute(model);
            assertTrue(label.getText().contains("Issued"));
            assertTrue(label.getText().contains("list"));
            parser.parseCommand("find Alice").execute(model);
            assertTrue(label.getText().contains("Alice"));
            parser.parseCommand("list").execute(model);
            assertEquals("Showing all equipment\nShowing 1 of 1 equipment records.", label.getText());
        });
    }

}
