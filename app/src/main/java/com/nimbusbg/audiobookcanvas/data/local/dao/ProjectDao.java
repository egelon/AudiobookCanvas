package com.nimbusbg.audiobookcanvas.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;

import java.util.List;

@Dao
public abstract class ProjectDao extends BaseDao<AudiobookProject> {
    public ProjectDao() {
        super("audiobookProject", AudiobookProject.class);
    }

    @Query("SELECT * FROM AudiobookProject ORDER BY last_modified DESC")
    abstract LiveData<List<AudiobookProject>> getAllProjectsNewestToOldest();

}