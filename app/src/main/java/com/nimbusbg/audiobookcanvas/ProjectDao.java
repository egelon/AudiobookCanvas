package com.nimbusbg.audiobookcanvas;

import androidx.room.Dao;

@Dao
public interface ProjectDao {
    @Insert
    void insert(Project project);

    @Update
    void update(Project project);

    @Delete
    void delete(Project project);

    @Query("DELETE FROM Project")
    void deleteAllProjects();

    @Query("SELECT * FROM Project ORDER BY id DESC")
    LiveData<List<Project>> getAllProjects();
}