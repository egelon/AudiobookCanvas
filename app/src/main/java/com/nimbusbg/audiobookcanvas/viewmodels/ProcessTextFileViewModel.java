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
import com.nimbusbg.audiobookcanvas.data.repository.FileOperationListener;
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
    
    public void performNamedEntityRecognition(String[] textLines, String tag, ApiResponseListener rspListener)
    {
        try
        {
            gptApiRepository.getCompletion(textLines, tag, rspListener);
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
        String rawResponseText = getRawResponseText(apiResponse);
        if (rawResponseText == null)
        {
            throw new JSONException("Empty API response");
        }
        
        JSONArray characters = new JSONObject(rawResponseText).getJSONArray("characters");
        ArrayList<String> voices = ttsRepository.getVoicesForLocale("en", "US");
        List<StoryCharacter> storyCharacters = new ArrayList<>();
        storyCharacters.add(new StoryCharacter("Narrator", "none", getRandomVoice(voices)));
        addUniqueStoryCharacters(characters, storyCharacters, getRandomVoice(voices));
        
    
        JSONArray characterLines = new JSONObject(rawResponseText).getJSONArray("characterLines");
        List<CharacterLine> characterLinesList = new ArrayList<>();
        for (int i = 0; i < characterLines.length(); i++)
        {
            addCharacterLine(characterLines.getJSONObject(i), textBlock, characterLinesList);
    
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
        }
        
        databaseRepository.storeCharacterLinesAndCharacters(characterLinesList, storyCharacters, onInsertListener);
        ttsRepository.stitchWavFiles(textBlock.getId(), textBlock.getGeneratedAudioPath());
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
    
    private void addUniqueStoryCharacters(JSONArray characters, List<StoryCharacter> existingCharacters, String voice) throws JSONException
    {
        for (int i = 0; i < characters.length(); i++)
        {
            JSONObject character = characters.getJSONObject(i);
            String characterName = character.getString("character");
            String characterGender = character.has("gender") ? character.getString("gender") : "none";
            
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
                existingCharacters.add(new StoryCharacter(characterName, characterGender, voice));
            }
        }
    }
    
    private void addCharacterLine(JSONObject characterLine, TextBlock textBlock, List<CharacterLine> characterLinesList) throws JSONException
    {
        int startingIndex = determineTextLineStartIndex(characterLine.getString("line"), textBlock.getTextLines());
        if (startingIndex >= 0)
        {
            characterLinesList.add(new CharacterLine(textBlock.getId(), startingIndex, characterLine.getString("character")));
        }
    }
    
    private int determineTextLineStartIndex(String textLine, String[] lines)
    {
        // Iterate over the lines array.
        for (int i = 0; i < lines.length; i++)
        {
            // If the line matches textLine, return the current index.
            if (lines[i].trim().contains(textLine.trim()))
            {
                return i;
            }
        }
        
        // If no match was found, return -1.
        return -1;
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
