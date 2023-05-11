package com.nimbusbg.audiobookcanvas.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Audiobook_Data",
        foreignKeys = @ForeignKey(entity = Project.class,
                parentColumns = "id",
                childColumns = "project_id",
                onDelete = ForeignKey.CASCADE))
public class AudiobookData {

    @NonNull
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "project_id")
    public int project_id;

    @ColumnInfo(name = "book_title")
    public String bookTitle;

    @ColumnInfo(name = "author")
    public String author;

    @NonNull
    @ColumnInfo(name = "book_language")
    public String language;

    @ColumnInfo(name = "description")
    public String description;

    public AudiobookData(final int id, final int project_id, @NonNull String language) {
        this.id = id;
        this.project_id = project_id;
        this.language = language;
    }

    public int getId() {
        return id;
    }
}
