package com.nimbusbg.audiobookcanvas.viewmodels;

import android.app.Application;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.nimbusbg.audiobookcanvas.data.listeners.ApiResponseListener;
import com.nimbusbg.audiobookcanvas.data.listeners.FileOperationListener;
import com.nimbusbg.audiobookcanvas.data.listeners.InsertedItemListener;
import com.nimbusbg.audiobookcanvas.data.listeners.InsertedMultipleItemsListener;
import com.nimbusbg.audiobookcanvas.data.listeners.TtsInitListener;
import com.nimbusbg.audiobookcanvas.data.local.entities.BlockState;
import com.nimbusbg.audiobookcanvas.data.local.entities.CharacterLine;
import com.nimbusbg.audiobookcanvas.data.local.entities.StoryCharacter;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithTextBlocks;
import com.nimbusbg.audiobookcanvas.data.local.relations.TextBlockWithData;
import com.nimbusbg.audiobookcanvas.data.network.GptCharacter;
import com.nimbusbg.audiobookcanvas.data.network.GptCharacterLine;
import com.nimbusbg.audiobookcanvas.data.network.GptChatResponse;
import com.nimbusbg.audiobookcanvas.data.network.GptCompletion;
import com.nimbusbg.audiobookcanvas.data.repository.AudiobookRepository;
import com.nimbusbg.audiobookcanvas.data.repository.GptApiRepository;
import com.nimbusbg.audiobookcanvas.data.repository.TextFileRepository;
import com.nimbusbg.audiobookcanvas.data.repository.TtsRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


public class ProcessTextFileViewModel extends AndroidViewModel
{
    private final AudiobookRepository databaseRepository;
    private final TextFileRepository fileRepository;
    private final GptApiRepository gptApiRepository;
    private final TtsRepository ttsRepository;
    private ArrayList<String> textChunks;
    private boolean isProcessingStarted;
    private boolean isErrorRetryStarted;
    private ArrayList<String> voices;
    private ArrayList<TextBlockWithData> textBlocks;
    
    private LiveData<ProjectWithTextBlocks> currentProjectWithTextBlocks;
    private MutableLiveData<Boolean> ttsInitStatus = new MutableLiveData<>();
    
    public TextToSpeech tts;
    
    public boolean isProcessingStarted()
    {
        return isProcessingStarted;
    }
    
    public boolean isErrorRetryStarted()
    {
        return isErrorRetryStarted;
    }
    
    public void setDialogueStartChar(char start)
    {
        fileRepository.setDialogueStartChar(start);
        gptApiRepository.setDialogueStartChar(start);
    }
    
    public void insertTextBlocks(int projID, InsertedItemListener listener)
    {
        databaseRepository.insertTextBlocks(projID, textChunks, listener);
    }
    
    public void setDialogueEndChar(char end)
    {
        fileRepository.setDialogueEndChar(end);
        gptApiRepository.setDialogueEndChar(end);
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
    
    public ProcessTextFileViewModel(@NonNull Application application, int proj_id)
    {
        super(application);
    
        databaseRepository = new AudiobookRepository(application);
        fileRepository = new TextFileRepository(application);
        gptApiRepository = new GptApiRepository(application);
        ttsRepository = new TtsRepository(application);
        ttsInitStatus.postValue(false);
        currentProjectWithTextBlocks = databaseRepository.getProjectWithTextBlocksById(proj_id);
    }
    
    public LiveData<Boolean> getTtsInitStatus()
    {
        if(ttsInitStatus.getValue() == false)
        {
            waitForTTS();
        }
        return ttsInitStatus;
    }
    
    private void waitForTTS()
    {
        String languageISOCode = currentProjectWithTextBlocks.getValue().audiobookData.getLanguage();
        ttsRepository.initTTS(languageISOCode, new TtsInitListener()
        {
            @Override
            public void OnInitSuccess()
            {
                ttsInitStatus.postValue(true); // Update LiveData
            }
        
            @Override
            public void OnInitFailure()
            {
                ttsInitStatus.postValue(false); // Handle failure
            }
        });
    }
    
    public void chunkInputFile(Uri uri, FileOperationListener listener) {
        fileRepository.GetSanitisedChunks(uri, listener);
    }
    
    public LiveData<ProjectWithTextBlocks> getProjectWithTextBlocks()
    {
        return currentProjectWithTextBlocks;
    }
    
    public void fetchCharactersForUnprocessedTextBlocks()
    {
        isProcessingStarted = true;
        for(TextBlock textBlock : currentProjectWithTextBlocks.getValue().textBlocks)
        {
            if(textBlock.getState() == BlockState.NOT_REQUESTED)
            {
                performNamedEntityRecognition(textBlock, "TAG_" + String.valueOf(textBlock.getId()));
            }
        }
    }
    
    public void retryErrorTextBlocks()
    {
        isErrorRetryStarted = true;
        for(TextBlock textBlock : currentProjectWithTextBlocks.getValue().textBlocks)
        {
            if(textBlock.getState() == BlockState.ERROR)
            {
                performNamedEntityRecognition(textBlock, "TAG_" + String.valueOf(textBlock.getId()));
            }
        }
    }
    
    public void stopCharacterRequests()
    {
        isProcessingStarted = false;
        isErrorRetryStarted = false;
        for (TextBlock textBlock : currentProjectWithTextBlocks.getValue().textBlocks)
        {
            if (textBlock.getState() == BlockState.WAITING_RESPONSE)
            {
                setTextBlockStateById(textBlock.getId(), BlockState.NOT_REQUESTED);
            }
        }
        
        Log.d("ProcessTextFileViewModel", "stopRequests");
        gptApiRepository.stopQueuedRequests();
    }
    
    public void performNamedEntityRecognition(TextBlock textBlock, String tag)
    {
        setTextBlockStateById(textBlock.getId(), BlockState.WAITING_RESPONSE);
        gptApiRepository.getCompletion(textBlock.getText(), tag, new ApiResponseListener()
        {
            @Override
            public void OnResponse(@NonNull Call call, @NonNull Response response)
            {
                Log.d("ProcessTextFileViewModel", "OnResponse received for request: " + call.request().body().toString());
    
                OnCompletionResponse(response, textBlock);
            }

            @Override
            public void OnError(@NonNull Call call, @NonNull IOException e)
            {
                OnCompletionError(e.getMessage(), textBlock);
            }
        });
    }
    
    
    private void OnCompletionResponse(Response response, TextBlock textBlock)
    {
        Gson gson = new Gson();
        try {
            String jsonBody = response.body().string();
            Log.d("ProcessTextFileViewModel", "OnCompletionResponse received response: " + jsonBody);
    
            try
            {
                GptChatResponse gptResponse = gson.fromJson(jsonBody, GptChatResponse.class);
            
                if(gptResponse.hasError())
                {
                    Log.d("gptResponse", "error: " + gptResponse.getError().getMessage());
                    Log.d("gptResponse", "error type: " + gptResponse.getError().getType());
                    setTextBlockStateById(textBlock.getId(), BlockState.ERROR);
                }
                else
                {
                    storeCharactersForTextBlock(gptResponse, textBlock, new InsertedMultipleItemsListener()
                    {
                        @Override
                        public void onInsert(List<Long> itemIds)
                        {
                            Log.d("ProcessTextFileViewModel", "OnCompletionResponse - inserted " + String.valueOf(itemIds.size()) + " characters for text block ID: " + String.valueOf(textBlock.getId()));
                            setTextBlockStateById(textBlock.getId(), BlockState.NOT_REVIEWED);
                        }
                    });
                }
            }
            catch (com.google.gson.JsonSyntaxException ex)
            {
                Log.d("ProcessTextFileViewModel", "OnCompletionResponse Problem parsing response: " + ex);
                setTextBlockStateById(textBlock.getId(), BlockState.ERROR);
            }
        }
        catch (IOException e)
        {
            // Handle the exception.
            Log.d("ProcessTextFileViewModel", "OnCompletionResponse exception: " + e.getMessage());
            setTextBlockStateById(textBlock.getId(), BlockState.ERROR);
        }
    }
    
    private void OnCompletionError(String error,  TextBlock textBlock)
    {
        Log.d("ProcessTextFileViewModel", "OnCompletionError exception: " + error);
        if (error.equals("Canceled"))
        {
            setTextBlockStateById(textBlock.getId(), BlockState.NOT_REQUESTED);
        }
        else
        {
            setTextBlockStateById(textBlock.getId(), BlockState.ERROR);
        }
    }
    
    public void setTextBlockStateById(int textBlock_Id, BlockState state)
    {
        databaseRepository.setTextBlockStateById(textBlock_Id, state);
    }
    
    
    
    public void storeCharactersForTextBlock(GptChatResponse apiResponse, TextBlock textBlock, InsertedMultipleItemsListener onInsertListener) {
        Gson gson = new Gson();
        String responseContent = apiResponse.getChoices().get(0).getMessage().getContent();
        try {
            GptCompletion completion = gson.fromJson(responseContent, GptCompletion.class);
            
            // Capitalize the 'character' member of each GptCharacterLine
            for (GptCharacterLine characterLine : completion.getCharacterLines()) {
                characterLine.setCharacter(capitalizeFirstLetter(characterLine.getCharacter()));
            }
            
            // Capitalize the 'character' member of each GptCharacter
            for (GptCharacter character : completion.getCharacters()) {
                character.setCharacter(capitalizeFirstLetter(character.getCharacter()));
            }
            
            List<CharacterLine> characterLinesList = addCharacterLines(textBlock.getId(), completion.getCharacterLines());
            List<StoryCharacter> storyCharacters = addAllStoryCharacters(completion.getCharacters(), textBlock.getProjectId());
            
            databaseRepository.storeCharacterLinesAndCharacters(characterLinesList, storyCharacters, onInsertListener);
        } catch (com.google.gson.JsonSyntaxException ex) {
            Log.d("ProcessTextFileViewModel", "storeCharactersForTextBlock couldn't parse completion json: " + ex);
            setTextBlockStateById(textBlock.getId(), BlockState.ERROR);
        }
    }
    
    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return Character.toTitleCase(input.charAt(0)) + input.substring(1);
    }
    
    private List<StoryCharacter> addAllStoryCharacters(List<GptCharacter> characters, int projectId) {
        ArrayList<StoryCharacter> result = new ArrayList<>();
        result.add(new StoryCharacter("Narrator", "none", ttsRepository.getRandomVoiceName(), projectId));
    
        for (GptCharacter character : characters)
        {
            String characterName = character.getCharacter();
            String characterGender = character.getGender();
    
            result.add(new StoryCharacter(characterName, characterGender, ttsRepository.getRandomVoiceName(), projectId));
        }
        return result;
    }
    
    private ArrayList<CharacterLine> addCharacterLines(int textBlockId, List<GptCharacterLine> completionLines)
    {
        ArrayList<CharacterLine> result = new ArrayList<>();
        int i=0;
        for (GptCharacterLine characterLine : completionLines)
        {
            result.add(new CharacterLine(textBlockId, i, characterLine.getCharacter(), characterLine.getLine()));
            i++;
        }
        return result;
    }

    public void destroyTTS()
    {
        ttsInitStatus.postValue(false);
        ttsRepository.destroyTTS();
    }
    
}

