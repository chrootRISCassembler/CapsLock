import game_info.GameEntry;
import game_info.JSONDBReader;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestValidJSON {
    private static final Path DB_PATH = Paths.get("./valid.json");

    private JSONDBReader reader = new JSONDBReader(DB_PATH);
    private List<GameEntry> entryList;
    private GameEntry entry;

    @Test(groups = {"normal"})
    public void loadJSONDB(){
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
