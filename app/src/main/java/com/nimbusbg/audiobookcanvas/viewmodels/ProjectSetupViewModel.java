package com.nimbusbg.audiobookcanvas.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.nimbusbg.audiobookcanvas.MyAudiobookCanvasApplication;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithTextBlocks;
import com.nimbusbg.audiobookcanvas.data.repository.AudiobookRepository;

import java.util.List;

public class ProjectSetupViewModel extends AndroidViewModel {
    private AudiobookRepository repository;
    private LiveData<ProjectWithTextBlocks> projectWithTextBlocks;

    public ProjectSetupViewModel(@NonNull Application application) {
        super(application);

        repository = new AudiobookRepository(application, ((MyAudiobookCanvasApplication)application).getExecutorService());
    }

    public void readTxtFile(Uri fileUri) {
        repository.readTxtFile(fileUri);
    }

    public void fetchProjectWithTextBlocksById(int id) {
        projectWithTextBlocks = repository.getProjectWithTextBlocksById(id);
    }

    public LiveData<ProjectWithTextBlocks> getProjectWithTextBlocks() {
        return projectWithTextBlocks;
    }
}
