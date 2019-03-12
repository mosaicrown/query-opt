package Operation_allocator.DebugManager;

public class Report {

    private String className = "";
    private LogType type = LogType.DATA_TRACE;
    private String message = "";

    public Report(String cname, LogType ty, String m) {
        className = cname;
        type = ty;
        message = m;
    }

    public String getClassName() {
        return className;
    }

    public LogType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        String s = "";
        s += ("***" + className + "***\n\t->" + type + "\n" + message);
        s += "\n-------------------------------------------------------------------------------------------------------\n";
        return s;
    }
}
