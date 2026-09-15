package kpn.projects.gradehub.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "periods")
public class Period {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String startDate;
    private String endDate;
}
