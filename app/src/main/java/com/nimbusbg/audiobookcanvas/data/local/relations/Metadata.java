package com.nimbusbg.audiobookcanvas.data.local.relations;

import androidx.room.Embedded;

import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;

public class Metadata {
    @Embedded
    AppInfo appInfo;

    @Embedded
    AudiobookData audiobookData;
}
