package com.nimbusbg.audiobookcanvas;

import androidx.room;

@Database(entities = {AppInfo.class, AudiobookData.class, Character.class, CharacterLine.class, EditHistory.class, MusicTrack.class, Project.class, TextBlock.class},
          version = 1)
public abstract class AudiobookProjectDatabase extends RoomDatabase{
    private static NoteDatabase instance;

    public abstract ProjectDao projectDao();
    //TODO: add the other DAOs here!

    public static synchronized AudiobookProjectDatabase getInstance(Context context)
    {
        if(instance == null)
        {
            instance = Room.databaseBuilder(context.getApplicationContext(), AudiobookProjectDatabase.Class, "Audiobook_Projects").fallbackToDestructiveMigration().Build();
        }
        return instance;
    }
}