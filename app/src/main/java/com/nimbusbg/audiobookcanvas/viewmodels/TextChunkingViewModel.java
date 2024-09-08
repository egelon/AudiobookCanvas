package com.nimbusbg.audiobookcanvas.viewmodels;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.nimbusbg.audiobookcanvas.data.listeners.ApiResponseListener;
import com.nimbusbg.audiobookcanvas.data.listeners.DeletedItemListener;
import com.nimbusbg.audiobookcanvas.data.listeners.FileOperationListener;
import com.nimbusbg.audiobookcanvas.data.listeners.FileSampleListener;
import com.nimbusbg.audiobookcanvas.data.listeners.InsertedItemListener;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithTextBlocks;
import com.nimbusbg.audiobookcanvas.data.network.GptChatResponse;
import com.nimbusbg.audiobookcanvas.data.network.GptLanguageIdentification;
import com.nimbusbg.audiobookcanvas.data.repository.AudiobookRepository;
import com.nimbusbg.audiobookcanvas.data.repository.GptApiRepository;
import com.nimbusbg.audiobookcanvas.data.repository.TextFileRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class TextChunkingViewModel extends AndroidViewModel
{
    private final AudiobookRepository databaseRepository;
    private final TextFileRepository fileRepository;
    private LiveData<ProjectWithTextBlocks> currentProjectWithTextBlocks;
    private int projID;
    private Uri fileUri;
    private boolean areTextBlocksInserted;
    private final GptApiRepository gptApiRepository;
    
    public boolean areTextBlocksInserted()
    {
        return areTextBlocksInserted;
    }
    
    public TextChunkingViewModel(@NonNull Application application, int proj_id, String textFileUri)
    {
        super(application);
        
        databaseRepository = new AudiobookRepository(application);
        fileRepository = new TextFileRepository(application);
    
        projID = proj_id;
        fileUri = Uri.parse(textFileUri);
        areTextBlocksInserted = false;
        gptApiRepository = new GptApiRepository(application);
    }
    
    public void setDialogueStartEndChar(char start, char end)
    {
        fileRepository.setDialogueStartChar(start);
        fileRepository.setDialogueEndChar(end);
    }
    
    public void getTextFileLanguage()
    {
        fileRepository.getTextStartSample(fileUri, new FileSampleListener()
        {
            @Override
            public void OnSampleLoaded(String sample)
            {
                gptApiRepository.getTextLanguage(sample, "langSample", new ApiResponseListener()
                {
                    @Override
                    public void OnResponse(@NonNull Call call, @NonNull Response response)
                    {
                        Log.d("TextChunkingViewModel", "OnResponse received for request: " + call.request().body().toString());
                        OnLanguageResponse(response);
                    }
    
                    @Override
                    public void OnError(@NonNull Call call, @NonNull IOException e)
                    {
                        OnLanguageError(e.getMessage());
                    }
                });
            }
        });
    }
    
    private void OnLanguageResponse(Response response)
    {
        Gson gson = new Gson();
        try {
            String jsonBody = response.body().string();
            Log.d("TextChunkingViewModel", "OnCompletionResponse received response: " + jsonBody);
            
            try
            {
                GptChatResponse gptResponse = gson.fromJson(jsonBody, GptChatResponse.class);
                
                if(gptResponse.hasError())
                {
                    Log.d("gptResponse", "error: " + gptResponse.getError().getMessage());
                    Log.d("gptResponse", "error type: " + gptResponse.getError().getType());
                }
                else
                {
                   //and here we parse the response and get the language
                    String responseContent = gptResponse.getChoices().get(0).getMessage().getContent();
                    try {
                        GptLanguageIdentification languageIdentification = gson.fromJson(responseContent, GptLanguageIdentification.class);
                        setDialogueStartEndChar(languageIdentification.getDialogue_start(), languageIdentification.getDialogue_end());
    
                        fileRepository.GetSanitisedChunks(fileUri, new FileOperationListener()
                        {
                            @Override
                            public void OnFileLoaded(String data)
                            {
                                Log.d("TextChunkingFragment", "OnFileLoaded");
                            }
        
                            @Override
                            public void OnFileChunked(ArrayList<String> chunks)
                            {
                                databaseRepository.insertTextBlocks(projID, chunks, new InsertedItemListener()
                                {
                                    @Override
                                    public void onInsert(int itemId)
                                    {
                                        areTextBlocksInserted = true;
                                    }
                                });
                            }
        
                            @Override
                            public void OnChunkingStopped()
                            {
                                Log.d("TextChunkingFragment", "textChunkingViewModel.chunkInputFile OnChunkingStopped");
                            }
                        });
                        
                        
                    } catch (com.google.gson.JsonSyntaxException ex) {
                        Log.d("ProcessTextFileViewModel", "storeCharactersForTextBlock couldn't parse completion json: " + ex);
                    }
                }
            }
            catch (com.google.gson.JsonSyntaxException ex)
            {
                Log.d("TextChunkingViewModel", "OnCompletionResponse Problem parsing response: " + ex);
            }
        }
        catch (IOException e)
        {
            // Handle the exception.
            Log.d("TextChunkingViewModel", "OnCompletionResponse exception: " + e.getMessage());
        }
    }
    
    private void OnLanguageError(String error)
    {
        Log.d("TextChunkingViewModel", "OnCompletionError exception: " + error);
        if (error.equals("Canceled"))
        {
            Log.d("TextChunkingViewModel", "request was canceled");
        }
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