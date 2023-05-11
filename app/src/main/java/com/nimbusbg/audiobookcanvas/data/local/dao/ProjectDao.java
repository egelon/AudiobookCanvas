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
public interface ProjectDao {
    @Insert
    void insert(AudiobookProject audiobookProject);

    @Update
    void update(AudiobookProject audiobookProject);

    @Delete
    void delete(AudiobookProject audiobookProject);

    @Query("DELETE FROM AudiobookProject")
    void deleteAllProjects();

    @Query("SELECT * FROM AudiobookProject ORDER BY id DESC")
    LiveData<List<AudiobookProject>> getAllProjects();

    @Query("SELECT * FROM AudiobookProject WHERE id =:requestedID LIMIT 1")
    LiveData<AudiobookProject> getProjectById(int requestedID);
}