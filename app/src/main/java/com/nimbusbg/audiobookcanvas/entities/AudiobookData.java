package com.nimbusbg.audiobookcanvas.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Audiobook_Data")
public class AudiobookData {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String bookTitle;

    public String Author;

    @NonNull
    public String language;

    public String description;

    public AudiobookData(final int id, @NonNull String language) {
        this.id = id;
        this.language = language;
    }
}
