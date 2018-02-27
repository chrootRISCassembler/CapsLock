import capslock.AchievementWindow
import javafx.stage.Stage
import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec
import trivial_logger.LogLevel
import trivial_logger.Logger

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
        achievementWindow = new AchievementWindow(null, "message")
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
