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
public interface ProjectWithMetadataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertProject(AudiobookProject project);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAppInfo(AppInfo appInfo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAudiobookData(AudiobookData audiobookData);

    @Transaction
    @Query("Select * FROM audiobookProject WHERE id = :id")
    LiveData<List<ProjectWithMetadata>> getProjectWithMetadataById(int id);

    @Transaction
    @Query("Select * FROM audiobookProject ORDER BY id ASC")
    LiveData<List<ProjectWithMetadata>> getAllProjectWithMetadata();
}
