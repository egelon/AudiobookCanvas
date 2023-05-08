package com.nimbusbg.audiobookcanvas;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "metadata",
        foreignKeys = @ForeignKey(entity = Project.class,
                parentColumns = "id",
                childColumns = "project_id",
                onDelete = ForeignKey.CASCADE))
public class Metadata {
    @PrimaryKey
    public int project_id;

    @NonNull
    public String title;

    @NonNull
    public String author;

    @NonNull
    public String language;

    @NonNull
    public String description;
}
