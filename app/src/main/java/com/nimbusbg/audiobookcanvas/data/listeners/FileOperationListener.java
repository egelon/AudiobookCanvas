package com.nimbusbg.audiobookcanvas.data.listeners;
import java.util.ArrayList;

public interface FileOperationListener
{
    public void OnFileLoaded(String data);
    public void OnFileChunked(ArrayList<String> chunks);
    
    public void OnChunkingStopped();
}
