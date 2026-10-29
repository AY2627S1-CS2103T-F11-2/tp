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
    private static final int MAX_REPLACE_ATTEMPTS = 20;
    private static final long REPLACE_RETRY_DELAY_MILLIS = 25;

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
            replaceWithRetry(staged, destination);
        } catch (IOException | RuntimeException failure) {
            discardStagedFile(staged, failure);
            throw failure;
        }
    }

    /**
     * Atomically moves {@code staged} over {@code destination}, retrying briefly while access is denied.
     * Windows refuses to replace a file that another process (e.g. antivirus or a sync client) has open,
     * so a short bounded retry lets the save succeed once that process releases the file.
     */
    private static void replaceWithRetry(Path staged, Path destination) throws IOException {
        for (int attempt = 1; ; attempt++) {
            try {
                Files.move(staged, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                return;
            } catch (AccessDeniedException denied) {
                if (attempt >= MAX_REPLACE_ATTEMPTS || Files.isDirectory(destination)) {
                    throw denied;
                }
                pauseBeforeRetry(denied);
            }
        }
    }

    private static void pauseBeforeRetry(AccessDeniedException denied) throws AccessDeniedException {
        try {
            Thread.sleep(REPLACE_RETRY_DELAY_MILLIS);
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw denied;
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
