package Operation_allocator.DebugManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.common.io.Closer;


public final class Debugger {

    //singleton debugger instance
    private static Debugger debugger = new Debugger();
    //folder to place the report file
    private static String pathToSave = null;
    //list of record to be printed
    private static List<Report> trace = null;

    private Debugger() {
        pathToSave = "../../Launcher/OutputData/Reports";
        trace = new LinkedList<>();
    }

    /**
     * Singleton instance of debug manager
     *
     * @return debugger instance
     */
    public static synchronized Debugger getDebugger() {
        new Debugger();
        return debugger;
    }

    public static String getPathToSave() {
        return pathToSave;
    }

    public static void setPathToSave(String pathToSave) {
        Debugger.pathToSave = pathToSave;
    }

    public void leaveTrace(Report r) {
        trace.add(r);
    }

    public void writeLog() throws IOException {
        //make the report path more readable
        String s0, s1, s2, s3;
        s0 = Long.toString(new Date().getTime());
        int s0len = s0.length();
        s1 = s0.substring(0, s0len - 4);
        s2 = s0.substring(s0len - 4, s0len - 3);
        s3 = s0.substring(s0len - 3);
        //create the path
        Path path = Paths.get(pathToSave + "/report_" + s1 + "_" + s2 + "_" + s3 + ".txt").normalize();
        //create the file
        if (Files.notExists(path))
            Files.createFile(path);
        File file = new File(path.toUri());
        //write the file
        final Closer closer = Closer.create();
        try {
            final Writer writer = closer.register(new BufferedWriter(new FileWriter(file)));
            for (Report r : trace
            ) {
                writer.write(r.toString());
            }
        } finally {
            closer.close();
        }

    }


}
