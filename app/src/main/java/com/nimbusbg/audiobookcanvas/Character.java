package com.nimbusbg.audiobookcanvas;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Character")
public class Character {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    public String name;

    public String gender;

    public String voice;
}

