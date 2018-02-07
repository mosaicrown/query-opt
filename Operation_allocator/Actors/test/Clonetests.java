package Actors.test;

import Actors.Operation;
import Actors.Provider;
import Statistics.Metrics.BasicMetric;
import Statistics.Metrics.CostMetric;
import Statistics.Metrics.ProviderMetric;

public class Clonetests {
    public static void main(String[] args) {
        /**
         * Operation Metrics
         */
        BasicMetric m1 = new BasicMetric(0, 0, 12000.0, 4.0, 0.09, 0.002);
        /**
         * Check operation metrics
         */
        System.out.println(m1.toString());
        /**
         * Provider metrics
         */
        ProviderMetric pm1 = new ProviderMetric(0.04e-6, 0.37, 0.98, 0.37 * (10e-3 / 5), 0.37 * (10e-3 / 4));
        /**
         * Provider
         */
        Provider provider1 = new Provider("EC2", pm1);
        System.out.println("Provider" + provider1.selfDescription());
        System.out.println(provider1.getMetrics().toString());
        /**
         * Operations
         */
        Operation o1 = new Operation("scan");

        //set operation metrics
        o1.setOp_metric(m1);

        //set executors
        o1.setExecutor(provider1);

        //set costs
        CostMetric cm1 = new CostMetric();

        cm1.setAllZero();

        o1.setCost(cm1);

        /**
         * Cloning tests
         */
        Operation o1clone = o1.deepClone();
        /**
         * results
         */
        System.out.println("Operation:"+o1.hashCode()+" "+o1.toString());
        System.out.println("Operation:"+o1clone.hashCode()+" "+o1clone.toString());
        System.out.println("Executor:"+o1.getExecutor().hashCode()+" "+o1.getExecutor().selfDescription());
        System.out.println("Executor:"+o1clone.getExecutor().hashCode()+" "+o1clone.getExecutor().selfDescription());
    }
}
