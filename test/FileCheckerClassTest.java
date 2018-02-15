import file_checker.FileChecker;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

class FileCheckerClassTest {

    @Test
    @Parameters("read")
    private static void readableFileTest(String read){
        final boolean isWorking = new FileChecker(read)
                .onCannotWrite(dummy -> true)
                .check()
                .isPresent();

        assert isWorking;
    }

    @Test
    @Parameters("notFound")
    private static void notFoundTest(String notFound){
        final boolean isWorking = ! new FileChecker(notFound)
                .check()
                .isPresent();

        assert isWorking;
    }

    @Test
    @Parameters("dir")
    private static void dirTest(String dir){
        final boolean isWorking = ! new FileChecker(dir)
                .check()
                .isPresent();

        assert isWorking;
    }

    @Test
    @Parameters("notPath")
    private static void notPathTest(String notPath){
        final boolean isWorking = ! new FileChecker(notPath)
                .check()
                .isPresent();

        assert isWorking;
    }

    @Test
    @Parameters("notFoundIgnore")
    private static void notFoundIgnoreTest(String notFoundIgnore){
        final boolean isWorking = new FileChecker(notFoundIgnore)
                .onNotExists(dummy -> true)
                .check()
                .isPresent();

        assert isWorking;
    }

}
