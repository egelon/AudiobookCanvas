package com.nimbusbg.audiobookcanvas.data.repository;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.nimbusbg.audiobookcanvas.data.local.AudiobookProjectDatabase;
import com.nimbusbg.audiobookcanvas.data.local.dao.ProjectWithMetadataDao;
import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AudiobookRepository
{
    private final ExecutorService executorService;
    private ProjectWithMetadataDao projectWithMetadataDao;
    private LiveData<List<ProjectWithMetadata>> allProjects;

    public AudiobookRepository(Application application, ExecutorService executorService)
    {
        AudiobookProjectDatabase database = AudiobookProjectDatabase.getInstance(application);
        this.executorService = executorService;
        projectWithMetadataDao = database.projectWithMetadataDao();
    }

    private <T> T getFuture(Callable<T> callable)
    {
        T result = null;
        Future<T> future = executorService.submit(callable);
        try {
            result = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    //TODO: THIS DOESN'T NEED TO BE SYNCRONOUS! Remove this call from the FAB button! Add this as async to the Save Button! Or better yet, remove the Save button and make it save when the user moves forward and we chunk the file!
    public int insertProjectWithMetadata(AudiobookProject project, AppInfo appInfo, AudiobookData audiobookData)
    {
        Callable<Long> insertCallable = () -> projectWithMetadataDao.insertProject(audiobookProject);
        long finalRowId = getFuture(insertCallable);
        Callable<Integer> fetchIdCallable = () -> projectWithMetadataDao.getProjectIdByRowId(finalRowId);
        int project_id = getFuture(fetchIdCallable);

        appInfo.setProject_id(project_id);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                projectWithMetadataDao.insertAppInfo(appInfo);
            }});

        audiobookData.setProject_id(project_id);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                projectWithMetadataDao.insertAudiobookData(audiobookData);
            }});

        return project_id;
    }

    public void deleteProjectWithMetadataById(int id)
    {
        projectWithMetadataDao.deleteProjectWithMetadataById(id);
    }

    public void deleteAllProjectsWithMetadata()
    {
        projectWithMetadataDao.deleteAllProjectsWithMetadata();
    }

    public  LiveData<ProjectWithMetadata> getProjectWithMetadataById(int id)
    {
        return projectWithMetadataDao.getProjectWithMetadataById(id);
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
        /*
        executorService.execute(new Runnable() {
            @Override
            public void run()
            {
                try {

                    InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    // Store or process the read data...
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
         */
    }
}
