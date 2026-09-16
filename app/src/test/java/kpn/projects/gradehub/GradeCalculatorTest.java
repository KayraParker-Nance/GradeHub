package kpn.projects.gradehub;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import kpn.projects.gradehub.model.Assessment;
import kpn.projects.gradehub.utils.GradeCalculator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GradeCalculatorTest {

    private Assessment assessment(String name, double weight, double mark, boolean completed, boolean classMark) {
        return new Assessment(1L, name, "Test", weight, mark, "01/01/2026", completed, classMark);
    }

    @Test
    public void currentAverage_onlyCountsCompletedAssessments() {
        List<Assessment> assessments = Arrays.asList(
                assessment("Test 1", 20, 80, true, true),
                assessment("Test 2", 20, 60, true, true),
                assessment("Exam", 60, 0, false, false)
        );
        // (80*20 + 60*20) / 40 * 100/100 = 70
        assertEquals(70.0, GradeCalculator.calculateCurrentAverage(assessments), 0.001);
    }

    @Test
    public void currentAverage_isZeroWithNoCompletedWork() {
        List<Assessment> assessments = Arrays.asList(
                assessment("Exam", 60, 0, false, false)
        );
        assertEquals(0.0, GradeCalculator.calculateCurrentAverage(assessments), 0.001);
    }

    @Test
    public void securedMark_sumsContributionOfCompletedWork() {
        List<Assessment> assessments = Arrays.asList(
                assessment("Test 1", 20, 80, true, true),
                assessment("Exam", 60, 0, false, false)
        );
        assertEquals(16.0, GradeCalculator.calculateSecuredMark(assessments), 0.001);
    }

    @Test
    public void examEntrance_metWhenSecuredExceedsRequired() {
        List<Assessment> assessments = Arrays.asList(
                assessment("Test 1", 40, 90, true, true),
                assessment("Exam", 60, 0, false, false)
        );
        // secured = 36, required = 40 * 60/100 = 24 -> met
        assertTrue(GradeCalculator.calculateExamEntrance(assessments, 40));
    }

    @Test
    public void classify_matchesExpectedBands() {
        assertEquals(GradeCalculator.GradeStatus.GOOD, GradeCalculator.classify(75));
        assertEquals(GradeCalculator.GradeStatus.BORDERLINE, GradeCalculator.classify(55));
        assertEquals(GradeCalculator.GradeStatus.RISK, GradeCalculator.classify(30));
    }
}
