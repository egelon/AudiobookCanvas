package com.nimbusbg.audiobookcanvas.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "Project")
public class Project {
    @NonNull
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "project_version")
    private String projectVersion;

    @NonNull
    @ColumnInfo(name = "completed")
    private Boolean isCompleted;

    @NonNull
    @ColumnInfo(name = "last_processed_block_id")
    private int lastProcessedBlockId;

    @NonNull
    @ColumnInfo(name = "name")
    private String projectName;

    @ColumnInfo(name = "input_file_path")
    private String inputFilePath;

    @ColumnInfo(name = "output_XML_file_path")
    private String outputXMLFilePath;

    @ColumnInfo(name = "output_audiobook_path")
    private String output_audiobook_path;

    @NonNull
    @ColumnInfo(name = "created_on")
    private Date createdOn;

    @NonNull
    @ColumnInfo(name = "last_modified")
    private Date lastModified;

    public Project(final int id, @NonNull String projectVersion, @NonNull Boolean isCompleted, final int lastProcessedBlockId, @NonNull String projectName, @NonNull Date createdOn, @NonNull Date lastModified) {
        this.id = id;
        this.projectVersion = projectVersion;
        this.isCompleted = isCompleted;
        this.lastProcessedBlockId = lastProcessedBlockId;
        this.projectName = projectName;
        this.createdOn = createdOn;
        this.lastModified = lastModified;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }

    public int getLastProcessedBlockId() {
        return lastProcessedBlockId;
    }

    public void setLastProcessedBlockId(int lastProcessedBlockId) {
        this.lastProcessedBlockId = lastProcessedBlockId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getInputFilePath() {
        return inputFilePath;
    }

    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public String getOutputXMLFilePath() {
        return outputXMLFilePath;
    }

    public void setOutputXMLFilePath(String outputXMLFilePath) {
        this.outputXMLFilePath = outputXMLFilePath;
    }

    public String getOutput_audiobook_path() {
        return output_audiobook_path;
    }

    public void setOutput_audiobook_path(String output_audiobook_path) {
        this.output_audiobook_path = output_audiobook_path;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}