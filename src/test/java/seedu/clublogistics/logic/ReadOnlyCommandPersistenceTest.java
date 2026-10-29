package seedu.clublogistics.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.clublogistics.logic.commands.exceptions.CommandException;
import seedu.clublogistics.model.ClubLogisticsData;
import seedu.clublogistics.model.ModelManager;
import seedu.clublogistics.model.ReadOnlyClubLogisticsData;
import seedu.clublogistics.model.equipment.BorrowerName;
import seedu.clublogistics.model.equipment.Equipment;
import seedu.clublogistics.model.equipment.EquipmentId;
import seedu.clublogistics.storage.JsonClubLogisticsStorage;
import seedu.clublogistics.storage.JsonUserPrefsStorage;
import seedu.clublogistics.storage.StorageManager;

/** Verifies the resolved PR preserves read-only access and transactional mutations. */
public class ReadOnlyCommandPersistenceTest {
    @TempDir
    public Path home;

    /** Read-only commands must preserve a rejected original after startup's empty-model fallback. */
    @Test
    public void readOnlyCommands_emptyFallback_preserveRejectedDataFile() throws Exception {
        Path file = home.resolve("data.json");
        String rejected = "{ invalid json }";
        Files.writeString(file, rejected);
        LogicManager logic = new LogicManager(new ModelManager(), storage(file));
        for (String command : List.of("list", "filter available", "filter issued", "find Alice", "help", "exit")) {
            logic.execute(command);
            assertEquals(rejected, Files.readString(file), command);
        }
    }

    /** Read-only commands must stay usable when equipment storage cannot be written. */
    @Test
    public void readOnlyCommands_unwritableStorage_neverAttemptSave() throws Exception {
        StorageManager failingStorage = failingStorage();
        LogicManager logic = new LogicManager(new ModelManager(), failingStorage);
        for (String command : List.of("list", "filter available", "filter issued", "find Alice", "help", "exit")) {
            logic.execute(command);
        }
    }

    /** Every mutating command must attempt persistence and restore its original data on failure. */
    @Test
    public void mutations_failedSave_restoreOriginalRegister() {
        for (String command : List.of("add MIC001 Microphone", "issue CAM001 Alice", "return CAM001",
                "remove CAM001", "clear")) {
            ModelManager model = new ModelManager();
            Equipment item = command.startsWith("return")
                    ? new Equipment(new EquipmentId("CAM001"), "Camera", new BorrowerName("John Tan"))
                    : new Equipment(new EquipmentId("CAM001"), "Camera");
            model.addEquipment(item);
            ClubLogisticsData before = new ClubLogisticsData(model.getClubLogisticsData());
            LogicManager logic = new LogicManager(model, failingStorage());
            assertThrows(CommandException.class, () -> logic.execute(command), command);
            assertEquals(before, model.getClubLogisticsData(), command);
        }
    }

    private StorageManager storage(Path file) {
        return new StorageManager(new JsonClubLogisticsStorage(file),
                new JsonUserPrefsStorage(home.resolve("preferences.json")));
    }

    private StorageManager failingStorage() {
        return new StorageManager(new JsonClubLogisticsStorage(home.resolve("unwritable.json")),
                new JsonUserPrefsStorage(home.resolve("preferences.json"))) {
            @Override
            public void saveClubLogisticsData(ReadOnlyClubLogisticsData data) throws IOException {
                throw new IOException("Simulated unavailable storage");
            }
        };
    }
}
