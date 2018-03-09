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

import java.nio.file.Path;
import java.time.Instant;
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
