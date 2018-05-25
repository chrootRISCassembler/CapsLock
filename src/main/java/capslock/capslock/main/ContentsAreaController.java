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

import capslock.game_info.Game;
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
import methg.commonlib.trivial_logger.Logger;

import java.nio.file.Path;
import java.util.Iterator;

final class ContentsAreaController {
    private static final int IMAGE_DISPLAY_INTERVAL_MS = 5000;

    private final Region parentRegion;
    private final MediaView mediaView;
    private final ImageView imageView;
    private final Timeline imageTimer;

    private volatile Iterator<Path> movieIterator;
    private volatile Iterator<Path> imageIterator;
    private volatile Game game;
    private volatile MediaPlayer player = null;

    ContentsAreaController(Region pane, MediaView mediaView, ImageView imageView){
        this.parentRegion = pane;
        this.mediaView = mediaView;
        this.imageView = imageView;

        imageTimer = new Timeline(new KeyFrame(Duration.millis(IMAGE_DISPLAY_INTERVAL_MS), dummy -> onImageEnd()));
        imageTimer.setCycleCount(Animation.INDEFINITE);
    }

    final void setGame(Game game){
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
        imageTimer.pause();//imageTimerはnullにならないか? チェック
        if(player != null)player.pause();
    }

    final void resume(){
        if(game == null ||
                game.getMovieList().isEmpty() && game.getImageList().isEmpty())return;

        if(imageTimer.getStatus() == Animation.Status.PAUSED)imageTimer.play();
        if(player.getStatus() == MediaPlayer.Status.PAUSED)player.play();
    }

    /**
     * リソースを解放する.
     * <p>動画関係のクラスは参照が無くなってもGCに回収されないため,このメソッドを呼び出して解放する.</p>
     */
    final void kill(){
        if(player != null){
            player.stop();
            player.dispose();
            player = null;
        }

        if(movieIterator != null){
            movieIterator = null;
        }

        mediaView.setMediaPlayer(null);
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
        movieIterator = game.getMovieList().iterator();
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
        player.setOnEndOfMedia(this::onMovieEnd);
        player.setCycleCount(1);

        mediaView.setMediaPlayer(player);
        mediaView.setFitWidth(parentRegion.getWidth());
    }

    private void displayImage(){
        final var image = new Image(imageIterator.next().toUri().toString());
        if(image == null)Logger.INST.info(game.getUUID() + " Failed to load image.");
        {
            final var ex = image.getException();
            if(ex != null)Logger.INST.logException(ex);
        }
        imageView.setImage(image);
        if(image.getWidth() >= image.getHeight()){
            imageView.setFitWidth(parentRegion.getWidth());
        }else{
            imageView.setFitHeight(parentRegion.getHeight());
        }

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
