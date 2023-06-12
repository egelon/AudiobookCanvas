package com.nimbusbg.audiobookcanvas.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.nimbusbg.audiobookcanvas.data.local.relations.TextBlockWithData;

import java.util.List;

@Dao
public interface TextBlockWithDataDao
{
    @Transaction
    @Query("Select * FROM textBlock WHERE project_id = :id")
    LiveData<List<TextBlockWithData>> getTextBlocksWithDataByProjectId(int id);
}
