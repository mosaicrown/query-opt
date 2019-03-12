package Operation_allocator.Statistics.UDFprofilers;

import java.io.Serializable;
import java.util.List;

public class QuadraticProfile implements Profiler, Serializable {

    /**
     * This class provide CPU quadratic profile function
     * It could be used to build test
     */

    private Double[] k = null;

    public QuadraticProfile(List<Double> kvect) throws RuntimeException {
        if (kvect.size() < 3)
            throw new RuntimeException("Missing profile parameters");
        k = new Double[3];
        int i = 0;
        for (double p : kvect
        ) {
            k[i] = p;
            i++;
        }
    }

    @Override
    public double cpuComplexityProfile(double inputSize) {
        return k[0] + k[1] * inputSize + k[2] * inputSize * inputSize;
    }

    @Override
    public double ioComplexityProfile(double inputSize) {
        return (k[0] + k[1] * inputSize + k[2] * inputSize * inputSize) / (k[0] + k[1] + k[2]);
    }

}