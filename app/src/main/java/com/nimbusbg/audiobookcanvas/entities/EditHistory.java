package com.nimbusbg.audiobookcanvas.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Edit_History",
        foreignKeys = @ForeignKey(entity = CharacterLine.class,
                parentColumns = "id",
                childColumns = "line_id",
                onDelete = ForeignKey.CASCADE))
public class EditHistory {
    @NonNull
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "line_id")
    public int lineId;

    @NonNull
    @ColumnInfo(name = "start_index")
    public int startIndex;

    @NonNull
    @ColumnInfo(name = "character_name")
    public String characterName;

    public EditHistory(int id, int lineId, int startIndex, String characterName) {
        this.id = id;
        this.lineId = lineId;
        this.startIndex = startIndex;
        this.characterName = characterName;
    }

    public int getId() {
        return id;
    }
}
