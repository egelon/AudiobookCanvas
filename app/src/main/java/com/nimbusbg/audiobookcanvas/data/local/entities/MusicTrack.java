package com.nimbusbg.audiobookcanvas.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity(tableName = "musicTrack")
public class MusicTrack  implements Serializable {
    @NonNull
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "primary_atmosphere")
    private String primaryAtmosphere;

    @NonNull
    @ColumnInfo(name = "secondary_atmosphere")
    private String secondaryAtmosphere;

    @NonNull
    @ColumnInfo(name = "music_path")
    private String musicPath;

    public MusicTrack(String primaryAtmosphere, String secondaryAtmosphere, String musicPath) {
        this.primaryAtmosphere = primaryAtmosphere;
        this.secondaryAtmosphere = secondaryAtmosphere;
        this.musicPath = musicPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrimaryAtmosphere() {
        return primaryAtmosphere;
    }

    public void setPrimaryAtmosphere(String primaryAtmosphere) {
        this.primaryAtmosphere = primaryAtmosphere;
    }

    public String getSecondaryAtmosphere() {
        return secondaryAtmosphere;
    }

    public void setSecondaryAtmosphere(String secondaryAtmosphere) {
        this.secondaryAtmosphere = secondaryAtmosphere;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }
}
