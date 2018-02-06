package Statistics.Metrics;

import java.io.Serializable;

/**
 * Economic cost metrics
 */

public class CostMetric implements Serializable {

    //estimated cost of motion as dollars per program
    public double Cm;
    //estimated cost of execution as dollars per program
    public double Ce;
    //etimated ciphering cost as dollars per program
    public double Cc;

    //estimated total cost
    public double Ct;

    public void setAllZero() {
        this.Cm = 0;
        this.Ce = 0;
        this.Cc = 0;
        this.Ct = 0;
    }

    public String toString() {
        return "\t Ct:" + Ct +
                "\t Cm:" + Cm +
                "\t Ce:" + Ce +
                "\t Cc:" + Cc;
    }


}