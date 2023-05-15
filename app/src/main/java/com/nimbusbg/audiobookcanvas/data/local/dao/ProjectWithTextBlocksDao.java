package com.nimbusbg.audiobookcanvas.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithTextBlocks;

import java.util.List;

@Dao
public interface ProjectWithTextBlocksDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertProject(AudiobookProject project);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTextBlock(TextBlock textBlock);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertTextBlocks(List<TextBlock> textBlocks);

    @Transaction
    @Query("Select * FROM audiobookProject WHERE id = :id")
    LiveData<List<ProjectWithTextBlocks>> getProjectWithTextBlocksById(int id);
}