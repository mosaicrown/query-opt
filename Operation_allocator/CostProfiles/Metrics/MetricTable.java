import java.lang.*;

public class MetricTable {
    /**
     * Input data metrics
     */
    double inputSize;                //total size of input data in KBs
    double inputTupleSize;           //size of input tuple in KBs
    /**
     * Output data metrics
     */
    double outputSize;                //total size of output data in KBs
    double outputTupleSize;           //size of output tuple in KBs
    /**
     * Machine architecture metrics
     */
    double CPI = 2.3;                //average standard complexity adimensional
    double CT = 0.5 * 10e-9;         //average clock time in seconds
    /**
     * Operation metrics
     */
    double CPU_time;                 //estimated cpu execution time
    double IO_time;                  //estimated io execution time
    /**
     * Algorithm metrics
     */
    double Not;                      //number of tuples
    double Nop_cpu;                  //estimated number of CPU operations
    double Nop_io;                   //estimated number of I/O operations
    /**
     * Economic cost metrics
     */
    //Motion
    double Mc;                       //estimated cost of motion as dollars per program
    double Km;                       //provider inbound/outbound specific cost of motion as dollars per KB
    //Execution
    double Ec;                       //estimated cost of execution as dollars per program
    double Kcpu;                     //provider specific cost of cpu execution as dollars per second
    double Kio;                      //provider specific cost of io execution as dollars per second
    //Cipher
    double Cc;                       //etimated ciphering cost as dollars per program
    double Kc1;                      //provider specific cost of asymmetric cipher per KB
    double Kc2;                      //provider specific cost of homomorphic cipher per KB
}