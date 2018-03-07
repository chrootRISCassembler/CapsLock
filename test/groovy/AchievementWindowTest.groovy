import capslock.capslock.main.AchievementWindow
import javafx.scene.image.Image
import javafx.stage.Stage
import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec
import methg.commonlib.trivial_logger.LogLevel
import methg.commonlib.trivial_logger.Logger

import java.nio.file.Paths

class AchievementWindowTest extends ApplicationSpec {
    def achievementWindow

    @Override
    void init() throws Exception {
        Logger.INST.setCurrentLogLevel(LogLevel.DEBUG)
        FxToolkit.registerStage {
            new Stage()
        }
    }

    @Override
    void start(Stage stage) {
        final Image icon = new Image(Paths.get("./hourglass.png").toUri().toString())
        achievementWindow = new AchievementWindow(icon, "message")
        achievementWindow.display()
    }

    @Override
    void stop() throws Exception {

    }

    def 'test'(){
        expect:
        1 == 1
        achievementWindow.waitForClose()
    }
}
