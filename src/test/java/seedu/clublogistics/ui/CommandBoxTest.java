package seedu.clublogistics.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import seedu.clublogistics.logic.commands.CommandResult;
import seedu.clublogistics.logic.parser.exceptions.ParseException;

/** Checks keyboard history through the command box's actual event handlers. */
public class CommandBoxTest extends JavaFxTest {
    /** Browses commands without executing them and restores the in-progress draft. */
    @Test
    public void history_upDown_restoresDraftWithoutExecuting() throws Exception {
        runOnFxThread(() -> {
            List<String> executed = new ArrayList<>();
            CommandBox box = new CommandBox(text -> {
                executed.add(text);
                return new CommandResult("OK");
            });
            TextField field = (TextField) box.getRoot().lookup("#commandTextField");
            submit(field, "list");
            submit(field, "filter issued");
            field.setText("issue CAM001 Alice");
            press(field, KeyCode.DOWN);
            assertEquals("issue CAM001 Alice", field.getText());
            for (int modifier = 0; modifier < 4; modifier++) {
                field.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.UP,
                        modifier == 0, modifier == 1, modifier == 2, modifier == 3));
                assertEquals("issue CAM001 Alice", field.getText());
            }
            press(field, KeyCode.A);
            press(field, KeyCode.UP);
            assertEquals("filter issued", field.getText());
            press(field, KeyCode.UP);
            press(field, KeyCode.UP);
            assertEquals("list", field.getText());
            press(field, KeyCode.DOWN);
            assertEquals("filter issued", field.getText());
            press(field, KeyCode.DOWN);
            press(field, KeyCode.DOWN);
            assertEquals("issue CAM001 Alice", field.getText());
            assertEquals(List.of("list", "filter issued"), executed);
        });
    }

    /** Failed and blank commands do not displace successful history. */
    @Test
    public void history_failedAndBlankInput_preserveUsefulHistory() throws Exception {
        runOnFxThread(() -> {
            CommandBox box = new CommandBox(text -> {
                if (text.equals("bad")) {
                    throw new ParseException("Invalid");
                }
                return new CommandResult("OK");
            });
            TextField field = (TextField) box.getRoot().lookup("#commandTextField");
            press(field, KeyCode.UP);
            press(field, KeyCode.DOWN);
            assertEquals("", field.getText());
            submit(field, "list");
            submit(field, "bad");
            submit(field, "bad");
            assertTrue(field.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            press(field, KeyCode.UP);
            assertEquals("list", field.getText());
            press(field, KeyCode.DOWN);
            assertEquals("bad", field.getText());
            submit(field, " \t ");
            assertEquals("", field.getText());
            submit(field, "");
            press(field, KeyCode.UP);
            assertEquals("list", field.getText());
        });
    }

    /** Bounds retained history so a long session does not grow memory indefinitely. */
    @Test
    public void history_longSession_keepsMostRecentHundredCommands() throws Exception {
        runOnFxThread(() -> {
            CommandBox box = new CommandBox(text -> new CommandResult("OK"));
            TextField field = (TextField) box.getRoot().lookup("#commandTextField");
            for (int i = 0; i < 105; i++) {
                submit(field, "command " + i);
            }
            for (int i = 0; i < 110; i++) {
                press(field, KeyCode.UP);
            }
            assertEquals("command 5", field.getText());
        });
    }

    private void submit(TextField field, String text) {
        field.setText(text);
        field.fireEvent(new ActionEvent());
    }

    private void press(TextField field, KeyCode code) {
        field.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", code, false, false, false, false));
    }
}
