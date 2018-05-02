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

package capslock.capslock.main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Generates a square panel image from char.
 * <p>This class is utility class.MUST NOT create the instance.</p>
 * @author RISCassembler
 */
final class CharPanelGenerator{
    private static final int PANEL_IMAGE_SIZE = 200;
    private static final double FONT_SIZE = 130;
    
    /**
     * You MUST NOT create the instance of this class.
     */
    private CharPanelGenerator(){
        assert false : "DO NOT create instance of CharPanelGenerator";
    }
    
    /**
     * 文字からパネル画像を生成する.
     * <p>引数の文字を大文字にし, その文字を使って{@link Image}を生成して返す.</p>
     * <p>{@link #PANEL_IMAGE_SIZE}と{@link #FONT_SIZE}で生成する画像の解像度を変更できる.</p>
     * @param ch パネルの生成に使う1文字.
     * @param color 背景色.
     * @return 生成されたパネル.
     */
    static Image generate(char ch, Color color){
        final var label = new Label(Character.toString(Character.toUpperCase(ch)));
        label.setMinSize(PANEL_IMAGE_SIZE, PANEL_IMAGE_SIZE);
        label.setMaxSize(PANEL_IMAGE_SIZE, PANEL_IMAGE_SIZE);
        label.setPrefSize(PANEL_IMAGE_SIZE, PANEL_IMAGE_SIZE);
        label.setFont(Font.font(FONT_SIZE));
        label.setAlignment(Pos.CENTER);
        label.setTextFill(Color.WHITE);
        label.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        final var scene = new Scene(new Group(label));
        final var img = new WritableImage(PANEL_IMAGE_SIZE, PANEL_IMAGE_SIZE);
        scene.snapshot(img);
        return img ;
    }
}
