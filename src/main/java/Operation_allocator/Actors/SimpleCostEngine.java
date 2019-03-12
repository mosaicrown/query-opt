package Operation_allocator.Actors;

import Operation_allocator.Statistics.Metrics.CostMetric;

public final class SimpleCostEngine {

    public SimpleCostEngine() {

    }

    public static CostMetric computeExecutionVsMotionCost(Operation op, Provider exec) {
        CostMetric m = new CostMetric();
        m.setAllZero();
        //Compute execution cost, hour conversion
        m.Ce = op.getOp_metric().CPU_time * exec.getMetrics().Kcpu / 3600 + op.getOp_metric().IO_time * exec.getMetrics().Kio / 3600;
        //Compute motion cost(inbound + outbound)
        m.Cm = 2 * op.getOp_metric().outputSize * exec.getMetrics().Km;
        return m;
    }


}
