package Operation_allocator.Statistics.Metrics;

import java.io.*;

public class ProviderMetric implements Serializable {

    //motion
    public double Km;                       //provider inbound/outbound specific cost of motion as dollars per KB
    //execution
    public double Kcpu;                     //provider specific cost of cpu execution as dollars per hour
    public double Kio;                      //provider specific cost of io execution as dollars per hour
    //cipher
    public double Kc_rse;                   //provider specific cost of randomized symmetric encryption per KB
    public double Kc_dse;                   //provider specific cost of deterministic symmetric encryption per KB
    public double Kc_pal;                   //provider specific cost of Paillier crypto-system per KB
    public double Kc_ope;                   //provider specific cost of OPE scheme per KB

    public ProviderMetric() {

    }

    public ProviderMetric(double m1, double m2, double m3, double m4, double m5, double m6, double m7) {
        Km = m1;
        Kcpu = m2;
        Kio = m3;
        Kc_rse = m4;
        Kc_dse = m5;
        Kc_pal = m6;
        Kc_ope = m7;
    }

    public String toString() {
        return
                "Km:\t" + Km +
                        "\t Kcpu:\t" + Kcpu +
                        "\t Kio:\t" + Kio +
                        "\t Kc_rse:\t" + Kc_rse +
                        "\t Kc2_dse:\t" + Kc_dse+
                        "\t Kc_pal:\t" + Kc_pal +
                        "\t Kc2_ope:\t" + Kc_ope;

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