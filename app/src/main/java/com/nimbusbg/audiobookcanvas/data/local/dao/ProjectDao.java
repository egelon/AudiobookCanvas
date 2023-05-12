package com.nimbusbg.audiobookcanvas.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Transaction;
import androidx.room.Update;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;

import java.util.List;

@Dao
public interface ProjectDao extends BaseDao<AudiobookProject> {
    @Transaction
    @Query("DELETE FROM audiobookProject")
    int deleteAllProjects();

    @Transaction
    @Query("SELECT * from audiobookProject")
    LiveData<List<AudiobookProject>> getAllProjects();

    @Transaction
    @Query("SELECT * from audiobookProject WHERE id = :id")
    LiveData<AudiobookProject> getProjectById(int id);

    @Transaction
    @Query("SELECT * from audiobookProject WHERE id IN (:ids)")
    LiveData<List<AudiobookProject>> getProjectsByIds(int[] ids);

    @Transaction
    @Query("SELECT * FROM AudiobookProject ORDER BY last_modified DESC")
    LiveData<List<AudiobookProject>> getAllProjectsNewestToOldest();
}