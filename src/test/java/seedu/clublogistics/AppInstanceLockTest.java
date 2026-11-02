package seedu.clublogistics;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Checks that one application home cannot be opened by concurrent sessions. */
public class AppInstanceLockTest {
    @TempDir
    public Path home;

    /** Rejects a second session while the first owns the register. */
    @Test
    public void acquire_sameHome_rejectsSecondInstance() throws IOException {
        try (AppInstanceLock first = AppInstanceLock.acquire(home)) {
            IOException error = assertThrows(IOException.class, () -> AppInstanceLock.acquire(home));
            assertTrue(error.getMessage().contains("already running"));
        }
    }

    /** Keeps separate application homes independent. */
    @Test
    public void acquire_differentHome_allowsBothInstances() throws IOException {
        Path otherHome = Files.createDirectory(home.resolve("other"));
        try (AppInstanceLock first = AppInstanceLock.acquire(home);
                AppInstanceLock second = AppInstanceLock.acquire(otherHome)) {
            assertTrue(Files.exists(otherHome.resolve(".clublogistics.lock")));
        }
    }

    /** Allows restart after shutdown, including a repeated close. */
    @Test
    public void close_releasesLockAndCanBeRepeated() throws IOException {
        AppInstanceLock first = AppInstanceLock.acquire(home);
        first.close();
        first.close();
        assertDoesNotThrow(() -> AppInstanceLock.acquire(home).close());
    }

    /** Reports an unusable lock path without starting a session. */
    @Test
    public void acquire_lockPathIsDirectory_throwsIoException() throws IOException {
        Files.createDirectory(home.resolve(".clublogistics.lock"));
        assertThrows(IOException.class, () -> AppInstanceLock.acquire(home));
    }

    /** A home folder where the lock file cannot be created is explained, not reported as a raw path. */
    @Test
    public void acquire_lockFileCannotBeCreated_explainsWritableFolder() throws IOException {
        Path notAFolder = Files.writeString(home.resolve("not-a-folder"), "");
        IOException error = assertThrows(IOException.class, () -> AppInstanceLock.acquire(notAFolder));
        assertTrue(error.getMessage().contains("must be able to create files"), error.getMessage());
        assertTrue(error.getMessage().contains(notAFolder.toString()), error.getMessage());
    }

    /** A second session is refused visibly instead of only being logged. */
    @Test
    public void launchOrReport_existingInstance_reportsWithoutLaunching() throws IOException {
        List<String> reports = new ArrayList<>();
        try (AppInstanceLock first = AppInstanceLock.acquire(home)) {
            Main.launchOrReport(home, () -> {
                throw new AssertionError("A second session must not load or save the register");
            }, reports::add);
        }
        assertEquals(1, reports.size());
        assertTrue(reports.get(0).contains("already running"));
    }

    /** An unusable home folder is reported visibly instead of the app silently not starting. */
    @Test
    public void launchOrReport_unusableHome_reportsWritableFolder() throws IOException {
        Path notAFolder = Files.writeString(home.resolve("not-a-folder"), "");
        List<String> reports = new ArrayList<>();
        Main.launchOrReport(notAFolder, () -> {
            throw new AssertionError("The app must not start without owning its home folder");
        }, reports::add);
        assertEquals(1, reports.size());
        assertTrue(reports.get(0).contains("must be able to create files"));
    }

    /** A free home launches normally and reports nothing. */
    @Test
    public void launchOrReport_freeHome_launchesWithoutReport() {
        List<String> reports = new ArrayList<>();
        List<String> launches = new ArrayList<>();
        Main.launchOrReport(home, () -> launches.add("launched"), reports::add);
        assertEquals(List.of("launched"), launches);
        assertTrue(reports.isEmpty());
    }

    /** Verifies exclusion across JVMs, not just overlapping locks in one JVM. */
    @Test
    public void acquire_otherProcess_rejectedUntilOwnerCloses() throws Exception {
        try (AppInstanceLock first = AppInstanceLock.acquire(home)) {
            assertEquals(2, runProbe());
        }
        assertEquals(0, runProbe());
    }

    /** Exercises the OS refusal path when a different JVM owns the lock. */
    @Test
    public void acquire_homeOwnedByOtherProcess_throwsIoException() throws Exception {
        Process process = startProbe(true);
        try {
            assertEquals("Application home acquired", process.inputReader().readLine());
            IOException error = assertThrows(IOException.class, () -> AppInstanceLock.acquire(home));
            assertTrue(error.getMessage().contains("already running"));
        } finally {
            process.getOutputStream().close();
            process.destroyForcibly();
            assertTrue(process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS));
        }
        assertDoesNotThrow(() -> AppInstanceLock.acquire(home).close());
    }

    /** Prevents initialization from running when an existing session owns the home. */
    @Test
    public void launchWithLock_existingInstance_doesNotRunApplication() throws IOException {
        try (AppInstanceLock first = AppInstanceLock.acquire(home)) {
            assertThrows(IOException.class, () -> Main.launchWithLock(home, () -> {
                throw new AssertionError("A second session must not load or save the register");
            }));
        }
    }

    /** Holds ownership throughout startup and releases it when the application exits. */
    @Test
    public void launchWithLock_applicationThrows_releasesOwnership() {
        assertThrows(IllegalStateException.class, () -> Main.launchWithLock(home, () -> {
            assertThrows(IOException.class, () -> AppInstanceLock.acquire(home));
            throw new IllegalStateException("Simulated application failure");
        }));
        assertDoesNotThrow(() -> Main.launchWithLock(home, () -> { }));
    }

    /**
     * Checks the packaged entry point refuses a second session before touching its register.
     * The refusal is shown in a dialog that waits for the user, so the process is closed by the test.
     */
    @Test
    public void packagedJar_existingInstance_refusesWithoutLoadingData() throws Exception {
        Path jar = home.resolve("clublogistics.jar");
        Files.copy(Path.of("build", "libs", "clublogistics.jar"), jar);
        Path output = home.resolve("second-instance.txt");
        try (AppInstanceLock first = AppInstanceLock.acquire(home)) {
            Process process = new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                    "-jar", jar.toString()).directory(home.toFile())
                    .redirectErrorStream(true).redirectOutput(output.toFile()).start();
            try {
                String message = awaitOutputContaining(output, "already running", process);
                assertTrue(message.contains("already running"), message);
                assertTrue(Files.notExists(home.resolve("preferences.json")));
                assertTrue(Files.notExists(home.resolve("data/clublogistics.json")));
            } finally {
                process.destroyForcibly();
            }
        }
    }

    /** Waits up to 10 seconds for {@code expected} to appear in the process output, or for the process to end. */
    private static String awaitOutputContaining(Path output, String expected, Process process) throws Exception {
        long deadline = System.nanoTime() + java.util.concurrent.TimeUnit.SECONDS.toNanos(10);
        String text = "";
        while (System.nanoTime() < deadline) {
            text = Files.readString(output, StandardCharsets.UTF_8);
            if (text.contains(expected) || !process.isAlive()) {
                return Files.readString(output, StandardCharsets.UTF_8);
            }
            Thread.sleep(100);
        }
        return text;
    }

    private int runProbe() throws Exception {
        Process process = startProbe(false);
        try {
            assertTrue(process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS), "Lock probe must finish");
            return process.exitValue();
        } finally {
            process.destroyForcibly();
        }
    }

    private Process startProbe(boolean holdLock) throws Exception {
        String separator = System.getProperty("path.separator");
        String classPath = Path.of(AppInstanceLock.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                + separator
                + Path.of(AppInstanceLockProbe.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        ProcessBuilder builder = new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-cp", classPath, AppInstanceLockProbe.class.getName(), home.toString());
        if (holdLock) {
            builder.command().add("hold");
        }
        return builder.redirectErrorStream(true).start();
    }
}
