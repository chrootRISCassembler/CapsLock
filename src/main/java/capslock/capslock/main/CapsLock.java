/*
    Copyright (C) 2018 RISCassembler

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package capslock.capslock.main;

import capslock.capslock.os_absorbing.NativeBinaryExtractor;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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


    /**
     * デフォルトのエントリポイント.
     * 別アプリケーションからこのプログラムを起動するときは{@link CapsLock#InjectionPoint}から起動する.
     * @param args 変な引数を注入されてもいいよう,無視する.
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

        try {
            Logger.INST.setLogFile(Paths.get("./Capslock.log"));
        }catch (IOException ex){
            Logger.INST.logException(ex);
        }

        Logger.INST.info("CapsLock started.");


        if(System.getProperty("java.library.path").contains("dependentBinary")){
            Logger.INST.debug("It can be launched by Gradle");
        }else{
            NativeBinaryExtractor.extractBinaries();
        }

        MainHandler.INST.loadJSONDB();

        launch();


        Logger.INST.info("CapsLock terminated.");
        Logger.INST.flush();
    }

    @Override
    public final void start(Stage stage){
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
            final ResourceObserver resourceObserver=new ResourceObserver();
            resourceObserver.Launch();

            final MainFormController controller = loader.getController();

            MainHandler.INST.setController(controller);

            final Scene scene=new Scene(root);
            stage.setScene(scene);
            stage.setOnShown(event -> controller.onShown());
            stage.setOnCloseRequest(event -> MainHandler.INST.onTerminate());
            stage.setTitle("CapsLock");
            stage.setFullScreen(true);
            stage.setAlwaysOnTop(true);

            Logger.INST.debug("Try to display MainForm window.");
            stage.show();
            controller.initFirst();
        }catch (Exception ex){
            Logger.INST.logException(ex);
        }
    }

    /**
     * 他のJavaFXアプリケーションからこのプログラムを起動する時のエントリポイント.
     * JVM上で@{link {@link Application#launch(String...)}}を2回呼び出しては行けないため,この関数から起動する
     */
    public final void InjectionPoint(Stage stage, Path JSONPath, String realGameRootDir){
        Logger.INST.info("CapsLock#InjectionPoint in");
        MainHandler.INST.loadJSONDB(JSONPath, realGameRootDir);
        start(stage);
        Logger.INST.info("CapsLock#InjectionPoint out");
    }

    static ExecutorService getExecutor() {
        return executor;
    }
}
