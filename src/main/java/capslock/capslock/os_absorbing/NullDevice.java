package capslock.capslock.os_absorbing;

import java.io.File;

public class NullDevice {
    static public File getAsFile(){
        return System.getProperty("os.name").toLowerCase().equals("windows") ?
                new File("nul") : new File("/dev/null");
    }
}
