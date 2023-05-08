package com.nimbusbg.audiobookcanvas;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "background_atmosphere",
        foreignKeys = @ForeignKey(entity = TextBlock.class,
                parentColumns = "id",
                childColumns = "text_block_id",
                onDelete = ForeignKey.CASCADE))
public class BackgroundAtmosphere {
    @PrimaryKey
    public int text_block_id;

    @NonNull
    public String primary;

    @NonNull
    public String secondary;
}

