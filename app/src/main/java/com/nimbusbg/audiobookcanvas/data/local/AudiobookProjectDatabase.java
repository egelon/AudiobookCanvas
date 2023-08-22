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
import com.nimbusbg.audiobookcanvas.data.local.dao.TextBlockWithDataDao;
import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;
import com.nimbusbg.audiobookcanvas.data.local.entities.CharacterLine;
import com.nimbusbg.audiobookcanvas.data.local.entities.EditHistory;
import com.nimbusbg.audiobookcanvas.data.local.entities.MusicTrack;
import com.nimbusbg.audiobookcanvas.data.local.entities.StoryCharacter;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;
import com.nimbusbg.audiobookcanvas.data.local.relations.TextBlockWithData;
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
    public abstract TextBlockWithDataDao textBlockWithDataDao();
    //TODO: add the other DAOs here!
    
    public static synchronized AudiobookProjectDatabase getInstance(Context context)
    {
        if (instance == null)
        {
            instance = Room.databaseBuilder(context.getApplicationContext(), AudiobookProjectDatabase.class, "Audiobook_Projects")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
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
        }
    };
}