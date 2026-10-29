package seedu.clublogistics.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;

/** Verifies Help includes usable offline syntax while retaining the online guide link. */
public class HelpWindowTest extends JavaFxTest {
    /** Renders the local command reference and preserves the Copy URL action. */
    @Test
    public void help_offlineReference_includesCommandsAndCopyLink() throws Exception {
        runOnFxThread(() -> {
            HelpWindow help = new HelpWindow();
            try {
                help.show();
                assertTrue(help.isShowing());
                help.focus();
                help.hide();
                assertFalse(help.isShowing());
                help.show();
                help.getRoot().getScene().getRoot().applyCss();
                Label reference = (Label) help.getRoot().getScene().lookup("#quickReference");
                for (String command : List.of("add <", "remove <", "issue <", "return <", "find <",
                        "filter <", "list", "clear", "help", "exit", "Up", "Down")) {
                    assertTrue(reference.getText().contains(command), command);
                }
                Label link = (Label) help.getRoot().getScene().lookup("#helpMessage");
                assertTrue(link.getText().contains(HelpWindow.USERGUIDE_URL));
                Button copy = (Button) help.getRoot().getScene().lookup("#copyButton");
                copy.fire();
                assertTrue(Clipboard.getSystemClipboard().getString().equals(HelpWindow.USERGUIDE_URL));
            } finally {
                help.getRoot().close();
            }
        });
    }
}
