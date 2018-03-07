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

package capslock.capslock.main;

import javafx.scene.paint.Color;

/**
 * 暗めの定義済み色を返す.
 * <p>パネル画像を文字から生成するとき,背景色の指定に使う.</p>
 */
final class ColorSequencer{
    static private final Color[] DARK_COLORS = {
        Color.DARKBLUE,
        Color.DARKCYAN,
        Color.DARKGOLDENROD,
        Color.DARKGRAY,
        Color.DARKGREEN,
        Color.DARKKHAKI,
        Color.DARKMAGENTA,
        Color.DARKOLIVEGREEN,
        Color.DARKORANGE,
        Color.DARKORCHID,
        Color.DARKRED,
        Color.DARKSALMON,
        Color.DARKSEAGREEN,
        Color.DARKSLATEBLUE,
        Color.DARKSLATEGRAY,
        Color.DARKSLATEGREY,
        Color.DARKTURQUOISE,
        Color.DARKVIOLET
    };
    
    static private final int COLOR_MAX_INDEX = DARK_COLORS.length;
    private int ColorIndex = 0;
    
    /**
     * 呼び出すたびに異なる暗めの色を返す.
     */
    final Color get(){
        if(ColorIndex == COLOR_MAX_INDEX)ColorIndex = 0;
        return DARK_COLORS[ColorIndex++];
    }
}