package capslock.capslock.gamepad;

abstract class CDST {
    static final byte COUNTDOWN_WAIT = 10;
    boolean state = false;
    byte countdown;

    boolean get(){
        if(!state)return false;

        if(countdown == 0){
            countdown = COUNTDOWN_WAIT;
            return true;
        }
        countdown--;
        return false;
    }

    abstract boolean test(float val);

    static CDST positive(float low, float high){
        return new PositiveCDST(low, high);
    }

    static CDST negative(float low, float high){
        return new NegativeCDST(low, high);
    }
}
