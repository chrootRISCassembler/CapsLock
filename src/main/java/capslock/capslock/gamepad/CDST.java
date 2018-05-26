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
    static final float highThreshold = 0.6f;
    static final float lowThreshold = 0.4f;
    static final byte INITIAL_COUNT_FIRST = -30;
    static final byte REPEATED_COUNT_FIRST = 50;

    boolean state = false;
    byte countdown = INITIAL_COUNT_FIRST;

    final void reset(){
        state = false;
        countdown = INITIAL_COUNT_FIRST;
    }

    abstract boolean test(float val);

    static CDST positive(){
        return new PositiveCDST();
    }

    static CDST negative(){
        return new NegativeCDST();
    }
}
