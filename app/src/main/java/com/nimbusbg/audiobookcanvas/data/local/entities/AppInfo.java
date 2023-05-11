package com.nimbusbg.audiobookcanvas.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


@Entity(tableName = "appInfo",
        foreignKeys = @ForeignKey(entity = AudiobookProject.class,
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
    private int project_id;

    @NonNull
    @ColumnInfo(name = "app_version")
    private String appVersion;

    @ColumnInfo(name = "android_version")
    private String osVersion;

    @ColumnInfo(name = "device_type")
    private String deviceType;

    public AppInfo(int project_id, @NonNull String appVersion, String osVersion, String deviceType) {
        this.project_id = project_id;
        this.appVersion = appVersion;
        this.osVersion = osVersion;
        this.deviceType = deviceType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProject_id() {
        return project_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
