package seedu.clublogistics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

public class MainAppTest {
    /** Verifies that the application uses its own data file. */
    @Test
    public void dataFilePath_usesClubLogisticsFile() throws Exception {
        Field dataFilePath = MainApp.class.getDeclaredField("CLUB_LOGISTICS_FILE_PATH");
        dataFilePath.setAccessible(true);

        assertEquals(Paths.get("data", "clublogistics.json"), dataFilePath.get(null));
    }
}
