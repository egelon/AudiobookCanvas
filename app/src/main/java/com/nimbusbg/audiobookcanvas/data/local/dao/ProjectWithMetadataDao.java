package com.nimbusbg.audiobookcanvas.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;

import java.util.List;

@Dao
public interface ProjectWithMetadataDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertProject(AudiobookProject project);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAppInfo(AppInfo appInfo);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAudiobookData(AudiobookData audiobookData);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProjectWithMetadata(AudiobookProject project, AudiobookData audiobookData, AppInfo appInfo);
    
    
    @Transaction
    @Query("SELECT id FROM audiobookProject WHERE rowid = :rowId")
    int getProjectIdByRowId(long rowId);
    
    @Transaction
    @Query("SELECT * FROM audiobookProject WHERE rowid = :rowId")
    LiveData<ProjectWithMetadata> getProjectWithMetadataByRowId(long rowId);
    
    @Transaction
    @Query("Select * FROM audiobookProject WHERE id = :id")
    LiveData<ProjectWithMetadata> getProjectWithMetadataById(int id);
    
    @Transaction
    @Query("Select * FROM audiobookProject ORDER BY id DESC")
    LiveData<List<ProjectWithMetadata>> getAllProjectWithMetadata();
    
    @Transaction
    @Query("DELETE FROM audiobookProject WHERE id = :id")
    void deleteProjectWithMetadataById(int id);
    
    @Transaction
    @Query("DELETE FROM audiobookProject")
    void deleteAllProjectsWithMetadata();
}
