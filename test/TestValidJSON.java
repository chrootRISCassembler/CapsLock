import game_info.GameEntry;
import game_info.JSONDBReader;
import org.testng.Assert;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestValidJSON {
    private Path JSONPath;
    private JSONDBReader reader;
    private List<GameEntry> entryList;
    private GameEntry entry;

    @BeforeGroups(groups = {"normal"})
    @Parameters("JSONFilePathString")
    public void preparePath(String JSONFilePathString){
        JSONPath = Paths.get(JSONFilePathString);

        if(Files.isReadable(JSONPath)){
            System.out.println("OK, \"" + JSONPath + "\" is readable.");
            System.out.println("Preparation of the test succeeded.");
            return;
        }

        if(Files.isRegularFile(JSONPath)){
            System.out.println(JSONPath + " is a regular file. But cannot read.");
            System.out.println("Check the permission or the owner of the file.");
        }

        if(Files.isDirectory(JSONPath)){
            System.out.println(JSONPath + " is a directory.");
            System.out.println("You specified the wrong path.");
        }

        if(!Files.exists(JSONPath)){
            System.out.println("There is no such file : " + JSONPath);
            System.out.println("You specified the wrong path.");
        }

        System.err.println("Unable to open " + JSONPath);
    }


    @Test(groups = {"normal"})
    public void loadJSONDB(){
        reader = new JSONDBReader(JSONPath);
        Assert.assertTrue(reader.isLoadedFine());
        System.out.println(reader.getDocCount());
    }

    @Test(groups = {"normal"}, dependsOnMethods = { "loadJSONDB" })
    public void getEntryList(){
        entryList = reader.toGameEntryList();
        Assert.assertNotNull(entryList);
        Assert.assertEquals(entryList.size(), 1);
    }

    @Test(groups = {"normal"}, dependsOnMethods = { "getEntryList" })
    public void getAnEntry(){
        entry = entryList.get(0);
        Assert.assertNotNull(entry);
        Assert.assertEquals(entry.getClass(), GameEntry.class);
    }


    @Test(groups = {"normal"}, dependsOnMethods = { "getAnEntry" })
    public void dumpAllField(){
        System.out.println(entry.getUUID());
        Assert.assertNotNull(entry.getUUID());

        System.out.println(entry.getExe());
        Assert.assertNotNull(entry.getExe());

        System.out.println(entry.getLastMod());
        Assert.assertNotNull(entry.getLastMod());

        System.out.println(entry.getDesc());
        Assert.assertNotNull(entry.getDesc());

        System.out.println(entry.getPanel());
        Assert.assertNotNull(entry.getPanel());

        System.out.println(entry.getImageList());
        Assert.assertNotNull(entry.getImageList());

        System.out.println(entry.getMovieList());
        Assert.assertNotNull(entry.getMovieList());

        System.out.println(entry.getGameID());
        Assert.assertNotNull(entry.getGameID());
    }
}
