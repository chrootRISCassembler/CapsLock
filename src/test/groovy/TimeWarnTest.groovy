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
