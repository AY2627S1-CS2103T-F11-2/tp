package seedu.clublogistics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Checks the actual packaged application in a separate JVM and disposable folders. */
public class PackagedAppStorageTest {
    @TempDir
    public Path testFolder;

    /** The launch directory must not receive application data outside the JAR's home. */
    @Test
    public void startup_fromOtherDirectory_keepsFilesBesideJar() throws Exception {
        Path appHome = Files.createDirectory(testFolder.resolve("application home"));
        Path launchFolder = Files.createDirectory(testFolder.resolve("unrelated folder"));
        runPackagedApp(appHome, launchFolder);

        assertTrue(Files.exists(appHome.resolve("preferences.json")));
        assertTrue(Files.exists(appHome.resolve("data/clublogistics.json")));
        assertTrue(Files.exists(appHome.resolve("clublogistics.log.0")));
        try (var files = Files.list(launchFolder)) {
            assertEquals(0, files.count());
        }
    }

    /** Failure to create a log file must not prevent normal startup and saving. */
    @Test
    public void startup_logPathUnavailable_usesConsoleLogging() throws Exception {
        Path appHome = Files.createDirectory(testFolder.resolve("application home"));
        Files.createDirectory(appHome.resolve("clublogistics.log.0"));
        runPackagedApp(appHome, appHome);

        assertTrue(Files.exists(appHome.resolve("preferences.json")));
        assertTrue(Files.exists(appHome.resolve("data/clublogistics.json")));
    }

    /** FileHandler must not interpret a literal folder name as its user-home pattern. */
    @Test
    public void startup_percentHomeInFolderName_keepsLogsBesideJar() throws Exception {
        assertLiteralLogFolder("application %h home");
    }

    /** FileHandler must not interpret a literal folder name as its temporary-folder pattern. */
    @Test
    public void startup_percentTempInFolderName_keepsLogsBesideJar() throws Exception {
        assertLiteralLogFolder("application %t home");
    }

    private void assertLiteralLogFolder(String name) throws Exception {
        Path appHome = Files.createDirectory(testFolder.resolve(name));
        Path launchFolder = Files.createDirectory(testFolder.resolve("unrelated folder"));
        runPackagedApp(appHome, launchFolder);
        assertTrue(Files.exists(appHome.resolve("clublogistics.log.0")));
        try (var files = Files.list(testFolder.resolve("user-home"))) {
            assertEquals(0, files.count());
        }
        try (var files = Files.list(testFolder.resolve("java-temp"))) {
            assertEquals(0, files.count());
        }
    }

    private void runPackagedApp(Path appHome, Path launchFolder) throws Exception {
        Path jar = appHome.resolve("club logistics.jar");
        Files.copy(Path.of("build/libs/clublogistics.jar"), jar);
        Path testClasses = Path.of(PackagedAppStorageTest.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI());
        String executable = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
        Path java = Path.of(System.getProperty("java.home"), "bin", executable);
        Path userHome = Files.createDirectories(testFolder.resolve("user-home"));
        Path javaTemp = Files.createDirectories(testFolder.resolve("java-temp"));
        Path output = testFolder.resolve("process-output.txt");
        Process process = new ProcessBuilder(java.toString(), "-Duser.home=" + userHome,
                "-Djava.io.tmpdir=" + javaTemp, "-cp",
                testClasses + File.pathSeparator + jar, StartupProbe.class.getName())
                .directory(launchFolder.toFile()).redirectErrorStream(true).redirectOutput(output.toFile()).start();
        try {
            assertTrue(process.waitFor(20, TimeUnit.SECONDS), "Packaged startup timed out");
            assertEquals(0, process.exitValue(), () -> readOutput(output));
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly().waitFor(5, TimeUnit.SECONDS);
            }
        }
    }

    private String readOutput(Path output) {
        try {
            return Files.readString(output);
        } catch (java.io.IOException e) {
            return e.toString();
        }
    }

    /** Exercises startup, a saved command and shutdown using application classes from the JAR. */
    public static class StartupProbe {
        /** Runs without opening a graphical stage. */
        public static void main(String[] args) throws Exception {
            MainApp app = new MainApp();
            app.init();
            app.logic.execute("add TEST001 Tripod");
            app.stop();
        }
    }
}
