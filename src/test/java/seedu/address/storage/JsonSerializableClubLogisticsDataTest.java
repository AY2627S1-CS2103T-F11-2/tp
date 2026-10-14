package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ClubLogisticsData;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableClubLogisticsDataTest {

    private static final Path TEST_DATA_FOLDER =
            Paths.get("src", "test", "data", "JsonSerializableClubLogisticsDataTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsClubLogisticsData.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonClubLogisticsData.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonClubLogisticsData.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableClubLogisticsData dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableClubLogisticsData.class).get();
        ClubLogisticsData clubLogisticsDataFromFile = dataFromFile.toModelType();
        ClubLogisticsData typicalPersonsClubLogisticsData = TypicalPersons.getTypicalClubLogisticsData();
        assertEquals(clubLogisticsDataFromFile, typicalPersonsClubLogisticsData);
        assertTrue(clubLogisticsDataFromFile.getEquipmentList().isEmpty());
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableClubLogisticsData dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableClubLogisticsData.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableClubLogisticsData dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableClubLogisticsData.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableClubLogisticsData.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateEquipmentIds_throwsIllegalValueException() {
        List<JsonAdaptedEquipment> equipment = List.of(
                new JsonAdaptedEquipment("CAM001", "Sony Camera", null, null),
                new JsonAdaptedEquipment("cam001", "Another Camera", null, "John Tan"));
        JsonSerializableClubLogisticsData data = new JsonSerializableClubLogisticsData(null, equipment);
        assertThrows(IllegalValueException.class, JsonSerializableClubLogisticsData.MESSAGE_DUPLICATE_EQUIPMENT,
                data::toModelType);
    }

    @Test
    public void toModelType_nullEquipmentRecord_throwsIllegalValueException() {
        JsonSerializableClubLogisticsData data = new JsonSerializableClubLogisticsData(null, Arrays.asList(
                new JsonAdaptedEquipment("CAM001", "Sony Camera", null, null), null));
        assertThrows(IllegalValueException.class, JsonSerializableClubLogisticsData.MESSAGE_NULL_EQUIPMENT,
                data::toModelType);
    }

    @Test
    public void toModelType_missingLists_returnsEmptyClubLogisticsData() throws Exception {
        JsonSerializableClubLogisticsData data = JsonUtil.fromJsonString("{}", JsonSerializableClubLogisticsData.class);
        assertEquals(new ClubLogisticsData(), data.toModelType());
    }

}
