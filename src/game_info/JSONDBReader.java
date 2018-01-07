package game_info;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class JSONDBReader {
    private final String rawString;
    private final int docCount;
    private List<GameInfoBuilder> games;

    private boolean isLoadedFine = true;

    public JSONDBReader(Path filePath){

        String JSON_String = "";

        try{
            JSON_String = Files.newBufferedReader(filePath).lines()
                    .collect(Collectors.joining());

        } catch (SecurityException ex) {//セキュリティソフト等に読み込みを阻害されたとき
            System.err.println("File-loading is blocked by security manager");
            ex.printStackTrace();
            isLoadedFine = false;
        } catch (IOException ex) {
            System.err.println("Failed to open " + filePath.toString());
            ex.printStackTrace();
            isLoadedFine = false;
        } catch(Exception ex) {
            System.err.println("Unexpected Exception!");
            ex.printStackTrace();
            isLoadedFine = false;
        }

        rawString = JSON_String;
        List<GameInfoBuilder> builderList = new ArrayList<>();
        int loadedGamesCount = 0;

        for (Object unchecked : new JSONArray(JSON_String)){
            System.out.println(unchecked.getClass());
            System.out.println(unchecked);
            if(unchecked instanceof JSONObject){
                final GameInfoBuilder builder = new GameInfoBuilder((JSONObject) unchecked);
                builderList.add(builder);
                loadedGamesCount++;
            }
            System.err.println();
        }

        games = Collections.unmodifiableList(builderList);

        docCount = loadedGamesCount;

    }

    public final int getDocCount(){return docCount;}

    public final boolean isLoadedFine(){return isLoadedFine;}
    public final String getRawString() {return rawString;}
    public final List<GameRecord> toGameRecordList() {
        return Collections.emptyList();
    }

    public final List<GameEntry> toGameEntryList(){
        if(isLoadedFine){
            return games.stream()
                    .filter(builder -> builder.canBuild())
                    .map(builder -> builder.buildGameEntry())
                    .collect(Collectors.toList());
        }else {
            return Collections.emptyList();
        }
    }

}
