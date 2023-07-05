package com.nimbusbg.audiobookcanvas.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "textBlock",
        foreignKeys = {
                @ForeignKey(entity = AudiobookProject.class,
                        parentColumns = "id",
                        childColumns = "project_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = MusicTrack.class,
                        parentColumns = "id",
                        childColumns = "background_track_id",
                        onDelete = ForeignKey.SET_NULL)
        })
public class TextBlock  implements Serializable {
    @NonNull
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "project_id")
    private int projectId;

    @ColumnInfo(name = "background_track_id")
    private Integer backgroundTrackId; // Nullable, so use Integer instead of int

    @ColumnInfo(name = "background_track_volume")
    private int backgroundTrackVolume;

    @NonNull
    @ColumnInfo(name = "generated_audio_path")
    private String generatedAudioPath;

    @NonNull
    @ColumnInfo(name = "text")
    private String text;
    
    @NonNull
    @ColumnInfo(name="state")
    private BlockState state;
    
    @Ignore
    private String[] textLines;

    public TextBlock(final int projectId, String generatedAudioPath, String text) {
        this.projectId = projectId;
        this.generatedAudioPath = generatedAudioPath;
        this.text = text;
        this.backgroundTrackVolume = 50;
        this.state = BlockState.NOT_REQUESTED;
    
        storeTextLines();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public Integer getBackgroundTrackId() {
        return backgroundTrackId;
    }

    public void setBackgroundTrackId(Integer backgroundTrackId) {
        this.backgroundTrackId = backgroundTrackId;
    }

    public int getBackgroundTrackVolume() {
        return backgroundTrackVolume;
    }

    public void setBackgroundTrackVolume(int backgroundTrackVolume) {
        this.backgroundTrackVolume = backgroundTrackVolume;
    }

    public String getGeneratedAudioPath() {
        return generatedAudioPath;
    }

    public void setGeneratedAudioPath(String generatedAudioPath) {
        this.generatedAudioPath = generatedAudioPath;
    }

    public String getText() {
        return text;
    }
    
    private void storeTextLines()
    {
        if(!this.text.isEmpty())
        {
            this.textLines = this.text.split("\n");
        }
    }

    public void setText(String text)
    {
        this.text = text;
        storeTextLines();
    }
    
    public String[] getTextLines()
    {
        return this.textLines;
    }
    
    public String getLineByIndex(int index)
    {
        if(this.textLines != null && index >= 0 && index < this.textLines.length)
        {
            return this.textLines[index];
        }
        return null;
    }
    
    public String getLineAudioPath(int index)
    {
        if(index > 0 && index <= this.textLines.length)
        {
            return "textBlock_" + String.valueOf(id) + "_Line_" + String.valueOf(index) + ".wav";
        }
        else
        {
            return "textBlock_" + String.valueOf(id) + "_Line_-1.wav";
        }
    }
    
    @NonNull
    public BlockState getState()
    {
        return state;
    }
    
    public void setState(BlockState state)
    {
        this.state = state;
    }
}
