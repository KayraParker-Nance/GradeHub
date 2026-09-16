package kpn.projects.gradehub.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import kpn.projects.gradehub.utils.GradeCalculator;
import kpn.projects.gradehub.utils.AppExecutors;
import kpn.projects.gradehub.utils.DataCallback;

public class ModuleRepository {
    private final ModuleDao moduleDao;
    private final AssessmentDao assessmentDao;
    private final AppExecutors executors;

    public ModuleRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        moduleDao = db.moduleDao();
        assessmentDao = db.assessmentDao();
        executors = AppExecutors.getInstance();
    }

    public void getModulesForPeriod(long periodId, DataCallback<List<Module>> callback) {
        executors.diskIO().execute(() -> {
            List<Module> result = moduleDao.getModulesForPeriod(periodId);
            executors.mainThread().execute(() -> callback.onResult(result));
        });
    }

    /** Modules for a period, each paired with its computed grade stats. */
    public void getModulesWithStatsForPeriod(long periodId, DataCallback<List<ModuleWithStats>> callback) {
        executors.diskIO().execute(() -> {
            List<Module> modules = moduleDao.getModulesForPeriod(periodId);
            List<ModuleWithStats> result = new ArrayList<>();
            for (Module module : modules) {
                result.add(buildStats(module));
            }
            executors.mainThread().execute(() -> callback.onResult(result));
        });
    }

    public void getModuleWithStats(long moduleId, DataCallback<ModuleWithStats> callback) {
        executors.diskIO().execute(() -> {
            Module module = moduleDao.getModuleById(moduleId);
            ModuleWithStats stats = module == null ? null : buildStats(module);
            executors.mainThread().execute(() -> callback.onResult(stats));
        });
    }

    private ModuleWithStats buildStats(Module module) {
        List<Assessment> assessments = assessmentDao.getAssessmentsForModule(module.getId());
        double currentAverage = GradeCalculator.calculateCurrentAverage(assessments);
        double secured = GradeCalculator.calculateSecuredMark(assessments);
        double remainingWeight = GradeCalculator.calculateTotalRemainingWeight(assessments);
        double completedWeight = assessments.stream()
                .filter(Assessment::isCompleted)
                .mapToDouble(Assessment::getWeight)
                .sum();
        boolean hasCompleted = assessments.stream().anyMatch(Assessment::isCompleted);
        boolean examEntranceMet = !module.isExamEntranceRequired()
                || GradeCalculator.calculateExamEntrance(assessments, module.getExamEntranceMark());
        return new ModuleWithStats(module, currentAverage, secured, completedWeight,
                remainingWeight, examEntranceMet, hasCompleted);
    }

    public void insert(Module module, DataCallback<Long> onComplete) {
        executors.diskIO().execute(() -> {
            long id = moduleDao.insert(module);
            if (onComplete != null) executors.mainThread().execute(() -> onComplete.onResult(id));
        });
    }

    public void update(Module module, Runnable onComplete) {
        executors.diskIO().execute(() -> {
            moduleDao.update(module);
            if (onComplete != null) executors.mainThread().execute(onComplete);
        });
    }

    public void delete(Module module, Runnable onComplete) {
        executors.diskIO().execute(() -> {
            moduleDao.delete(module);
            if (onComplete != null) executors.mainThread().execute(onComplete);
        });
    }

    public void getModule(long id, DataCallback<Module> callback) {
        executors.diskIO().execute(() -> {
            Module module = moduleDao.getModuleById(id);
            executors.mainThread().execute(() -> callback.onResult(module));
        });
    }
}
