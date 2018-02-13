import file_checker.FileChecker;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class FileCheckerClassTest {
    private final String dir;
    private final String readableFile;
    private final String notFoundFile;

    @Parameters({"dir", "read", "notFound"})
    public FileCheckerClassTest(String dir, String read, String notFound){
        this.dir = dir;
        readableFile = read;
        notFoundFile = notFound;
    }

    @Test
    public void ReadableFileTest(){
        final boolean isWorking = new FileChecker(readableFile)
                .OnCannotWrite(dummy -> true)
                .check()
                .isPresent();

        assert isWorking;
    }

    @Test
    public void NotFoundTest(){
        final boolean isWorking = ! new FileChecker(notFoundFile)
                .check()
                .isPresent();

        assert isWorking;
    }

    @Test
    public void DirTest(){
        final boolean isWorking = ! new FileChecker(dir)
                .check()
                .isPresent();

        assert isWorking;
    }
}
