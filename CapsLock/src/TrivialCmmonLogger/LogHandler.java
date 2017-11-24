package TrivialCmmonLogger;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ロガー.
 * <p>ファイルに書き込みたいログはここに投げる.
 * LastStackTrace.logには例外発生時のスタックトレースを書き込む.
 * log.txtには人間が読みやすい形のエラーを吐く.</p>
 */
public enum LogHandler{
    inst;
    
    private FileHandler handler;
    
    private final Logger logger;
    private PrintStream StackOutStream;
    
    private LogHandler() {
        logger = Logger.getLogger("DefaultLogger");

        try {
            StackOutStream = new PrintStream(Files.newOutputStream(Paths.get("./LastStackTrace.log")));
        } catch (IOException | SecurityException ex) {
            System.err.println(ex);
        }
        
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
    
    public final void close(){handler.close();}
    public final void severe(String msg){
        System.err.println(msg);
        logger.severe(msg);
    }
    public final void warning(String msg){
        System.err.println(msg);
        logger.warning(msg);
    }
    public final void info(String msg){
        System.err.println(msg);
        logger.info(msg);
    }
    public final void config(String msg){
        System.err.println(msg);
        logger.config(msg);
    }
    public final void fine(String msg){
        System.err.println(msg);
        logger.fine(msg);
    }
    public final void finer(String msg){
        System.err.println(msg);
        logger.finer(msg);
    }
    public final void finest(String msg){
        System.err.println(msg);
        logger.finest(msg);
    }
    
    public final void severe(Object object){logger.severe(object.toString());}
    public final void warning(Object object){logger.warning(object.toString());}
    public final void info(Object object){logger.info(object.toString());}
    public final void config(Object object){logger.config(object.toString());}
    public final void fine(Object object){logger.fine(object.toString());}
    public final void finer(Object object){logger.finer(object.toString());}
    public final void finest(Object object){logger.finest(object.toString());}
    
    public final void DumpStackTrace(Exception ex){
        ex.printStackTrace(StackOutStream);
    }
}
