package com.nimbusbg.audiobookcanvas;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "edit_history",
        foreignKeys = @ForeignKey(entity = TextBlock.class,
                parentColumns = "id",
                childColumns = "text_block_id",
                onDelete = ForeignKey.CASCADE))
public class EditHistory {
    @PrimaryKey(autoGenerate = true)
    public int history_id;

    public int text_block_id;

    public int line_id;

    public int start_index;

    @NonNull
    public String character;
}

