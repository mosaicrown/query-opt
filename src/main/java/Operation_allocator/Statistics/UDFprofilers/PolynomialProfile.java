package Operation_allocator.Statistics.UDFprofilers;

import java.io.Serializable;
import java.util.List;

public class PolynomialProfile implements Profiler, Serializable {

    /**
     * This class provide CPU polynomial profile function
     * It could be used to build test
     */

    private Double[] k = null;

    public PolynomialProfile(List<Double> kvect) throws RuntimeException {
        if (kvect.size() < 1)
            throw new RuntimeException("Missing profile parameters");
        k = new Double[1];
        int i = 0;
        for (double p : kvect
        ) {
            k[i] = p;
            i++;
        }
    }

    @Override
    public double cpuComplexityProfile(double inputSize) {
        return Math.pow(inputSize, k[0]);
    }

    @Override
    public double ioComplexityProfile(double inputSize) {
        return Math.pow(inputSize, k[0]) / k[0];
    }

}