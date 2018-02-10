package Statistics.UDFprofilers;

import java.io.Serializable;

public class QuadraticProfile implements Profiler, Serializable {

    /**
     * This class provide CPU quadratic profile function
     * It could be used to build test
     */

    private double k1, k2, k3;

    public QuadraticProfile(double c1, double c2, double c3) {
        k1 = c1;
        k2 = c2;
        k3 = c3;
    }

    @Override
    public double cpuComplexityProfile(double inputSize) {
        return k1 + k1 * inputSize + k3 * Math.pow(inputSize, 2);
    }

    @Override
    public double ioComplexityProfile(double inputSize) {

        return (k1 + k1 * inputSize + k3 * Math.pow(inputSize, 2)) / (k1 + k2 + k3);
    }

}
