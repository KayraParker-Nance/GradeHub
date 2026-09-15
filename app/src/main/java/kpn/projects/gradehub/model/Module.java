package kpn.projects.gradehub.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import kpn.projects.gradehub.Colour;

@Entity(tableName = "modules",
        foreignKeys = @ForeignKey(
                entity = Period.class,
                parentColumns = "id",
                childColumns = "periodId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE
        ))
public class Module {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long periodId;
    private String name;
    private String code;
    private double examWeight;
    private boolean examEntranceRequired;
    private double examEntranceMark;
    private Colour moduleColour;

    public Module() {
    }

    public Module(long periodId, String code, String name, double examWeight,
                  boolean examEntranceRequired, double examEntranceMark, Colour moduleColour) {
        this.periodId = periodId;
        this.code = code;
        this.name = name;
        this.examWeight = examWeight;
        this.examEntranceRequired = examEntranceRequired;
        this.examEntranceMark = examEntranceMark;
        this.moduleColour = moduleColour;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(long periodId) {
        this.periodId = periodId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getExamWeight() {
        return examWeight;
    }

    public void setExamWeight(double examWeight) {
        this.examWeight = examWeight;
    }

    public boolean isExamEntranceRequired() {
        return examEntranceRequired;
    }

    public void setExamEntranceRequired(boolean examEntranceRequired) {
        this.examEntranceRequired = examEntranceRequired;
    }

    public double getExamEntranceMark() {
        return examEntranceMark;
    }

    public void setExamEntranceMark(double examEntranceMark) {
        this.examEntranceMark = examEntranceMark;
    }

    public Colour getModuleColour() {
        return moduleColour;
    }

    public void setModuleColour(Colour moduleColour) {
        this.moduleColour = moduleColour;
    }
}
