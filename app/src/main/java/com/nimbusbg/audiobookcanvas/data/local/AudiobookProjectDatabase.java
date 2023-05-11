package com.nimbusbg.audiobookcanvas.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;


import com.nimbusbg.audiobookcanvas.data.local.dao.ProjectDao;
import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;
import com.nimbusbg.audiobookcanvas.data.local.entities.CharacterLine;
import com.nimbusbg.audiobookcanvas.data.local.entities.EditHistory;
import com.nimbusbg.audiobookcanvas.data.local.entities.MusicTrack;
import com.nimbusbg.audiobookcanvas.data.local.entities.StoryCharacter;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;
import com.nimbusbg.audiobookcanvas.data.repository.Converters;

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
public abstract class AudiobookProjectDatabase extends RoomDatabase {
    private static AudiobookProjectDatabase instance;

    public abstract ProjectDao projectDao();
    //public abstract ProjectWithMetadataDao projectWithMetadataDao();
    //TODO: add the other DAOs here!

    public static synchronized AudiobookProjectDatabase getInstance(Context context)
    {
        if(instance == null)
        {
            instance = Room.databaseBuilder(context.getApplicationContext(), AudiobookProjectDatabase.class, "Audiobook_Projects").fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}