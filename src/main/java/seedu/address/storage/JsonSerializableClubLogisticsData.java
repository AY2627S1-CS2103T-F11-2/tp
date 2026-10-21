package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.ClubLogisticsData;
import seedu.address.model.ReadOnlyClubLogisticsData;
import seedu.address.model.equipment.Equipment;

/**
 * An immutable snapshot of ClubLogistics data that is serializable to JSON.
 */
@JsonRootName(value = "clublogistics")
class JsonSerializableClubLogisticsData {

    public static final String MESSAGE_DUPLICATE_EQUIPMENT = "Equipment list contains duplicate equipment ID(s).";
    public static final String MESSAGE_NULL_EQUIPMENT = "Equipment list contains a missing equipment record.";

    private final List<JsonAdaptedEquipment> equipment = new ArrayList<>();

    /** Constructs a snapshot containing the given equipment records. */
    @JsonCreator
    public JsonSerializableClubLogisticsData(@JsonProperty("equipment") List<JsonAdaptedEquipment> equipment) {
        if (equipment != null) {
            this.equipment.addAll(equipment);
        }
    }

    /**
     * Converts a given {@code ReadOnlyClubLogisticsData} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableClubLogisticsData}.
     */
    public JsonSerializableClubLogisticsData(ReadOnlyClubLogisticsData source) {
        equipment.addAll(source.getEquipmentList().stream()
                .map(JsonAdaptedEquipment::new).collect(Collectors.toList()));
    }

    /**
     * Converts this ClubLogistics data into the model's {@code ClubLogisticsData} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public ClubLogisticsData toModelType() throws IllegalValueException {
        ClubLogisticsData clubLogisticsData = new ClubLogisticsData();
        for (JsonAdaptedEquipment jsonAdaptedEquipment : equipment) {
            if (jsonAdaptedEquipment == null) {
                throw new IllegalValueException(MESSAGE_NULL_EQUIPMENT);
            }
            Equipment item = jsonAdaptedEquipment.toModelType();
            if (clubLogisticsData.hasEquipment(item)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_EQUIPMENT);
            }
            clubLogisticsData.addEquipment(item);
        }
        return clubLogisticsData;
    }

}
