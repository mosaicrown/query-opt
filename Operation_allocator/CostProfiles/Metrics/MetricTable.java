import java.lang.*;

public class MetricTable {
    /**
     * Input data metrics
     */
    public double inputSize;                //total size of input data in KBs
    public double inputTupleSize;           //size of input tuple in KBs
    /**
     * Output data metrics
     */
    public double outputSize;                //total size of output data in KBs
    public double outputTupleSize;           //size of output tuple in KBs
    /**
     * Machine architecture metrics
     */
    public double CPI = 2.3;                //average standard complexity adimensional
    public double CT = 0.5 * 10e-9;         //average clock time in seconds
    /**
     * Operation metrics
     */
    public double CPU_time;                 //estimated cpu execution time
    public double IO_time;                  //estimated io execution time
    /**
     * Algorithm metrics
     */
    public double Not;                      //number of tuples
    public double Nop_cpu;                  //estimated number of CPU operations
    public double Nop_io;                   //estimated number of I/O operations
    /**
     * Economic cost metrics
     */
    //Motion
    public double Mc;                       //estimated cost of motion as dollars per program
    public double Km;                       //provider inbound/outbound specific cost of motion as dollars per KB
    //Execution
    public double Ec;                       //estimated cost of execution as dollars per program
    public double Kcpu;                     //provider specific cost of cpu execution as dollars per second
    public double Kio;                      //provider specific cost of io execution as dollars per second
    //Cipher
    public double Cc;                       //etimated ciphering cost as dollars per program
    public double Kc1;                      //provider specific cost of asymmetric cipher per KB
    public double Kc2;                      //provider specific cost of homomorphic cipher per KB
}