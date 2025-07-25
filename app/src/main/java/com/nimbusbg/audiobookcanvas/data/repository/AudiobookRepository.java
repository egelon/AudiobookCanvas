package com.nimbusbg.audiobookcanvas.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.nimbusbg.audiobookcanvas.data.listeners.DeletedItemListener;
import com.nimbusbg.audiobookcanvas.data.listeners.InsertedItemListener;
import com.nimbusbg.audiobookcanvas.data.listeners.InsertedMultipleItemsListener;
import com.nimbusbg.audiobookcanvas.data.local.AudiobookProjectDatabase;
import com.nimbusbg.audiobookcanvas.data.local.dao.ProjectWithMetadataDao;
import com.nimbusbg.audiobookcanvas.data.local.dao.ProjectWithTextBlocksDao;
import com.nimbusbg.audiobookcanvas.data.local.dao.TextBlockWithDataDao;
import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;
import com.nimbusbg.audiobookcanvas.data.local.entities.BlockState;
import com.nimbusbg.audiobookcanvas.data.local.entities.CharacterLine;
import com.nimbusbg.audiobookcanvas.data.local.entities.StoryCharacter;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;
import com.nimbusbg.audiobookcanvas.data.local.relations.MetadataWithCharacters;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithTextBlocks;
import com.nimbusbg.audiobookcanvas.data.local.relations.TextBlockWithData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AudiobookRepository
{
    private final ProjectWithMetadataDao projectWithMetadataDao;
    private final ProjectWithTextBlocksDao projectWithTextBlocksDao;
    private final TextBlockWithDataDao textBlockWithDataDao;
    private LiveData<List<ProjectWithMetadata>> allProjects;
    private long lastInsertedRowID;
    private int lastInsertedProjID;
    
    public AudiobookRepository(Application application)
    {
        AudiobookProjectDatabase database = AudiobookProjectDatabase.getInstance(application);
        projectWithMetadataDao = database.projectWithMetadataDao();
        projectWithTextBlocksDao = database.projectWithTextBlocksDao();
        textBlockWithDataDao = database.textBlockWithDataDao();
    }
    
    public void insertProjectWithMetadata(AudiobookProject project, AppInfo appInfo, AudiobookData audiobookData, final InsertedItemListener onInsertListener)
    {
        
        AudiobookProjectDatabase.databaseWriteExecutor.execute(() -> {
            lastInsertedRowID = projectWithMetadataDao.insertProject(project);
            lastInsertedProjID = projectWithMetadataDao.getProjectIdByRowId(lastInsertedRowID);
            appInfo.setProject_id(lastInsertedProjID);
            projectWithMetadataDao.insertAppInfo(appInfo);
            audiobookData.setProject_id(lastInsertedProjID);
            projectWithMetadataDao.insertAudiobookData(audiobookData);
            onInsertListener.onInsert(lastInsertedProjID);
        });
    }
    
    public void deleteProjectWithMetadataById(int id, final DeletedItemListener onDeleteListener)
    {
        AudiobookProjectDatabase.databaseWriteExecutor.execute(() -> {
            projectWithMetadataDao.deleteProjectWithMetadataById(id);
            onDeleteListener.onDelete();
        });
    }
    
    public void deleteTextBlocksByProjectId(int id, final DeletedItemListener onDeleteListener)
    {
        AudiobookProjectDatabase.databaseWriteExecutor.execute(() -> {
            projectWithMetadataDao.deleteProjectWithMetadataById(id);
            onDeleteListener.onDelete();
        });
    }
    
    public LiveData<ProjectWithMetadata> getProjectWithMetadataById(int id)
    {
        return projectWithMetadataDao.getProjectWithMetadataById(id);
    }
    
    public LiveData<List<ProjectWithMetadata>> getAllProjectsWithMetadata()
    {
        return projectWithMetadataDao.getAllProjectWithMetadata();
    }
    
    public LiveData<List<TextBlockWithData>> getTextBlocksWithDataByProjectId(int proj_id)
    {
        return textBlockWithDataDao.getTextBlocksWithDataByProjectId(proj_id);
    }
    
    
    
    public void updateProjectWithMetadata(int id, String projectNameStr, String audiobookFileName, String bookNameStr, String authorNameStr, String projectDescriptionStr, Date currentTime)
    {
        AudiobookProjectDatabase.databaseWriteExecutor.execute(() -> {
            projectWithMetadataDao.updateProjectNameById(id, projectNameStr, audiobookFileName, Converters.dateToTimestamp(currentTime));
            projectWithMetadataDao.updateProjectMetadataById(id, bookNameStr, authorNameStr, projectDescriptionStr);
        });
    }
    
    public void storeProjectLanguageByProjectId(int id, String languageISOCode)
    {
        AudiobookProjectDatabase.databaseWriteExecutor.execute(() -> {
            projectWithMetadataDao.updateProjectLanguageById(id, languageISOCode);
        });
    }
    
    
    public void insertTextBlocks(int projectID, ArrayList<String> textChunks, InsertedItemListener onInsertListener)
    {
        AudiobookProjectDatabase.databaseWriteExecutor.execute(() -> {
            List<TextBlock> textBlocks = new ArrayList();
            for(int i = 0; i < textChunks.size(); i++)
            {
                String audioPath = String.valueOf(projectID) + "_textBlock_" + String.valueOf(i) + ".wav";
                textBlocks.add(new TextBlock(projectID, audioPath, textChunks.get(i)));
            }
            projectWithTextBlocksDao.insertTextBlocks(textBlocks);
            onInsertListener.onInsert(0); //we don't care about the IDs here
        });
    }
    
    public LiveData<List<TextBlock>> getTextBlocksByProjectId(int prj_id)
    {
        return projectWithTextBlocksDao.getTextBlocksByProjectId(prj_id);
    }
    
    
    
    public LiveData<ProjectWithTextBlocks> getProjectWithTextBlocksById(int id)
    {
        return projectWithTextBlocksDao.getProjectWithTextBlocksById(id);
    }
    
    public void setTextBlockStateById(int textBlock_id, BlockState state)
    {
        AudiobookProjectDatabase.databaseWriteExecutor.execute(() -> {
            projectWithTextBlocksDao.setTextBlockStateById(textBlock_id, state);
        });
    }
    
    public void storeCharacterLinesAndCharacters(List<CharacterLine> characterLines, List<StoryCharacter> characters, InsertedMultipleItemsListener onInsertListener)
    {
        AudiobookProjectDatabase.databaseWriteExecutor.execute(() -> {
            projectWithTextBlocksDao.insertCharacters(characters);
            List<Long> insertedLines = projectWithTextBlocksDao.setCharacterLines(characterLines);
            onInsertListener.onInsert(insertedLines);
        });
    }
    
    public LiveData<List<CharacterLine>> getCharacterLinesByTextBlockId(int id)
    {
        return textBlockWithDataDao.getCharacterLinesByTextBlockId(id);
    }
    
    public LiveData<List<StoryCharacter>> getAllCharactersByProjectId(int prjId)
    {
        return textBlockWithDataDao.getAllCharactersByProjectId(prjId);
    }
    
    public LiveData<MetadataWithCharacters> getMetadataWithAllCharactersByProjectId(int prjId)
    {
        return textBlockWithDataDao.getMetadataWithAllCharactersByProjectId(prjId);
    }
    
    public LiveData<TextBlockWithData> getTextBlockWithDataByTextBlockId(int textblockId)
    {
        return textBlockWithDataDao.getTextBlockWithDataByTextBlockId(textblockId);
    }
    
    public LiveData<AudiobookData> getMetadataByProjectId(int id)
    {
        return projectWithMetadataDao.getMetadataByProjectId(id);
    }
    
    public void updateCharacter(String selectedCharacter, int itemIndex, int textblockId)
    {
        AudiobookProjectDatabase.databaseWriteExecutor.execute(() -> {
            textBlockWithDataDao.updateCharacter(selectedCharacter, itemIndex, textblockId);
        });
    }
    
    public void updateCharacterVoice(String selectedCharacterName, String newVoice, int projectId)
    {
        AudiobookProjectDatabase.databaseWriteExecutor.execute(() -> {
            textBlockWithDataDao.updateCharacterVoice(selectedCharacterName, newVoice, projectId);
        });
    }
}
