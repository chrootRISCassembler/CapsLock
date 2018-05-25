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

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import methg.commonlib.trivial_logger.Logger;
import net.java.games.input.*;

import java.util.concurrent.Executor;

public class GamepadHandler {
    //ゲームパッドが検出されていないとき, 接続されたかどうかの検査間隔時間
    private static final int GAMEPAD_DETECTION_INTERVAL_MS = 1000;

    //ゲームパッドの入力のポーリング間隔時間
    private static final int POLL_INTERVAL_MS = 20;

    private final CDST rightCDST = CDST.positive(0.4f, 0.7f);
    private final CDST leftCDST = CDST.negative(-0.4f, -0.7f);
    private final CDST upCDST = CDST.negative(-0.4f, -0.7f);
    private final CDST downCDST = CDST.positive(0.4f, 0.7f);

    private final ScheduledService<Void> pollService;
    private final Gamepad notionalGamepad;

    private Controller gamepadController;

    public GamepadHandler(Gamepad gamepadBehavior, Executor executor) {
        notionalGamepad = gamepadBehavior;
        gamepadController = getGamepadController();

        pollService = new ScheduledService<>() {
            protected Task<Void> createTask() {
                return new Task<>() {
                    protected Void call() {
                        Platform.runLater(GamepadHandler.this::pool);
                        return null;
                    }
                };
            }
        };
        pollService.setExecutor(executor);
        pollService.setDelay(Duration.seconds(1));

        if (gamepadController == null){
            Logger.INST.info("Gamepad is not found.");
            pollService.setPeriod(Duration.millis(GAMEPAD_DETECTION_INTERVAL_MS));
        }else {
            Logger.INST.info("Gamepad is detected.");
            pollService.setPeriod(Duration.millis(POLL_INTERVAL_MS));
        }

        pollService.start();
    }

    private Controller getGamepadController() {
        Controller fallbackController = null;

        for (final var controller : new DirectAndRawInputEnvironmentPlugin().getControllers()) {
            if(controller == null)continue;

            if (controller.getType() == Controller.Type.GAMEPAD) {
                return controller;
            }

            //for F310 gamepad
            if(controller.getType() == Controller.Type.STICK && controller.getName().equals("Logicool Dual Action")){
                fallbackController = controller;
            }
        }
        return fallbackController;
    }

    private void pool() {
        if (gamepadController == null){
            gamepadController = getGamepadController();
            if(gamepadController == null)return;

            Logger.INST.info("Gamepad is detected.");
            pollService.setPeriod(Duration.millis(POLL_INTERVAL_MS));
        }

        if (!gamepadController.poll()) {
            Logger.INST.warn("Gamepad is invalid. It should be disconnected.");
            gamepadController = null;
            pollService.setPeriod(Duration.millis(GAMEPAD_DETECTION_INTERVAL_MS));
            return;
        }

        final EventQueue eventQueue = gamepadController.getEventQueue();
        final var event = new Event();
        while (eventQueue.getNextEvent(event)) {
            final var type = event.getComponent().getIdentifier();

            if (type.equals(Component.Identifier.Axis.X)) {
                final float val = event.getValue();
                if (rightCDST.test(val)){
                    leftCDST.reset();
                    notionalGamepad.onRight();
                }
                if (leftCDST.test(val)){
                    rightCDST.reset();
                    notionalGamepad.onLeft();
                }
            }

            if (type.equals(Component.Identifier.Axis.Y)) {
                final float val = event.getValue();
                if (downCDST.test(val)){
                    upCDST.reset();
                    notionalGamepad.onDown();
                }
                if (upCDST.test(val)){
                    downCDST.reset();
                    notionalGamepad.onUp();
                }
            }

            if (type.equals(Component.Identifier.Button._0)) {
                if (event.getValue() != 0.0f) continue;
                notionalGamepad.onOkButtonReleased();
            }

            if (type.equals(Component.Identifier.Button._1)) {
                if (event.getValue() != 0.0f) continue;
                notionalGamepad.onCancelButtonReleased();
            }
        }

        if(rightCDST.get())notionalGamepad.onRight();
        if(leftCDST.get())notionalGamepad.onLeft();
        if(upCDST.get())notionalGamepad.onUp();
        if(downCDST.get())notionalGamepad.onDown();
    }

    /**
     * ゲームパッドによる操作を有効にする.
     * <p>
     *     ゲームパッドが接続されていない状態から接続されると,
     *     自動的にゲームパッドによる操作が有効になる.
     * </p>
     */
    public final void enable(){
        if(Platform.isFxApplicationThread()){
            pollService.restart();
        }else{
            Platform.runLater(pollService::restart);
        }

        Logger.INST.debug("Gamepad is enabled");
    }

    /**
     * ゲームパッドによる操作を無効にする.
     * <p>
     *     ゲームパッドが抜き差しされるなどのイベントの検知も無効になる.
     * </p>
     */
    public final void disable(){
        assert Platform.isFxApplicationThread() : "This function must be call by the JavaFX Application Thread";
        pollService.cancel();
        Logger.INST.debug("Gamepad is disabled");
    }
}
