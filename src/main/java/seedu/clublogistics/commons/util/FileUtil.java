package seedu.clublogistics.commons.util;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

/**
 * Writes and reads files
 */
public class FileUtil {

    private static final String CHARSET = "UTF-8";

    /**
     * Creates a file if it does not exist along with its missing parent directories.
     * @throws IOException if the file or directory cannot be created.
     */
    public static void createIfMissing(Path file) throws IOException {
        if (Files.exists(file)) {
            return;
        }

        createParentDirsOfFile(file);

        Files.createFile(file);
    }

    /**
     * Creates parent directories of file if it has a parent directory
     */
    private static void createParentDirsOfFile(Path file) throws IOException {
        Path parentDir = file.getParent();

        if (parentDir != null) {
            Files.createDirectories(parentDir);
        }
    }

    /**
     * Assumes file exists
     */
    public static String readFromFile(Path file) throws IOException {
        return new String(Files.readAllBytes(file), CHARSET);
    }

    /**
     * Writes a complete replacement beside the destination, then atomically replaces it.
     * Creates missing parent folders and rejects read-only destinations.
     *
     * @throws IOException if staging or atomic replacement fails. No direct-write fallback is used.
     */
    public static void writeToFile(Path file, String content) throws IOException {
        requireNonNull(file);
        requireNonNull(content);
        Path destination = file.toAbsolutePath();
        if (Files.exists(destination) && !Files.isWritable(destination)) {
            throw new AccessDeniedException(destination.toString());
        }
        createParentDirsOfFile(destination);
        Path staged = Files.createTempFile(destination.getParent(), ".clublogistics-", ".tmp");
        try {
            writeAndFlush(staged, content);
            Files.move(staged, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | RuntimeException failure) {
            discardStagedFile(staged, failure);
            throw failure;
        }
    }

    private static void writeAndFlush(Path staged, String content) throws IOException {
        Files.write(staged, content.getBytes(CHARSET));
        try (FileChannel channel = FileChannel.open(staged, StandardOpenOption.WRITE)) {
            channel.force(true);
        }
    }

    private static void discardStagedFile(Path staged, Exception failure) {
        try {
            Files.deleteIfExists(staged);
        } catch (IOException cleanupFailure) {
            failure.addSuppressed(cleanupFailure);
        }
    }

}
