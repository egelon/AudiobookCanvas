package com.nimbusbg.audiobookcanvas.data.local.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity(tableName = "characterLine",
        foreignKeys = {
                @ForeignKey(entity = TextBlock.class,
                        parentColumns = "id",
                        childColumns = "text_block_id",
                        onDelete = ForeignKey.CASCADE)
        })
public class CharacterLine implements Serializable {
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
    
    @NonNull
    @ColumnInfo(name = "line")
    private String line;

    public CharacterLine(int textBlockId, int startIndex, String characterName, String line) {
        this.textBlockId = textBlockId;
        this.startIndex = startIndex;
        this.characterName = characterName;
        this.line = line;
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
    
    public String getLine() {return line;}
    
    public void setLine( String line) {this.line = line;}
}
