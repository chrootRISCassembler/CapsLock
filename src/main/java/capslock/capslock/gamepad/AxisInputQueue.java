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

package capslock.capslock.gamepad;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

final class AxisInputQueue {
    enum Axis{
        X,
        Y
    }

    enum Direction{
        UP,
        DOWN,
        RIGHT,
        LEFT
    }

    static class Builder{
        private float highThreshold = 0.6f;
        private float lowThreshold = 0.4f;
        private int intervalOfKeepHigh_ms = 20;

        final Builder setHighThreshold(float val){
            highThreshold = val;
            return this;
        }

        final Builder setLowThreshold(float val){
            lowThreshold = val;
            return this;
        }

        final Builder setIntervalOfKeepHigh(int interval_ms){
            intervalOfKeepHigh_ms = interval_ms;
            return this;
        }

        final AxisInputQueue build(){
            return new AxisInputQueue(this);
        }
    }

    final private float highThreshold;
    final private float lowThreshold;
    final private int intervalOfKeepHigh_ms;

    private final Queue<Direction> eventQueue = new ArrayDeque<>();

    static private final byte DEFALT_COUNT = -30;
    static private final byte RESET_COUNT = 50;

    private boolean isFiredX = true;
    private float lastValX = 0.0f;
    private boolean isFiredY = true;
    private float lastValY = 0.0f;

    private boolean upState = false;
    private byte upCountDown = DEFALT_COUNT;

    private boolean downState = false;
    private byte downCountDown = DEFALT_COUNT;

    private boolean rightState = false;
    private byte rightCountDown = DEFALT_COUNT;

    private boolean leftState = false;
    private byte leftCountDown = DEFALT_COUNT;

    private AxisInputQueue(Builder builder){
        highThreshold = builder.highThreshold;
        lowThreshold = builder.lowThreshold;
        intervalOfKeepHigh_ms = builder.intervalOfKeepHigh_ms;
    }

    final void event(Axis axis, float value){
        if(axis == Axis.X){
            isFiredX = true;
            lastValX = value;

            if(rightState){
                if(value < lowThreshold){
                    rightState = false;
                    rightCountDown = DEFALT_COUNT;
                }else {
                    --rightCountDown;
                    if(rightCountDown == 0){
                        rightCountDown = RESET_COUNT;
                        eventQueue.add(Direction.RIGHT);
                    }
                }
            }else {
                if(value > highThreshold){
                    rightState = true;
                    eventQueue.add(Direction.RIGHT);
                }
            }

            if(leftState){
                if(value > -lowThreshold){
                    leftState = false;
                    leftCountDown = DEFALT_COUNT;
                }else{
                    --leftCountDown;
                    if(leftCountDown == 0){
                        leftCountDown = RESET_COUNT;
                        eventQueue.add(Direction.LEFT);
                    }
                }
            }else{
                if(value < -highThreshold) {
                    leftState = true;
                    eventQueue.add(Direction.LEFT);
                }
            }
        }else{
            isFiredY = true;
            lastValY = value;

            if(downState){
                if(value < lowThreshold){
                    downState = false;
                    downCountDown = DEFALT_COUNT;
                }else {
                    --downCountDown;
                    if(downCountDown == 0){
                        downCountDown = RESET_COUNT;
                        eventQueue.add(Direction.DOWN);
                    }
                }
            }else {
                if(value > highThreshold){
                    downState = true;
                    eventQueue.add(Direction.DOWN);
                }
            }

            if(upState){
                if(value > -lowThreshold){
                    upState = false;
                    upCountDown = DEFALT_COUNT;
                }else {
                    --upCountDown;
                    if(upCountDown == 0){
                        upCountDown = RESET_COUNT;
                        eventQueue.add(Direction.UP);
                    }
                }
            }else {
                if(value < -highThreshold){
                    upState = true;
                    eventQueue.add(Direction.UP);
                }
            }
        }
    }

    final void emulateEvent(){
        if(!isFiredX){
            event(Axis.X, lastValX);
            event(Axis.X, lastValX);
            event(Axis.X, lastValX);
            event(Axis.X, lastValX);
            event(Axis.X, lastValX);
            event(Axis.X, lastValX);
        }
        isFiredX = false;

        if(!isFiredY){
            event(Axis.Y, lastValY);
            event(Axis.Y, lastValY);
            event(Axis.Y, lastValY);
            event(Axis.Y, lastValY);
            event(Axis.Y, lastValY);
            event(Axis.Y, lastValY);
        }
        isFiredY = false;
    }

    final Optional<Direction> pop(){
        return Optional.ofNullable(eventQueue.poll());
    }
}
