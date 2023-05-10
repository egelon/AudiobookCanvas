package com.nimbusbg.audiobookcanvas.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "App_Info")
public class AppInfo {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    @ColumnInfo(name = "version")
    public String appVersion;

    @ColumnInfo(name = "android_version")
    public String osVersion;

    @ColumnInfo(name = "device_type")
    public String deviceType;

    public AppInfo(final int id, @NonNull String appVersion) {
        this.id = id;
        this.appVersion = appVersion;
    }
}
