package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ClubLogisticsData;
import seedu.address.model.ReadOnlyClubLogisticsData;
import seedu.address.model.equipment.BorrowerName;
import seedu.address.model.equipment.EquipmentId;

public class ClubLogisticsStorageCompatibilityTest {
    @TempDir
    public Path testFolder;

    @Test
    public void readAndSave_legacyFile_preservesRecordsAndJsonFields() throws Exception {
        Path file = testFolder.resolve("addressbook.json");
        Files.writeString(file, """
                {
                  "persons": [{"name": "John Tan", "phone": "98765432",
                    "email": "john@example.com", "address": "Main Street", "tags": []}],
                  "equipment": [
                    {"id": "CAM001", "name": "Sony Camera", "status": "AVAILABLE"},
                    {"id": "MIC001", "name": "Microphone", "status": "ISSUED", "issuedTo": "John Tan"}
                  ]
                }
                """);
        JsonClubLogisticsStorage storage = new JsonClubLogisticsStorage(file);
        ReadOnlyClubLogisticsData loaded = storage.readClubLogisticsData().orElseThrow();
        assertEquals("John Tan", loaded.getPersonList().get(0).getName().fullName);
        assertEquals(2, loaded.getEquipmentList().size());
        assertFalse(loaded.getEquipmentList().get(0).isIssued());
        assertEquals(new EquipmentId("MIC001"), loaded.getEquipmentList().get(1).getId());
        assertEquals(new BorrowerName("John Tan"), loaded.getEquipmentList().get(1).getIssuedTo().orElseThrow());

        storage.saveClubLogisticsData(loaded);
        assertEquals(new ClubLogisticsData(loaded),
                new ClubLogisticsData(storage.readClubLogisticsData().orElseThrow()));
        assertEquals(Set.of("persons", "equipment"), JsonUtil.readJsonFile(file, java.util.Map.class)
                .orElseThrow().keySet());
    }
}
