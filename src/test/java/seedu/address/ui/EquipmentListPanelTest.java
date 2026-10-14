package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import seedu.address.logic.commands.IssueCommand;
import seedu.address.model.ModelManager;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.equipment.EquipmentId;

/** Tests real FXML-loaded equipment cells on the JavaFX application thread. */
public class EquipmentListPanelTest {

    private static final Equipment CAMERA = new Equipment(new EquipmentId("CAM001"), "Sony Camera");
    private static final Equipment MICROPHONE = new Equipment(new EquipmentId("MIC001"), "Wireless Microphone");

    /** Starts JavaFX once and waits for toolkit initialization without sleeping. */
    @BeforeAll
    public static void startJavaFx() throws InterruptedException {
        CountDownLatch started = new CountDownLatch(1);
        Runnable initialize = () -> {
            Platform.setImplicitExit(false);
            started.countDown();
        };
        try {
            Platform.startup(initialize);
        } catch (IllegalStateException alreadyStarted) {
            Platform.runLater(initialize);
        }
        assertTrue(started.await(20, TimeUnit.SECONDS), "JavaFX toolkit did not start");
    }

    /** Verifies the card factory shows ID, name, and availability for a new item. */
    @Test
    public void card_availableEquipment_showsIdentityAndAvailability() throws Exception {
        runOnFxThread(() -> {
            ObservableList<Equipment> items = FXCollections.observableArrayList(CAMERA);
            ListView<Equipment> view = getListView(new EquipmentListPanel(items));
            assertEquals(items, view.getItems());
            assertEquals(List.of("CAM001", "Sony Camera", "Available"), cardText(createCell(view)));
        });
    }

    /** Verifies issuing through the real model refreshes the existing displayed cell. */
    @Test
    public void card_issueCommand_refreshesCurrentBorrower() throws Exception {
        runOnFxThread(() -> {
            ModelManager model = new ModelManager();
            model.addEquipment(CAMERA);
            EquipmentListPanel panel = new EquipmentListPanel(model.getFilteredEquipmentList());
            ListCell<Equipment> cell = createCell(getListView(panel));
            new IssueCommand(new EquipmentId("cam001"), new BorrowerName("John Tan")).execute(model);
            assertEquals(List.of("CAM001", "Sony Camera", "Issued to: John Tan"), cardText(cell));
        });
    }

    /** Verifies a recycled cell does not retain a previous holder or stale graphics. */
    @Test
    public void card_reusedAndEmptied_clearsPreviousBorrowerAndGraphic() throws Exception {
        runOnFxThread(() -> {
            ObservableList<Equipment> items = FXCollections.observableArrayList(
                    CAMERA.issueTo(new BorrowerName("John Tan")), MICROPHONE);
            ListCell<Equipment> cell = createCell(getListView(new EquipmentListPanel(items)));
            assertEquals(List.of("CAM001", "Sony Camera", "Issued to: John Tan"), cardText(cell));
            cell.updateIndex(1);
            assertEquals(List.of("MIC001", "Wireless Microphone", "Available"), cardText(cell));
            items.set(1, null);
            assertNull(cell.getGraphic());
            assertNull(cell.getText());
            items.clear();
            assertTrue(cell.isEmpty());
            assertNull(cell.getGraphic());
            assertNull(cell.getText());
        });
    }

    @SuppressWarnings("unchecked")
    private static ListView<Equipment> getListView(EquipmentListPanel panel) {
        return (ListView<Equipment>) panel.getRoot().lookup("#equipmentListView");
    }

    private static ListCell<Equipment> createCell(ListView<Equipment> view) {
        ListCell<Equipment> cell = view.getCellFactory().call(view);
        cell.updateListView(view);
        cell.updateIndex(0);
        return cell;
    }

    private static List<String> cardText(ListCell<Equipment> cell) {
        HBox card = (HBox) cell.getGraphic();
        VBox details = (VBox) card.getChildren().getFirst();
        return details.getChildren().stream().map(node -> ((Label) node).getText()).toList();
    }

    private static void runOnFxThread(FxAction action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            action.run();
            return null;
        });
        Platform.runLater(task);
        task.get(20, TimeUnit.SECONDS);
    }

    /** An assertion action that may execute a command throwing a checked exception. */
    private interface FxAction {
        void run() throws Exception;
    }
}
