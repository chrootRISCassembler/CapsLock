package capslock;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * ゲーム1件の情報を格納するクラス.
 * <p>このクラスはイミュータブル.</p>
 */

final class GameCertification {
    private final UUID uuid;
    private final String name;
    private final String description;
    private final Path ExecutablePath;
    private final String version;
    private final Path PanelPath;
    private final List<Path> ImagesPathList;
    private final List<Path> MoviesPathList;
    private final int ID;

    public GameCertification(JSONObject record){
        uuid = UUID.fromString(record.getString("UUID"));
        name = record.getString("name");
        description = record.getString("description");
        ExecutablePath = new File(record.getString("executable")).toPath();
        version = record.getString("version");
        PanelPath = new File(record.getString("panel")).toPath();
        ImagesPathList = BuildImmutableArray(record.getJSONArray("image"));
        MoviesPathList = BuildImmutableArray(record.getJSONArray("movie"));
        ID=Integer.parseInt(record.getString("ID"));
    }

    final UUID getUUID(){return uuid;}
    final String getName(){return name;}
    final String getDescription(){return description;}
    final Path getExecutablePath(){return ExecutablePath;}
    final String getVersion(){return version;}
    final Path getPanelPath(){return PanelPath;}
    final List<Path> getImagesPathList(){return ImagesPathList;}
    final List<Path> getMoviePathList(){return MoviesPathList;}
    final int getGameID() {return ID;}

    final void dump(){
        System.out.println(uuid.toString());
        System.out.println(name);
        System.out.println(description);
        System.out.println(ExecutablePath.toString());
        System.out.println(version);
        System.out.println(PanelPath);
        System.out.println(ImagesPathList);
        System.out.println(MoviesPathList);
    }

    private List<Path> BuildImmutableArray(JSONArray DataArray){
        ArrayList<Path> Builder = new ArrayList();
        DataArray.forEach(file -> Builder.add(new File(file.toString()).toPath()));
        return Collections.unmodifiableList(Builder);
    }
}
