package Operation_allocator.Statistics.Metrics;

public final class BasicMetricUpdater {

    //new CPU_time (logarithmic model)
    public static double updateCPUtime(double oldTime, double oldDataVolume, double newDataVolume) {
        return oldTime * (1 + Math.log10(newDataVolume / oldDataVolume) / Math.log10(2));
    }
    //new IO_time (linear model)
    public static double updateIOtime(double oldTime, double oldDataVolume, double newDataVolume) {
        return oldTime * (newDataVolume / oldDataVolume);
    }


}
