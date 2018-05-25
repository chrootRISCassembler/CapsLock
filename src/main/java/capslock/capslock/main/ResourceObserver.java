package capslock.capslock.main;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import methg.commonlib.trivial_logger.Logger;

import static capslock.capslock.main.OsMBeanWrapper.GetSizeStr;

///<summary>
///It observe cpu utilization rate and memory utilization rate with MBean in javax.management.MBeanServerConnection.
///Rates provided by this class are approximate value because entrust MBean,so you should be careful.
///</summary>

public class ResourceObserver {

    private final float interval_ms=60000;//60000=1m


    public ResourceObserver(){}

    public void Launch(){
        final Timeline timer = new Timeline(new KeyFrame(Duration.millis(interval_ms), event -> OutPutLog()));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void OutPutLog(){
        Logger.INST.info("--PlatformResourceLog--"+GetUsageCpuPersent()+","+GetUsageMemoryPersent());
    }

    private String GetUsageCpuPersent(){
        final double percent=(OsMBeanWrapper.GetSystemCpuLoad()*100);

        return "Cpu:"+String.format("%.2f",percent)+"%";
    }

    private String GetUsageMemoryPersent(){
        final long totalmemory=OsMBeanWrapper.GetTotalPhysicalMemorySize();
        final long usingmemory=totalmemory-OsMBeanWrapper.GetFreePhysicalMemorySize();
        final double percent=((double)usingmemory/(double) totalmemory*100);

        return "Memory:"+GetSizeStr(usingmemory)+"("+String.format("%.2f",percent)+"%)";
    }
}
