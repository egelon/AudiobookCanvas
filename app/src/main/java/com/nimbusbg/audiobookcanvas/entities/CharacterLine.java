package com.nimbusbg.audiobookcanvas.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "character_line",
        foreignKeys = {
                @ForeignKey(entity = TextBlock.class,
                        parentColumns = "id",
                        childColumns = "text_block_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Character.class,
                        parentColumns = "name",
                        childColumns = "character_name",
                        onDelete = ForeignKey.CASCADE)
        })
public class CharacterLine {
    @NonNull
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "text_block_id")
    private int textBlockId;

    @NonNull
    @ColumnInfo(name = "start_index")
    private int startIndex;

    @NonNull
    @ColumnInfo(name = "character_name")
    private String characterName;

    public CharacterLine(int id, int textBlockId, int startIndex, String characterName) {
        this.id = id;
        this.textBlockId = textBlockId;
        this.startIndex = startIndex;
        this.characterName = characterName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTextBlockId() {
        return textBlockId;
    }

    public void setTextBlockId(int textBlockId) {
        this.textBlockId = textBlockId;
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
