package capslock.capslock.main;

import com.sun.management.OperatingSystemMXBean;
import methg.commonlib.trivial_logger.Logger;

import javax.management.MBeanServerConnection;
import java.io.IOException;
import java.lang.management.ManagementFactory;


///<summary>
///I need to static osMBean,so i created this class.
///</summary>
public class OsMBeanWrapper {
    private static OperatingSystemMXBean osMBean;

    private static void SetMBean(){
        if(osMBean!=null)return;
        try{
            MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();
            osMBean = ManagementFactory.newPlatformMXBeanProxy(
                    mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
        }catch (IOException e){
            Logger.INST.logException(e);
        }
    }

    public static double GetSystemCpuLoad(){
        SetMBean();
        return osMBean.getSystemCpuLoad();
    }
    public static long GetTotalPhysicalMemorySize(){
        SetMBean();
        return osMBean.getTotalPhysicalMemorySize();
    }
    public static long GetFreePhysicalMemorySize(){
        SetMBean();
        return osMBean.getFreePhysicalMemorySize();
    }

    //I can not determine whether I can increase a small class,but this method is outside the responsibility of this class.
    public static String GetSizeStr(long size) {
        if (1024 > size) {
            return size + " Byte";
        } else if (1024 * 1024 > size) {
            double dsize = size;
            dsize = dsize / 1024;
            double value = Math.floor(dsize);
            return value + " KByte";
        } else{
            double dsize = size;
            dsize = dsize / 1024 / 1024;
            double value = Math.floor(dsize);
            return value + " MB";
        }
    }
}

