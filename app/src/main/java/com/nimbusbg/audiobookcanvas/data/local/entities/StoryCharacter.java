package com.nimbusbg.audiobookcanvas.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "storyCharacter")
public class StoryCharacter {
    @NonNull
    @ColumnInfo(name = "name")
    @PrimaryKey(autoGenerate = false)
    private String Name;

    @NonNull
    @ColumnInfo(name = "gender")
    private String gender;

    @NonNull
    @ColumnInfo(name = "voice")
    private String voice;

    public StoryCharacter(String gender, String voice) {
        this.gender = gender;
        this.voice = voice;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
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
}