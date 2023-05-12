package com.nimbusbg.audiobookcanvas.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.nimbusbg.audiobookcanvas.data.local.AudiobookProjectDatabase;
import com.nimbusbg.audiobookcanvas.data.local.dao.ProjectDao;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AudiobookRepository
{
    private ProjectDao projectDao;
    private List<AudiobookProject> allProjects;

    public AudiobookRepository(Application application)
    {
        AudiobookProjectDatabase database = AudiobookProjectDatabase.getInstance(application);
        projectDao = database.projectDao();
        allProjects = projectDao.getAllEntities().getValue();
    }

    public void insert(AudiobookProject audiobookProject)
    {
        //new InsertProjectAsyncTask(projectDao).execute(audiobookProject);

        ExecutorService insertProjectService = Executors.newSingleThreadExecutor();
        insertProjectService.execute(
                new Runnable() {
                    @Override
                    public void run()
                    {
                        long result = projectDao.insert(audiobookProject);
                    }
                }
        );
    }

    public void update(AudiobookProject audiobookProject)
    {
        ExecutorService updateProjectService = Executors.newSingleThreadExecutor();
        updateProjectService.execute(
                new Runnable() {
                    @Override
                    public void run()
                    {
                        projectDao.update(audiobookProject);
                    }
                }
        );
    }

    public void delete(AudiobookProject audiobookProject)
    {
        ExecutorService deleteProjectService = Executors.newSingleThreadExecutor();
        deleteProjectService.execute(
                new Runnable() {
                    @Override
                    public void run()
                    {
                        projectDao.delete(audiobookProject);
                    }
                }
        );
    }

    public void deleteAllProjects()
    {
        ExecutorService deleteAllProjectService = Executors.newSingleThreadExecutor();
        deleteAllProjectService.execute(
                new Runnable() {
                    @Override
                    public void run()
                    {
                        projectDao.deleteAll();
                    }
                }
        );
    }

    public List<AudiobookProject> getAllProjects()
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
