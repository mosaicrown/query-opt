package Statistics.Metrics;

import java.io.Serializable;

public class BasicMetric implements Serializable {
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
     * Operation metrics
     */
    public double CPU_time;                 //estimated cpu execution time
    public double IO_time;                  //estimated io execution time

    public BasicMetric() {

    }

    public BasicMetric(double m1, double m2, double m3, double m4, double m5, double m6) {
        inputSize = m1;
        inputTupleSize = m2;
        outputSize = m3;
        outputTupleSize = m4;
        CPU_time = m5;
        IO_time = m6;
    }

    public String toString() {
        return
                "InputSize:\t" + inputSize +
                        "\t InputTupleSize:\t" + inputTupleSize +
                        "\t OutputSize:\t" + outputSize +
                        "\t OutputTupleSize:\t" + outputTupleSize +
                        "\t CPU_time:\t" + CPU_time +
                        "\t IO_time:\t" + IO_time;
    }

}