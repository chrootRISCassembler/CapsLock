package capslock.capslock.gamepad;

class NegativeCDST extends CDST {
    final private float highThreshold;
    final private float lowThreshold;
    NegativeCDST(float low, float high){
        lowThreshold = low;
        highThreshold = high;
    }

    @Override
    boolean test(float val) {
        if(state){//High state
            if(val > lowThreshold){
                state = false;
            }
        }else {//Low state
            if(val < highThreshold){
                state = true;
                countdown = COUNTDOWN_WAIT;
                return true;
            }
        }
        return false;
    }
}