/*
    Copyright (C) 2018 RISCassembler

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package capslock.game_info;

import methg.commonlib.file_checker.FileChecker;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class GameDocument extends Game {

    /**
     * 新規登録用コンストラクタ.UUIDが自動でセットされる.
     */
    public GameDocument(){
        uuid = UUID.randomUUID();
    }

    /**
     * このコンストラクタは{@link JSONDBReader}からのみ呼び出される.
     * 依存ライブラリの型が外部に漏れることを防ぐため,このコンストラクタはpackage-privateである.
     */
    GameDocument(JSONObject document){
        try {
            uuid = UUID.fromString(document.getString("UUID"));
        }catch (JSONException ex){
            if(document.has("UUID")) {
                System.err.println("There is \"UUID\" key, but wrong value.");
            }else {
                System.err.println("There is no \"UUID\" field. This field is necessary.");
            }
            ex.printStackTrace();
        }catch (IllegalArgumentException ex){
            System.err.println("There is \"UUID\" key, but wrong value. This is not a UUID.");
            ex.printStackTrace();
        }

//        if(uuid == null){
//            uuid = UUID.randomUUID();
//            System.err.println("UUID isn't set.");
//            System.err.println("");
//        }

        ExeField: try {
            final String uncheckedString = document.getString("exe");
            if(uncheckedString.isEmpty()){
                System.err.println("\"exe\" field is empty String.");
                break ExeField;
            }

            exe = new FileChecker(uncheckedString)
                    .onNotExists(file -> {
                        System.err.println("Warning : exe file isn't found.");
                        return true;
                    })
                    .onCanExec(dummy -> true)
                    .onCannotRead(file -> true)
                    .onCannotWrite(dummy -> true)
                    .check()
                    .get();

        }catch (JSONException ex){
            if(document.has("exe")) {
                System.err.println("There is \"exe\" key, but wrong value.");
            }else {
                System.err.println("There is no \"exe\" key. \"exe\" field is necessary.");
            }
            ex.printStackTrace();
        }

        try {
            name = document.getString("name");
        }catch (JSONException ex){
            if(document.has("name")) {
                System.err.println("There is \"name\" key, but wrong value.");
                ex.printStackTrace();
            }
        }

        try {
            lastMod = Instant.parse(document.getString("lastMod"));
        }catch (JSONException ex){
            if(document.has("lastMod")) {
                System.err.println("There is \"lastMod\" key, but wrong value.");
                ex.printStackTrace();
            }
        }

        try {
            desc = document.getString("desc");
        }catch (JSONException ex){
            if(document.has("desc")) {
                System.err.println("There is \"desc\" key, but wrong value.");
                ex.printStackTrace();
            }
        }

        try {
            panel = new FileChecker(document.getString("panel"))
                    .onNotExists(dummy -> {
                        System.err.println("Warning : panel file isn't found.");
                        return true;
                    })
                    .onCanExec(dummy -> {
                        //余計なパーミッションの警告
                        return true;
                    })
                    .onCannotWrite(dummy -> true)
                    .check()
                    .get();

        }catch (JSONException ex){
            if(document.has("panel")) {
                System.err.println("There is \"panel\" key, but wrong value.");
                ex.printStackTrace();
            }
        }

        if(document.has("imageList")){
            imageList = new ArrayList<>();
            for (final Object unchecked : document.getJSONArray("imageList")){
                new FileChecker((String)unchecked)
                        .onNotExists(dummy -> {
                            System.err.println("Warning : image file isn't found.");
                            return true;
                        })
                        .onCanExec(dummy -> {
                            //余計なパーミッションの警告
                            return true;
                        })
                        .onCannotWrite(dummy -> true)
                        .check()
                        .ifPresent(path -> imageList.add(path));
            }
        }


        if(document.has("movieList")){
            movieList = new ArrayList<>();
            for (final Object unchecked : document.getJSONArray("movieList")){
                new FileChecker((String)unchecked)
                        .onNotExists(dummy -> {
                            System.err.println("Warning : movie file isn't found.");
                            return true;
                        })
                        .onCanExec(dummy -> {
                            //余計なパーミッションの警告
                            return true;
                        })
                        .onCannotWrite(dummy -> true)
                        .check()
                        .ifPresent(path -> movieList.add(path));
            }
        }

        try {
            gameID = document.getInt("gameID");
            if(Integer.signum(gameID) != 1){
                System.err.println("\"gameID\" field must be natural number and unique. But the value is " + gameID + '.');
            }
        }catch (JSONException ex){
            if(document.has("gameID")) {
                System.err.println("There is \"gameID\" key, but wrong value.");
                ex.printStackTrace();
            }
        }
    }

    /**
     * UUIDを置換する.
     * UUIDはゲームを特性する主キーである.登録情報の整合性が取れなくなる可能性があるため注意して使用する.
     * @param newUUID 新しいUUID{@link UUID}
     * @return this
     */
    public GameDocument setUUID(UUID newUUID) {
        uuid = newUUID;
        return this;
    }

    /***
     * ゲームの実行ファイルのパスを置換する.
     * @param path 新しい実行ファイルのパス.
     * @return this
     */
    public GameDocument setExe(Path path) {
        exe = path;
        return this;
    }

    /***
     * ゲーム名を置換する.
     * @param newName 新しいゲーム名.
     * @return this
     */
    public GameDocument setName(String newName) {
        name = newName;
        return this;
    }

    /***
     * 最終更新日時を更新する.
     * @param updateTime 更新日時.
     * @return this
     */
    public GameDocument setLastMod(Instant updateTime) {
        lastMod = updateTime;
        return this;
    }

    /***
     * ゲームの説明を置換する.
     * @param newDesc ゲームの新しい説明.
     * @return this
     */
    public GameDocument setDesc(String newDesc) {
        desc = newDesc;
        return this;
    }

    /***
     * ゲームのパネル画像のパスを置換する.
     * @param newPanel ゲームの新しいパネル画像のパス.
     * @return this
     */
    public GameDocument setPanel(Path newPanel) {
        panel = newPanel;
        return this;
    }

    /***
     * ゲームの紹介画像のパスのリストを置換する
     * @param newImageList 新しい紹介画像のパスのリスト
     * @return this
     */
    public GameDocument setImageList(List<Path> newImageList) {
        imageList = newImageList;
        return this;
    }

    /***
     * ゲームの紹介映像のパスのリストを置換する
     * @param newMovieList 新しい紹介映像のパスのリスト
     * @return this
     */
    public GameDocument setMovieList(List<Path> newMovieList) {
        movieList = newMovieList;
        return this;
    }

    /***
     * ゲームの作品番号を書き換える.
     * @param newID 新しい作品番号.
     * @return this
     */
    public GameDocument setGameID(int newID) {
        gameID = newID;
        return this;
    }
}
