package com.nimbusbg.audiobookcanvas.data.repository;

import android.app.Application;

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



    private LiveData<List<AudiobookProject>> allProjects;
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

        allProjects = projectDao.getAllProjects();
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






        /*
        executor.execute(new Runnable() {
            @Override
            public void run() {
                projectWithMetadataDao.insertProject(project);
            }});

        executor.execute(new Runnable() {
            @Override
            public void run() {
                projectWithMetadataDao.insertAppInfo(appInfo);
            }});

        executor.execute(new Runnable() {
            @Override
            public void run() {
                projectWithMetadataDao.insertAudiobookData(audiobookData);
            }});
        */
    }



    public long insert(AudiobookProject audiobookProject)
    {
        Callable<Long> insertCallable = () -> projectWithMetadataDao.insertProject(audiobookProject);
        long rowId = 0;

        Future<Long> future = executorService.submit(insertCallable);
        try {
            rowId = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return rowId;
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

    public void deleteAllProjects()
    {
        /*
        executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        affectedEntities = projectDao.deleteAllProjects();
                    }});

         */
    }

    public LiveData<List<AudiobookProject>> getAllProjectsLiveData()
    {
        return allProjects;
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
