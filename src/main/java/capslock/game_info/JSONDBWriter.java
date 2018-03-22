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
import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * ゲームの登録情報を受け取ってJSON形式でファイルに書き込む
 */
public final class JSONDBWriter {
    private final JSONArray gameList = new JSONArray();
    private final Path filePath;

    /**
     * 書き込み先ファイルパスを指定してインスタンスを作る.
     * @throws IllegalArgumentException 引数のファイルが書き込み用に開けないとき
     */
    public JSONDBWriter(Path filePath) throws IllegalArgumentException {
        this.filePath = new FileChecker(filePath)
                .onNotExists(dummy -> true)
                .onCannotRead(dummy -> true)
                .onCanExec(dummy -> true)
                .check()
                .orElseThrow(IllegalArgumentException::new);
    }

    /**
     * 書き込むゲーム情報を追加する.
     * @param game 追加するゲーム情報
     */
    public final JSONDBWriter add(Game game){
        gameList.put(game.toJSON());
        return this;
    }

    /**
     * {@link #add(Game)}で追加した全てのゲーム情報をファイルに書き込む.
     * @throws IOException ファイル書き込み中にエラーが発生した.
     */
    public final void flush() throws IOException{
        try(final BufferedWriter writer = Files.newBufferedWriter(filePath,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)){
            gameList.write(writer);
        } catch (IOException ex) {
            System.err.println("Failed to write on " + filePath);
            throw ex;
        }
    }
}
