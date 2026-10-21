package seedu.clublogistics.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import seedu.clublogistics.logic.LogicManager;
import seedu.clublogistics.model.ModelManager;
import seedu.clublogistics.storage.JsonClubLogisticsStorage;
import seedu.clublogistics.storage.JsonUserPrefsStorage;
import seedu.clublogistics.storage.StorageManager;

public class MainWindowTest extends JavaFxTest {
    @TempDir
    public Path testFolder;

    @Test
    public void equipmentCommands_updateRenderedEquipmentPanel() throws Exception {
        runOnFxThread(() -> {
            ModelManager model = new ModelManager();
            Path dataFile = testFolder.resolve("data.json");
            StorageManager storage = new StorageManager(new JsonClubLogisticsStorage(dataFile),
                    new JsonUserPrefsStorage(testFolder.resolve("prefs.json")));
            LogicManager logic = new LogicManager(model, storage);
            Stage stage = new Stage();
            try {
                MainWindow window = new MainWindow(stage, logic, dataFile);
                window.fillInnerParts();
                stage.show();
                Parent root = stage.getScene().getRoot();
                assertNull(root.lookup("#personListView"));
                ListView<?> equipment = (ListView<?>) root.lookup("#equipmentListView");
                assertNotNull(equipment);
                logic.execute("add CAM001 Sony Camera");
                root.applyCss();
                root.layout();
                assertEquals(model.getEquipmentList(), equipment.getItems());
                for (String text : List.of("CAM001", "Sony Camera", "Available")) {
                    assertTrue(equipment.lookupAll(".label").stream()
                            .anyMatch(node -> node instanceof Label label && text.equals(label.getText())),
                            "Missing equipment label: " + text);
                }
                model.updateFilteredEquipmentList(item -> false);
                assertTrue(equipment.getItems().isEmpty());
                logic.execute("list");
                assertEquals(model.getEquipmentList(), equipment.getItems());
                logic.execute("issue CAM001 John Tan");
                root.applyCss();
                root.layout();
                assertTrue(equipment.lookupAll(".label").stream()
                        .anyMatch(node -> node instanceof Label label
                                && "Issued to: John Tan".equals(label.getText())));
                logic.execute("clear");
                assertTrue(equipment.getItems().isEmpty());
                logic.execute("add CAM001 Sony Camera");
                logic.execute("remove cam001");
                assertTrue(equipment.getItems().isEmpty());
                logic.execute("add MIC001 Microphone");
                logic.execute("clear");
                assertTrue(equipment.getItems().isEmpty());
            } finally {
                stage.close();
            }
        });
    }
}
