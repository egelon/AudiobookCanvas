package com.nimbusbg.audiobookcanvas.viewmodels;

import android.app.Application;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.nimbusbg.audiobookcanvas.data.local.entities.BlockState;
import com.nimbusbg.audiobookcanvas.data.local.entities.CharacterLine;
import com.nimbusbg.audiobookcanvas.data.local.entities.StoryCharacter;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithTextBlocks;
import com.nimbusbg.audiobookcanvas.data.local.relations.TextBlockWithData;
import com.nimbusbg.audiobookcanvas.data.repository.ApiResponseListener;
import com.nimbusbg.audiobookcanvas.data.repository.AudiobookRepository;
import com.nimbusbg.audiobookcanvas.data.repository.FIleOperationListener;
import com.nimbusbg.audiobookcanvas.data.repository.GptApiRepository;
import com.nimbusbg.audiobookcanvas.data.repository.InsertedItemListener;
import com.nimbusbg.audiobookcanvas.data.repository.InsertedMultipleItemsListener;
import com.nimbusbg.audiobookcanvas.data.repository.TextFileRepository;
import com.nimbusbg.audiobookcanvas.data.repository.TtsListener;
import com.nimbusbg.audiobookcanvas.data.repository.TtsRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProcessTextFileViewModel extends AndroidViewModel
{
    private final AudiobookRepository databaseRepository;
    private final TextFileRepository fileRepository;
    private final GptApiRepository gptApiRepository;
    private final TtsRepository ttsRepository;
    private ArrayList<String> textChunks;
    private ArrayList<TextBlockWithData> textBlocks;
    
    public TextToSpeech tts;
    
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
        ttsRepository = new TtsRepository(application);
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
    
    public void setTextBlockStateById(int textBlock_Id, BlockState state)
    {
        databaseRepository.setTextBlockStateById(textBlock_Id, state);
    }
    
    public LiveData<List<TextBlockWithData>> getTextBlocksWithDataByProjectId(int proj_id)
    {
        return databaseRepository.getTextBlocksWithDataByProjectId(proj_id);
    }
    
    public String getRandomVoice(ArrayList<String> voices) {
        // Create a Random object
        Random rand = new Random();
        
        // Generate a random index within the bounds of the list
        int randomIndex = rand.nextInt(voices.size());
        
        // Return the element at the random index
        return voices.get(randomIndex);
    }
    
    public void storeCharactersForTextBlock(JSONObject apiResponse, TextBlock textBlock, InsertedMultipleItemsListener onInsertListener) throws JSONException
    {
        List<CharacterLine> characterLinesList = new ArrayList<>();
        List<StoryCharacter> characters = new ArrayList<>();
        
        String rawResponseText = getRawResponseText(apiResponse);
        if (rawResponseText == null)
        {
            throw new JSONException("Empty API response");
        }
        
        ArrayList<String> voices = ttsRepository.getVoicesForLocale("en", "US");
        JSONArray characterLines = new JSONObject(rawResponseText).getJSONArray("characterLines");
        
        for (int i = 0; i < characterLines.length(); i++)
        {
            JSONObject characterLine = characterLines.getJSONObject(i);
            StoryCharacter storyCharacter = getOrCreateStoryCharacter(characterLine, characters, voices);
            if (storyCharacter != null && !characters.contains(storyCharacter))
            {
                characters.add(storyCharacter);
            }
            
            CharacterLine newCharacterLine = createCharacterLine(characterLine, textBlock);
            if (newCharacterLine != null)
            {
                //TODO: REMOVE ME!!! TEST ONLY!!!
    
                ttsRepository.speakCharacterLine(getTextLineFromBlockByIndex(textBlock.getText(), newCharacterLine.getStartIndex()), textBlock.getGeneratedAudioPath(), new TtsListener()
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
                
                
                characterLinesList.add(newCharacterLine);
            }
            
        }
        
        
        
        databaseRepository.storeCharacterLinesAndCharacters(characterLinesList, characters, onInsertListener);
    }
    
    private String getRawResponseText(JSONObject apiResponse) throws JSONException
    {
        JSONArray choicesArray = apiResponse.getJSONArray("choices");
        for (int i = 0; i < choicesArray.length(); i++)
        {
            JSONObject choiceObject = choicesArray.getJSONObject(i);
            return choiceObject.getString("text");
        }
        return null;
    }
    
    private StoryCharacter getOrCreateStoryCharacter(JSONObject characterLine, List<StoryCharacter> existingCharacters, ArrayList<String> voices) throws JSONException
    {
        String characterName = characterLine.getString("character");
        String characterGender = characterLine.has("gender") ? characterLine.getString("gender") : "none";
        
        for (StoryCharacter character : existingCharacters)
        {
            if (character.getName().equals(characterName))
            {
                return null;
            }
        }
        return new StoryCharacter(characterName, characterGender, getRandomVoice(voices));
    }
    
    private CharacterLine createCharacterLine(JSONObject characterLine, TextBlock textBlock) throws JSONException {
        int startingIndex = determineTextLineStartIndex(characterLine.getString("text"), textBlock.getText());
        if (startingIndex >= 0) {
            return new CharacterLine(textBlock.getId(), startingIndex, characterLine.getString("character"));
        }
        return null;
    }
    
    private int determineTextLineStartIndex(String textLine, String textBlock)
    {
        // Split the textBlock into lines using "\n" as the separator.
        String[] lines = textBlock.split("\n");
        
        // Iterate over the lines array.
        for (int i = 0; i < lines.length; i++)
        {
            // If the line matches textLine, return the current index.
            if (lines[i].trim().equals(textLine.trim()))
            {
                return i;
            }
        }
        
        // If no match was found, return -1.
        return -1;
    }
    
    private String getTextLineFromBlockByIndex(String textBlock, int index)
    {
        // Split the textBlock into lines using "\n" as the separator.
        String[] lines = textBlock.split("\n");
        if(index > 0 && index <= lines.length)
        {
            return lines[index];
        }
        return null;
    }
    
    public void initTTS(TtsListener listener)
    {
        ttsRepository.initTTS(listener);
    }
    
    
    public void destroyTTS()
    {
        ttsRepository.destroyTTS();
    }
    
}
