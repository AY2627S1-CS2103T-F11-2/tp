package seedu.clublogistics.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import seedu.clublogistics.commons.core.GuiSettings;
import seedu.clublogistics.logic.LogicManager;
import seedu.clublogistics.logic.commands.exceptions.CommandException;
import seedu.clublogistics.logic.parser.exceptions.ParseException;
import seedu.clublogistics.model.ModelManager;
import seedu.clublogistics.storage.JsonClubLogisticsStorage;
import seedu.clublogistics.storage.JsonUserPrefsStorage;
import seedu.clublogistics.storage.StorageManager;

public class MainWindowTest extends JavaFxTest {
    @TempDir
    public Path testFolder;

    /** Invalid saved positions must be discarded before a native window is shown. */
    @Test
    public void constructor_offScreenCoordinates_usesDefaultPosition() throws Exception {
        runOnFxThread(() -> {
            ModelManager model = new ModelManager();
            model.setGuiSettings(new GuiSettings(740, 600, Integer.MAX_VALUE, Integer.MAX_VALUE));
            Stage stage = createWindow(model);
            try {
                assertTrue(Double.isNaN(stage.getX()));
                assertTrue(Double.isNaN(stage.getY()));
            } finally {
                stage.close();
            }
        });
    }

    /** Unsupported dimensions must not reach the native windowing API. */
    @Test
    public void constructor_invalidDimensions_usesUsableSize() throws Exception {
        runOnFxThread(() -> {
            for (double invalid : List.of(Double.NaN, Double.POSITIVE_INFINITY, -1.0, 0.0, Double.MAX_VALUE)) {
                ModelManager model = new ModelManager();
                model.setGuiSettings(new GuiSettings(invalid, invalid, 0, 0));
                Stage stage = createWindow(model);
                try {
                    assertTrue(Double.isFinite(stage.getWidth()) && stage.getWidth() > 0);
                    assertTrue(Double.isFinite(stage.getHeight()) && stage.getHeight() > 0);
                    assertTrue(stage.getWidth() <= Screen.getPrimary().getVisualBounds().getWidth());
                    assertTrue(stage.getHeight() <= Screen.getPrimary().getVisualBounds().getHeight());
                } finally {
                    stage.close();
                }
            }
        });
    }

    /** A valid saved position on the current screen is retained. */
    @Test
    public void constructor_visibleCoordinates_preservesPosition() throws Exception {
        runOnFxThread(() -> {
            int x = (int) Screen.getPrimary().getVisualBounds().getMinX() + 20;
            int y = (int) Screen.getPrimary().getVisualBounds().getMinY() + 20;
            ModelManager model = new ModelManager();
            model.setGuiSettings(new GuiSettings(500, 600, x, y));
            Stage stage = createWindow(model);
            try {
                assertEquals(x, stage.getX());
                assertEquals(y, stage.getY());
            } finally {
                stage.close();
            }
        });
    }

    /** Command diagnostics must retain outcomes without recording input or borrower-bearing feedback. */
    @Test
    public void executeCommand_borrowerWorkflows_doNotLogPersonalData() throws Exception {
        runOnFxThread(() -> {
            List<String> messages = new ArrayList<>();
            Logger logger = Logger.getLogger("clublogistics");
            Handler capture = new Handler() {
                /** Captures diagnostic messages. */
                @Override
                public void publish(LogRecord record) {
                    messages.add(record.getMessage());
                }

                /** No buffered output. */
                @Override
                public void flush() {
                }

                /** No resources to release. */
                @Override
                public void close() {
                }
            };
            Stage stage = new Stage();
            logger.addHandler(capture);
            try {
                Path dataFile = testFolder.resolve("privacy.json");
                StorageManager storage = new StorageManager(new JsonClubLogisticsStorage(dataFile),
                        new JsonUserPrefsStorage(testFolder.resolve("prefs.json")));
                MainWindow window = new MainWindow(stage, new LogicManager(new ModelManager(), storage), dataFile);
                window.fillInnerParts();
                var execute = MainWindow.class.getDeclaredMethod("executeCommand", String.class);
                execute.setAccessible(true);
                for (String command : List.of("add CAM001 Camera", "issue CAM001 Alpha Privacy Tester",
                        "issue CAM001 Other Private Borrower", "remove CAM001", "find Alpha Privacy Tester",
                        "return CAM001", "issue CAM001 Invalid Private 123", "unknown Alpha Privacy Tester")) {
                    try {
                        execute.invoke(window, command);
                    } catch (InvocationTargetException e) {
                        assertTrue(e.getCause() instanceof CommandException
                                || e.getCause() instanceof ParseException);
                    }
                }
                String diagnostics = String.join("\n", messages);
                for (String personal : List.of("Alpha", "Privacy", "Tester", "Other", "Borrower", "Invalid")) {
                    assertTrue(!diagnostics.contains(personal), diagnostics);
                }
                assertTrue(diagnostics.contains("IssueCommand"), diagnostics);
                assertTrue(diagnostics.contains("completed"), diagnostics);
                assertTrue(diagnostics.contains("failed"), diagnostics);
            } finally {
                logger.removeHandler(capture);
                stage.close();
            }
        });
    }

    private Stage createWindow(ModelManager model) {
        Path dataFile = testFolder.resolve("window-data.json");
        StorageManager storage = new StorageManager(new JsonClubLogisticsStorage(dataFile),
                new JsonUserPrefsStorage(testFolder.resolve("window-prefs.json")));
        Stage stage = new Stage();
        new MainWindow(stage, new LogicManager(model, storage), dataFile);
        return stage;
    }

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
                logic.execute("clear force");
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
