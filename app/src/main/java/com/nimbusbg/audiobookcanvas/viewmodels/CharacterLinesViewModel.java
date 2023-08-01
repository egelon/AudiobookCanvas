package com.nimbusbg.audiobookcanvas.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nimbusbg.audiobookcanvas.data.local.entities.CharacterLine;
import com.nimbusbg.audiobookcanvas.data.local.entities.StoryCharacter;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;
import com.nimbusbg.audiobookcanvas.data.local.relations.TextBlockWithData;
import com.nimbusbg.audiobookcanvas.data.repository.AudiobookRepository;
import com.nimbusbg.audiobookcanvas.data.listeners.TtsInitListener;
import com.nimbusbg.audiobookcanvas.data.listeners.TtsUtteranceListener;
import com.nimbusbg.audiobookcanvas.data.repository.TtsRepository;
import com.nimbusbg.audiobookcanvas.ui_state.CharacterLinesViewState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CharacterLinesViewModel extends AndroidViewModel
{
    private final AudiobookRepository databaseRepository;
    private final TtsRepository ttsRepository;
    private boolean isUtteranceStarted;
    
    private LiveData<TextBlockWithData> currentTextBlockWithData;
    private LiveData<List<StoryCharacter>> allCharacters;

    private AtomicInteger processedUtterances;
    
    public CharacterLinesViewModel(@NonNull Application application, int textblockId, int projectId)
    {
        super(application);
        databaseRepository = new AudiobookRepository(application);
        ttsRepository = new TtsRepository(application);
    
        currentTextBlockWithData = databaseRepository.getTextBlockWithDataByTextBlockId(textblockId);
        allCharacters = databaseRepository.getAllCharactersByProjectId(projectId);
    }
    
    public void waitForTTS(TtsInitListener listener)
    {
        ttsRepository.initTTS(listener);
    }
    
    public LiveData<List<StoryCharacter>> getAllCharacters()
    {
        return allCharacters;
    }
    
    public LiveData<TextBlockWithData> getTextBlockWithData()
    {
        return currentTextBlockWithData;
    }
    
    private String getVoiceByCharacterName(String charName)
    {
        for(StoryCharacter character : allCharacters.getValue())
        {
            if(charName.equals(character.getName()))
            {
                return character.getVoice();
            }
        }
        return "en-us-x-tpd-network";
    }
    
    public void recordAllCharacterLines()
    {
        processedUtterances = new AtomicInteger(0);
        TextBlock currentTextBlock = currentTextBlockWithData.getValue().textBlock;
        List<CharacterLine> characterLines = currentTextBlockWithData.getValue().characterLines;
        
        for (CharacterLine line: characterLines)
        {
            recordCharacterLine(line, new TtsUtteranceListener()
            {
                @Override
                public void OnUtteranceStart(String s)
                {
                    Log.d("CharacterLinesViewModel", "OnUtteranceStart: " + s);
                }
    
                @Override
                public void OnUtteranceDone(String s)
                {
                    processedUtterances.getAndIncrement();
                    Log.d("CharacterLinesViewModel", "OnUtteranceDone: " + s);
                    if(processedUtterances.get() >= characterLines.size())
                    {
                        Log.d("CharacterLinesViewModel", "stitchWavFiles");
                        ttsRepository.stitchWavFiles(currentTextBlock.getId(), currentTextBlock.getGeneratedAudioPath());
                    }
                }
    
                @Override
                public void OnUtteranceError(String s)
                {
                    processedUtterances.getAndIncrement();
                    if(processedUtterances.get() >= characterLines.size())
                    {
                        ttsRepository.stitchWavFiles(currentTextBlock.getId(), currentTextBlock.getGeneratedAudioPath());
                    }
                    Log.d("CharacterLinesViewModel", "OnUtteranceError: " + s);
                }
            });
        }
        
        while (processedUtterances.get() < characterLines.size())
        {
        
        }
    }
    
    public void recordCharacterLine(CharacterLine characterLine, TtsUtteranceListener listener)
    {
        TextBlock currentTextBlock = currentTextBlockWithData.getValue().textBlock;
        String characterLineStr = currentTextBlock.getLineByIndex(characterLine.getStartIndex());
        String characterVoice = getVoiceByCharacterName(characterLine.getCharacterName());
        
        ttsRepository.speakCharacterLine(characterLineStr, characterVoice, currentTextBlock.getLineAudioPath(characterLine.getStartIndex()), new TtsUtteranceListener()
        {
            @Override
            public void OnUtteranceStart(String s)
            {
                isUtteranceStarted = true;
                listener.OnUtteranceStart(s);
            }
    
            @Override
            public void OnUtteranceDone(String s)
            {
                isUtteranceStarted = false;
                listener.OnUtteranceDone(s);
            }
    
            @Override
            public void OnUtteranceError(String s)
            {
                isUtteranceStarted = false;
                listener.OnUtteranceError(s);
            }
        });
    }
    
    public void destroyTTS()
    {
        ttsRepository.destroyTTS();
    }
    
    public void updateCharacter(String selectedCharacter, int itemIndex, int textblockId)
    {
        databaseRepository.updateCharacter(selectedCharacter, itemIndex, textblockId);
    }
}
