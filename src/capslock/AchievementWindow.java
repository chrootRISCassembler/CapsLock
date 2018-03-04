package capslock;

import javafx.animation.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import methg.commonlib.trivial_logger.Logger;


public final class AchievementWindow {
    private static final double WINDOW_WIDTH_RATIO = 0.2;
    private static final double WINDOW_HEIGHT_RATIO = 0.12;
    private static final double MARGIN_TOP_BOTTOM_RATIO = 0.1;
    private static final double MARGIN_LEFT_RIGHT_RATIO = 0.08;

    private static final double ICON_RATIO = 0.1;

    volatile private boolean isDisplayed = false;
    private final Stage stage;
    private final AnchorPane rootPane;
    private final ImageView iconView;

    //private final HBox hBox;

    public AchievementWindow(Image icon, String message){
        iconView = new ImageView(icon);
        iconView.setPreserveRatio(true);
        final Label label = new Label(message);
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        //iconView.
        label.setGraphic(iconView);
        //System.err.println("ImageView resizable ? " + iconView.isResizable());

        AnchorPane.setBottomAnchor(label, 0.0);
        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);
        AnchorPane.setTopAnchor(label, 0.0);

        rootPane = new AnchorPane(label);
        rootPane.setStyle("-fx-background-color: rgba(0,0,0,0);");

        final Scene scene = new Scene(rootPane);
        scene.setFill(Color.TRANSPARENT);

        stage = new Stage(StageStyle.TRANSPARENT);
        stage.setFullScreen(true);
        stage.initModality(Modality.NONE);
        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
    }

    public final void display(){

        if(isDisplayed){
            Logger.INST.critical("AchievementWindow is already shown.");
            return;
        }

        isDisplayed = true;

        final Rectangle2D displayRect = Screen.getPrimary().getBounds();

        final double width = displayRect.getWidth() * WINDOW_WIDTH_RATIO;
        final double height = displayRect.getHeight() * WINDOW_HEIGHT_RATIO;


        //Stage#show()を呼び出す前にHBox#resize()やHBox#relocate()を呼び出すと表示がおかしくなる
        stage.show();

        rootPane.resize(width, height);

        rootPane.relocate(displayRect.getWidth() - width - displayRect.getWidth() * MARGIN_LEFT_RIGHT_RATIO,
                displayRect.getHeight());

        rootPane.setStyle("-fx-background-color: rgba(100,0,0,1);");

        iconView.setFitWidth(width * ICON_RATIO);

        final FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), rootPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        final TranslateTransition moveUp = new TranslateTransition(Duration.seconds(1), rootPane);
        moveUp.setByY(- height - displayRect.getHeight() * MARGIN_TOP_BOTTOM_RATIO);

        final ParallelTransition in = new ParallelTransition(fadeIn, moveUp);
        final PauseTransition pause = new PauseTransition(Duration.seconds(1));
        final FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), rootPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        final TranslateTransition moveRight = new TranslateTransition(Duration.seconds(1), rootPane);
        moveRight.setByX(width + displayRect.getWidth() * MARGIN_LEFT_RIGHT_RATIO);

        final ParallelTransition out = new ParallelTransition(fadeOut, moveRight);
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
