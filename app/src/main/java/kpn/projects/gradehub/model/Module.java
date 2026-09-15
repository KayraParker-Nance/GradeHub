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
}
