package Statistics.UDFprofilers;

public abstract class Profiler{

    /**
     * A function that predicts the number of CPU operations performed by UDF or algorithm
     * starting from a certain quantity of data
     * @param inputSize input data
     * @return the number of CPU operations
     */
    public abstract double cpuComplexityProfile(double inputSize);
    /**
     * A function that predicts the number of I/O operations performed by UDF or algorithm
     * starting from a certain quantity of data
     * @param inputSize input data
     * @return the number of CPU operations
     */
    public abstract double ioComplexityProfile(double inputSize);

}