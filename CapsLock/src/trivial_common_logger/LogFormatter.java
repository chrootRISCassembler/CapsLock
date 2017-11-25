package trivial_common_logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 *
 * @author RISCassembler
 */
public class LogFormatter extends Formatter{
    private final SimpleDateFormat TimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    @Override
    public String format(LogRecord lr){
        final StringBuffer buf = new StringBuffer();

        buf.append(TimeFormat.format(
            new Date(lr.getMillis())));
        buf.append(" [");
        buf.append(lr.getLevel());
        buf.append("] ");
        buf.append(lr.getMessage());
        buf.append(System.lineSeparator());

        return buf.toString();
    }
}
