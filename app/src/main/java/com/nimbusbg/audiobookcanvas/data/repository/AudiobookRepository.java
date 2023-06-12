package com.nimbusbg.audiobookcanvas.data.repository;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.nimbusbg.audiobookcanvas.data.local.AudiobookProjectDatabase;
import com.nimbusbg.audiobookcanvas.data.local.dao.ProjectWithMetadataDao;
import com.nimbusbg.audiobookcanvas.data.local.dao.ProjectWithTextBlocksDao;
import com.nimbusbg.audiobookcanvas.data.local.dao.TextBlockWithDataDao;
import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithTextBlocks;
import com.nimbusbg.audiobookcanvas.data.local.relations.TextBlockWithData;

import java.util.ArrayList;
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
    
    public void insertProjectWithMetadata(AudiobookProject project, AppInfo appInfo, AudiobookData audiobookData)
    {
        AudiobookProjectDatabase.databaseWriteExecutor.execute(() -> {
            lastInsertedRowID = projectWithMetadataDao.insertProject(project);
            lastInsertedProjID = projectWithMetadataDao.getProjectIdByRowId(lastInsertedRowID);
            appInfo.setProject_id(lastInsertedProjID);
            projectWithMetadataDao.insertAppInfo(appInfo);
            audiobookData.setProject_id(lastInsertedProjID);
            projectWithMetadataDao.insertAudiobookData(audiobookData);
        });
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
    
    public void deleteProjectWithMetadataById(int id)
    {
        AudiobookProjectDatabase.databaseWriteExecutor.execute(() -> {
            projectWithMetadataDao.deleteProjectWithMetadataById(id);
        });
    }
    
    public void deleteProjectWithMetadataById(int id, final DeletedItemListener onDeleteListener)
    {
        AudiobookProjectDatabase.databaseWriteExecutor.execute(() -> {
            projectWithMetadataDao.deleteProjectWithMetadataById(id);
            onDeleteListener.onDelete();
        });
    }
    
    public void deleteAllProjectsWithMetadata()
    {
        //maybe this also needs the executor?
        projectWithMetadataDao.deleteAllProjectsWithMetadata();
    }
    
    public LiveData<ProjectWithMetadata> getProjectWithMetadataById(int id)
    {
        return projectWithMetadataDao.getProjectWithMetadataById(id);
    }
    
    public LiveData<ProjectWithMetadata> getProjectWithMetadataByRowId(int row_id)
    {
        return projectWithMetadataDao.getProjectWithMetadataByRowId(row_id);
    }
    
    public LiveData<List<ProjectWithMetadata>> getAllProjectsWithMetadata()
    {
        return projectWithMetadataDao.getAllProjectWithMetadata();
    }
    
    public LiveData<List<TextBlockWithData>> getTextBlocksWithDataByProjectId(int proj_id)
    {
        return textBlockWithDataDao.getTextBlocksWithDataByProjectId(proj_id);
    }
    
    
    
    public void updateProjectWithMetadata(AudiobookProject project, AppInfo appInfo, AudiobookData audiobookData)
    {
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
    
    public LiveData<ProjectWithTextBlocks> getProjectWithTextBlocksById(int id)
    {
        return projectWithTextBlocksDao.getProjectWithTextBlocksById(id);
    }
    
    public void setAPIProcessedFlagForTextBlockId(int textBlock_id, Boolean flag)
    {
        AudiobookProjectDatabase.databaseWriteExecutor.execute(() -> {
            projectWithTextBlocksDao.setAPIProcessedFlagForTextBlockId(textBlock_id, flag);
        });
    }
    
    
}
