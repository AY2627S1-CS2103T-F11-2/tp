package seedu.clublogistics;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/** Prevents concurrent application sessions from overwriting the same register. */
final class AppInstanceLock implements AutoCloseable {
    private static final String ALREADY_RUNNING =
            "ClubLogistics is already running in this folder. Close the existing window before starting another.";
    private static final String FOLDER_NOT_WRITABLE = "ClubLogistics must be able to create files in its folder:\n"
            + "%s\nMove clublogistics.jar to a folder you can write to (e.g. Documents) and start it again.";

    private final FileChannel channel;
    private final FileLock lock;

    private AppInstanceLock(FileChannel channel, FileLock lock) {
        this.channel = channel;
        this.lock = lock;
    }

    /** Acquires ownership before any equipment or preference data is loaded. */
    static AppInstanceLock acquire(Path home) throws IOException {
        FileChannel channel = openLockFile(home);
        try {
            FileLock lock = channel.tryLock();
            if (lock == null) {
                throw new IOException(ALREADY_RUNNING);
            }
            return new AppInstanceLock(channel, lock);
        } catch (OverlappingFileLockException e) {
            channel.close();
            throw new IOException(ALREADY_RUNNING, e);
        } catch (IOException | RuntimeException e) {
            channel.close();
            throw e;
        }
    }

    private static FileChannel openLockFile(Path home) throws IOException {
        try {
            return FileChannel.open(home.resolve(".clublogistics.lock"),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new IOException(String.format(FOLDER_NOT_WRITABLE, home), e);
        }
    }

    /** Releases ownership; the OS also releases the lock if the process terminates. */
    @Override
    public void close() throws IOException {
        try {
            if (lock.isValid()) {
                lock.release();
            }
        } finally {
            channel.close();
        }
        // Keep the file: deleting it could let later sessions lock different file identities.
    }
}
