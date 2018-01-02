/*  
    This file is part of CapsLock. CapsLock is a simple game launcher.
    Copyright (C) 2017 RISCassembler, domasyake, su-u

    CapsLock is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
*/

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
    private boolean isdoing;

    void Start() {
    	if(isdoing)return;
    	isdoing=true;
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
    	isdoing=false;
    	timer.stop();
    	window.Exe(-1);
    }
}
