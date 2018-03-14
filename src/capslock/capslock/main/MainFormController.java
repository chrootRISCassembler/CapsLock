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

import capslock.game_info.Game;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import methg.commonlib.trivial_logger.Logger;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * メインフォームのFXMLコントローラークラス.
 */
public final class MainFormController{

    /** Constants */
    private static final double PANEL_RATIO = 0.25;
    private static final double PANEL_GAP_RATIO = 0.03;

    private MainHandler handler;
    private ContentsAreaController contentsAreaController;
    private volatile Game game;

    /** FXML binding */
    @FXML private ScrollPane LeftScrollPane;
        @FXML private TilePane PanelTilePane;
    @FXML private VBox RightVBox;
        @FXML private StackPane ViewStackPane;
            @FXML private volatile ImageView StackedImageView;
            @FXML private volatile MediaView StackedMediaView;
    @FXML private Label NameLabel;
    @FXML private Label DescriptionLabel;
    @FXML private Button playButton;

    void onCreated(MainHandler handler){
        this.handler = handler;

        final double PanelImageSideLength;

        Logger.INST.debug("Start calculation of dynamic UI.");
        {
            final Rectangle2D ScreenRect = Screen.getPrimary().getVisualBounds();
            final double FullScreenWidth = ScreenRect.getWidth();
            final double FullScreenHeight = ScreenRect.getHeight();
            final double LeftSize = FullScreenWidth / 5 * 2;

            LeftScrollPane.setPrefViewportWidth(LeftSize);
            LeftScrollPane.setMinViewportWidth(LeftSize);

            PanelImageSideLength = LeftSize * PANEL_RATIO;

            final double Gap = LeftSize * PANEL_GAP_RATIO;
            PanelTilePane.setPadding(new Insets(LeftSize / 12));
            PanelTilePane.setVgap(Gap);
            PanelTilePane.setHgap(Gap);

            final double RightContentPadding = (FullScreenWidth - LeftSize) / 20;
            RightVBox.setPadding(new Insets(RightContentPadding));
            RightVBox.setMaxWidth(FullScreenWidth - LeftSize);

            NameLabel.setFont(Font.font(FullScreenHeight / 20));
            DescriptionLabel.setFont(Font.font(FullScreenHeight/40));
        }
        Logger.INST.debug("Finished calculation of dynamic UI.");


        final ColorSequencer sequencer = new ColorSequencer();
        final Tooltip tooltip = new Tooltip("ダブルクリックでゲーム起動");
        for(final Game game : handler.getGameList()){

            final Image panelImage;
            final Path panelPath = game.getPanel();
            if(panelPath == null){
                panelImage = CharPanelGenerator.generate(game.getName().charAt(0), sequencer.get());
            }else{
                panelImage = new Image(panelPath.toUri().toString());
            }

            final ImageView view = new ImageView(panelImage);

            view.setPreserveRatio(false);
            view.setFitWidth(PanelImageSideLength);
            view.setFitHeight(PanelImageSideLength);
            view.setOnMouseClicked(eve -> onPanelClicked(eve));
            Tooltip.install(view, tooltip);
            view.setUserData(game);
            PanelTilePane.getChildren().add(view);
        }

        contentsAreaController = new ContentsAreaController(ViewStackPane, StackedMediaView, StackedImageView);

        Logger.INST.debug("MainForm window is displayed.");
        System.gc();
    }

    void onGameLaunched(){
        contentsAreaController.suspend();
    }

    void onLaunchFailed(){

    }

    void onGameQuit(){
        contentsAreaController.resume();
    }

    @FXML
    protected void onButtonClick(ActionEvent evt) {
        handler.launch(game);
    }

    void onPanelClicked(MouseEvent event){
        if(!event.getButton().equals(MouseButton.PRIMARY))return;//右クリックじゃない

        final ImageView view = (ImageView)event.getSource();//クリックされたパネルの取得
        final Game NextGame = (Game)view.getUserData();//パネルが示すゲーム

        if(game != NextGame) {

            PanelTilePane.getChildren().stream()
                    .peek(panel -> panel.setScaleX(1))
                    .peek(panel -> panel.setScaleY(1))
                    .forEach(panel -> panel.setEffect(null));

            {//選択されたパネルにエフェクトを適応
                view.setScaleX(1.15);
                view.setScaleY(1.15);

                final DropShadow effect = new DropShadow(20, Color.BLUE);//影つけて
                effect.setInput(new Glow(0.5));//光らせる
                view.setEffect(effect);
            }

            game = NextGame;

            Logger.INST.debug("ContentsAreaController#setGame() call");
            contentsAreaController.setGame(game);

            {
                final String name = NextGame.getName();
                NameLabel.setText("[P-" + NextGame.getGameID() + "]" + name);
            }

            if (NextGame.getDesc() == null) {
                Logger.INST.debug("No desc!");
            }

            DescriptionLabel.setText(NextGame.getDesc());
            DescriptionLabel.setPadding(Insets.EMPTY);
            DescriptionLabel.autosize();
        }

        if(event.getClickCount() != 2)return;//ダブルクリックじゃない

        handler.launch(game);
    }
    
    final void ShufflePanels(){
        System.out.println("Shuffle called");
        
        final int last = PanelTilePane.getChildren().size();   
        final Node FirstView = PanelTilePane.getChildren().get(0);
        final List<Node> views = PanelTilePane.getChildren().subList(1, last).stream()
                .map(node -> (ImageView)node)
                .collect(Collectors.toList());
        
        PanelTilePane.getChildren().clear();
        Collections.shuffle(views);
        PanelTilePane.getChildren().add(FirstView);   
        PanelTilePane.getChildren().addAll(views);
        
        System.out.println("shuffle end");
    }

    /**
     * ランチャー終了時に呼び出される.
     */
    final void onTerminate(){
        contentsAreaController.kill();
    }
}
