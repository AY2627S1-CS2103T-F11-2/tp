package seedu.clublogistics;

import java.io.IOException;
import java.nio.file.Path;

/** Runs the register lock check in a separate JVM for the regression test. */
public class AppInstanceLockProbe {
    /** Exits with status two if another process already owns this home. */
    public static void main(String[] args) throws IOException {
        try (AppInstanceLock lock = AppInstanceLock.acquire(Path.of(args[0]))) {
            System.out.println("Application home acquired");
            if (args.length > 1) {
                System.in.read();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
    }
}
