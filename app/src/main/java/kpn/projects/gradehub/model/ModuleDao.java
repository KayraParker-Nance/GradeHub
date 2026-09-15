package kpn.projects.gradehub.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ModuleDao {
    @Insert
    void insert(Module module);

    @Update
    void update(Module module);

    @Delete
    void delete(Module module);

    @Query("SELECT * FROM modules WHERE id = :id")
    Module getModuleById(long id);

    @Query("SELECT * FROM modules WHERE periodId = :periodId")
    List<Module> getModulesForPeriod(long periodId);

    @Query("SELECT * FROM modules")
    List<Module> getModules();
}
