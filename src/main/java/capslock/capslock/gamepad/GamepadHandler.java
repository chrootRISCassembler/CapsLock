package capslock.capslock.gamepad;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class GamepadHandler {
    private final Controller gamepad;

    private enum XAxisInput{
        RIGHT,
        NEUTRAL,
        LEFT;
    }

    private enum YAxisInput{
        UP,
        NEUTRAL,
        DOWN;
    }

    public GamepadHandler(){
        gamepad = getGamepad();
        if(gamepad == null)return;

    }

    private Controller getGamepad(){
        for (final var controller : ControllerEnvironment.getDefaultEnvironment().getControllers()) {
            if (controller != null && controller.getType() == Controller.Type.GAMEPAD) {
                return controller;
            }
        }
        return null;
    }

    public final void pool(){
        System.out.println("pool called");
        if(gamepad == null)return;

        System.out.println("pool not return");

        if(gamepad.poll()){
            final var Xinput = getXAxisInput();
            switch (Xinput){
                case RIGHT:
                case LEFT:
            }

            final var Yinput = getYAxisInput();
            switch (Yinput){
                case UP:
                case DOWN:
            }
        }
        System.out.println("pool OK");
    }

    private XAxisInput getXAxisInput(){
        final float x = gamepad.getComponent(Component.Identifier.Axis.X).getPollData();
        if(x > 1.0f){
            return XAxisInput.RIGHT;
        }else if(x < -1.0f){
            return XAxisInput.LEFT;
        }
        return XAxisInput.NEUTRAL;
    }

    private YAxisInput getYAxisInput(){
        final float y = gamepad.getComponent(Component.Identifier.Axis.Y).getPollData();
        if(y > 1.0f){
            return YAxisInput.UP;
        }else if(y < -1.0f){
            return YAxisInput.DOWN;
        }
        return YAxisInput.NEUTRAL;
    }
}
