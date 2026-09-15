package kpn.projects.gradehub.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AssessmentDao {
    @Insert
    void insert(Assessment assessment);

    @Update
    void update(Assessment assessment);

    @Delete
    void delete(Assessment assessment);

    @Query("SELECT * FROM assessments WHERE id = :id")
    Assessment getAssessmentById(long id);

    @Query("SELECT * FROM assessments WHERE moduleId = :moduleId")
    List<Assessment> getAssessmentsForModule(long moduleId);

    @Query("SELECT * FROM assessments WHERE moduleId = :moduleId AND completed = 1")
    List<Assessment> getCompletedAssessmentsForModule(long moduleId);

    @Query("SELECT * FROM assessments WHERE moduleId = :moduleId AND completed = 0")
    List<Assessment> getIncompleteAssessmentsForModule(long moduleId);
}
