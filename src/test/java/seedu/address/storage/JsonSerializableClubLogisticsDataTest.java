package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ClubLogisticsData;

public class JsonSerializableClubLogisticsDataTest {

    /** Rejects stored equipment with duplicate IDs. */
    @Test
    public void toModelType_duplicateEquipmentIds_throwsIllegalValueException() {
        List<JsonAdaptedEquipment> equipment = List.of(
                new JsonAdaptedEquipment("CAM001", "Sony Camera", null, null),
                new JsonAdaptedEquipment("cam001", "Another Camera", null, "John Tan"));
        JsonSerializableClubLogisticsData data = new JsonSerializableClubLogisticsData(equipment);
        assertThrows(IllegalValueException.class, JsonSerializableClubLogisticsData.MESSAGE_DUPLICATE_EQUIPMENT,
                data::toModelType);
    }

    /** Rejects null equipment records in stored data. */
    @Test
    public void toModelType_nullEquipmentRecord_throwsIllegalValueException() {
        JsonSerializableClubLogisticsData data = new JsonSerializableClubLogisticsData(Arrays.asList(
                new JsonAdaptedEquipment("CAM001", "Sony Camera", null, null), null));
        assertThrows(IllegalValueException.class, JsonSerializableClubLogisticsData.MESSAGE_NULL_EQUIPMENT,
                data::toModelType);
    }

    /** Loads an empty register when no equipment array is present. */
    @Test
    public void toModelType_missingLists_returnsEmptyClubLogisticsData() throws Exception {
        JsonSerializableClubLogisticsData data = JsonUtil.fromJsonString("{}", JsonSerializableClubLogisticsData.class);
        assertEquals(new ClubLogisticsData(), data.toModelType());
    }

}
