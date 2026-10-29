package seedu.clublogistics.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seedu.clublogistics.logic.commands.CommandResult;
import seedu.clublogistics.logic.commands.exceptions.CommandException;
import seedu.clublogistics.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";
    private static final int MAX_HISTORY_SIZE = 100;

    private final CommandExecutor commandExecutor;
    private final List<String> history = new ArrayList<>();
    private int historyCursor;
    private String draft = "";

    @FXML
    private TextField commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, this::handleHistoryKey);
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.isBlank()) {
            commandTextField.setText("");
            return;
        }

        try {
            commandExecutor.execute(commandText);
            remember(commandText);
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    private void remember(String command) {
        history.add(command);
        if (history.size() > MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
        historyCursor = history.size();
        draft = "";
    }

    private void handleHistoryKey(KeyEvent event) {
        if (event.isControlDown() || event.isAltDown() || event.isMetaDown() || event.isShiftDown()
                || history.isEmpty()) {
            return;
        }
        if (event.getCode() == KeyCode.UP) {
            if (historyCursor == history.size()) {
                draft = commandTextField.getText();
            }
            historyCursor = Math.max(0, historyCursor - 1);
        } else if (event.getCode() == KeyCode.DOWN) {
            if (historyCursor == history.size()) {
                return;
            }
            historyCursor = Math.min(history.size(), historyCursor + 1);
        } else {
            return;
        }
        commandTextField.setText(historyCursor == history.size() ? draft : history.get(historyCursor));
        commandTextField.positionCaret(commandTextField.getLength());
        event.consume();
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.clublogistics.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

}
