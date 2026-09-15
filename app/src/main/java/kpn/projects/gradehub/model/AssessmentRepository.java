package kpn.projects.gradehub.model;

import android.content.Context;

import java.util.List;

import kpn.projects.gradehub.utils.AppExecutors;
import kpn.projects.gradehub.utils.DataCallback;

public class AssessmentRepository {
    private final AssessmentDao dao;
    private final AppExecutors executors;

    public AssessmentRepository(Context context) {
        dao = AppDatabase.getInstance(context).assessmentDao();
        executors = AppExecutors.getInstance();
    }

    public void getAssessmentsForModule(long moduleId, DataCallback<List<Assessment>> callback) {
        executors.diskIO().execute(() -> {
            List<Assessment> result = dao.getAssessmentsForModule(moduleId);
            executors.mainThread().execute(() -> callback.onResult(result));
        });
    }

    public void insert(Assessment assessment, Runnable onComplete) {
        executors.diskIO().execute(() -> {
            dao.insert(assessment);
            if (onComplete != null) executors.mainThread().execute(onComplete);
        });
    }

    public void update(Assessment assessment, Runnable onComplete) {
        executors.diskIO().execute(() -> {
            dao.update(assessment);
            if (onComplete != null) executors.mainThread().execute(onComplete);
        });
    }

    public void delete(Assessment assessment, Runnable onComplete) {
        executors.diskIO().execute(() -> {
            dao.delete(assessment);
            if (onComplete != null) executors.mainThread().execute(onComplete);
        });
    }
}
