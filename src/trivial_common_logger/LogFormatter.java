/*  
    This file is part of CapsLock. CapsLock is a simple game launcher.
    Copyright (C) 2017 RISCassembler, domasyake, su-u

    CapsLock is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
*/

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
