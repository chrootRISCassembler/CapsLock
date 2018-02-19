package capslock;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import trivial_logger.Logger;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

class SyncMediaPlayer {
    private final FutureTask<?> onMovieEnd;
    private final MediaPlayer player;

    SyncMediaPlayer(Path moviePath){
        onMovieEnd = new FutureTask<>(() -> null);

        final Media media = new Media(moviePath.toUri().toString());
        player = new MediaPlayer(media);
        player.setOnEndOfMedia(onMovieEnd);
        player.setAutoPlay(true);
        player.setCycleCount(1);
        //player.setMute(true);
    }

    final MediaPlayer getPlayer(){
        return player;
    }

    final void waitFor(){
        try {
            onMovieEnd.get();
        }catch (InterruptedException ex){
            Logger.INST.critical(() -> this.toString() + "is interrupted.").logException(ex);
        }catch (ExecutionException ex){
            Logger.INST.logException(ex);
        }
    }

    final void release(){
        player.dispose();
    }
}
