package com.nimbusbg.audiobookcanvas.data.repository;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.nimbusbg.audiobookcanvas.data.local.AudiobookProjectDatabase;
import com.nimbusbg.audiobookcanvas.data.local.dao.ProjectWithMetadataDao;
import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;

import java.util.List;

public class AudiobookRepository
{
    private final ProjectWithMetadataDao projectWithMetadataDao;
    private LiveData<List<ProjectWithMetadata>> allProjects;
    private long lastInsertedRowID;
    private int lastInsertedProjID;
    
    public AudiobookRepository(Application application)
    {
        AudiobookProjectDatabase database = AudiobookProjectDatabase.getInstance(application);
        projectWithMetadataDao = database.projectWithMetadataDao();
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
    
    public void updateProjectWithMetadata(AudiobookProject project, AppInfo appInfo, AudiobookData audiobookData)
    {
    }
    
    public void readTxtFile(Uri fileUri)
    {
    }
}
