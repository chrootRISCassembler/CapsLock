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

final class PositiveCDST extends CDST {
    @Override
    boolean test(float value) {
        if(state){
            if(value < lowThreshold){
                state = false;
                countdown = INITIAL_COUNT_FIRST;
            }else {
                --countdown;
                if(countdown == 0){
                    countdown = REPEATED_COUNT_FIRST;
                    return true;
                }
            }
        }else {
            if(value > highThreshold){
                state = true;
                return true;
            }
        }
        return false;
    }
}
