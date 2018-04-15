package capslock.capslock.gamepad;

public interface Gamepad {
    default void onRight(){}
    default void onLeft(){}
    default void onUp(){}
    default void onDown(){}
    default void onOkButtonReleased(){}
    default void onCancelButtonReleased(){}
}
