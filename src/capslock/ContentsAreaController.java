package capslock;

import game_info.GameEntry;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

final class ContentsAreaController {
    private final StackPane stackPane;
    private final MediaView mediaView;
    private final ImageView imageView;

    private volatile Timer timer;

    private volatile Iterator<Path> movieIterator;
    private volatile Iterator<Path> imageIterator;
    private volatile GameEntry game;
    private volatile MediaPlayer player = null;

    ContentsAreaController(StackPane pane, MediaView mediaView, ImageView imageView){
        this.stackPane = pane;
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
            final Media media = new Media(movieIterator.next().toUri().toString());
            player = new MediaPlayer(media);
            player.setAutoPlay(true);
            player.setOnEndOfMedia(() -> onMovieEnd());
            player.setCycleCount(1);

            mediaView.setMediaPlayer(player);
            mediaView.setFitWidth(stackPane.getWidth());

            mediaView.setVisible(true);
            imageView.setVisible(false);

        }else{
            final Image image = new Image(imageIterator.next().toUri().toString());
            imageView.setImage(image);
            imageView.setFitWidth(stackPane.getWidth());

            timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    onImageEnd();
                }
            }, 1000);

            mediaView.setVisible(false);
            imageView.setVisible(true);
        }
    }

    final void onMovieEnd(){
        mediaView.setMediaPlayer(null);
        player.dispose();

        if(movieIterator.hasNext()){
            final Media media = new Media(movieIterator.next().toUri().toString());
            player = new MediaPlayer(media);
            player.setAutoPlay(true);
            player.setOnEndOfMedia(() -> onMovieEnd());
            player.setCycleCount(1);

            mediaView.setMediaPlayer(player);
            mediaView.setFitWidth(stackPane.getWidth());
        }else {
            onMovieExhaust();
        }
    }

    final void onImageEnd(){
        timer.cancel();

        if(imageIterator.hasNext()){
            final Image image = new Image(imageIterator.next().toUri().toString());
            imageView.setImage(image);
            imageView.setFitWidth(stackPane.getWidth());

            timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    onImageEnd();
                }
            }, 1000);
        }else{
            onImageExhaust();
        }
    }

    final void onMovieExhaust(){
        if(imageIterator.hasNext()) {
            player = null;
            movieIterator = game.getMovieList().iterator();

            final Image image = new Image(imageIterator.next().toUri().toString());
            imageView.setImage(image);
            imageView.setFitWidth(stackPane.getWidth());

            timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    onImageEnd();
                }
            }, 1000);

            mediaView.setVisible(false);
            imageView.setVisible(true);

        }else {
            movieIterator = game.getMovieList().iterator();
            final Media media = new Media(movieIterator.next().toUri().toString());
            player = new MediaPlayer(media);
            player.setAutoPlay(true);
            player.setOnEndOfMedia(() -> onMovieEnd());
            player.setCycleCount(1);

            mediaView.setMediaPlayer(player);
            mediaView.setFitWidth(stackPane.getWidth());
        }
    }

    final void onImageExhaust(){
        imageIterator = game.getImageList().iterator();

        if(movieIterator.hasNext()){
            final Media media = new Media(movieIterator.next().toUri().toString());
            player = new MediaPlayer(media);
            player.setAutoPlay(true);
            player.setOnEndOfMedia(() -> onMovieEnd());
            player.setCycleCount(1);

            mediaView.setMediaPlayer(player);
            mediaView.setFitWidth(stackPane.getWidth());

            mediaView.setVisible(true);
            imageView.setVisible(false);

        }else{
            final Image image = new Image(imageIterator.next().toUri().toString());
            imageView.setImage(image);
            imageView.setFitWidth(stackPane.getWidth());

            timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    onImageEnd();
                }
            }, 1000);
        }
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
