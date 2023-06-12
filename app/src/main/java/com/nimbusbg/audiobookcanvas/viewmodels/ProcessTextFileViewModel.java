package com.nimbusbg.audiobookcanvas.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithTextBlocks;
import com.nimbusbg.audiobookcanvas.data.local.relations.TextBlockWithData;
import com.nimbusbg.audiobookcanvas.data.repository.ApiResponseListener;
import com.nimbusbg.audiobookcanvas.data.repository.AudiobookRepository;
import com.nimbusbg.audiobookcanvas.data.repository.FIleOperationListener;
import com.nimbusbg.audiobookcanvas.data.repository.GptApiRepository;
import com.nimbusbg.audiobookcanvas.data.repository.InsertedItemListener;
import com.nimbusbg.audiobookcanvas.data.repository.TextFileRepository;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ProcessTextFileViewModel extends AndroidViewModel
{
    private final AudiobookRepository databaseRepository;
    private final TextFileRepository fileRepository;
    private final GptApiRepository gptApiRepository;
    private ArrayList<String> textChunks;
    private ArrayList<TextBlockWithData> textBlocks;
    
    public void setDialogueStartChar(char start)
    {
        fileRepository.setDialogueStartChar(start);
    }
    
    public void insertTextBlocks(int projID, InsertedItemListener listener)
    {
        databaseRepository.insertTextBlocks(projID, textChunks, listener);
    }
    
    public void setDialogueEndChar(char end)
    {
        fileRepository.setDialogueEndChar(end);
    }
    
    public ArrayList<String> getTextChunks()
    {
        return textChunks;
    }
    
    public int getNumberTextChunks()
    {
        return textChunks.size();
    }
    
    public void setTextChunks(ArrayList<String> textChunks)
    {
        this.textChunks = textChunks;
    }
    
    public ProcessTextFileViewModel(@NonNull Application application)
    {
        super(application);
    
        databaseRepository = new AudiobookRepository(application);
        fileRepository = new TextFileRepository(application);
        gptApiRepository = new GptApiRepository(application);
    }
    
    public void chunkInputFile(Uri uri, FIleOperationListener listener) {
        fileRepository.GetSanitisedChunks(uri, listener);
    }
    
    public LiveData<ProjectWithTextBlocks> getProjectWithTextBlocksById(int id)
    {
        return databaseRepository.getProjectWithTextBlocksById(id);
    }
    
    public void performNamedEntityRecognition(String textBlock, String tag, ApiResponseListener rspListener)
    {
        try
        {
            gptApiRepository.getCompletion(textBlock, tag, rspListener);
        }
        catch (JSONException e)
        {
            rspListener.OnException(e);
        }
    }
    
    public void setProcessedFlag(int textBlock_Id, Boolean flag_value)
    {
        databaseRepository.setAPIProcessedFlagForTextBlockId(textBlock_Id, flag_value);
    }
    
    public LiveData<List<TextBlockWithData>> getTextBlocksWithDataByProjectId(int proj_id)
    {
        return databaseRepository.getTextBlocksWithDataByProjectId(proj_id);
    }
}
