package com.nimbusbg.audiobookcanvas;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "character",
        foreignKeys = @ForeignKey(entity = Content.class,
                parentColumns = "project_id",
                childColumns = "content_id",
                onDelete = ForeignKey.CASCADE))
public class Character {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int content_id;

    @NonNull
    public String name;

    public String gender;

    public String voice;
}

