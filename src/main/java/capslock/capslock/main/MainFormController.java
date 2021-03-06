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

import capslock.capslock.gamepad.Gamepad;
import capslock.capslock.gamepad.GamepadHandler;
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
import javafx.scene.effect.Effect;
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

/**
 * メインフォームのFXMLコントローラークラス.
 */
public final class MainFormController{

    /** Constants */
    private static final double PANEL_RATIO = 0.25;
    private static final double PANEL_GAP_RATIO = 0.03;

    private boolean onCreatedCalled = false;
    private ContentsAreaController contentsAreaController;
    private volatile ImageView panelView;

    private GamepadHandler gamepadHandler;
    private boolean isConfirm = false;
    private boolean isLaunchSelected = false;

    /** FXML binding */
    @FXML private ScrollPane LeftScrollPane;
        @FXML private TilePane PanelTilePane;
    @FXML private VBox RightVBox;
        @FXML private StackPane ViewStackPane;
            @FXML private volatile ImageView StackedImageView;
            @FXML private volatile MediaView StackedMediaView;
    @FXML private Label NameLabel;
    @FXML private Label DescriptionLabel;

    @FXML private VBox confirmVBox;
    @FXML private Label confirmLabel;
    @FXML private Button cancelButton;
    @FXML private Button OKButton;

    final void onShown(){
        if(onCreatedCalled)return;

        onCreatedCalled = true;
        onCreated();
    }

    /**
     * 初めてウィンドウが表示されたときに呼び出される.UIの初期化を行う.
     */
    private void onCreated(){

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
        for(final Game game : MainHandler.INST.getGameList()){

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
            view.setOnMouseClicked(this::onPanelClicked);
            Tooltip.install(view, tooltip);
            view.setUserData(game);
            PanelTilePane.getChildren().add(view);
        }

        contentsAreaController = new ContentsAreaController(ViewStackPane, StackedMediaView, StackedImageView);

        Logger.INST.debug("MainForm window is displayed.");

        gamepadHandler = new GamepadHandler(new Gamepad() {
            private final Effect selectedButtonEffect;

            {
                final var effect = new DropShadow(20, Color.BLUE);//影つけて
                effect.setInput(new Glow(0.5));//光らせる
                selectedButtonEffect = effect;
            }

            @Override
            public final void onOkButtonReleased() {
                if(isConfirm){
                    if(isLaunchSelected){
                        gamepadHandler.disable();
                        MainHandler.INST.launch((Game) panelView.getUserData());
                    }

                    confirmVBox.setVisible(false);
                    isConfirm = false;
                    isLaunchSelected = false;
                } else {
                    final Game selectedGame = (Game)panelView.getUserData();
                    confirmLabel.setText("ID " + selectedGame.getGameID() + " 「" + selectedGame.getName() + "」\nこのゲームを遊びますか?");
                    confirmVBox.setVisible(true);
                    isConfirm = true;

                    unperkButton(OKButton);
                    perkButton(cancelButton);
                }
            }

            @Override
            public final void onCancelButtonReleased() {
                isConfirm = false;
                isLaunchSelected = false;
                confirmVBox.setVisible(false);
            }

            private void perkButton(Button button){
                button.setScaleX(1.15);
                button.setScaleY(1.15);
                button.setEffect(selectedButtonEffect);
            }

            private void unperkButton(Button button){
                button.setScaleX(1);
                button.setScaleY(1);
                button.setEffect(null);
            }

            @Override
            public final void onRight() {
                if(isConfirm) {
                    unperkButton(cancelButton);
                    perkButton(OKButton);
                    isLaunchSelected = true;

                } else {
                    final int nextIndex = PanelTilePane.getChildren().indexOf(panelView) + 1;
                    if (nextIndex % 3 == 0) return;
                    if (nextIndex == PanelTilePane.getChildren().size()) return;

                    final var panel = (ImageView)PanelTilePane.getChildren().get(nextIndex);
                    autoScroll(panel);
                    emulateClick(panel);
                }
            }

            @Override
            public final void onLeft() {
                if(isConfirm) {
                    unperkButton(OKButton);
                    perkButton(cancelButton);
                    isLaunchSelected = false;
                } else {
                    final int currentIndex = PanelTilePane.getChildren().indexOf(panelView);
                    if (currentIndex % 3 == 0) return;

                    final var panel = (ImageView)PanelTilePane.getChildren().get(currentIndex - 1);
                    autoScroll(panel);
                    emulateClick(panel);
                }
            }

            @Override
            public final void onUp() {
                if(isConfirm)return;
                final int nextIndex = PanelTilePane.getChildren().indexOf(panelView) - 3;
                if(nextIndex >= 0){
                    final var panel = (ImageView)PanelTilePane.getChildren().get(nextIndex);
                    autoScroll(panel);
                    emulateClick(panel);
                }
            }

            @Override
            public final void onDown() {
                if(isConfirm)return;
                final int nextIndex = PanelTilePane.getChildren().indexOf(panelView) + 3;
                if(nextIndex < PanelTilePane.getChildren().size()){
                    final var panel = (ImageView)PanelTilePane.getChildren().get(nextIndex);
                    autoScroll(panel);
                    emulateClick(panel);
                }
            }

            /**
             * 選択されたパネル画像が範囲外で隠れてしまいそうなとき,　自動的にスクロールを行う.
             * @param panelView 選択されているパネル画像の{@link ImageView}
             */
            private void autoScroll(ImageView panelView) {
                final double h = LeftScrollPane.getContent().getBoundsInLocal().getHeight();
                final double y = (panelView.getBoundsInParent().getMaxY() + panelView.getBoundsInParent().getMinY()) / 2.0;
                final double v = LeftScrollPane.getViewportBounds().getHeight();
                LeftScrollPane.setVvalue(LeftScrollPane.getVmax() * ((y - 0.5 * v) / (h - v)));
            }

        }, CapsLock.getExecutor());

        confirmVBox.setStyle("-fx-background-color: rgba(50,50,50,1); -fx-background-radius: 1em; -fx-border-color: CORNFLOWERBLUE; -fx-border-radius: 1em;");
    }

    /**
     * {@link javafx.stage.Stage}が表示された直後の処理を行う
     */
    final void initFirst(){
        emulateClick(PanelTilePane.getChildren().get(0));
    }

    final void onGameLaunched(){
        contentsAreaController.suspend();
    }

    final void onLaunchFailed(){
        gamepadHandler.enable();
    }

    final void onGameQuit(){
        gamepadHandler.enable();
        contentsAreaController.resume();
    }

    @FXML
    private void onCancelButtonClicked(ActionEvent event){
        isConfirm = false;
        confirmVBox.setVisible(false);
    }

    @FXML
    private void onOKButtonClicked(ActionEvent event){
        MainHandler.INST.launch((Game) panelView.getUserData());
        isConfirm = false;
        confirmVBox.setVisible(false);
    }

    final void onPanelClicked(MouseEvent event){
        if(!event.isSynthesized()){
            confirmVBox.setVisible(false);
            isConfirm = false;
            isLaunchSelected = false;
        }
        if(!event.getButton().equals(MouseButton.PRIMARY))return;//右クリックじゃない

        final ImageView eventSourcePanel = (ImageView)event.getSource();//クリックされたパネルの取得
        final var nextGame = (Game)eventSourcePanel.getUserData();

        if(panelView != eventSourcePanel){

            PanelTilePane.getChildren().stream()
                    .peek(panel -> panel.setScaleX(1))
                    .peek(panel -> panel.setScaleY(1))
                    .forEach(panel -> panel.setEffect(null));

            {//選択されたパネルにエフェクトを適応
                eventSourcePanel.setScaleX(1.15);
                eventSourcePanel.setScaleY(1.15);

                final DropShadow effect = new DropShadow(20, Color.BLUE);//影つけて
                effect.setInput(new Glow(0.5));//光らせる
                eventSourcePanel.setEffect(effect);
            }

            panelView = eventSourcePanel;

            contentsAreaController.setGame((Game) panelView.getUserData());

            {
                final String name = nextGame.getName();
                NameLabel.setText("[P-" + nextGame.getGameID() + "]" + name);
            }

            if (nextGame.getDesc() == null) {
                Logger.INST.debug("No desc!");
            }

            DescriptionLabel.setText(nextGame.getDesc());
            DescriptionLabel.setPadding(Insets.EMPTY);
            DescriptionLabel.autosize();
        }

        if(event.getClickCount() != 2)return;//ダブルクリックじゃない

        gamepadHandler.disable();
        MainHandler.INST.launch((Game)eventSourcePanel.getUserData());
    }

    /**
     * ランチャー終了時に呼び出される.
     */
    final void onTerminate(){
        contentsAreaController.kill();
    }

    /**
     * ゲームパッド用, マウスクリックをエミュレートする.
     * @param target クリックイベントを発生させる対象の{@link Node}
     */
    private void emulateClick(Node target){
        onPanelClicked(new MouseEvent(
                target,
                null,
                MouseEvent.MOUSE_CLICKED,
                0,
                0,
                0,
                0,
                MouseButton.PRIMARY,
                1,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                true,
                false,
                false,
                null
        ));
    }
}
