package seedu.clublogistics.ui;

import java.awt.Point;
import java.util.List;

import javafx.geometry.Rectangle2D;
import seedu.clublogistics.commons.core.GuiSettings;

/** Resolves saved window geometry against the displays available at startup. */
record WindowPlacement(double width, double height, Point position) {
    static WindowPlacement resolve(GuiSettings saved, List<Rectangle2D> screens) {
        Point position = saved.getWindowCoordinates();
        Rectangle2D savedScreen = position == null ? null : screens.stream()
                .filter(screen -> hasAccessibleTitleBar(screen, position)).findFirst().orElse(null);
        Rectangle2D bounds = savedScreen == null ? screens.getFirst() : savedScreen;
        GuiSettings defaults = new GuiSettings();
        double width = validDimension(saved.getWindowWidth(), defaults.getWindowWidth(), bounds.getWidth());
        double height = validDimension(saved.getWindowHeight(), defaults.getWindowHeight(), bounds.getHeight());
        return new WindowPlacement(width, height, savedScreen == null ? null : position);
    }

    // Keep enough of the top edge visible for the coordinator to drag the window back on screen.
    private static boolean hasAccessibleTitleBar(Rectangle2D screen, Point position) {
        return screen.contains(position.x, position.y, 100, 30);
    }

    private static double validDimension(double saved, double fallback, double maximum) {
        return Double.isFinite(saved) && saved > 0 && saved <= maximum
                ? saved : Math.min(fallback, maximum);
    }
}
