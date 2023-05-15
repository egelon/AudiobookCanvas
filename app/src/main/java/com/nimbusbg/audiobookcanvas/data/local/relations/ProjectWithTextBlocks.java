package com.nimbusbg.audiobookcanvas.data.local.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;

import java.util.List;

public class ProjectWithTextBlocks {
    @Embedded
    public AudiobookProject project;

    @Relation(parentColumn = "id",
              entityColumn = "project_id")
    public List<TextBlock> textBlocks;
}
