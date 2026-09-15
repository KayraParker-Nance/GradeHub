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

    public Assessment() {
    }

    public Assessment(long moduleId, String name, String type, double weight, double mark,
                      String date, boolean completed, boolean countsForClassMark) {
        this.moduleId = moduleId;
        this.name = name;
        this.type = type;
        this.weight = weight;
        this.mark = mark;
        this.date = date;
        this.completed = completed;
        this.countsForClassMark = countsForClassMark;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getModuleId() {
        return moduleId;
    }

    public void setModuleId(long moduleId) {
        this.moduleId = moduleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isCompleted(){
        return completed;
    }

    public boolean isIncomplete(){
        return !completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public double getWeight(){
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getMark() {
        return mark;
    }

    public void setMark(double mark) {
        this.mark = mark;
    }

    public double getContribution(){
        return mark * weight / 100;
    }

    public boolean countsForClassMark(){
        return countsForClassMark;
    }

    public void setCountsForClassMark(boolean countsForClassMark) {
        this.countsForClassMark = countsForClassMark;
    }

    public boolean isExam(){
        return !countsForClassMark;
    }
}
