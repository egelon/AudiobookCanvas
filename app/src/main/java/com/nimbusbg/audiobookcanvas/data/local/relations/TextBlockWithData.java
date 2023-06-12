package com.nimbusbg.audiobookcanvas.data.local.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.nimbusbg.audiobookcanvas.data.local.entities.CharacterLine;
import com.nimbusbg.audiobookcanvas.data.local.entities.MusicTrack;
import com.nimbusbg.audiobookcanvas.data.local.entities.StoryCharacter;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;

import java.io.Serializable;
import java.util.List;

public class TextBlockWithData implements Serializable
{
    @Embedded public TextBlock textBlock;
    @Relation(
            parentColumn = "id",
            entityColumn = "text_block_id"
    )
    public List<CharacterLine> characterLines;
    
    @Relation(
            parentColumn = "background_track_id",
            entityColumn = "id"
    )
    public MusicTrack musicTrack;
}
