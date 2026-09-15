package kpn.projects.gradehub.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "assessments", foreignKeys = @ForeignKey(
        entity = Module.class,
        parentColumns = "id",
        childColumns = "moduleId",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
))
public class Assessment {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long moduleId;
    private String name;
    private String type;
    private double weight;
    private double mark;
    private String date;
    private boolean completed;
    private boolean countsForClassMark;

    public boolean isCompleted(){
        return completed;
    }

    public boolean isIncomplete(){
        return !completed;
    }

    public double getWeight(){
        return weight;
    }

    public double getMark(){
        return mark;
    }

    public double getContribution(){
        return mark * weight / 100;
    }

    public boolean countsForClassMark(){
        return countsForClassMark;
    }

    public boolean isExam(){
        return !countsForClassMark;
    }
}
