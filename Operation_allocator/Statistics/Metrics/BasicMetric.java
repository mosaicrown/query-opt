package Statistics.Metrics;

import Data.Attribute;

import java.io.*;
import java.util.List;

public class BasicMetric implements Serializable {
    /**
     * Input data metrics
     */
    public double inputSize;                //total size of input data in KB
    public double inputTupleSize;           //size of input tuple in KB
    /**
     * Output data metrics
     */
    public double outputSize;                //total size of output data in KB
    public double outputTupleSize;           //size of output tuple in KB
    /**
     * Operation metrics
     */
    public double CPU_time;                 //estimated cpu execution time
    public double IO_time;                  //estimated io execution time

    public BasicMetric() {
        inputSize = 0;
        inputTupleSize = 0;
        outputSize = 0;
        outputTupleSize = 0;
        CPU_time = 0;
        IO_time = 0;
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

    public BasicMetric copyBasicMetric() {
        return new BasicMetric(inputSize, inputTupleSize, outputSize, outputTupleSize, CPU_time, IO_time);
    }

    public BasicMetric deepClone() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (BasicMetric) ois.readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public double getInputSize() {
        return inputSize;
    }

    public void setInputSize(double inputSize) {
        this.inputSize = inputSize;
    }

    public double getInputTupleSize() {
        return inputTupleSize;
    }

    public void setInputTupleSize(double inputTupleSize) {
        this.inputTupleSize = inputTupleSize;
    }

    public double getOutputSize() {
        return outputSize;
    }

    public void setOutputSize(double outputSize) {
        this.outputSize = outputSize;
    }

    public double getOutputTupleSize() {
        return outputTupleSize;
    }

    public void setOutputTupleSize(double outputTupleSize) {
        this.outputTupleSize = outputTupleSize;
    }

    public double getCPU_time() {
        return CPU_time;
    }

    public void setCPU_time(double CPU_time) {
        this.CPU_time = CPU_time;
    }

    public double getIO_time() {
        return IO_time;
    }

    public void setIO_time(double IO_time) {
        this.IO_time = IO_time;
    }

    public double getOutNofTuples() {
        return (outputSize / outputTupleSize);
    }

    public double getInNofTuples() {
        return (inputSize / inputTupleSize);
    }
}