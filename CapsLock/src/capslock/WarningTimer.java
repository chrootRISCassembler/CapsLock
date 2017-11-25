package capslock;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;

class WarningTimer {
    private int counter=0;
    private final int POPUP_SECONDS=300;
    private final OverLayWindow window=new OverLayWindow();
    private Timeline timer=new Timeline();

    void Start() {
    	counter=0;
    	timer.stop();
        timer = new Timeline(new KeyFrame(Duration.seconds(POPUP_SECONDS), (ActionEvent event) -> {

        		System.err.println("overlay");
                counter++;
                window.Exe(counter);
                if(counter>=2)timer.stop();

        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }
    void Stop() {
    	counter=0;
    	timer.stop();
    	window.Exe(-1);
    }
}
