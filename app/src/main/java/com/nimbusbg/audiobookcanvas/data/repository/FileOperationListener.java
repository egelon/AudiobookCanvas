package com.nimbusbg.audiobookcanvas.data.repository;

import java.util.ArrayList;

public interface FileOperationListener
{
    public void OnFileLoaded(String data);
    public void OnFileChunked(ArrayList<String> chunks);
}
