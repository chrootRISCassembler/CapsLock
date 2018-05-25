package capslock.capslock.main;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import  methg.commonlib.trivial_logger.Logger;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static capslock.capslock.main.OsMBeanWrapper.GetSizeStr;


///<summary>
///It provide process's cpu utilization rate and memory utilization rate with WMIC command by Windows.
///</summary>
public class ProcessResourceObserver {

    private String targetName;
    private final float interval_ms=60000;//60000=1m

    private Timeline timer;
    Pattern pattern = Pattern.compile("(\\d+)");

    public ProcessResourceObserver(String path){
        targetName=GetProcessName(path);
    }

    public void Launch(){
        timer = new Timeline(new KeyFrame(Duration.millis(interval_ms), event -> OutPutLog()));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    public void Close(){
        timer.stop();
        targetName=null;
    }

    private void OutPutLog(){
        if(targetName==null)return;

        Logger.INST.info(GetUsageCpuAndMemoryPercent());
    }

    private String GetProcessName(String target){
        String fileName = new File(target).getName();
        fileName=fileName.replaceFirst("(.exe)","");
        return fileName;
    }

    private String GetUsageCpuAndMemoryPercent() {
        Matcher m=null;
        try {
            m = pattern.matcher(GetUsageString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String cpu="-1";
        if (m.find()){
            cpu= m.group()+"%";
        }else{
           Logger.INST.warn("Dont get Process CPU percent");
        }
        String memory="-1";
        if(m.find()){
            memory=m.group();
        }else{
            Logger.INST.warn("Dont get Process memory percent");
        }
        if(cpu=="-1"&&memory=="-1"){
            return "-----Process Not Found-----";
        }
        final long memorysize=Integer.parseInt(memory);
        String memorybyte=GetSizeStr(memorysize);
        memory=String.format("%.2f",CalcUsageMemoryPercent(memorysize));

        return "ProcessName:"+targetName+",CPU:"+cpu+"%,Memory:"+memorybyte+"("+memory+")";
    }

    private String GetUsageString() throws IOException {
        InputStream inputStream = RunWmicOnCmd();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder sb = new StringBuilder();

        String line;

        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        bufferedReader.close();
        return sb.toString();
    }

    private InputStream RunWmicOnCmd(){
        final String wmiccommand="WMIC PATH Win32_PerfFormattedData_PerfProc_Process WHERE \"Name LIKE'"
                +targetName+"'\" GET PercentUserTime,WorkingSetPrivate/FORMAT:LIST";

        final String[] Command = { "cmd", "/c", wmiccommand};
        Runtime runtime = Runtime.getRuntime();
        Process cmdprocess=null;

        try {
            cmdprocess=runtime.exec(Command);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cmdprocess.getInputStream();
    }

    private double CalcUsageMemoryPercent(long memory){
        final double percent=(((double)memory/ OsMBeanWrapper.GetTotalPhysicalMemorySize())*100);
        return percent;
    }
}
