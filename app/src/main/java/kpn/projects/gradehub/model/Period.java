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

    public Period() {
    }

    public Period(String name, String startDate, String endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
