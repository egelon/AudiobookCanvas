package com.nimbusbg.audiobookcanvas.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.nimbusbg.audiobookcanvas.data.local.AudiobookProjectDatabase;
import com.nimbusbg.audiobookcanvas.data.local.dao.ProjectDao;
import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudiobookRepository
{
    private final Executor executor;
    private ProjectDao projectDao;
    private AppInfo appInfoDao;
    private LiveData<List<AudiobookProject>> allProjects;
    private long lastInsertedProjectId;
    private long[] lastInsertedProjectIds;
    private int affectedEntities;

    public AudiobookRepository(Application application, Executor executor)
    {
        AudiobookProjectDatabase database = AudiobookProjectDatabase.getInstance(application);
        this.executor = executor;
        projectDao = database.projectDao();
        allProjects = projectDao.getAllProjects();
    }

    public void insert(AudiobookProject audiobookProject)
    {
        executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        lastInsertedProjectId = projectDao.insert(audiobookProject);
                    }});
    }

    public void insert(List<AudiobookProject> audiobookProjectList)
    {
        executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        lastInsertedProjectIds = projectDao.insert(audiobookProjectList);
                    }});
    }

    public void update(AudiobookProject audiobookProject)
    {
        executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        projectDao.update(audiobookProject);
                    }});
    }

    public void delete(AudiobookProject audiobookProject)
    {
        executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        projectDao.delete(audiobookProject);
                    }});
    }

    public void deleteAllProjects()
    {
        executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        affectedEntities = projectDao.deleteAllProjects();
                    }});
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
