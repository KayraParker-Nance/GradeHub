package kpn.projects.gradehub.utils;

import java.util.List;
import java.util.stream.Collectors;

import kpn.projects.gradehub.model.Assessment;

public class GradeCalculator {
    public static double calculateCurrentAverage(List<Assessment> assessments){
        List<Assessment> completedAssessments = assessments.stream()
                .filter(Assessment::isCompleted)
                .collect(Collectors.toList());

        double totalWeight = completedAssessments.stream()
                .mapToDouble(Assessment::getWeight)
                .sum();

        double totalMark = completedAssessments.stream()
                .mapToDouble(Assessment::getContribution)
                .sum();

        if (totalWeight == 0) return 0;

        return totalMark / totalWeight * 100;
    }

    public static double calculateSecuredMark(List<Assessment> assessments){
        return assessments.stream()
                .filter(Assessment::isCompleted)
                .mapToDouble(Assessment::getContribution)
                .sum();
    }

    public static double calculateTotalRemainingWeight(List<Assessment> assessments){
        return assessments.stream()
                .filter(Assessment::isIncomplete)
                .mapToDouble(Assessment::getWeight)
                .sum();
    }

    public static double calculateClassRemainingWeight(List<Assessment> assessments){
        return assessments.stream()
                .filter(Assessment::isIncomplete)
                .filter(Assessment::countsForClassMark)
                .mapToDouble(Assessment::getWeight)
                .sum();
    }

    public static double calculateRequiredAverageForTarget(List<Assessment> assessments, double target){
        double secured = calculateSecuredMark(assessments);
        double remainingWeight = calculateTotalRemainingWeight(assessments);

        if (remainingWeight == 0) return -1; //no more work to do

        return (target - secured) * 100 / remainingWeight;
    }

    public static double calculateRequiredAverageForExamEntrance(List<Assessment> assessments, double examEntryMark){
        double currentAverage = calculateCurrentAverage(assessments);
        double currentWeight = assessments.stream()
                .filter(Assessment::isCompleted)
                .mapToDouble(Assessment::getWeight)
                .sum();

        double remainingWeight = calculateClassRemainingWeight(assessments);

        if (remainingWeight == 0) return -1; //no more work to do

        double requiredSecuredMark = examEntryMark * getExamWeight(assessments) / 100;

        return (requiredSecuredMark - (currentAverage * currentWeight / 100)) * 100 / remainingWeight;

    }

    public static boolean calculateExamEntrance(List<Assessment> assessments, double examEntryMark){
        double secured = calculateSecuredMark(assessments);

        double requiredMark = examEntryMark * getExamWeight(assessments) / 100;

        return secured >= requiredMark;
    }

    public static double getExamWeight(List<Assessment> assessments){
        return assessments.stream()
                .filter(Assessment::isExam)
                .mapToDouble(Assessment::getWeight)
                .sum();
    }

    public static double calculateRequiredExamMark(double classMark, double examWeight, double target){
        return (target - (classMark * (100 - examWeight))) / examWeight;
    }

    public static double calculateProjectedFinalMark(List<Assessment> assessments, double remainingAverage){
        double currentAverage = calculateCurrentAverage(assessments);
        double currentWeight = assessments.stream()
                .filter(Assessment::isCompleted)
                .mapToDouble(Assessment::getWeight)
                .sum();

        double remainingWeight = calculateTotalRemainingWeight(assessments);

        return ((currentAverage * currentWeight / 100) + (remainingAverage * remainingWeight / 100));
    }

    public enum GradeStatus { GOOD, BORDERLINE, RISK }

    public static GradeStatus classify(double average) {
        if (average >= 60) return GradeStatus.GOOD;
        if (average >= 50) return GradeStatus.BORDERLINE;
        return GradeStatus.RISK;
    }
}
