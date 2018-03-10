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

package capslock.capslock.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import methg.commonlib.trivial_logger.LogLevel;
import methg.commonlib.trivial_logger.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * エントリポイント.
 */
public final class CapsLock extends Application {
	private static final Path CONFIG_FILE = Paths.get("./config.properties");
    private static final ExecutorService executor = Executors.newWorkStealingPool();
    private MainHandler handler;

    /**
     * @param args コマンドライン引数. confirmモード時はargs[0]にJSONファイルのパス, args[1]にゲームの実行ファイルパス
     *             を渡す.
     */
    public static void main(String[] args) {
        Logger.INST.setCurrentLogLevel(LogLevel.DEBUG);

        try (final Reader reader = Files.newBufferedReader(CONFIG_FILE)){
            final Properties properties = new Properties();
            properties.load(reader);

            final String rawValue = properties.getProperty("LogLevel");
            if(rawValue == null)throw new IOException("LogLevel value is not found.");

            Logger.INST.setCurrentLogLevel(LogLevel.valueOf(rawValue.toUpperCase()));

        }catch (IOException | IllegalArgumentException ex) {
            Logger.INST.warn(() -> "Failed to load config form " + CONFIG_FILE)
                    .warn(() -> "Logger runs default log level : " + Logger.INST.getCurrentLogLevel())
                    .logException(ex);
        }

        Logger.INST.info("CapsLock started.");

        if (args.length == 2){
            Logger.INST.info("Run as confirm mode.");
            MainHandler.INST.loadJSONDB(args[0], args[1]);
        }else{
            MainHandler.INST.loadJSONDB();
        }

        launch(args);

        Logger.INST.info("CapsLock terminated.");
        Logger.INST.flush();
    }

    @Override
    public void start(Stage stage){
        Logger.INST.debug("Application#start called.");

        final FXMLLoader loader;
        try{
            loader = new FXMLLoader(getClass().getResource("MainForm.fxml"));
        }catch(Exception ex){
            Logger.INST.critical("Failed to get resource.");
            Logger.INST.logException(ex);
            return;
        }

        final Parent root;

        try {
            root = loader.load();
        } catch (IOException ex) {
            Logger.INST.critical("Failed to load MainForm.fxml");
            Logger.INST.logException(ex);
            return;
        }


        try {
            final MainFormController controller = (MainFormController)loader.getController();

            MainHandler.INST.setController(controller);

            final Scene scene=new Scene(root);
            scene.setOnKeyPressed(event -> PushKey(event, controller));
            stage.setScene(scene);
            stage.setOnShown(MainHandler.INST::onLoad);
            stage.setTitle("CapsLock");
            stage.setFullScreen(true);
            stage.setAlwaysOnTop(true);

            Logger.INST.debug("Try to display MainForm window.");
            stage.show();
        }catch (Exception ex){
            Logger.INST.logException(ex);
        }
    }

    static ExecutorService getExecutor() {
        return executor;
    }

    private void  PushKey(KeyEvent event, MainFormController controller) {
    	final KeyCode code = event.getCode();
        switch(code){
            case F1:
                System.err.println("F1_Key_Pushed");
                controller.ShufflePanels();
                break;
            default:
                break;
	}
    }
}
