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
import methg.commonlib.trivial_logger.Logger;

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
        Logger.INST.critical("Utility class' instance \"CharPanelGenerator\" is created! Call the developer!");
    }
    
    /**
     * Generates a panel image form char.
     * <p>First, this function converts ch to upper case if ch is lower case.</p>
     * <p>Then, this generates javafx's image from ch.And return it.</p>
     * You can fix the resolution of image through {@link capslock.CharPanelGenerator#PANEL_IMAGE_SIZE}
     * and {@link capslock.CharPanelGenerator#FONT_SIZE}.
     * @param ch パネルの生成に使う1文字.
     * @param color 背景色.
     * @return 生成されたパネル.
     */
    static final Image generate(char ch, Color color){
        final Label label = new Label(Character.toString(Character.toUpperCase(ch)));
        label.setMinSize(PANEL_IMAGE_SIZE, PANEL_IMAGE_SIZE);
        label.setMaxSize(PANEL_IMAGE_SIZE, PANEL_IMAGE_SIZE);
        label.setPrefSize(PANEL_IMAGE_SIZE, PANEL_IMAGE_SIZE);
        label.setFont(Font.font(FONT_SIZE));
        label.setAlignment(Pos.CENTER);
        label.setTextFill(Color.WHITE);
        label.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        final Scene scene = new Scene(new Group(label));
        final WritableImage img = new WritableImage(PANEL_IMAGE_SIZE, PANEL_IMAGE_SIZE);
        scene.snapshot(img);
        return img ;
    }
}
