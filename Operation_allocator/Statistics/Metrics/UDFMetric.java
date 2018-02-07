package Statistics.Metrics;

import Statistics.UDFprofilers.Profiler;

import java.io.*;

public class UDFMetric<T extends Profiler> extends BasicMetric implements Serializable {

    /**
     * Machine architecture metrics
     */
    public double CPI;                  //average standard complexity adimensional
    public double CT;                   //average clock time in seconds
    public double IOPS;                 //average number of IO operations per second

    /**
     * Algorithm metrics
     */
    public double Not;                      //number of tuples
    public double Nop_cpu;                  //estimated number of CPU operations
    public double Nop_io;                   //estimated number of I/O operations

    /**
     * Complexity profiler
     */
    T profiler;

    public UDFMetric() {

    }

    public UDFMetric(double m1, double m2, double m3, double m4, double m5, double m6, double m7) {
        super(m4, m5, m6, m7, 0.0, 0.0);
        CPI = m1;
        CT = m2;
        IOPS = m3;
        Not = m4 / m5;
    }

    public void setProfiler(T p) {
        profiler = p;
    }

    public void computeMetrics() {
        super.CPU_time = profiler.cpuComplexityProfile(Not) * CPI * CT;
        super.IO_time = profiler.ioComplexityProfile(Not) / IOPS;
    }

    public String toString() {
        return super.toString() +
                "\t CPI:\t" + CPI +
                "\t CT:\t" + CT +
                "\t IOPS:\t" + IOPS +
                "\t Not:\t" + Not;
    }

    public T getProfiler() {
        return profiler;
    }

    public UDFMetric<T> deepClone() {
        /**
         * NB profiler is not cloned

         */
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (UDFMetric<T>) ois.readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}