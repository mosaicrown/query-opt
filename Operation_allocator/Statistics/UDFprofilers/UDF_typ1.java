package Statistics.UDFprofilers;

import java.io.Serializable;

public class UDF_typ1 implements Profiler, Serializable {

    @Override
    public double cpuComplexityProfile(double inputSize) {
        return 2500 + Math.pow(inputSize, 2) + Math.log10(inputSize);
    }

    @Override
    public double ioComplexityProfile(double inputSize) {
        return 3 * Math.log10(inputSize) + inputSize;
    }
}
