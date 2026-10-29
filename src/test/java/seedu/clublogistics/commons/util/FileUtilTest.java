package seedu.clublogistics.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Checks complete-file replacement and preservation of existing data when saving fails. */
public class FileUtilTest {
    @TempDir
    public Path testFolder;

    /** A reader may see either complete version, but never a truncated intermediate version. */
    @Test
    public void writeToFile_repeatedReplacement_readersSeeOnlyCompleteVersions() throws Exception {
        Path target = testFolder.resolve("equipment.json");
        String previous = "A".repeat(500_000);
        String replacement = "B".repeat(500_000);
        Files.writeString(target, previous);
        AtomicBoolean finished = new AtomicBoolean();
        CountDownLatch started = new CountDownLatch(1);
        try (var executor = Executors.newSingleThreadExecutor()) {
            var reader = executor.submit(() -> {
                started.countDown();
                int reads = 0;
                do {
                    String content = Files.readString(target);
                    assertTrue(content.equals(previous) || content.equals(replacement),
                            "Reader observed incomplete file of length " + content.length());
                    reads++;
                } while (!finished.get());
                return reads;
            });
            try {
                assertTrue(started.await(5, TimeUnit.SECONDS));
                for (int attempt = 0; attempt < 40; attempt++) {
                    FileUtil.writeToFile(target, attempt % 2 == 0 ? replacement : previous);
                }
            } finally {
                finished.set(true);
            }
            assertTrue(reader.get(10, TimeUnit.SECONDS) > 0);
        }
        assertEquals(previous, Files.readString(target));
        assertOnlyTargetRemains(target);
    }

    /**
     * A destination briefly held open by another reader (e.g. antivirus or a sync client) must still be replaced
     * once the reader lets go. Windows refuses to replace a file while it is open, so this exercises the retry.
     */
    @Test
    public void writeToFile_destinationBrieflyOpen_replacesAfterRelease() throws Exception {
        Path target = testFolder.resolve("equipment.json");
        Files.writeString(target, "previous data");
        CountDownLatch opened = new CountDownLatch(1);
        try (var executor = Executors.newSingleThreadExecutor()) {
            var holder = executor.submit(() -> {
                try (FileChannel channel = FileChannel.open(target, StandardOpenOption.READ)) {
                    opened.countDown();
                    Thread.sleep(150);
                }
                return null;
            });
            assertTrue(opened.await(5, TimeUnit.SECONDS));

            FileUtil.writeToFile(target, "replacement");

            holder.get(5, TimeUnit.SECONDS);
        }
        assertEquals("replacement", Files.readString(target));
        assertOnlyTargetRemains(target);
    }

    /** Failed replacement must keep the existing destination and remove temporary content. */
    @Test
    public void writeToFile_destinationIsDirectory_preservesDestinationAndCleansUp() throws Exception {
        Path target = Files.createDirectory(testFolder.resolve("equipment.json"));
        Path original = target.resolve("original.txt");
        Files.writeString(original, "previous data");

        assertThrows(IOException.class, () -> FileUtil.writeToFile(target, "replacement"));

        assertEquals("previous data", Files.readString(original));
        assertOnlyTargetRemains(target);
    }

    /** A missing home subfolder is created without exposing an empty destination file. */
    @Test
    public void writeToFile_missingParent_createsCompleteFile() throws Exception {
        Path target = testFolder.resolve("data/equipment.json");
        FileUtil.writeToFile(target, "complete register");
        assertEquals("complete register", Files.readString(target));
    }

    /** Replacing a file must not bypass the user's read-only protection. */
    @Test
    public void writeToFile_readOnlyDestination_preservesOriginal() throws Exception {
        Path target = testFolder.resolve("equipment.json");
        Files.writeString(target, "previous data");
        assertTrue(target.toFile().setWritable(false, false));
        try {
            assumeTrue(!Files.isWritable(target), "The test account can bypass read-only permissions");
            assertThrows(IOException.class, () -> FileUtil.writeToFile(target, "replacement"));
            assertEquals("previous data", Files.readString(target));
            assertOnlyTargetRemains(target);
        } finally {
            assertTrue(target.toFile().setWritable(true, true));
        }
    }

    /** An unwritable staging folder must not lead to an unsafe direct overwrite. */
    @Test
    public void writeToFile_parentCannotStage_preservesOriginal() throws Exception {
        assumeTrue(Files.getFileStore(testFolder).supportsFileAttributeView("posix"));
        Path parent = Files.createDirectory(testFolder.resolve("data"));
        Path target = parent.resolve("equipment.json");
        Files.writeString(target, "previous data");
        Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(parent);
        Files.setPosixFilePermissions(parent, Set.of(PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_EXECUTE));
        try {
            assumeTrue(!Files.isWritable(parent), "The test account can bypass directory permissions");
            assertThrows(IOException.class, () -> FileUtil.writeToFile(target, "replacement"));
            assertEquals("previous data", Files.readString(target));
        } finally {
            Files.setPosixFilePermissions(parent, permissions);
        }
    }

    private void assertOnlyTargetRemains(Path target) throws IOException {
        try (var files = Files.list(target.getParent())) {
            assertEquals(List.of(target), files.toList());
        }
    }
}
