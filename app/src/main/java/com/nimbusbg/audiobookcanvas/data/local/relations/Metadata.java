package com.nimbusbg.audiobookcanvas.data.local.relations;

import androidx.room.Embedded;

import com.nimbusbg.audiobookcanvas.data.local.entities.AppInfo;
import com.nimbusbg.audiobookcanvas.data.local.entities.AudiobookData;

import java.io.Serializable;

public class Metadata  implements Serializable {
    @Embedded
    AppInfo appInfo;

    @Embedded
    AudiobookData audiobookData;
}
