package Actors;

import Statistics.Metrics.CostMetric;

public class SimpleCostEngine {

    public SimpleCostEngine() {

    }

    public static CostMetric computeExecutionVsMotionCost(Operation op, Provider exec) {
        CostMetric m = new CostMetric();
        m.setAllZero();
        //Compute execution cost
        m.Ce = op.getOp_metric().CPU_time * exec.getMetrics().Kcpu + op.getOp_metric().IO_time * exec.getMetrics().Kio;
        //Compute motion cost
        m.Cm = op.getOp_metric().outputSize * exec.getMetrics().Km;
        return m;
    }


}