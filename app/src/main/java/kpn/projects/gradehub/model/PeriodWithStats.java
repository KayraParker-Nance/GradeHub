package kpn.projects.gradehub.model;

public class PeriodWithStats {
    public final Period period;
    public final double average;
    public final int moduleCount;

    public PeriodWithStats(Period period, double average, int moduleCount) {
        this.period = period;
        this.average = average;
        this.moduleCount = moduleCount;
    }

}
