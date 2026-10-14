package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class MainWindowTest extends JavaFxTest {
    @TempDir
    public Path testFolder;

    @Test
    public void equipmentCommands_updateRenderedEquipmentPanel() throws Exception {
        runOnFxThread(() -> {
            ModelManager model = new ModelManager();
            Path dataFile = testFolder.resolve("data.json");
            StorageManager storage = new StorageManager(new JsonAddressBookStorage(dataFile),
                    new JsonUserPrefsStorage(testFolder.resolve("prefs.json")));
            LogicManager logic = new LogicManager(model, storage);
            Stage stage = new Stage();
            try {
                MainWindow window = new MainWindow(stage, logic, dataFile);
                window.fillInnerParts();
                stage.show();
                Parent root = stage.getScene().getRoot();
                assertNotNull(root.lookup("#personListView"));
                ListView<?> equipment = (ListView<?>) root.lookup("#equipmentListView");
                assertNotNull(equipment);
                logic.execute("add-equipment CAM001 Sony Camera");
                root.applyCss();
                root.layout();
                assertEquals(model.getEquipmentList(), equipment.getItems());
                for (String text : List.of("CAM001", "Sony Camera", "Status: Available")) {
                    assertTrue(equipment.lookupAll(".label").stream()
                            .anyMatch(node -> node instanceof Label label && text.equals(label.getText())),
                            "Missing equipment label: " + text);
                }
                logic.execute("remove cam001");
                assertTrue(equipment.getItems().isEmpty());
                logic.execute("add-equipment MIC001 Microphone");
                logic.execute("clear");
                assertTrue(equipment.getItems().isEmpty());
            } finally {
                stage.close();
            }
        });
    }
}
