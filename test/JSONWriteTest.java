import methg.commonlib.file_checker.FileChecker;
import game_info.GameInfoBuilder;
import game_info.GameRecord;
import game_info.JSONDBWriter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JSONWriteTest {
    private Path outputFile;

    @Parameters({"file"})
    JSONWriteTest(String file) {
        outputFile = new FileChecker(file)
                .onNotExists(dummy -> {
                    System.err.println(dummy + " is not exist");
                    return true;
                })
                .check()
                .get();
    }

    @Test
    void writeOneGame() throws IOException {
        final GameRecord record = new GameInfoBuilder()
                .setExe(Paths.get("dummyExe"))
                .buildGameRecord();

        final JSONDBWriter writer = new JSONDBWriter(outputFile)
                .add(record);

        try {
            writer.flush();
        }catch (IOException ex){
            System.err.println("Failed to write game record.");
            ex.printStackTrace();
            throw ex;
        }
    }
}
