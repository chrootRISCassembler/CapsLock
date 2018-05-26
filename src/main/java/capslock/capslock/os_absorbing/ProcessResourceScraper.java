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

package capslock.capslock.os_absorbing;

import capslock.game_info.Game;
import methg.commonlib.trivial_logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 起動したプロセスが使用するリソースの使用を取得する.
 */
public final class ProcessResourceScraper {
    private ProcessResourceScraper(){
        assert false : "DO NOT create instance of ProcessResourceScraper. This is a utility class.";
    }

    static public final class UsageOfResource{
        private long userTime;
        private long kernelTime;
        private long mem;

        private double userTimePercent;
        private double kernelTimePercent;
        private double memPercent;

        public final long getUserCPUTime(){ return userTime; }
        public final double getUserCPUTimePercent(){
            return userTimePercent;
        }

        public final long getKernelCPUTime() {return kernelTime;}
        public final double getKernelCPUTimePercent() {return kernelTimePercent;}

        public final long getMem(){
            return mem;
        }
        public final double getMemPercentage(){
            return memPercent;
        }
    }

    public static UsageOfResource query(Game game){
        if(System.getProperty("os.name").toLowerCase().startsWith("windows")){
            return windowsWay(game);
        }else{
            return new UsageOfResource();
        }
    }

    private static UsageOfResource windowsWay(Game game){
        final String processName;

        {
            final String fileName = game.getExe().getFileName().toString();
            final int lastExtensionIndex = fileName.lastIndexOf(".exe");
            processName = lastExtensionIndex == -1 ? fileName : fileName.substring(0, lastExtensionIndex);
        }

        final String[] command = {"cmd", "/c", "WMIC PATH Win32_PerfFormattedData_PerfProc_Process WHERE \"Name LIKE'"
                + processName + "'\" GET PercentUserTime,PercentPrivilegedTime,WorkingSetPrivate /FORMAT:LIST"};

        final var usage = new UsageOfResource();

        try {
            final Process commandProcess = Runtime.getRuntime().exec(command);
            commandProcess.waitFor();
            final var reader = new BufferedReader(new InputStreamReader(commandProcess.getInputStream()));

            while (true){
                final String line = reader.readLine();
                if(line == null)break;

                final String trimmed = line.trim();

                if(trimmed.isEmpty())continue;

                final int equalCharIndex = trimmed.indexOf('=');

                switch (trimmed.substring(0, equalCharIndex)){
                    case "PercentPrivilegedTime":
                        usage.kernelTimePercent = Double.valueOf(trimmed.substring(equalCharIndex + 1));
                        break;
                    case "PercentUserTime":
                        usage.userTimePercent = Double.valueOf(trimmed.substring(equalCharIndex + 1));
                        break;
                    case "WorkingSetPrivate":
                        usage.mem = Integer.valueOf(trimmed.substring(equalCharIndex + 1));
                        break;
                }
            }

        }catch (IOException ex) {
            Logger.INST.logException(ex);
        }catch (InterruptedException ex){
            Logger.INST.critical("WMIC command process is interrupted").logException(ex);
        }

        usage.memPercent = ((double) usage.mem * 100) / getWholeMemorySize_Windows();

        usage.userTime = -1;
        usage.kernelTime = -1;

        return usage;
    }

    private static long getWholeMemorySize_Windows(){
        final String[] command = {"cmd", "/c", "WMIC memorychip get Capacity /FORMAT:LIST"};

        try {
            final Process commandProcess = Runtime.getRuntime().exec(command);
            commandProcess.waitFor();
            final var reader = new BufferedReader(new InputStreamReader(commandProcess.getInputStream()));

            long memory = 0;

            while (true){
                final String line = reader.readLine();
                if(line == null)break;

                final String trimmed = line.trim();

                if(trimmed.isEmpty())continue;

                memory += Long.parseLong(trimmed.substring(trimmed.indexOf('=') + 1));
            }
            return memory;

        }catch (IOException ex) {
            Logger.INST.logException(ex);
        }catch (InterruptedException ex){
            Logger.INST.critical("WMIC command process is interrupted").logException(ex);
        }
        return 1;
    }
}
