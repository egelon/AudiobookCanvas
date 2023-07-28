package com.nimbusbg.audiobookcanvas.viewmodels;

import android.app.Application;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
import com.nimbusbg.audiobookcanvas.data.repository.ApiResponseListener;
import com.nimbusbg.audiobookcanvas.data.repository.AudiobookRepository;
import com.nimbusbg.audiobookcanvas.data.repository.FileOperationListener;
import com.nimbusbg.audiobookcanvas.data.repository.GptApiRepository;
import com.nimbusbg.audiobookcanvas.data.repository.InsertedItemListener;
import com.nimbusbg.audiobookcanvas.data.repository.InsertedMultipleItemsListener;
import com.nimbusbg.audiobookcanvas.data.repository.TextFileRepository;
import com.nimbusbg.audiobookcanvas.data.repository.TtsListener;
import com.nimbusbg.audiobookcanvas.data.repository.TtsRepository;

import org.json.JSONException;
import org.json.JSONObject;

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
    private ArrayList<String> voices;
    private ArrayList<TextBlockWithData> textBlocks;
    
    private LiveData<ProjectWithTextBlocks> currentProjectWithTextBlocks;
    
    public TextToSpeech tts;
    
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
        gptApiRepository.setDialogueStartChar(end);
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
    
        currentProjectWithTextBlocks = databaseRepository.getProjectWithTextBlocksById(proj_id);
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
        for(TextBlock textBlock : currentProjectWithTextBlocks.getValue().textBlocks)
        {
            if(textBlock.getState() == BlockState.NOT_REQUESTED)
            {
                performNamedEntityRecognition(textBlock, "TAG_" + String.valueOf(textBlock.getId()));
            }
        }
    }
    
    public void stopCharacterRequests()
    {
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
        gptApiRepository.getCompletion(textBlock.getTextLines(), tag, new ApiResponseListener()
        {
            @Override
            public void OnResponse(@NonNull Call call, @NonNull Response response)
            {
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
        setTextBlockStateById(textBlock.getId(), BlockState.ERROR);
    }
    
    public void setTextBlockStateById(int textBlock_Id, BlockState state)
    {
        databaseRepository.setTextBlockStateById(textBlock_Id, state);
    }
    
    
    
    public void storeCharactersForTextBlock(GptChatResponse apiResponse, TextBlock textBlock, InsertedMultipleItemsListener onInsertListener)
    {
        Gson gson = new Gson();
        String responseContent = apiResponse.getChoices().get(0).getMessage().getContent();
        try
        {
            GptCompletion completion = gson.fromJson(apiResponse.getChoices().get(0).getMessage().getContent(), GptCompletion.class);
    
            List<StoryCharacter> storyCharacters = new ArrayList<>();
            storyCharacters.add(new StoryCharacter("Narrator", "none", ttsRepository.getRandomVoiceName(), textBlock.getProjectId()));
    
            addUniqueStoryCharacters(completion.getCharacters(), storyCharacters, ttsRepository.getRandomVoiceName(), textBlock.getProjectId());
    
    
            List<CharacterLine> characterLinesList = new ArrayList<>();
            for (GptCharacterLine characterLine : completion.getCharacterLines())
            {
                addCharacterLine(characterLine, textBlock, characterLinesList);
/*
//TODO: REMOVE ME!!! TEST ONLY!!!
CharacterLine newCharacterLine = characterLinesList.get(characterLinesList.size()-1);
String characterLine = textBlock.getLineByIndex(newCharacterLine.getStartIndex());
String characterVoice = getVoiceByCharacterName(newCharacterLine.getCharacterName(), storyCharacters);

if (newCharacterLine != null)
{
    ttsRepository.speakCharacterLine(characterLine, characterVoice, textBlock.getLineAudioPath(newCharacterLine.getStartIndex()), new TtsListener()
    {
        @Override
        public void OnInitSuccess()
        {

        }

        @Override
        public void OnInitFailure()
        {

        }

        @Override
        public void OnUtteranceStart(String s)
        {

        }

        @Override
        public void OnUtteranceDone(String s)
        {
            Log.i("TTS_REPO_GENERATION", "Utterance " + s + " generated");
        }

        @Override
        public void OnUtteranceError(String s)
        {

        }
    });
}
 */
            }
    
            databaseRepository.storeCharacterLinesAndCharacters(characterLinesList, storyCharacters, onInsertListener);
        }
        catch (com.google.gson.JsonSyntaxException ex)
        {
            Log.d("ProcessTextFileViewModel", "storeCharactersForTextBlock couldn't parse completion json: " + ex);
            setTextBlockStateById(textBlock.getId(), BlockState.ERROR);
        }
        //ttsRepository.stitchWavFiles(textBlock.getId(), textBlock.getGeneratedAudioPath());
    }
    
    private String getVoiceByCharacterName(String charName, List<StoryCharacter> characters)
    {
        for(StoryCharacter character : characters)
        {
            if(charName.equals(character.getName()))
            {
                return character.getVoice();
            }
        }
        return "en-us-x-tpd-network";
    }
    
    /*
    private String getRawResponseText(JSONObject apiResponse) throws JSONException
    {
        JSONArray choicesArray = apiResponse.getJSONArray("choices");
        for (int i = 0; i < choicesArray.length(); i++)
        {
            String message = choicesArray.getJSONObject(i).getJSONObject("message").getString("content");
            return message;
        }
        return null;
    }
     */
    
    private void addUniqueStoryCharacters(List<GptCharacter> characters, List<StoryCharacter> existingCharacters, String voice, int projectId)
    {
        for (GptCharacter character : characters)
        {
            String characterName = character.getCharacter();
            String characterGender = character.getGender();
        
            boolean isExisting = false;
            for (StoryCharacter storyCharacter : existingCharacters)
            {
                if (storyCharacter.getName().equals(characterName))
                {
                    isExisting = true;
                    break;
                }
            }
        
            if(!isExisting)
            {
                existingCharacters.add(new StoryCharacter(characterName, characterGender, voice, projectId));
            }
        }
    }
    
    private void addCharacterLine(GptCharacterLine characterLine, TextBlock textBlock, List<CharacterLine> characterLinesList)
    {
        int startingIndex = determineTextLineStartIndex(characterLine.getLine(), textBlock.getTextLines());
        if (startingIndex >= 0)
        {
            characterLinesList.add(new CharacterLine(textBlock.getId(), startingIndex, characterLine.getCharacter()));
        }
    }
    
    private int determineTextLineStartIndex(String textLine, String[] lines)
    {
        // Normalize all types of quotation marks in textLine
        if (textLine.startsWith(String.valueOf(fileRepository.getDialogueStartChar())) || textLine.startsWith(String.valueOf(fileRepository.getDialogueStartChar())))
        {
            textLine = normalizeQuotes(textLine);
        }

        // Iterate over the lines array.
        for (int i = 0; i < lines.length; i++)
        {
            String line = lines[i];
        
            // Normalize all types of quotation marks in the current line
            if (line.startsWith(String.valueOf(fileRepository.getDialogueStartChar())) || line.startsWith(String.valueOf(fileRepository.getDialogueEndChar())))
            {
                line = normalizeQuotes(line);
            }
        
            // If the line matches textLine, return the current index.
            if (line.trim().contains(textLine.trim()))
            {
                return i;
            }
        }
    
        // If no match was found, return -1.
        return -1;
    }
    
    private String normalizeQuotes(String s)
    {
        // Replace all types of quotation marks with a standard one
        String res = s.replace(fileRepository.getDialogueStartChar(), '\"').replace(fileRepository.getDialogueEndChar(), '\"');
        return res;
    }
    
    
    public void initTTS()
    {
        ttsRepository.initTTS(new TtsListener()
        {
        @Override
        public void OnInitSuccess()
        {
            voices = ttsRepository.getVoiceNamesForCurrentLocale(); //("en", "US");
        }
    
        @Override
        public void OnInitFailure()
        {
        }
    
        @Override
        public void OnUtteranceStart(String s)
        {
        }
    
        @Override
        public void OnUtteranceDone(String s)
        {
        }
    
        @Override
        public void OnUtteranceError(String s)
        {
        }
        });
    }

    public void destroyTTS()
    {
        ttsRepository.destroyTTS();
    }
    
}

