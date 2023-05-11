package com.nimbusbg.audiobookcanvas.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "App_Info",
        foreignKeys = @ForeignKey(entity = Project.class,
                parentColumns = "id",
                childColumns = "project_id",
                onDelete = ForeignKey.CASCADE))
public class AppInfo {

    @NonNull
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "project_id")
    public int project_id;

    @NonNull
    @ColumnInfo(name = "app_version")
    public String appVersion;

    @ColumnInfo(name = "android_version")
    public String osVersion;

    @ColumnInfo(name = "device_type")
    public String deviceType;

    public AppInfo(final int id, final int project_id, @NonNull String appVersion) {
        this.id = id;
        this.project_id = project_id;
        this.appVersion = appVersion;
    }

    public int getId() {
        return id;
    }
}
