package com.nimbusbg.audiobookcanvas.data.local.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;

import java.io.Serializable;
import java.util.List;

public class ProjectWithTextBlocks implements Serializable
{
    @Embedded public AudiobookProject project;
    
    @Relation(
            parentColumn = "id",
            entityColumn = "project_id"
    )
    public List<TextBlock> textBlocks;
    @Relation(
            parentColumn = "id",
            entityColumn = "project_id"
    )
    public AudiobookData audiobookData;
}
