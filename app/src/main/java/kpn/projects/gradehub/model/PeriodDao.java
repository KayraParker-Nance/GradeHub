package kpn.projects.gradehub.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PeriodDao {
    @Insert
    void insert(Period period);

    @Update
    void update(Period period);

    @Delete
    void delete(Period period);

    @Query("SELECT * FROM periods WHERE id = :id")
    Period getPeriodById(long id);

    @Query("SELECT * FROM periods")
    List<Period> getPeriods();
}
