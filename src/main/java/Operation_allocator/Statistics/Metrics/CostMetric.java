package Operation_allocator.Statistics.Metrics;

import java.io.*;

/**
 * Economic cost metrics
 */

public class CostMetric implements Serializable {

    //estimated cost of motion as dollars per operation
    public double Cm;
    //estimated cost of execution as dollars per operation
    public double Ce;
    //etimated ciphering cost as dollars per operation
    public double Cc;

    //estimated total cost of a sub-tree
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

    public CostMetric copy() {
        CostMetric temp = new CostMetric();
        temp.setAllZero();
        temp.Ct = this.Ct;
        temp.Ce = this.Ce;
        temp.Cc = this.Cc;
        temp.Cm = this.Cm;
        return temp;
    }

    public double getIncrCost() {
        return this.Ce + this.Cc + this.Cm;
    }

    public CostMetric deepClone() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (CostMetric) ois.readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }


}