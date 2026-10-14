package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;

import javafx.application.Platform;

/** Runs UI assertions on the JavaFX Application Thread. Requires a graphical display or Xvfb. */
public abstract class JavaFxTest {
    private static boolean initialized;

    /** Starts JavaFX once for all UI test classes in this process. */
    @BeforeAll
    public static synchronized void initializeJavaFx() throws Exception {
        if (initialized) {
            return;
        }
        CountDownLatch started = new CountDownLatch(1);
        Platform.startup(() -> {
            // Keep JavaFX running when a test closes its last window.
            Platform.setImplicitExit(false);
            started.countDown();
        });
        assertTrue(started.await(10, TimeUnit.SECONDS), "JavaFX did not start in time");
        initialized = true;
    }

    /** Waits for UI assertions and propagates their failures to JUnit. */
    protected void runOnFxThread(UiAction action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            action.run();
            return null;
        });
        Platform.runLater(task);
        try {
            task.get(10, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof Error error) {
                throw error;
            }
            if (e.getCause() instanceof Exception exception) {
                throw exception;
            }
            throw new AssertionError(e.getCause());
        }
    }

    /** A UI operation that may execute commands or read storage. */
    @FunctionalInterface
    protected interface UiAction {
        void run() throws Exception;
    }
}
