package capslock;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ロガー.
 * <p>ファイルに書き込みたいログはここに投げる</p>
 */
enum LogHandler{
    inst;
    
    private FileHandler handler;
    private final Logger logger;
    
    private LogHandler() {
        logger = Logger.getLogger("DefaultLogger");

        try {
            handler = new FileHandler("log.txt", true);
        } catch (IOException | SecurityException ex) {
            System.err.println(ex);
            System.exit(1);
        }
        
        handler.setFormatter(new LogFormatter());
        logger.setLevel(Level.ALL);
        logger.addHandler(handler);
    }
    
    final void close(){handler.close();}
    final void severe(String msg){logger.severe(msg);}
    final void warning(String msg){logger.warning(msg);}
    final void info(String msg){logger.info(msg);}
    final void config(String msg){logger.config(msg);}
    final void fine(String msg){logger.fine(msg);}
    final void finer(String msg){logger.finer(msg);}
    final void finest(String msg){logger.finest(msg);}
    
    final void severe(Object object){logger.severe(object.toString());}
    final void warning(Object object){logger.warning(object.toString());}
    final void info(Object object){logger.info(object.toString());}
    final void config(Object object){logger.config(object.toString());}
    final void fine(Object object){logger.fine(object.toString());}
    final void finer(Object object){logger.finer(object.toString());}
    final void finest(Object object){logger.finest(object.toString());}
}
