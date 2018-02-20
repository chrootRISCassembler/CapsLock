package capslock;

import game_info.GameEntry;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

final class ContentsAreaController {
    private final Region parentRegion;
    private final MediaView mediaView;
    private final ImageView imageView;

    private volatile Timer timer;

    private volatile Iterator<Path> movieIterator;
    private volatile Iterator<Path> imageIterator;
    private volatile GameEntry game;
    private volatile MediaPlayer player = null;

    ContentsAreaController(Region pane, MediaView mediaView, ImageView imageView){
        this.parentRegion = pane;
        this.mediaView = mediaView;
        this.imageView = imageView;
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
        timer.cancel();

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

        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onImageEnd();
            }
        }, 1000);
    }

    private void clear(){
        if(player != null){
            player.dispose();
            player = null;
        }

        if(timer != null){
            timer.cancel();
            timer = null;
        }

        mediaView.setMediaPlayer(null);
        imageView.setImage(null);
    }
}
