package com.nimbusbg.audiobookcanvas.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.nimbusbg.audiobookcanvas.data.local.entities.CharacterLine;
import com.nimbusbg.audiobookcanvas.data.local.entities.StoryCharacter;
import com.nimbusbg.audiobookcanvas.data.local.relations.TextBlockWithData;

import java.util.List;

@Dao
public interface TextBlockWithDataDao
{
    @Transaction
    @Query("Select * FROM textBlock WHERE project_id = :id")
    LiveData<List<TextBlockWithData>> getTextBlocksWithDataByProjectId(int id);
    
    @Transaction
    @Query("Select * FROM textBlock WHERE id = :id")
    LiveData<TextBlockWithData> getTextBlockWithDataByTextBlockId(int id);
    
    @Transaction
    @Query("Select * FROM characterLine WHERE text_block_id = :id")
    LiveData<List<CharacterLine>> getCharacterLinesByTextBlockId(int id);
    
    @Transaction
    @Query("Select * FROM storyCharacter")
    LiveData<List<StoryCharacter>> getAllCharacters();
}
