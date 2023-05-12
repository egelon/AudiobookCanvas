package com.nimbusbg.audiobookcanvas.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;

@Dao
public interface AppInfoDao extends BaseDao<AppInfo> {
    @Transaction
    @Query("SELECT * from appInfo WHERE project_id = :project_id")
    LiveData<AppInfo> getAppInfoByProjectId(int project_id);

}
