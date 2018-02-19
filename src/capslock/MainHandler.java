package capslock;

import game_info.GameEntry;
import game_info.JSONDBReader;
import javafx.stage.WindowEvent;
import trivial_logger.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

class MainHandler {
    private static final Path JSON_DB_PATH = Paths.get("./GamesInfo.json");
    private final MainFormController controller;
    private final List<GameEntry> gameList;
    private boolean onCreatedDispatched = false;

    private Process gameProcess = null;

    MainHandler(MainFormController controller){
        this.controller = controller;

        final JSONDBReader reader;
        try {
            reader = new JSONDBReader(JSON_DB_PATH);
        }catch (IllegalArgumentException ex) {
            Logger.INST.warn("Failed to load game's information.");
            Logger.INST.logException(ex);
            gameList = Collections.emptyList();
            return;
        }

        gameList = Collections.unmodifiableList(reader.toGameEntryList());
        Logger.INST.info(gameList.size() + " games detected.");
    }

    void onLoad(WindowEvent event){
        Logger.INST.debug(" onLoad called.");
        if (!onCreatedDispatched){
            onCreatedDispatched = true;
            controller.onCreated(this);
        }
    }

    List<GameEntry> getGameList(){
        return gameList;
    }

    void launch(GameEntry game){
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
                // LogHandler.inst.finest("StopMovie");
                // playstop.stop();
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


    void onGameLaunched(GameEntry game){
        Logger.INST.info(() -> game.getExe() + " is launched successfully.");
        controller.onGameLaunched();
    }

    void onLaunchFailed(){
        gameProcess = null;
        controller.onLaunchFailed();
    }

    void onGameQuit(){
        Logger.INST.info("game quit.");
        controller.onGameQuit();
    }
}
