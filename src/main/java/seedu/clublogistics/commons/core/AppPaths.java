package seedu.clublogistics.commons.core;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Keeps packaged application files inside the directory containing the JAR. */
public final class AppPaths {
    private static final Path HOME_FOLDER = findHomeFolder();

    private AppPaths() {}

    /** Returns the JAR directory, or the working directory during development class launches. */
    public static Path getHomeFolder() {
        return HOME_FOLDER;
    }

    private static Path findHomeFolder() {
        try {
            Path codeLocation = Path.of(AppPaths.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).toAbsolutePath().normalize();
            return Files.isRegularFile(codeLocation) ? codeLocation.getParent() : Path.of("").toAbsolutePath();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Cannot determine the ClubLogistics home folder.", e);
        }
    }
}
