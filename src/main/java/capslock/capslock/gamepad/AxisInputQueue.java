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

    private final Queue<Direction> eventQueue = new ArrayDeque<>();

    private boolean isFiredX = true;
    private float lastValX = 0.0f;
    private boolean isFiredY = true;
    private float lastValY = 0.0f;

    private final CDST rightCDST = CDST.positive();
    private final CDST leftCDST = CDST.negative();
    private final CDST upCDST = CDST.negative();
    private final CDST downCDST = CDST.positive();

    final void event(Axis axis, float value){
        if(axis == Axis.X){
            isFiredX = true;
            lastValX = value;

            if(rightCDST.test(value))eventQueue.add(Direction.RIGHT);
            if(leftCDST.test(value))eventQueue.add(Direction.LEFT);
        }else{
            isFiredY = true;
            lastValY = value;

            if(upCDST.test(value))eventQueue.add(Direction.UP);
            if(downCDST.test(value))eventQueue.add(Direction.DOWN);
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

    final void clear(){
        rightCDST.reset();
        leftCDST.reset();
        upCDST.reset();
        downCDST.reset();
        eventQueue.clear();
    }
}
