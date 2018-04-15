package capslock.capslock.gamepad;

import methg.commonlib.trivial_logger.Logger;
import net.java.games.input.*;

public class GamepadHandler {
    private final Controller gamepadController;
    private final Gamepad notionalGamepad;
    private CDST rightCDST = CDST.positive(0.4f, 0.7f);
    private CDST leftCDST = CDST.negative(-0.4f, -0.7f);
    private CDST upCDST = CDST.negative(-0.4f, -0.7f);
    private CDST downCDST = CDST.positive(0.4f, 0.7f);

    public GamepadHandler(Gamepad gamepad) {
        notionalGamepad = gamepad;
        gamepadController = getGamepadController();
        if (gamepadController == null) return;

        Logger.INST.info("Gamepad is found.");
    }

    private Controller getGamepadController() {
        for (final var controller : ControllerEnvironment.getDefaultEnvironment().getControllers()) {
            if (controller != null && controller.getType() == Controller.Type.GAMEPAD) {
                return controller;
            }
        }
        return null;
    }

    public final void pool() {
        if (gamepadController == null) return;

        if (!gamepadController.poll()) {
            Logger.INST.warn("Gamepad is not valid");
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
}
