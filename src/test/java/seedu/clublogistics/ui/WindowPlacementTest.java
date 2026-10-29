package seedu.clublogistics.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.Point;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.geometry.Rectangle2D;
import seedu.clublogistics.commons.core.GuiSettings;

/** Checks restoration across display arrangements without creating a native window. */
public class WindowPlacementTest {
    private static final Rectangle2D PRIMARY = new Rectangle2D(0, 0, 1280, 800);

    /** A larger secondary display supports its previously saved window size. */
    @Test
    public void resolve_largerSecondaryDisplay_retainsSizeAndPosition() {
        WindowPlacement placement = WindowPlacement.resolve(new GuiSettings(1600, 900, 1300, 20),
                List.of(PRIMARY, new Rectangle2D(1280, 0, 1920, 1080)));
        assertEquals(1600, placement.width());
        assertEquals(900, placement.height());
        assertEquals(new Point(1300, 20), placement.position());
    }

    /** One visible bottom pixel is insufficient to let the coordinator move the window. */
    @Test
    public void resolve_titleBarOffScreen_discardsPosition() {
        WindowPlacement placement = WindowPlacement.resolve(new GuiSettings(740, 600, 20, -599),
                List.of(PRIMARY));
        assertNull(placement.position());
    }

    /** A screen to the left of the primary has valid negative coordinates. */
    @Test
    public void resolve_negativeSecondaryCoordinates_retainsPosition() {
        WindowPlacement placement = WindowPlacement.resolve(new GuiSettings(740, 600, -1200, 20),
                List.of(PRIMARY, new Rectangle2D(-1280, 0, 1280, 800)));
        assertEquals(new Point(-1200, 20), placement.position());
    }

    /** An oversized window with no usable saved display falls back to bounded defaults. */
    @Test
    public void resolve_extremeSettings_usesPrimaryDefaults() {
        WindowPlacement placement = WindowPlacement.resolve(
                new GuiSettings(Double.MAX_VALUE, Double.NaN, Integer.MAX_VALUE, Integer.MAX_VALUE),
                List.of(PRIMARY));
        assertEquals(740, placement.width());
        assertEquals(600, placement.height());
        assertNull(placement.position());
    }
}
