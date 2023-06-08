package com.nimbusbg.audiobookcanvas.data.repository;

import java.util.ArrayList;

public interface FIleOperationListener
{
    public void OnFileLoaded(String data);
    public void OnFileChunked(ArrayList<String> chunks);
}
