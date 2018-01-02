/*  
    This file is part of CapsLock. CapsLock is a simple game launcher.
    Copyright (C) 2017 RISCassembler, domasyake, su-u

    CapsLock is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
*/

package capslock;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import trivial_common_logger.LogHandler;

/**
 * エントリポイント.
 */
public final class CapsLock extends Application {

    /**
     * @param args the command line arguments
     */
	private WarningTimer warning=new WarningTimer();

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
        /*final Canvas canvas=new Canvas();
        canvas.setOnMouseClicked(event -> System.err.print("mouse_clicked"));*/
        final Scene scene=new Scene(root);
        scene.setOnKeyPressed(event -> PushKey(event, controller));
        EventHandler<MouseEvent>    sceneClickFilter= ( event ) -> warning.Start();
        scene.addEventFilter( MouseEvent.MOUSE_PRESSED , sceneClickFilter );
        stage.setScene(scene);
        stage.setOnShown(event -> controller.onLoad(event));
        stage.setTitle("CapsLock");
        stage.setFullScreen(true);
        stage.setAlwaysOnTop(true);
        LogHandler.inst.finest("try to display MainForm window.");
        stage.show();
    }
    private void  PushKey(KeyEvent event, MainFormController controller) {
    	final KeyCode code = event.getCode();
        switch(code){
            case F1:
                System.err.println("F1_Key_Pushed");
                controller.ShufflePanels();
                warning.Stop();
                break;
            default:
                break;
	}
    }
}
