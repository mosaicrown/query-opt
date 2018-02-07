package Statistics.Metrics.tests;

import Statistics.Metrics.BasicMetric;
import Statistics.Metrics.UDFMetric;
import Statistics.UDFprofilers.Profiler;
import Statistics.UDFprofilers.UDF_typ1;

public class MetricTest {
    public static void main(String [] args){
        /**
         * simple metric creation
         */
        BasicMetric m1 = new BasicMetric(0, 0, 12000.0, 4.0, 0.09, 0.002);
        /**
         * clone test
         */
        BasicMetric m1clone = m1.deepClone();
        /**
         * metric results
         */
        System.out.println(m1.hashCode());
        System.out.println(m1.toString());
        /**
         * clone results
         */
        System.out.println(m1clone.hashCode());
        System.out.println(m1clone.toString());

        /**
         * UDF metric creation
         */
        UDFMetric m2 = new UDFMetric<UDF_typ1>(18, 2.5e-8, 40e3, m1.outputSize,
                m1.outputTupleSize, 2800, 10);
        Profiler profiler = new UDF_typ1();
        m2.setProfiler(profiler);
        m2.computeMetrics();
        /**
         * clone test
         */
        UDFMetric m2clone = m2.deepClone();
        /**
         * metric results
         */
        System.out.println(m2.hashCode());
        System.out.println(m2.toString());
        /**
         * clone results
         */
        System.out.println(m2clone.hashCode());
        System.out.println(m2clone.toString());
        /**
         * referenced object clone
         */
        System.out.println(m2.getProfiler().toString());
        System.out.println(m2clone.getProfiler().toString());
    }
}
