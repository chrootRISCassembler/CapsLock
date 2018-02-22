package trivial_logger;

import file_checker.FileChecker;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Supplier;

public enum Logger {
    INST;

    private LogLevel currentLogLevel = LogLevel.INFO;//デフォルト値
    private Path logFile = null;
    private BufferedWriter writer = null;

    public LogLevel getCurrentLogLevel() {
        return currentLogLevel;
    }

    public void setCurrentLogLevel(LogLevel currentLogLevel) {
        this.currentLogLevel = currentLogLevel;
    }

    public Optional<Path> getLogFile() {
        return Optional.ofNullable(logFile);
    }

    public void setLogFile(Path logFile) throws IOException, IllegalArgumentException {
        final boolean isValidFile = new FileChecker(logFile)
                .onCannotRead(dummy -> true)
                .check()
                .isPresent();

        if(!isValidFile)throw new IllegalArgumentException(logFile + " is wrong.");

        this.logFile = logFile;

        try {
            writer = Files.newBufferedWriter(logFile, StandardOpenOption.APPEND);
        }catch (IOException ex){
            System.err.println("Failed to open " + logFile + " to write.");
            throw ex;
        }
    }

    public Logger critical(String msg){
        output(LogLevel.CRITICAL, null, msg);
        return INST;
    }

    public Logger critical(Supplier<String> supplier){
        output(LogLevel.CRITICAL, supplier, null);
        return INST;
    }

    public Logger warn(String msg){
        output(LogLevel.WARN, null, msg);
        return INST;
    }

    public Logger warn(Supplier<String> supplier){
        output(LogLevel.WARN, supplier, null);
        return INST;
    }

    public Logger info(String msg){
        output(LogLevel.INFO, null, msg);
        return INST;
    }

    public Logger info(Supplier<String> supplier){
        output(LogLevel.INFO, supplier, null);
        return INST;
    }

    public Logger debug(String msg){
        output(LogLevel.DEBUG, null, msg);
        return INST;
    }

    public Logger debug(Supplier<String> supplier){
        output(LogLevel.DEBUG, supplier, null);
        return INST;
    }

    private void output(LogLevel level, Supplier<String> supplier, String msg){
        if(level.getInt() > currentLogLevel.getInt())return;

        final Instant occurred = Instant.now();
        final String lineMessage = occurred + " [" + level.name() + "] "
                + (msg == null ? supplier.get() : msg);

        System.err.println(lineMessage);

        if(writer == null)return;

        try {
            writer.write(lineMessage);
            writer.newLine();
        } catch (IOException ex){
            System.err.println(
                    Instant.now() + " [" + LogLevel.CRITICAL.name()
                    + "] (INNER) Failed to write the log message to " + logFile);
        }
    }

    public Logger logException(Exception ex){
        ex.printStackTrace();

        if (writer == null)return this;

        try {
            writer.write("---------- StackTrace begin ----------");
            writer.newLine();
        }catch (IOException ioex){
            System.err.println(
                    Instant.now() + " [" + LogLevel.CRITICAL.name()
                            + "] (INNER) Failed to write the log message to " + logFile);
            ioex.printStackTrace();
        }

        final PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);

        try {
            writer.write("---------- StackTrace end ----------");
            writer.newLine();
        }catch (IOException ioex){
            System.err.println(
                    Instant.now() + " [" + LogLevel.CRITICAL.name()
                            + "] (INNER) Failed to write the log message to " + logFile);
            ioex.printStackTrace();
        }
        return this;
    }

    public void flush(){
        if(writer == null)return;

        try {
            writer.flush();
        }catch (IOException ex){
            System.err.println(
                    Instant.now() + " [" + LogLevel.CRITICAL.name()
                            + "] (INNER) Failed to write the log message to " + logFile);
            ex.printStackTrace();
        }
    }
}
