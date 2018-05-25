import capslock.capslock.main.AchievementWindow
import javafx.stage.Stage
import methg.commonlib.trivial_logger.LogLevel
import methg.commonlib.trivial_logger.Logger
import org.testfx.api.FxToolkit
import org.testfx.framework.spock.ApplicationSpec

final class TimeWarnTest extends ApplicationSpec {
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
        achievementWindow = new AchievementWindow(null, "プレイ開始から10分経過しました\n混雑している場合は次の方に\nお譲りください")
        achievementWindow.display()
    }

    @Override
    void stop() throws Exception {

    }

    def 'Time-Warn test'(){
        expect:
        1 == 1
        achievementWindow.waitForClose()
    }
}
