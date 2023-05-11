package com.nimbusbg.audiobookcanvas.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Character")
public class Character {
    @NonNull
    @ColumnInfo(name = "name")
    @PrimaryKey(autoGenerate = false)
    public String Name;

    @NonNull
    @ColumnInfo(name = "gender")
    public String gender;

    @NonNull
    @ColumnInfo(name = "voice")
    public String voice;

    public Character(String name, String gender, String voice) {
        Name = name;
        this.gender = gender;
        this.voice = voice;
    }
}