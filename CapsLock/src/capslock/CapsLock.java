package capslock;

import TrivialCmmonLogger.LogHandler;
import java.io.IOException;
import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * エントリポイント.
 */
public final class CapsLock extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final LogHandler logger = LogHandler.inst;
        logger.info("CapsLock started.");
        try{
            launch(args);
        }catch(Exception ex){
            logger.DumpStackTrace(ex);
        }
        logger.info("CapsLock terminated.");
        logger.close();
    }
    
    @Override
    public void start(Stage stage){
        LogHandler.inst.finer("Application#start called.");

        final FXMLLoader loader;
        try{
            loader = new FXMLLoader(getClass().getResource("MainForm.fxml"));
        }catch(Exception ex){
            LogHandler.inst.severe("Failed to get resource.");
            LogHandler.inst.DumpStackTrace(ex);
            return;
        }
        
        final Parent root;

        try {
            root = loader.load();
        } catch (IOException ex) {
            LogHandler.inst.severe("Failed to load MainForm.fxml");
            LogHandler.inst.DumpStackTrace(ex);
            return;
        }
        
        final MainFormController controller = (MainFormController)loader.getController();
        stage.setScene(new Scene(root));
        stage.setOnShown(event -> controller.onLoad(event));
        stage.setTitle("CapsLock");
        stage.setFullScreen(true);
        stage.setAlwaysOnTop(true);
        LogHandler.inst.finest("try to display MainForm window.");
        stage.show();
    }
}
