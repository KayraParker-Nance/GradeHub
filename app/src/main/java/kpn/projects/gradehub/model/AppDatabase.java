package kpn.projects.gradehub.model;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import kpn.projects.gradehub.utils.Converters;

@Database(entities = {Period.class, Module.class, Assessment.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract PeriodDao periodDao();

    public abstract ModuleDao moduleDao();

    public abstract AssessmentDao assessmentDao();

    public static AppDatabase getInstance(@NonNull Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "gradehub.db")
                            // Fine for now since there's only one schema version; revisit
                            // with a real Migration once the schema needs to change.
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
