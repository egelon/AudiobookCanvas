package com.nimbusbg.audiobookcanvas.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity(tableName = "editHistory",
        foreignKeys = @ForeignKey(entity = CharacterLine.class,
                parentColumns = "id",
                childColumns = "line_id",
                onDelete = ForeignKey.CASCADE))
public class EditHistory  implements Serializable {
    @NonNull
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "line_id")
    public int lineId;

    @NonNull
    @ColumnInfo(name = "start_index")
    private int startIndex;

    @NonNull
    @ColumnInfo(name = "character_name")
    private String characterName;

    public EditHistory(int lineId, int startIndex, String characterName) {
        this.lineId = lineId;
        this.startIndex = startIndex;
        this.characterName = characterName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }
}
