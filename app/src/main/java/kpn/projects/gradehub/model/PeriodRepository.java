package kpn.projects.gradehub.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kpn.projects.gradehub.Colour;
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

    /** Serialises every period/module/assessment into a single JSON document. */
    public void exportToJson(DataCallback<String> callback) {
        executors.diskIO().execute(() -> {
            String json;
            try {
                JSONObject root = new JSONObject();
                JSONArray periodsArr = new JSONArray();
                for (Period period : periodDao.getPeriods()) {
                    JSONObject p = new JSONObject();
                    p.put("id", period.getId());
                    p.put("name", period.getName());
                    p.put("startDate", period.getStartDate());
                    p.put("endDate", period.getEndDate());

                    JSONArray modulesArr = new JSONArray();
                    for (Module module : moduleDao.getModulesForPeriod(period.getId())) {
                        JSONObject m = new JSONObject();
                        m.put("code", module.getCode());
                        m.put("name", module.getName());
                        m.put("examWeight", module.getExamWeight());
                        m.put("examEntranceRequired", module.isExamEntranceRequired());
                        m.put("examEntranceMark", module.getExamEntranceMark());
                        m.put("colour", module.getModuleColour() == null ? null : module.getModuleColour().name());

                        JSONArray assessmentsArr = new JSONArray();
                        for (Assessment a : assessmentDao.getAssessmentsForModule(module.getId())) {
                            JSONObject aObj = new JSONObject();
                            aObj.put("name", a.getName());
                            aObj.put("type", a.getType());
                            aObj.put("weight", a.getWeight());
                            aObj.put("mark", a.getMark());
                            aObj.put("date", a.getDate());
                            aObj.put("completed", a.isCompleted());
                            aObj.put("countsForClassMark", a.countsForClassMark());
                            assessmentsArr.put(aObj);
                        }
                        m.put("assessments", assessmentsArr);
                        modulesArr.put(m);
                    }
                    p.put("modules", modulesArr);
                    periodsArr.put(p);
                }
                root.put("periods", periodsArr);
                json = root.toString(2);
            } catch (JSONException e) {
                json = null;
            }
            String finalJson = json;
            executors.mainThread().execute(() -> callback.onResult(finalJson));
        });
    }

    public void importFromJson(String json, DataCallback<Boolean> callback) {
        executors.diskIO().execute(() -> {
            boolean success = true;
            try {
                JSONObject root = new JSONObject(json);
                assessmentDao.deleteAll();
                moduleDao.deleteAll();
                periodDao.deleteAll();

                JSONArray periodsArr = root.getJSONArray("periods");
                for (int i = 0; i < periodsArr.length(); i++) {
                    JSONObject p = periodsArr.getJSONObject(i);
                    Period period = new Period(p.getString("name"), p.optString("startDate", ""), p.optString("endDate", ""));
                    long periodId = periodDao.insert(period);

                    JSONArray modulesArr = p.optJSONArray("modules");
                    if (modulesArr == null) continue;
                    for (int j = 0; j < modulesArr.length(); j++) {
                        JSONObject m = modulesArr.getJSONObject(j);
                        Colour colour = m.isNull("colour") ? null : Colour.valueOf(m.getString("colour"));
                        Module module = new Module(periodId, m.getString("code"), m.getString("name"),
                                m.optDouble("examWeight", 0), m.optBoolean("examEntranceRequired", false),
                                m.optDouble("examEntranceMark", 0), colour);
                        long moduleId = moduleDao.insert(module);

                        JSONArray assessmentsArr = m.optJSONArray("assessments");
                        if (assessmentsArr == null) continue;
                        for (int k = 0; k < assessmentsArr.length(); k++) {
                            JSONObject a = assessmentsArr.getJSONObject(k);
                            Assessment assessment = new Assessment(moduleId, a.getString("name"),
                                    a.optString("type", ""), a.optDouble("weight", 0), a.optDouble("mark", 0),
                                    a.optString("date", ""), a.optBoolean("completed", false),
                                    a.optBoolean("countsForClassMark", true));
                            assessmentDao.insert(assessment);
                        }
                    }
                }
            } catch (JSONException e) {
                success = false;
            }
            boolean finalSuccess = success;
            executors.mainThread().execute(() -> callback.onResult(finalSuccess));
        });
    }
}
