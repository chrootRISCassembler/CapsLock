import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import trivial_logger.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LoggerTest {
    @Test
    @Parameters("logFile")
    private static void simpleUse(String logFile){
        final Path path = Paths.get(logFile);

        try {
            Logger.INST.setLogFile(path);
        }catch (IOException ex){
            ex.printStackTrace();
            assert false;
        }

        Logger.INST.critical(() -> "This is test msg.");
        Logger.INST.info(() -> "INFO level");

        Logger.INST.flush();
    }
}
