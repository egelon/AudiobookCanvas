package com.nimbusbg.audiobookcanvas.data.repository;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.nimbusbg.audiobookcanvas.data.local.AudiobookProjectDatabase;
import com.nimbusbg.audiobookcanvas.data.local.dao.AppInfoDao;
import com.nimbusbg.audiobookcanvas.data.local.dao.AudiobookDataDao;
import com.nimbusbg.audiobookcanvas.data.local.dao.ProjectDao;
import com.nimbusbg.audiobookcanvas.data.local.dao.ProjectWithMetadataDao;
import com.nimbusbg.audiobookcanvas.data.local.dao.ProjectWithTextBlocksDao;
import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithTextBlocks;

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
    private ProjectDao projectDao;
    private AppInfoDao appInfoDao;
    private AudiobookDataDao audiobookDataDao;

    private ProjectWithMetadataDao projectWithMetadataDao;
    private ProjectWithTextBlocksDao projectWithTextBlocksDao;



    private LiveData<List<ProjectWithMetadata>> allProjects;
    private long lastInsertedProjectId;
    private long[] lastInsertedProjectIds;
    private int affectedEntities;

    private ProjectWithMetadata fullProjectInfo;

    public AudiobookRepository(Application application, ExecutorService executorService)
    {
        AudiobookProjectDatabase database = AudiobookProjectDatabase.getInstance(application);
        this.executorService = executorService;
        projectDao = database.projectDao();
        appInfoDao = database.appInfoDao();
        audiobookDataDao = database.audiobookDataDao();
        projectWithMetadataDao = database.projectWithMetadataDao();
        projectWithTextBlocksDao = database.projectWithTextBlocksDao();

        allProjects = projectWithMetadataDao.getAllProjectWithMetadata();
    }

    //public void insert(AudiobookProject audiobookProject)
   // {
        /*
        executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        projectWithMetadataDao.insertProject(audiobookProject);
                    }});

         */
   // }

    public void insert(AppInfo appInfo)
    {

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                projectWithMetadataDao.insertAppInfo(appInfo);
            }});


    }

    public void insert(AudiobookData audiobookData)
    {

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                projectWithMetadataDao.insertAudiobookData(audiobookData);
            }});


    }

    public void insertProjectWithMetadata(AudiobookProject project, AppInfo appInfo, AudiobookData audiobookData)
    {
        int id = insert(project);
        appInfo.setProject_id(id);
        audiobookData.setProject_id(id);
        insert(appInfo);
        insert(audiobookData);
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

    public int insert(AudiobookProject audiobookProject)
    {
        Callable<Long> insertCallable = () -> projectWithMetadataDao.insertProject(audiobookProject);

        /*

        Future<Long> insertProjectFuture = executorService.submit(insertCallable);
        try {
            rowId = insertProjectFuture.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

         */

        long finalRowId = getFuture(insertCallable);
        Callable<Integer> fetchIdCallable = () -> projectWithMetadataDao.getProjectIdByRowId(finalRowId);
        int project_id = getFuture(fetchIdCallable);
/*
        Future<Integer> queryIdFuture = executorService.submit(fetchIdCallable);
        try {
            project_id = queryIdFuture.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
 */
        return project_id;
    }

    public void insert(List<AudiobookProject> audiobookProjectList)
    {
        /*
        executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        lastInsertedProjectIds = projectDao.insert(audiobookProjectList);
                    }});

         */
    }

    public void update(AudiobookProject audiobookProject)
    {
        /*
        executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        projectDao.update(audiobookProject);
                    }});

         */
    }

    public void delete(AudiobookProject audiobookProject)
    {
        /*
        executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        projectDao.delete(audiobookProject);
                    }});

         */
    }

    public int deleteAllProjectsWithMetadata()
    {
        Callable<Integer> deleteAllCallable = () -> projectWithMetadataDao.deleteAllProjectsWithMetadata();
        int affectedRows = getFuture(deleteAllCallable);
        return affectedRows;
    }

    public LiveData<List<ProjectWithMetadata>> getAllProjectsWithMetadata()
    {
        return allProjects;
    }

    public void updateProjectWithMetadata(AudiobookProject project, AppInfo appInfo, AudiobookData audiobookData)
    {
        //TODO: finish me
    }

    public  LiveData<ProjectWithTextBlocks> getProjectWithTextBlocksById(int id)
    {
        return projectWithTextBlocksDao.getProjectWithTextBlocksById(id);
    }

    public void readTxtFile(Uri fileUri)
    {
        /*
        Callable<Long> insertCallable = () -> projectWithMetadataDao.insertProject(audiobookProject);

        long finalRowId = getFuture(insertCallable);
         */

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

    /*
    private static class InsertProjectAsyncTask extends AsyncTask<AudiobookProject, Void, Void>
    {
        private ProjectDao projectDao;

        private InsertProjectAsyncTask(ProjectDao projectDao)
        {
            this.projectDao = projectDao;
        }

        @Override
        protected Void doInBackground(AudiobookProject... projects)
        {
            projectDao.insert(projects[0]);
            return null;
        }
    }
    */
}
