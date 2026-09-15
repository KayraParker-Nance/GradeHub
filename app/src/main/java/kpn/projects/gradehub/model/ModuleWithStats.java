package kpn.projects.gradehub.model;

public class ModuleWithStats {
    public final Module module;
    public final double currentAverage;
    public final double securedMark;
    public final double completedWeight;
    public final double remainingWeight;
    public final boolean examEntranceMet;
    public final boolean hasCompletedAssessments;

    public ModuleWithStats(Module module, double currentAverage, double securedMark,
                           double completedWeight, double remainingWeight,
                           boolean examEntranceMet, boolean hasCompletedAssessments) {
        this.module = module;
        this.currentAverage = currentAverage;
        this.securedMark = securedMark;
        this.completedWeight = completedWeight;
        this.remainingWeight = remainingWeight;
        this.examEntranceMet = examEntranceMet;
        this.hasCompletedAssessments = hasCompletedAssessments;
    }
}
