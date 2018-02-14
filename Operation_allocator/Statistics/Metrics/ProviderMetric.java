package Statistics.Metrics;

import java.io.*;

public class ProviderMetric implements Serializable {

    //motion
    public double Km;                       //provider inbound/outbound specific cost of motion as dollars per KB
    //execution
    public double Kcpu;                     //provider specific cost of cpu execution as dollars per hour
    public double Kio;                      //provider specific cost of io execution as dollars per hour
    //cipher
    public double Kc1;                      //provider specific cost of asymmetric cipher per KB
    public double Kc2;                      //provider specific cost of homomorphic cipher per KB

    public ProviderMetric() {

    }

    public ProviderMetric(double m1, double m2, double m3, double m4, double m5) {
        Km = m1;
        Kcpu = m2;
        Kio = m3;
        Kc1 = m4;
        Kc2 = m5;
    }

    public String toString() {
        return
                "Km:\t" + Km +
                        "\t Kcpu:\t" + Kcpu +
                        "\t Kio:\t" + Kio +
                        "\t Kc1:\t" + Kc1 +
                        "\t Kc2:\t" + Kc2;

    }

    public ProviderMetric deepClone() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (ProviderMetric) ois.readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}