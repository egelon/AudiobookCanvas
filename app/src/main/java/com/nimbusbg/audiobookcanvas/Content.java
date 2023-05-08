package com.nimbusbg.audiobookcanvas;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "content",
        foreignKeys = @ForeignKey(entity = Project.class,
                parentColumns = "id",
                childColumns = "project_id",
                onDelete = ForeignKey.CASCADE))
public class Content {
    @PrimaryKey
    public int project_id;

    @NonNull
    @ColumnInfo(name = "filename")
    public String filename;
}

