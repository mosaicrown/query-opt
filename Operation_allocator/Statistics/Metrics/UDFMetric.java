package Statistics.Metrics;

public class UDFMetric extends BasicMetric {

    /**
     * Machine architecture metrics
     */
    public double CPI;                //average standard complexity adimensional
    public double CT;         //average clock time in seconds
    /**
     * Algorithm metrics
     */
    public double Not;                      //number of tuples
    public double Nop_cpu;                  //estimated number of CPU operations
    public double Nop_io;                   //estimated number of I/O operations

    public UDFMetric() {

    }

    public UDFMetric(double m1, double m2, double m3, double m4, double m5, double m6) {
        super(m3, m4, m5, m6, 0.0, 0.0);
        CPI = m1;
        CT = m2;
        Not = m3 / m4;
    }
}