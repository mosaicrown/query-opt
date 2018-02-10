package Statistics.UDFprofilers;

import java.io.Serializable;

public class CubicProfile implements Profiler, Serializable {

    /**
     * This class provide CPU cubic profile function
     * It could be used to build test
     */

    private double k1, k2, k3, k4;

    public CubicProfile(double c1, double c2, double c3, double c4) {
        k1 = c1;
        k2 = c2;
        k3 = c3;
        k4 = c4;
    }

    @Override
    public double cpuComplexityProfile(double inputSize) {
        return k1 + k1 * inputSize + k3 * Math.pow(inputSize, 2) + k4 * Math.pow(inputSize, 3);
    }

    @Override
    public double ioComplexityProfile(double inputSize) {

        return (k1 + k1 * inputSize + k3 * Math.pow(inputSize, 2) + k4 * Math.pow(inputSize, 3)) / (k1 + k2 + k3 + k4);
    }

}