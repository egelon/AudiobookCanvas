package com.nimbusbg.audiobookcanvas.data.local.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;
import com.nimbusbg.audiobookcanvas.data.local.entities.StoryCharacter;

import java.io.Serializable;
import java.util.List;

public class MetadataWithCharacters implements Serializable
{
    @Embedded
    public AudiobookData audiobookData;
    
    @Relation(
            parentColumn = "project_id",
            entityColumn = "project_id"
    )
    public List<StoryCharacter> storyCharacters;
}
