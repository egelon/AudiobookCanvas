package com.nimbusbg.audiobookcanvas.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.nimbusbg.audiobookcanvas.data.local.entities.BlockState;
import com.nimbusbg.audiobookcanvas.data.local.entities.CharacterLine;
import com.nimbusbg.audiobookcanvas.data.local.entities.StoryCharacter;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithTextBlocks;

import java.util.ArrayList;
import java.util.List;
@Dao
public interface ProjectWithTextBlocksDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> insertTextBlocks(List<TextBlock> textBlocks);
    
    @Transaction
    @Query("Select * FROM textBlock WHERE project_id = :prj_id")
    LiveData<List<TextBlock>> getTextBlocksByProjectId(int prj_id);
    
    @Transaction
    @Query("Select * FROM audiobookProject WHERE id = :id")
    LiveData<ProjectWithTextBlocks> getProjectWithTextBlocksById(int id);
    
    @Transaction
    @Query("DELETE FROM textBlock WHERE project_id = :project_id")
    void deleteAllTextBlocksForProject(int project_id);
    
    @Query("UPDATE textBlock SET state = :state WHERE id =:textblock_id")
    void setTextBlockStateById(int textblock_id, BlockState state);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public List<Long> setCharacterLines(List<CharacterLine> characterLines);
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertCharacter(StoryCharacter character);
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public List<Long> insertCharacters(List<StoryCharacter> characters);
    
    @Query("Select * FROM storyCharacter WHERE name = :name")
    StoryCharacter getCharacterByName(String name);
}
