package capslock;

import javafx.animation.*;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import trivial_logger.Logger;


public final class AchievementWindow {
    private static final double WINDOW_WIDTH_RATIO = 0.2;
    private static final double WINDOW_HEIGHT_RATIO = 0.12;
    private static final double MARGIN_TOP_BOTTOM_RATIO = 0.1;
    private static final double MARGIN_LEFT_RIGHT_RATIO = 0.08;

    volatile private boolean isDisplayed = false;
    private final Stage stage;
    private final HBox hBox;
    private final Scene scene;

    public AchievementWindow(Image icon, String message){
        final ImageView iconView = new ImageView(icon);
        final Label label = new Label(message);
        hBox = new HBox(iconView, label);
        //hBox.setBackground(Background.EMPTY);
        hBox.setStyle("-fx-background-color: rgba(100,0,0,0.3);");

        scene = new Scene(hBox);
        scene.setFill(Color.TRANSPARENT);

        stage = new Stage(StageStyle.TRANSPARENT);
        stage.setFullScreen(true);
        stage.initModality(Modality.NONE);
        stage.setScene(scene);
    }

    public final void display(){

        if(isDisplayed){
            Logger.INST.critical("AchievementWindow is already shown.");
            return;
        }

        isDisplayed = true;
        stage.show();

        final double width = scene.getWidth() * WINDOW_WIDTH_RATIO;
        final double height = scene.getHeight() * WINDOW_HEIGHT_RATIO;

        hBox.resize(width, height);

        hBox.relocate(scene.getWidth() - width - scene.getWidth() * MARGIN_LEFT_RIGHT_RATIO,
                scene.getHeight());

        final FadeTransition fadein = new FadeTransition(Duration.seconds(1), hBox);
        fadein.setFromValue(0);
        fadein.setToValue(1);

        final TranslateTransition moveUp = new TranslateTransition(Duration.seconds(1), hBox);
        moveUp.setByY(- height - scene.getHeight() * MARGIN_TOP_BOTTOM_RATIO);

        final ParallelTransition in = new ParallelTransition(fadein, moveUp);
        final PauseTransition pause = new PauseTransition(Duration.seconds(1));
        final FadeTransition fadeout = new FadeTransition(Duration.seconds(1), hBox);
        fadeout.setFromValue(1);
        fadeout.setToValue(0);

        final TranslateTransition moveRight = new TranslateTransition(Duration.seconds(1), hBox);
        moveRight.setByX(width + scene.getWidth() * MARGIN_LEFT_RIGHT_RATIO);

        final ParallelTransition out = new ParallelTransition(fadeout, moveRight);
        final SequentialTransition sequence = new SequentialTransition(in, pause, out);

        sequence.setOnFinished(dummy -> onEnd());
        sequence.play();
    }

    public final void waitForClose(){
        if(!isDisplayed){
            Logger.INST.debug("Not shown, return immediately");
            return;
        }

        Logger.INST.debug("wait start");
        while(isDisplayed){
        }
        Logger.INST.debug("wait end");
    }

    private void onEnd(){
        stage.close();
        isDisplayed = false;
    }
}
