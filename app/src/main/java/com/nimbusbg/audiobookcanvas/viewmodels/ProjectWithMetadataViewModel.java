package com.nimbusbg.audiobookcanvas.viewmodels;


import android.app.Application;
import android.icu.util.Calendar;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nimbusbg.audiobookcanvas.BuildConfig;
import com.nimbusbg.audiobookcanvas.MyAudiobookCanvasApplication;
import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;
import com.nimbusbg.audiobookcanvas.data.repository.AudiobookRepository;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProjectWithMetadataViewModel extends AndroidViewModel {

    private AudiobookRepository repository;

    public ProjectWithMetadataViewModel(@NonNull Application application) {
        super(application);

        repository = new AudiobookRepository(application, ((MyAudiobookCanvasApplication)application).getExecutorService());
    }

    public String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android SDK: " + sdkVersion + " (" + release +")";
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return (manufacturer + " " + model);
        }
    }


    public void insertProjectWithMetadata(AudiobookProject project, AppInfo appInfo, AudiobookData audiobookData)
    {
        repository.insertProjectWithMetadata(project, appInfo, audiobookData);
    }

    public void updateProjectWithMetadata(int id, String projectNameStr, String audiobookNameStr, String bookNameStr, String authorNameStr, String projectDescriptionStr)
    {

        //repository.updateProjectWithMetadata(project, appInfo, audiobookData);
    }

    public ProjectWithMetadata getEmptyProject()
    {
        ProjectWithMetadata newEmptyProject;

        Date currentTime = Calendar.getInstance().getTime();
        newEmptyProject.project = new AudiobookProject(getResourceString(R.string.xmlProjectFileVersion),
                false,
                0,
                getResourceString(R.string.defaultAudiobookProjName),
                "",
                getResourceString(R.string.defaultAudiobookProjName) + ".xml",
                getResourceString(R.string.defaultAudiobookTitle) + ".mp3",
                currentTime,
                currentTime);
        newEmptyProject.appInfo = new AppInfo(0, BuildConfig.VERSION_NAME, getAndroidVersion(), getDeviceName());
        newEmptyProject.audiobookData = new AudiobookData(0, getResourceString(R.string.defaultAudiobookTitle), "", Locale.getDefault().toLanguageTag(), "");

        return newEmptyProject;
    }

    public void insertNewEmptyProject()
    {
        Date currentTime = Calendar.getInstance().getTime();

        AudiobookProject newProject = new AudiobookProject(getResourceString(R.string.xmlProjectFileVersion),
                false,
                0,
                getResourceString(R.string.defaultAudiobookProjName),
                "",
                getResourceString(R.string.defaultAudiobookProjName) + ".xml",
                getResourceString(R.string.defaultAudiobookTitle) + ".mp3",
                currentTime,
                currentTime);
        AppInfo newAppInfo = new AppInfo(0, BuildConfig.VERSION_NAME, getAndroidVersion(), getDeviceName());
        AudiobookData newData = new AudiobookData(0, getResourceString(R.string.defaultAudiobookTitle), "", Locale.getDefault().toLanguageTag(), "");

        repository.insertProjectWithMetadata(newProject, newAppInfo, newData);
    }

    private String getResourceString(int resourceID) {
        return getApplication().getResources().getString(resourceID);
    }

    public int deleteAllProjectsWithMetadata()
    {
        return repository.deleteAllProjectsWithMetadata();
    }

    //TODO: do the same for the other methods of the repository

    public LiveData<List<ProjectWithMetadata>> getAllProjectsWithMetadata()
    {
        return repository.getAllProjectsWithMetadata();
    }

    public LiveData<ProjectWithMetadata> getProjectWithMetadataById(int id)
    {
        return repository.getProjectWithMetadataById(id);
    }
}

