package trivial_logger;

public enum LogLevel {
    DISABLE(0),
    CRITICAL(1),
    WARN(2),
    INFO(3),
    DEBUG(4);

    private int num;

    private LogLevel(int num){
        this.num = num;
    }

    int getInt(){
        return num;
    }
}
