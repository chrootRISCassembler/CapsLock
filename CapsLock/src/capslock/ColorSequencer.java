package capslock;

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