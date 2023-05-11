package com.nimbusbg.audiobookcanvas.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Music_Track")
public class MusicTrack {
    @NonNUll
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "primary_atmosphere")
    public String primaryAtmosphere;

    @NonNull
    @ColumnInfo(name = "secondary_atmosphere")
    public String secondaryAtmosphere;

    @NonNull
    @ColumnInfo(name = "music_path")
    public String musicPath;

    public MusicTrack(int id, String primaryAtmosphere, String secondaryAtmosphere, String musicPath) {
        this.id = id;
        this.primaryAtmosphere = primaryAtmosphere;
        this.secondaryAtmosphere = secondaryAtmosphere;
        this.musicPath = musicPath;
    }

    public int getId() {
        return id;
    }
}
