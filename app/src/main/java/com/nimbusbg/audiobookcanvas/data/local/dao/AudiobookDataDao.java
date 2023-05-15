package com.nimbusbg.audiobookcanvas.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;

@Dao
public interface AudiobookDataDao extends BaseDao<AudiobookData> {
    @Transaction
    @Query("SELECT * from audiobookData WHERE project_id = :project_id")
    LiveData<AudiobookData> getAudiobookDataByProjectId(int project_id);
}
