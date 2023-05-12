package com.nimbusbg.audiobookcanvas.data.local.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;

public class ProjectWithMetadata {
    @Embedded
    AudiobookProject audiobookProject;

    @Relation(parentColumn = "id", entityColumn = "project_id")
    AppInfo appInfo;

    @Relation(parentColumn = "id", entityColumn = "project_id")
    AudiobookData audiobookData;
}
