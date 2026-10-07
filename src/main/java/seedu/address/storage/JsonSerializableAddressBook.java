package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.equipment.Equipment;
import seedu.address.model.person.Person;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_DUPLICATE_EQUIPMENT = "Equipment list contains duplicate equipment ID(s).";
    public static final String MESSAGE_NULL_EQUIPMENT = "Equipment list contains a missing equipment record.";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedEquipment> equipment = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons and equipment.
     * Missing lists are empty so existing address book files can still be loaded.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
            @JsonProperty("equipment") List<JsonAdaptedEquipment> equipment) {
        if (persons != null) {
            this.persons.addAll(persons);
        }
        if (equipment != null) {
            this.equipment.addAll(equipment);
        }
    }

    /**
     * Constructs a {@code JsonSerializableAddressBook} containing only legacy person records.
     */
    public JsonSerializableAddressBook(List<JsonAdaptedPerson> persons) {
        this(persons, null);
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
        equipment.addAll(source.getEquipmentList().stream().map(JsonAdaptedEquipment::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
        }
        for (JsonAdaptedEquipment jsonAdaptedEquipment : equipment) {
            if (jsonAdaptedEquipment == null) {
                throw new IllegalValueException(MESSAGE_NULL_EQUIPMENT);
            }
            Equipment equipmentItem = jsonAdaptedEquipment.toModelType();
            if (addressBook.hasEquipment(equipmentItem.getId())) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_EQUIPMENT);
            }
            addressBook.addEquipment(equipmentItem);
        }
        return addressBook;
    }

}
