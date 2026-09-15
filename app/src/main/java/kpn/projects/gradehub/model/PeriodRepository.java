package kpn.projects.gradehub.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import kpn.projects.gradehub.utils.GradeCalculator;
import kpn.projects.gradehub.utils.AppExecutors;
import kpn.projects.gradehub.utils.DataCallback;

public class PeriodRepository {
    private final PeriodDao periodDao;
    private final ModuleDao moduleDao;
    private final AssessmentDao assessmentDao;
    private final AppExecutors executors;

    public PeriodRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        periodDao = db.periodDao();
        moduleDao = db.moduleDao();
        assessmentDao = db.assessmentDao();
        executors = AppExecutors.getInstance();
    }

    public void getPeriods(DataCallback<List<Period>> callback) {
        executors.diskIO().execute(() -> {
            List<Period> result = periodDao.getPeriods();
            executors.mainThread().execute(() -> callback.onResult(result));
        });
    }

    /** Every period paired with the (unweighted) average of its modules' current averages. */
    public void getPeriodsWithStats(DataCallback<List<PeriodWithStats>> callback) {
        executors.diskIO().execute(() -> {
            List<Period> periods = periodDao.getPeriods();
            List<PeriodWithStats> result = new ArrayList<>();
            for (Period period : periods) {
                result.add(buildStats(period));
            }
            executors.mainThread().execute(() -> callback.onResult(result));
        });
    }

    private PeriodWithStats buildStats(Period period) {
        List<Module> modules = moduleDao.getModulesForPeriod(period.getId());
        double total = 0;
        int countedModules = 0;
        for (Module module : modules) {
            List<Assessment> assessments = assessmentDao.getAssessmentsForModule(module.getId());
            if (assessments.isEmpty()) continue;
            total += GradeCalculator.calculateCurrentAverage(assessments);
            countedModules++;
        }
        double average = countedModules == 0 ? 0 : total / countedModules;
        return new PeriodWithStats(period, average, modules.size());
    }

    public void insert(Period period, DataCallback<Long> onComplete) {
        executors.diskIO().execute(() -> {
            long id = periodDao.insert(period);
            if (onComplete != null) executors.mainThread().execute(() -> onComplete.onResult(id));
        });
    }

    public void update(Period period, Runnable onComplete) {
        executors.diskIO().execute(() -> {
            periodDao.update(period);
            if (onComplete != null) executors.mainThread().execute(onComplete);
        });
    }

    public void delete(Period period, Runnable onComplete) {
        executors.diskIO().execute(() -> {
            periodDao.delete(period);
            if (onComplete != null) executors.mainThread().execute(onComplete);
        });
    }

    public void resetAllData(Runnable onComplete) {
        executors.diskIO().execute(() -> {
            assessmentDao.deleteAll();
            moduleDao.deleteAll();
            periodDao.deleteAll();
            if (onComplete != null) executors.mainThread().execute(onComplete);
        });
    }
}
