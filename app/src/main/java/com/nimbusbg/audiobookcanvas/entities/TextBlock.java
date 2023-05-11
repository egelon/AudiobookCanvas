package com.nimbusbg.audiobookcanvas.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Text_Block",
        foreignKeys = {
                @ForeignKey(entity = Project.class,
                        parentColumns = "id",
                        childColumns = "project_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = MusicTrack.class,
                        parentColumns = "id",
                        childColumns = "background_track_id",
                        onDelete = ForeignKey.SET_NULL)
        })
public class TextBlock {
    @NonNull
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "project_id")
    public int projectId;

    @ColumnInfo(name = "background_track_id")
    public Integer backgroundTrackId; // Nullable, so use Integer instead of int

    @ColumnInfo(name = "background_track_volume")
    public int backgroundTrackVolume;

    @NonNull
    @ColumnInfo(name = "generated_audio_path")
    public String generatedAudioPath;

    @NonNull
    @ColumnInfo(name = "text")
    public String text;

    public TextBlock(final int id, final int projectId, String generatedAudioPath, String text) {
        this.id = id;
        this.projectId = projectId;
        this.generatedAudioPath = generatedAudioPath;
        this.text = text;
    }

    // Add constructor, getters, and setters here

    public int getId() {
        return id;
    }
}
