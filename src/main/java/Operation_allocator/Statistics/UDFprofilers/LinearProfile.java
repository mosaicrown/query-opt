package Operation_allocator.Statistics.UDFprofilers;

import java.io.Serializable;
import java.util.List;

public class LinearProfile implements Profiler, Serializable {

    /**
     * This class provide CPU linear profile function
     * It could be used to build test
     */

    private Double[] k = null;

    public LinearProfile(List<Double> kvect) throws RuntimeException {
        if (kvect.size() < 2)
            throw new RuntimeException("Missing profile parameters");
        k = new Double[2];
        int i = 0;
        for (double p : kvect
        ) {
            k[i] = p;
            i++;
        }
    }

    @Override
    public double cpuComplexityProfile(double inputSize) {
        return k[0] + k[1] * inputSize;
    }

    @Override
    public double ioComplexityProfile(double inputSize) {
        return (k[0] + k[1] * inputSize) / (k[0] + k[1]);
    }

}