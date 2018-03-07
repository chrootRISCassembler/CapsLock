package capslock.capslock.main;

import capslock.game_info.GameEntry;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

final class ContentsAreaController {
    private static final int IMAGE_DISPLAY_INTERVAL_MS = 5000;

    private final Region parentRegion;
    private final MediaView mediaView;
    private final ImageView imageView;
    private final Timeline imageTimer;

    private volatile Iterator<Path> movieIterator;
    private volatile Iterator<Path> imageIterator;
    private volatile GameEntry game;
    private volatile MediaPlayer player = null;

    ContentsAreaController(Region pane, MediaView mediaView, ImageView imageView){
        this.parentRegion = pane;
        this.mediaView = mediaView;
        this.imageView = imageView;

        imageTimer = new Timeline(new KeyFrame(Duration.millis(IMAGE_DISPLAY_INTERVAL_MS), dummy -> onImageEnd()));
        imageTimer.setCycleCount(Animation.INDEFINITE);
    }

    final void setGame(GameEntry game){
        clear();

        this.game = game;

        if(game.getMovieList().isEmpty() && game.getImageList().isEmpty())return;

        imageIterator = game.getImageList().iterator();
        movieIterator = game.getMovieList().iterator();

        if(movieIterator.hasNext()){
            displayMovie();
            mediaView.setVisible(true);
            imageView.setVisible(false);

        }else{
            displayImage();
            mediaView.setVisible(false);
            imageView.setVisible(true);
        }
    }

    final void suspend(){
        if(game == null ||
                game.getMovieList().isEmpty() && game.getImageList().isEmpty())return;

        imageTimer.pause();
        player.pause();
    }

    final void resume(){
        if(game == null ||
                game.getMovieList().isEmpty() && game.getImageList().isEmpty())return;

        if(imageTimer.getStatus() == Animation.Status.PAUSED)imageTimer.play();
        if(player.getStatus() == MediaPlayer.Status.PAUSED)player.play();
    }

    private void onMovieEnd(){
        mediaView.setMediaPlayer(null);
        player.dispose();

        if(movieIterator.hasNext()){
            displayMovie();
        }else {
            onMovieExhaust();
        }
    }

    private void onImageEnd(){
        if(imageIterator.hasNext()){
            displayImage();
        }else{
            onImageExhaust();
        }
    }

    private void onMovieExhaust(){
        if(imageIterator.hasNext()) {
            player = null;
            displayImage();
            mediaView.setVisible(false);
            imageView.setVisible(true);

        }else {
            displayMovie();
        }
    }

    private void onImageExhaust(){
        imageTimer.stop();
        imageIterator = game.getImageList().iterator();

        if(movieIterator.hasNext()){
            displayMovie();

            mediaView.setVisible(true);
            imageView.setVisible(false);

        }else{
            displayImage();
        }
    }

    private void displayMovie(){
        final Media media = new Media(movieIterator.next().toUri().toString());
        player = new MediaPlayer(media);
        player.setAutoPlay(true);
        player.setOnEndOfMedia(() -> onMovieEnd());
        player.setCycleCount(1);

        mediaView.setMediaPlayer(player);
        mediaView.setFitWidth(parentRegion.getWidth());
    }

    private void displayImage(){
        final Image image = new Image(imageIterator.next().toUri().toString());
        imageView.setImage(image);
        imageView.setFitWidth(parentRegion.getWidth());
        imageTimer.play();
    }

    private void clear(){
        if(player != null){
            player.dispose();
            player = null;
        }

        imageTimer.stop();
        mediaView.setMediaPlayer(null);
        imageView.setImage(null);
    }
}
