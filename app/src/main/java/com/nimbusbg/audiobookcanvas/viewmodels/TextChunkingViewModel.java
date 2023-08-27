package com.nimbusbg.audiobookcanvas.viewmodels;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nimbusbg.audiobookcanvas.data.listeners.DeletedItemListener;
import com.nimbusbg.audiobookcanvas.data.listeners.FileOperationListener;
import com.nimbusbg.audiobookcanvas.data.listeners.InsertedItemListener;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithTextBlocks;
import com.nimbusbg.audiobookcanvas.data.repository.AudiobookRepository;
import com.nimbusbg.audiobookcanvas.data.repository.GptApiRepository;
import com.nimbusbg.audiobookcanvas.data.repository.TextFileRepository;
import com.nimbusbg.audiobookcanvas.data.repository.TtsRepository;

import java.util.ArrayList;
import java.util.List;

public class TextChunkingViewModel extends AndroidViewModel
{
    private final AudiobookRepository databaseRepository;
    private final TextFileRepository fileRepository;
    private LiveData<ProjectWithTextBlocks> currentProjectWithTextBlocks;
    private int projID;
    private Uri fileUri;
    private boolean areTextBlocksInserted;
    
    public boolean areTextBlocksInserted()
    {
        return areTextBlocksInserted;
    }
    
    public void setAreTextBlocksInserted(boolean areTextBlocksInserted)
    {
        this.areTextBlocksInserted = areTextBlocksInserted;
    }
    
    public TextChunkingViewModel(@NonNull Application application, int proj_id, String textFileUri)
    {
        super(application);
        
        databaseRepository = new AudiobookRepository(application);
        fileRepository = new TextFileRepository(application);
    
        projID = proj_id;
        fileUri = Uri.parse(textFileUri);
        areTextBlocksInserted = false;
    }
    
    public void setDialogueStartEndChar(char start, char end)
    {
        fileRepository.setDialogueStartChar(start);
        fileRepository.setDialogueEndChar(end);
    }
    
    public void chunkInputFile(FileOperationListener listener)
    {
        fileRepository.GetSanitisedChunks(fileUri, listener);
    }
    
    public void insertTextBlocks(ArrayList<String> chunks, InsertedItemListener listener)
    {
        databaseRepository.insertTextBlocks(projID, chunks, listener);
    }
    
    public LiveData<List<TextBlock>> getTextBlocksByProjectId(int prj_id)
    {
        return databaseRepository.getTextBlocksByProjectId(prj_id);
    }
    
    public void cleanupProject(DeletedItemListener listener)
    {
        if(!areTextBlocksInserted)
        {
            fileRepository.StopChunking();
            databaseRepository.deleteProjectWithMetadataById(projID, listener);
        }
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        cleanupProject(() -> {
            Log.d("TextChunkingViewModel", "onCleared");
        });
    }
}