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

package capslock.capslock.main;

import capslock.capslock.os_absorbing.NullDevice;
import capslock.game_info.Game;
import capslock.game_info.GameDocument;
import capslock.game_info.JSONDBReader;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.util.Duration;
import methg.commonlib.file_checker.FileChecker;
import methg.commonlib.trivial_logger.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * {@link MainFormController}と協調動作するEnum-singleton.
 * <p>
 *     このenumは{@link MainFormController}からUI処理以外を切り離すために存在する.
 * </p>
 */
enum MainHandler {
    INST;

    private final Path JSON_DB_PATH = Paths.get("./GamesInfo.json");
    private final int WARN_INTERVAL_MINUTE = 5;

    private MainFormController controller;
    private List<Game> gameList;
    private int pastMinutes = 0;
    private final Timeline timer;

    private Process gameProcess = null;

    MainHandler(){
        timer = new Timeline(new KeyFrame(Duration.minutes(WARN_INTERVAL_MINUTE), event -> {
            pastMinutes += WARN_INTERVAL_MINUTE;
            final AchievementWindow warn = new AchievementWindow(null,
                    "プレイ開始から" + pastMinutes + "分経過しました\n混雑している場合は次の人に\n交代してください");
            warn.display();
        }));
        timer.setCycleCount(Animation.INDEFINITE);
    }

    /**
     * 協調動作する{@link MainFormController}のインスタンスを登録する.
     * @param controller {@link MainFormController}のインスタンス.
     */
    final void setController(MainFormController controller){
        assert controller != null;
        this.controller = controller;
    }

    /**
     * 通常動作時のJSONの読み出し.
     */
    final void loadJSONDB(){
        final JSONDBReader reader;
        try {
            reader = new JSONDBReader(JSON_DB_PATH);
        }catch (IllegalArgumentException | IOException ex) {
            Logger.INST.warn("Failed to load game's information.");
            Logger.INST.logException(ex);
            gameList = Collections.emptyList();
            return;
        }

        gameList = Collections.unmodifiableList(reader.getDocumentList());
        Logger.INST.info(gameList.size() + " games detected.");
    }


    /**
     * confirmモード時のJSONの読み出し
     * @param JSONPath 1つのゲームだけが登録されたJSONファイルのパス
     * @param realGameRoot ゲームのルートディレクトリ
     */
    final void loadJSONDB(Path JSONPath, String realGameRoot){

        final Optional<Path> tmpJSONFile = new FileChecker(JSONPath)
                .onCannotWrite(dummy -> true)
                .onCanExec(dummy -> true)
                .check();

        if (!tmpJSONFile.isPresent()){
            Logger.INST.critical("(in ConfirmMode) JSON path is invalid.");
            return;
        }

        final JSONDBReader reader;
        try {
            reader = new JSONDBReader(tmpJSONFile.get());
        }catch (IllegalArgumentException | IOException ex) {
            Logger.INST.critical("(in ConfirmMode) Failed to load JSON.");
            Logger.INST.logException(ex);
            return;
        }

        final UnaryOperator<Path> closure = (final Path phantomPath) -> {
            final Iterator<Path> pathElementIterator = phantomPath.iterator();
            pathElementIterator.next();// Gamesディレクトリを無視
            pathElementIterator.next();// ゲームのルートディレクトリを無視

            final StringBuilder buf = new StringBuilder(realGameRoot);
            pathElementIterator.forEachRemaining(element -> {
                buf.append('/');
                buf.append(element);
            });

            return Paths.get(buf.toString());
        };

        final GameDocument doc = reader.getDocumentList().get(0);
        doc.setExe(closure.apply(doc.getExe()));
        doc.setPanel(closure.apply(doc.getPanel()));
        doc.setImageList(
                doc.getImageList().stream()
                .map(closure)
                .collect(Collectors.toList())
        );
        doc.setMovieList(
                doc.getMovieList().stream()
                .map(closure)
                .collect(Collectors.toList())
        );

        final List<Game> fixedGame = new ArrayList<>(1);
        fixedGame.add(doc);

        System.err.println(doc.getExe());
        System.err.println(doc.getPanel());
        System.err.println(doc.getImageList());
        System.err.println(doc.getMovieList());

        gameList = Collections.unmodifiableList(fixedGame);
    }

    /**
     * JSONファイルから読み出された変更不可能なゲームのリストを返す.
     * @return 変更不可能なゲームの {@link List}
     */
    final List<Game> getGameList(){
        return gameList;
    }

    /**
     * ゲームを別のプロセスで起動する.
     * @param game 起動するゲームの{@link Game}
     */
    final void launch(Game game){
        if(gameProcess != null){
            Logger.INST.debug("Game launched already ; ignore launch request.");
        }

        CapsLock.getExecutor().execute(new Task<Void>() {
            @Override protected Void call() throws Exception {

                final String ExePathString = game.getExe().toString();
                final ProcessBuilder pb = new ProcessBuilder(ExePathString);
                pb.directory(game.getExe().getParent().toFile());
                pb.redirectOutput(NullDevice.getAsFile());
                pb.redirectError(NullDevice.getAsFile());

                Logger.INST.debug("Try to launch " + ExePathString);

                try {
                    gameProcess = pb.start();
                } catch (SecurityException ex) {
                    Logger.INST.critical("Blocked by security software.").logException(ex);
                    onLaunchFailed();
                    return null;
                } catch (IOException ex) {
                    Logger.INST.warn("Failed to launch game : " + ExePathString).logException(ex);
                    onLaunchFailed();
                    return null;
                }

                onGameLaunched(game);

                try {
                    gameProcess.waitFor();

                } catch (InterruptedException ex) {
                    Logger.INST.debug("Game's process is interrupted.")
                            .logException(ex);
                } finally {
                    Logger.INST.debug("game process terminate");
                    gameProcess.destroyForcibly();
                    gameProcess = null;
                    onGameQuit();
                }
                return null;
            }
        });
    }

    /**
     * {@link #launch(Game)}が呼び出された結果, 正常にゲームが起動できたとき呼び出される.
     * @param game 正常に起動されたゲーム
     */
    final void onGameLaunched(Game game){
        Logger.INST.info(() -> game.getExe() + " is launched successfully.");
        timer.play();
        controller.onGameLaunched();
    }

    /**
     * {@link #launch(Game)}が呼び出された結果, ゲーム起動に失敗したとき呼び出される.
     */
    final void onLaunchFailed(){
        gameProcess = null;
        timer.stop();
        pastMinutes = 0;
        controller.onLaunchFailed();
    }

    /**
     * {@link #launch(Game)}によって起動されたゲームが終了したとき呼び出される.
     */
    final void onGameQuit(){
        Logger.INST.info("game quit.");
        timer.stop();
        pastMinutes = 0;
        controller.onGameQuit();
    }

    /**
     * ランチャー終了時に呼び出される.
     */
    final void onTerminate(){
        controller.onTerminate();
    }
}
