package com.nimbusbg.audiobookcanvas.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nimbusbg.audiobookcanvas.MyAudiobookCanvasApplication;
import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;
import com.nimbusbg.audiobookcanvas.data.repository.AudiobookRepository;

import java.util.List;

public class ProjectWithMetadataViewModel extends AndroidViewModel {

    private AudiobookRepository repository;
    private LiveData<List<ProjectWithMetadata>> allProjects;

    public ProjectWithMetadataViewModel(@NonNull Application application) {
        super(application);

        repository = new AudiobookRepository(application, ((MyAudiobookCanvasApplication)application).getExecutorService());
        allProjects = repository.getAllProjectsWithMetadata();
    }


    public void insertProjectWithMetadata(AudiobookProject project, AppInfo appInfo, AudiobookData audiobookData)
    {
        repository.insertProjectWithMetadata(project, appInfo, audiobookData);
    }

    public int deleteAllProjectsWithMetadata()
    {
        return repository.deleteAllProjectsWithMetadata();
    }

    //TODO: do the same for the other methods of the repository

    public LiveData<List<ProjectWithMetadata>> getAllProjectsWithMetadata()
    {
        return allProjects;
    }


}

