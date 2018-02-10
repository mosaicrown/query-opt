package Statistics.UDFprofilers;

import java.io.Serializable;

public class PseudolinearProfile implements Profiler, Serializable {

    /**
     * This class provide CPU pseudolinear profile function
     * It could be used to build test
     */

    private double k1, k2, k3;

    public PseudolinearProfile(double c1, double c2, double c3) {
        k1 = c1;
        k2 = c2;
        k3 = c3;
    }

    @Override
    public double cpuComplexityProfile(double inputSize) {
        return k1 + k1 * inputSize + k3 * inputSize * Math.log10(inputSize);
    }

    @Override
    public double ioComplexityProfile(double inputSize) {

        return (k1 + k1 * inputSize + k3 * inputSize * Math.log10(inputSize)
        ) / (k1 + k2 + k3);
    }

}