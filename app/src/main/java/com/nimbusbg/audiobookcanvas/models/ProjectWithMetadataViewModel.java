package com.nimbusbg.audiobookcanvas.models;

public class ProjectWithMetadataViewModel extends AndroidViewModel{

    private AudiobookRepository repository;
    private LiveData<List<ProjectWithMetadata>> allProjects;

    public ProjectWithMetadataViewModel(@NonNull Application application){
        super(application);
        repository = new AudiobookRepository(application, application.getExecutorService());
        allProjects = repository.getAllProjectsWithMetadata();

    }

    public void insertProjectWithMetadata(AudiobookProject project, AppInfo appInfo, AudiobookData audiobookData)
    {
        long rowID = repository.insert(project);
        appInfo.setProject_id(rowID);
        repository.insert(appInfo);
        audiobookData.setProject_id(rowID);
        repository.insert(audiobookData);
    }

    //TODO: do the same for the other methods of the repository

    public LiveData<List<ProjectWithMetadata>> getAllProjectsWithMetadata() {
        return allProjects;
    }
}

