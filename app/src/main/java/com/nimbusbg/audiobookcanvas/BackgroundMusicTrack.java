package com.nimbusbg.audiobookcanvas;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "background_music_track",
        foreignKeys = @ForeignKey(entity = Content.class,
                parentColumns = "project_id",
                childColumns = "content_id",
                onDelete = ForeignKey.CASCADE))
public class BackgroundMusicTrack {
    @PrimaryKey(autoGenerate = true)
    public int track_id;

    public int content_id;

    @NonNull
    public String primary_atmosphere;

    @NonNull
    public String secondary_atmosphere;

    public String volume;

    @NonNull
    public String filePath;
}

