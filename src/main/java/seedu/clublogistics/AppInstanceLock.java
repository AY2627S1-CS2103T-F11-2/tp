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

    private final FileChannel channel;
    private final FileLock lock;

    private AppInstanceLock(FileChannel channel, FileLock lock) {
        this.channel = channel;
        this.lock = lock;
    }

    /** Acquires ownership before any equipment or preference data is loaded. */
    static AppInstanceLock acquire(Path home) throws IOException {
        FileChannel channel = FileChannel.open(home.resolve(".clublogistics.lock"),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
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
