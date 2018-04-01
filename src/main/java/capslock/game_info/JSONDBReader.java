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
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JSONファイルからゲーム情報を読み出して{@link GameDocument}オブジェクトを作成する.
 */
public final class JSONDBReader {
    private final List<GameDocument> gameList;

    /**
     * ゲーム情報を読み出すJSONパスを指定してインスタンスを作成する.ファイル内容は直ちに読み出される.
     * @param filePath JSONファイルのパス.
     * @throws IllegalArgumentException 不正なファイルパスを渡した
     * @throws IOException ファイル読み込み中にエラーが発生した
     */
    public JSONDBReader(Path filePath) throws IllegalArgumentException, IOException {
        final String JSONRawString = Files.newBufferedReader
                (
                        new FileChecker(filePath)
                        .onCannotWrite(dummy -> true)
                        .onCanExec(dummy -> true)
                        .check()
                        .orElseThrow(IllegalArgumentException::new)
                )
                .lines()
                .collect(Collectors.joining());

        gameList = new ArrayList<>();

        for (Object unchecked : new JSONArray(JSONRawString)){
            System.out.println(unchecked.getClass());
            System.out.println(unchecked);
            if(unchecked instanceof JSONObject){
                final GameDocument document = new GameDocument((JSONObject) unchecked);
                gameList.add(document);
            }
            System.err.println();
        }
    }

    /**
     * {@link GameDocument}のリストを返す.
     */
    public final List<GameDocument> getDocumentList(){
        return gameList;
    }
}
