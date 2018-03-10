package capslock.capslock.main;

import capslock.game_info.Game;
import capslock.game_info.JSONDBReader;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import methg.commonlib.trivial_logger.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

enum MainHandler {
    INST;

    private final Path JSON_DB_PATH = Paths.get("./GamesInfo.json");
    private final int WARN_INTERVAL_MINUTE = 5;

    private MainFormController controller;
    private final List<Game> gameList;
    private boolean onCreatedDispatched = false;
    private int pastMinutes = 0;
    private final Timeline timer;

    private Process gameProcess = null;

    void setController(MainFormController controller){
        this.controller = controller;
    }

    private MainHandler(){
        timer = new Timeline(new KeyFrame(Duration.minutes(WARN_INTERVAL_MINUTE), event -> {
            pastMinutes += WARN_INTERVAL_MINUTE;
            final AchievementWindow warn = new AchievementWindow(null,
                    "プレイ開始から" + pastMinutes + "分経過しました\n混雑している場合は次の人に\n交代してください");
            warn.display();
        }));
        timer.setCycleCount(Animation.INDEFINITE);

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

    void onLoad(WindowEvent event){
        Logger.INST.debug(" onLoad called.");
        if (!onCreatedDispatched){
            onCreatedDispatched = true;
            controller.onCreated(this);
        }
    }

    List<Game> getGameList(){
        return gameList;
    }

    void launch(Game game){
        if(gameProcess != null){
            Logger.INST.debug("Game launched already ; ignore launch request.");
        }

        CapsLock.getExecutor().execute(() -> {

            final String ExePathString = game.getExe().toString();
            final ProcessBuilder pb = new ProcessBuilder(ExePathString);
            final Path gameDir = Paths.get(System.getProperty("user.dir") + "\\" + game.getExe());
            pb.directory(gameDir.getParent().toFile());
            pb.redirectErrorStream(true);

            Logger.INST.debug("Try to launch " + ExePathString);

            try {
                gameProcess = pb.start();
            } catch (SecurityException ex){
                Logger.INST.critical("Blocked by security software.").logException(ex);
                onLaunchFailed();
                return;
            } catch (IOException ex) {
                Logger.INST.warn("Failed to launch game : " + ExePathString).logException(ex);
                onLaunchFailed();
                return;
            }

            onGameLaunched(game);

            try {
                gameProcess.waitFor();

            } catch (InterruptedException ex){
                Logger.INST.debug("Game's process is interrupted.")
                        .logException(ex);
            }finally {
                gameProcess.destroyForcibly();
                gameProcess = null;
                onGameQuit();
            }
        });
    }


    void onGameLaunched(Game game){
        Logger.INST.info(() -> game.getExe() + " is launched successfully.");
        timer.play();
        controller.onGameLaunched();
    }

    void onLaunchFailed(){
        gameProcess = null;
        timer.stop();
        pastMinutes = 0;
        controller.onLaunchFailed();
    }

    void onGameQuit(){
        Logger.INST.info("game quit.");
        timer.stop();
        pastMinutes = 0;
        controller.onGameQuit();
    }
}
