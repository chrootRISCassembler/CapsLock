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

abstract class CDST {
    static final byte COUNTDOWN_WAIT = 40;
    boolean state = false;
    byte countdown;

    boolean get(){
        if(!state)return false;

        if(countdown == 0){
            countdown = COUNTDOWN_WAIT;
            return true;
        }
        countdown--;
        return false;
    }

    void reset(){
        state = false;
    }

    abstract boolean test(float val);

    static CDST positive(float low, float high){
        return new PositiveCDST(low, high);
    }

    static CDST negative(float low, float high){
        return new NegativeCDST(low, high);
    }
}
