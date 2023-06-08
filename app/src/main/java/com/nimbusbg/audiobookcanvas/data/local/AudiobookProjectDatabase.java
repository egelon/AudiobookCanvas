package com.nimbusbg.audiobookcanvas.data.local;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.nimbusbg.audiobookcanvas.data.local.dao.ProjectWithMetadataDao;
import com.nimbusbg.audiobookcanvas.data.local.dao.ProjectWithTextBlocksDao;
import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;
import com.nimbusbg.audiobookcanvas.data.local.entities.CharacterLine;
import com.nimbusbg.audiobookcanvas.data.local.entities.EditHistory;
import com.nimbusbg.audiobookcanvas.data.local.entities.MusicTrack;
import com.nimbusbg.audiobookcanvas.data.local.entities.StoryCharacter;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;
import com.nimbusbg.audiobookcanvas.data.repository.Converters;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        AppInfo.class,
        AudiobookData.class,
        AudiobookProject.class,
        StoryCharacter.class,
        CharacterLine.class,
        EditHistory.class,
        MusicTrack.class,
        TextBlock.class
}, version = 1, exportSchema = false)

@TypeConverters({Converters.class})

public abstract class AudiobookProjectDatabase extends RoomDatabase
{
    private static AudiobookProjectDatabase instance;
    
    private static final int NUMBER_OF_THREADS = 8;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    
    public abstract ProjectWithMetadataDao projectWithMetadataDao();
    public abstract ProjectWithTextBlocksDao projectWithTextBlocksDao();
    //TODO: add the other DAOs here!
    
    public static synchronized AudiobookProjectDatabase getInstance(Context context)
    {
        if (instance == null)
        {
            instance = Room.databaseBuilder(context.getApplicationContext(), AudiobookProjectDatabase.class, "Audiobook_Projects")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback) //populate our database with some test projects
                    .build();
        }
        return instance;
    }
    
    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback()
    {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db)
        {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };
    
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private final ProjectWithMetadataDao projectWithMetadataDao;
        
        private PopulateDbAsyncTask(AudiobookProjectDatabase db)
        {
            projectWithMetadataDao = db.projectWithMetadataDao();
        }
        
        @Override
        protected Void doInBackground(Void... voids)
        {
            AudiobookProject testProject = new AudiobookProject("1.0.0", false, 0,
                    "MyAudioBookProject_1",
                    "input.txt",
                    "output.xml",
                    "audiobook.mp3",
                    new Date(2012, 5, 12),
                    new Date(2023, 10, 29));
            AppInfo testAppInfo = new AppInfo(0, "1.0.2", "14.02_Pie", "Galaxy S22");
            AudiobookData testData = new AudiobookData(0, "AudioBookName_1", "secret1", "en-us", "no description");
            
            long rowId = projectWithMetadataDao.insertProject(testProject);
            int projId = projectWithMetadataDao.getProjectIdByRowId(rowId);
            testAppInfo.setProject_id(projId);
            testData.setProject_id(projId);
            projectWithMetadataDao.insertAppInfo(testAppInfo);
            projectWithMetadataDao.insertAudiobookData(testData);
            
            
            testProject = new AudiobookProject("1.0.0", false, 2,
                    "MyAudioBookProject_2",
                    "input_2.txt",
                    "output2.xml",
                    "audiobook2.mp3",
                    new Date(2012, 6, 13),
                    new Date(2023, 11, 30));
            testAppInfo = new AppInfo(0, "1.0.2", "14.02_Pie", "Galaxy S22");
            testData = new AudiobookData(0, "AudioBookName_2", "secret2", "en-us", "Maybe description");
            
            rowId = projectWithMetadataDao.insertProject(testProject);
            projId = projectWithMetadataDao.getProjectIdByRowId(rowId);
            testAppInfo.setProject_id(projId);
            testData.setProject_id(projId);
            projectWithMetadataDao.insertAppInfo(testAppInfo);
            projectWithMetadataDao.insertAudiobookData(testData);
            
            
            testProject = new AudiobookProject("1.0.0", true, 5,
                    "MyAudioBookProject_3",
                    "input_3.txt",
                    "output3.xml",
                    "audiobook3.mp3",
                    new Date(2012, 7, 14),
                    new Date(2023, 12, 31));
            testAppInfo = new AppInfo(0, "1.0.2", "14.02_Pie", "Galaxy S22");
            testData = new AudiobookData(0, "AudioBookName_3", "secret3", "en-us", "A full audiobook description");
            
            rowId = projectWithMetadataDao.insertProject(testProject);
            projId = projectWithMetadataDao.getProjectIdByRowId(rowId);
            testAppInfo.setProject_id(projId);
            testData.setProject_id(projId);
            projectWithMetadataDao.insertAppInfo(testAppInfo);
            projectWithMetadataDao.insertAudiobookData(testData);
            
            return null;
        }
    }
}