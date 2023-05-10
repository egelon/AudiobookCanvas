package com.nimbusbg.audiobookcanvas.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.nimbusbg.audiobookcanvas.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.entities.Project;

public class ProjectMetadata {
    @Embedded
    Project project;

    @Relation(
            parentColumn = "id",
            entityColumn = "id"
    )
    AppInfo appInfo;

}
