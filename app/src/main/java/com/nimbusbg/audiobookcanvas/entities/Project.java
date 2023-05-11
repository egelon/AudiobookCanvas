package com.nimbusbg.audiobookcanvas.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Project")
public class Project {
    public int getId() {
        return id;
    }

    @NonNull
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

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

    @ColumnInfo(name = "output_audiobook_path")
    public String output_audiobook_path;

    @NonNull
    @ColumnInfo(name = "created_on")
    public Date createdOn;

    @NonNull
    @ColumnInfo(name = "last_modified")
    public Date lastModified;

    public Project(final int id, @NonNull String projectVersion, @NonNull Boolean isCompleted, final int lastProcessedBlockId, @NonNull String projectName, @NonNull Date createdOn, @NonNull Date lastModified) {
        this.id = id;
        this.projectVersion = projectVersion;
        this.isCompleted = isCompleted;
        this.lastProcessedBlockId = lastProcessedBlockId;
        this.projectName = projectName;
        this.createdOn = createdOn;
        this.lastModified = lastModified;
    }
}