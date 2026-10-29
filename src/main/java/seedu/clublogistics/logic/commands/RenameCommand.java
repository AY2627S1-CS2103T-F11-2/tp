package seedu.clublogistics.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.clublogistics.logic.commands.exceptions.CommandException;
import seedu.clublogistics.model.Model;
import seedu.clublogistics.model.equipment.Equipment;
import seedu.clublogistics.model.equipment.EquipmentId;
import seedu.clublogistics.model.equipment.EquipmentName;

/** Corrects an equipment name while retaining its identity and active loan. */
public class RenameCommand extends Command {
    public static final String COMMAND_WORD = "rename";
    public static final String MESSAGE_USAGE = "rename <equipment-id> <equipment-name>";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT =
            "Error: Invalid command format. Usage: " + MESSAGE_USAGE;
    public static final String MESSAGE_SUCCESS = "Equipment %1$s renamed to %2$s.";
    public static final String MESSAGE_EQUIPMENT_NOT_FOUND = "Error: Equipment with ID %1$s does not exist.";

    private final EquipmentId equipmentId;
    private final EquipmentName name;

    /** Creates a correction for the equipment identified by {@code equipmentId}. */
    public RenameCommand(EquipmentId equipmentId, EquipmentName name) {
        this.equipmentId = requireNonNull(equipmentId);
        this.name = requireNonNull(name);
    }

    /** Replaces only the equipment name, including when the record is hidden by the current filter. */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Equipment original = findEquipment(model);
        Equipment renamed = original.withName(name);
        model.setEquipment(original, renamed);
        return new CommandResult(String.format(MESSAGE_SUCCESS, renamed.getId(), renamed.getName()));
    }

    private Equipment findEquipment(Model model) throws CommandException {
        return model.findEquipment(equipmentId)
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_EQUIPMENT_NOT_FOUND, equipmentId)));
    }

    /** Ensures name corrections use the normal save and rollback pipeline. */
    @Override
    public boolean isDataMutating() {
        return true;
    }
}
