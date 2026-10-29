package seedu.clublogistics.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ObservableList;
import seedu.clublogistics.commons.core.GuiSettings;
import seedu.clublogistics.commons.core.LogsCenter;
import seedu.clublogistics.logic.commands.Command;
import seedu.clublogistics.logic.commands.CommandResult;
import seedu.clublogistics.logic.commands.exceptions.CommandException;
import seedu.clublogistics.logic.parser.ClubLogisticsParser;
import seedu.clublogistics.logic.parser.exceptions.ParseException;
import seedu.clublogistics.model.ClubLogisticsData;
import seedu.clublogistics.model.Model;
import seedu.clublogistics.model.ReadOnlyClubLogisticsData;
import seedu.clublogistics.model.equipment.Equipment;
import seedu.clublogistics.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final ClubLogisticsParser clubLogisticsParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        clubLogisticsParser = new ClubLogisticsParser();
    }

    /** {@inheritDoc} */
    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        Command command = clubLogisticsParser.parseCommand(commandText);
        boolean mutatesData = command.isDataMutating();
        ReadOnlyClubLogisticsData previousData = mutatesData
                ? new ClubLogisticsData(model.getClubLogisticsData()) : null;
        CommandResult commandResult = command.execute(model);

        if (mutatesData) {
            saveDataOrRestore(previousData);
        }

        return commandResult;
    }

    private void saveDataOrRestore(ReadOnlyClubLogisticsData previousData) throws CommandException {
        try {
            storage.saveClubLogisticsData(model.getClubLogisticsData());
        } catch (AccessDeniedException e) {
            model.setClubLogisticsData(previousData);
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            model.setClubLogisticsData(previousData);
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }
    }

    /** {@inheritDoc} */
    @Override
    public ObservableList<Equipment> getEquipmentList() {
        return model.getEquipmentList();
    }

    /** Returns the model's persistent view description. */
    @Override
    public ReadOnlyStringProperty equipmentFilterDescriptionProperty() {
        return model.equipmentFilterDescriptionProperty();
    }

    /** {@inheritDoc} */
    @Override
    public ObservableList<Equipment> getFilteredEquipmentList() {
        return model.getFilteredEquipmentList();
    }

    /** {@inheritDoc} */
    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    /** {@inheritDoc} */
    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
