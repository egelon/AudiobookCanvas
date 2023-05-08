package com.nimbusbg.audiobookcanvas;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "text_block",
        foreignKeys = @ForeignKey(entity = Content.class,
                parentColumns = "project_id",
                childColumns = "content_id",
                onDelete = ForeignKey.CASCADE))
public class TextBlock {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int content_id;

    public int size;

    public int index;

    @NonNull
    public String text;

    @ColumnInfo(name = "generated_audio")
    public String generatedAudio;
}

