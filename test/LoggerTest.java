import org.testng.annotations.*;
import trivial_logger.LogLevel;
import trivial_logger.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

final class LoggerTest {
    static private final String DEBUG_LEVEL_MSG = "debug level message.";
    static private final String INFO_LEVEL_MSG = "info level message.";
    static private final String WARN_LEVEL_MSG = "warn level message.";
    static private final String CRITICAL_LEVEL_MSG = "critical level message.";

    private final PrintStream defaultSTDERR = System.err;
    private final STDERRSnatcher snatcher = new STDERRSnatcher();

    @BeforeClass
    private void snatch(){
        System.setErr(snatcher);
    }

    @AfterClass
    private void releaseSTDERR(){
        System.setErr(defaultSTDERR);
    }

    @Test(groups = "defaultLogLevel")
    @Parameters("logFile")
    private void simpleUse(String logFile){
        final Path path = Paths.get(logFile);

        try {
            Logger.INST.setLogFile(path);
        }catch (IOException ex){
            ex.printStackTrace();
            assert false;
        }

        Logger.INST.debug(DEBUG_LEVEL_MSG)
                .info(INFO_LEVEL_MSG)
                .warn(DEBUG_LEVEL_MSG)
                .critical(CRITICAL_LEVEL_MSG)
                .flush();

        snatcher.readLine();
        snatcher.readLine();
        snatcher.readLine();
    }

    @Test(groups = "defaultLogLevel")
    private void simplestUse(){
        Logger.INST.debug(DEBUG_LEVEL_MSG)
                .info(INFO_LEVEL_MSG)
                .warn(WARN_LEVEL_MSG)
                .critical(CRITICAL_LEVEL_MSG);

        assert cutTime(snatcher.readLine()).equals('[' + LogLevel.INFO.name() + "] " + INFO_LEVEL_MSG);
        assert cutTime(snatcher.readLine()).equals('[' + LogLevel.WARN.name() + "] " + WARN_LEVEL_MSG);
        assert cutTime(snatcher.readLine()).equals('[' + LogLevel.CRITICAL.name() + "] " + CRITICAL_LEVEL_MSG);
    }

    @Test(groups = "defaultLogLevel")
    private void lambdaUse(){
        Logger.INST.debug(() -> DEBUG_LEVEL_MSG)
                .info(() -> INFO_LEVEL_MSG)
                .warn(() -> WARN_LEVEL_MSG)
                .critical(() -> CRITICAL_LEVEL_MSG);

        assert cutTime(snatcher.readLine()).equals('[' + LogLevel.INFO.name() + "] " + INFO_LEVEL_MSG);
        assert cutTime(snatcher.readLine()).equals('[' + LogLevel.WARN.name() + "] " + WARN_LEVEL_MSG);
        assert cutTime(snatcher.readLine()).equals('[' + LogLevel.CRITICAL.name() + "] " + CRITICAL_LEVEL_MSG);
    }


    @Test(dependsOnGroups = "defaultLogLevel")
    private void changeLogLevel(){
        Logger.INST.setCurrentLogLevel(LogLevel.DEBUG);
        Logger.INST.debug(DEBUG_LEVEL_MSG)
                .info(INFO_LEVEL_MSG)
                .warn(WARN_LEVEL_MSG)
                .critical(CRITICAL_LEVEL_MSG);

        assert cutTime(snatcher.readLine()).equals('[' + LogLevel.DEBUG.name() + "] " + DEBUG_LEVEL_MSG);
        assert cutTime(snatcher.readLine()).equals('[' + LogLevel.INFO.name() + "] " + INFO_LEVEL_MSG);
        assert cutTime(snatcher.readLine()).equals('[' + LogLevel.WARN.name() + "] " + WARN_LEVEL_MSG);
        assert cutTime(snatcher.readLine()).equals('[' + LogLevel.CRITICAL.name() + "] " + CRITICAL_LEVEL_MSG);
    }

    private static String cutTime(String line){
        return line.substring(line.indexOf(' ') + 1);
    }
}
