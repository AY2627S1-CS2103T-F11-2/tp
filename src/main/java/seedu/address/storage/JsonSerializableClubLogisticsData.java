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
import seedu.address.model.person.Person;

/**
 * An immutable snapshot of ClubLogistics data that is serializable to JSON.
 */
// Preserve the legacy JSON root name for compatibility.
@JsonRootName(value = "addressbook")
class JsonSerializableClubLogisticsData {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_DUPLICATE_EQUIPMENT = "Equipment list contains duplicate equipment ID(s).";
    public static final String MESSAGE_NULL_EQUIPMENT = "Equipment list contains a missing equipment record.";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedEquipment> equipment = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableClubLogisticsData} with the given people and equipment.
     */
    @JsonCreator
    public JsonSerializableClubLogisticsData(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
            @JsonProperty("equipment") List<JsonAdaptedEquipment> equipment) {
        if (persons != null) {
            this.persons.addAll(persons);
        }
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
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
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
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (clubLogisticsData.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            clubLogisticsData.addPerson(person);
        }
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
