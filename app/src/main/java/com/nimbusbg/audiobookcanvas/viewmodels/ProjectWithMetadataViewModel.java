package com.nimbusbg.audiobookcanvas.viewmodels;

import android.app.Application;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;

import com.nimbusbg.audiobookcanvas.BuildConfig;
import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.listeners.DeletedItemListener;
import com.nimbusbg.audiobookcanvas.data.listeners.InsertedItemListener;
import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;
import com.nimbusbg.audiobookcanvas.data.repository.AudiobookRepository;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProjectWithMetadataViewModel extends AndroidViewModel
{
    private final AudiobookRepository repository;
    private ProjectWithMetadata newProject;
    SharedPreferences preferences;
    
    
    private LiveData<List<ProjectWithMetadata>> allProjectsWithMetadata;
    
    public void createEmptyNewProject()
    {
        newProject = new ProjectWithMetadata();
        
        Date currentTime = Calendar.getInstance().getTime();
        newProject.project = new AudiobookProject(getResourceString(R.string.xmlProjectFileVersion),
                false,
                0,
                getResourceString(R.string.defaultAudiobookProjName),
                "",
                getResourceString(R.string.defaultAudiobookProjName) + ".xml",
                getResourceString(R.string.defaultAudiobookTitle),
                currentTime,
                currentTime);
        newProject.appInfo = new AppInfo(0, BuildConfig.VERSION_NAME, getAndroidVersion(), getDeviceName());
        newProject.audiobookData = new AudiobookData(0, getResourceString(R.string.defaultAudiobookTitle), "", Locale.getDefault().toLanguageTag(), "");
    }
    
    public ProjectWithMetadataViewModel(@NonNull Application application)
    {
        super(application);
    
        preferences = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
        
        repository = new AudiobookRepository(application);
        createEmptyNewProject();
        allProjectsWithMetadata = repository.getAllProjectsWithMetadata();
    }
    
    public String getAndroidVersion()
    {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android SDK: " + sdkVersion + " (" + release + ")";
    }
    
    public String getDeviceName()
    {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer))
        {
            return model;
        } else
        {
            return (manufacturer + " " + model);
        }
    }
    
    public ProjectWithMetadata getNewProject()
    {
        return newProject;
    }
    
    public void setProjectName(String projectName) {
        if (newProject != null) {
            newProject.project.setProjectName(projectName);
        }
    }
    
    public void setBookName(String bookTitle) {
        if (newProject != null) {
            newProject.audiobookData.setBookTitle(bookTitle);
        }
    }
    
    public void setAuthorName(String author) {
        if (newProject != null) {
            newProject.audiobookData.setAuthor(author);
        }
    }
    
    public void setDescriptionText(String description) {
        if (newProject != null) {
            newProject.audiobookData.setDescription(description);
        }
    }
    
    public void setSelectedFileUri(String uri) {
        if (newProject != null) {
            newProject.project.setInputFilePath(uri);
        }
    }
    
    public void insertNewProject(String projName, String title, String author, String descr, String uri, InsertedItemListener onInsertListener)
    {
        createEmptyNewProject();
        newProject.project.setProjectName(projName);
        newProject.project.setInputFilePath(uri);
        newProject.audiobookData.setBookTitle(title);
        newProject.audiobookData.setAuthor(author);
        newProject.audiobookData.setDescription(descr);
        
        repository.insertProjectWithMetadata(newProject.project, newProject.appInfo, newProject.audiobookData, onInsertListener);
    }
    
    private String getResourceString(int resourceID)
    {
        return getApplication().getResources().getString(resourceID);
    }
    
    public void deleteProjectWithMetadataById(int id, DeletedItemListener onDeleteListener)
    {
        repository.deleteProjectWithMetadataById(id, onDeleteListener);
    }
    
    //TODO: do the same for the other methods of the repository
    
    public LiveData<List<ProjectWithMetadata>> getAllProjectsWithMetadata()
    {
        return allProjectsWithMetadata;
    }
    
    public LiveData<ProjectWithMetadata> getProjectWithMetadataById(int id)
    {
        return repository.getProjectWithMetadataById(id);
    }
    
    public void updateProjectWithMetadata(int id, String projectNameStr, String bookNameStr, String authorNameStr, String projectDescriptionStr)
    {
        Date currentTime = Calendar.getInstance().getTime();
        String newAudiobookName = projectNameStr + ".mp3";
        repository.updateProjectWithMetadata(id, projectNameStr, newAudiobookName, bookNameStr, authorNameStr, projectDescriptionStr, currentTime);
    }
    
    public boolean hasApiKey()
    {
        String API_key = preferences.getString("openai_API_key", "");
        if(API_key.isEmpty())
        {
            return false;
        }
        return true;
    }
}

