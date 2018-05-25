package capslock.capslock.main;

import com.sun.management.OperatingSystemMXBean;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import methg.commonlib.trivial_logger.Logger;

import javax.management.MBeanServerConnection;
import java.io.IOException;
import java.lang.management.ManagementFactory;

///<summary>
///It observe cpu utilization rate and memory utilization rate with MBean in javax.management.MBeanServerConnection.
///Rates provided by this class are approximate value because entrust MBean,so you should be careful.
///</summary>

public class ResourceObserver {

    private final float interval_ms=60000;

    private OperatingSystemMXBean osMBean;


    public ResourceObserver(){
        try{
            MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();
            osMBean = ManagementFactory.newPlatformMXBeanProxy(
                    mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
        }catch (IOException e){
            Logger.INST.logException(e);
        }
    }

    public void Launch(){
        final Timeline timer = new Timeline(new KeyFrame(Duration.millis(interval_ms), event -> OutPutLog()));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void OutPutLog(){
        Logger.INST.info(GetUsageCpuPersent()+","+GetUsageMemoryPersent());
    }

    private String GetUsageCpuPersent(){
        final long percent=(long) (osMBean.getSystemCpuLoad()*100);

        return "Cpu usage: "+percent+"%";
    }

    private String GetUsageMemoryPersent(){
        final long totalmemory=osMBean.getTotalPhysicalMemorySize();
        final long usingmemory=totalmemory-osMBean.getFreePhysicalMemorySize();
        final long persent=(long)((double)usingmemory/totalmemory*100);

        return "Memory usage: "+persent+"%";
    }
}
