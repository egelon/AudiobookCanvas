package com.nimbusbg.audiobookcanvas.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Project",
        foreignKeys = {
            @ForeignKey(entity = AppInfo.class,
                        parentColumns = "id",
                        childColumns = "id",
                        onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = AudiobookData.class,
                        parentColumns = "id",
                    childColumns = "id",
                    onDelete = ForeignKey.CASCADE)
        }
       )


public class Project {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    @ColumnInfo(name = "app_info_id")
    public int appInfoId;

    @NonNull
    @ColumnInfo(name = "audiobook_data_id")
    public int audiobookDataId;

    @NonNull
    @ColumnInfo(name = "project_version")
    public String projectVersion;

    @NonNull
    @ColumnInfo(name = "completed")
    public Boolean isCompleted;

    @NonNull
    @ColumnInfo(name = "last_processed_block_id")
    public int lastProcessedBlockId;

    @NonNull
    @ColumnInfo(name = "name")
    public String projectName;
    @ColumnInfo(name = "input_file_path")
    public String inputFilePath;
    @ColumnInfo(name = "output_XML_file_path")
    public String outputXMLFilePath;

    @NonNull
    @ColumnInfo(name = "created_on")
    public Date createdOn;

    @NonNull
    @ColumnInfo(name = "last_modified")
    public Date lastModified;

    public Project(final int id, final int appInfoId, final int audiobookDataId, @NonNull String projectVersion, @NonNull Boolean isCompleted, final int lastProcessedBlockId, @NonNull String projectName, @NonNull Date createdOn, @NonNull Date lastModified) {
        this.id = id;
        this.appInfoId = appInfoId;
        this.audiobookDataId = audiobookDataId;
        this.projectVersion = projectVersion;
        this.isCompleted = isCompleted;
        this.lastProcessedBlockId = lastProcessedBlockId;
        this.projectName = projectName;
        this.createdOn = createdOn;
        this.lastModified = lastModified;
    }
}