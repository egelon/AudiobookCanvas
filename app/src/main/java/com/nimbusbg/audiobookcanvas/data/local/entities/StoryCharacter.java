package com.nimbusbg.audiobookcanvas.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "storyCharacter",
        foreignKeys = {
                @ForeignKey(entity = AudiobookProject.class,
                        parentColumns = "id",
                        childColumns = "project_id",
                        onDelete = ForeignKey.CASCADE)},
        indices = {@Index(value = {"name", "project_id"}, unique = true)}) // Unique constraint)
public class StoryCharacter  implements Serializable {
    @NonNull
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "gender")
    private String gender;

    @NonNull
    @ColumnInfo(name = "voice")
    private String voice;
    
    @NonNull
    @ColumnInfo(name = "project_id")
    private int projectId;

    public StoryCharacter(String name, String gender, String voice, final int projectId) {
        this.name = name;
        this.gender = gender;
        this.voice = voice;
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }
    
    public int getProjectId()
    {
        return projectId;
    }
    
    public void setProjectId(int projectId)
    {
        this.projectId = projectId;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
}