package com.nimbusbg.audiobookcanvas.data.local.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookProject;

import java.io.Serializable;

public class ProjectWithMetadata implements Serializable {
    @Embedded
    public AudiobookProject project;

    @Relation(parentColumn = "id", entityColumn = "project_id")
    public AppInfo appInfo;

    @Relation(parentColumn = "id", entityColumn = "project_id")
    public AudiobookData audiobookData;
}
