import file_checker.FileChecker;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class FileCheckerClassTest {
    private final String dir;
    private final String readableFile;
    private final String notFoundFile;
    private final String notPathStr;

    @Parameters({"dir", "read", "notFound", "notPath"})
    public FileCheckerClassTest(String dir, String read, String notFound, String notPath){
        this.dir = dir;
        readableFile = read;
        notFoundFile = notFound;
        notPathStr = notPath;
    }

    @Test
    public void readableFileTest(){
        final boolean isWorking = new FileChecker(readableFile)
                .onCannotWrite(dummy -> true)
                .check()
                .isPresent();

        assert isWorking;
    }

    @Test
    public void notFoundTest(){
        final boolean isWorking = ! new FileChecker(notFoundFile)
                .check()
                .isPresent();

        assert isWorking;
    }

    @Test
    public void dirTest(){
        final boolean isWorking = ! new FileChecker(dir)
                .check()
                .isPresent();

        assert isWorking;
    }

    @Test
    public void notPathTest(){
        final boolean isWorking = ! new FileChecker(notPathStr)
                .check()
                .isPresent();

        assert isWorking;
    }

    @Test
    @Parameters("notFoundIgnore")
    public void notFoundIgnoreTest(String notFoundIgnore){
        final boolean isWorking = new FileChecker(notFoundIgnore)
                .onNotExists(dummy -> true)
                .check()
                .isPresent();

        assert isWorking;
    }

}
