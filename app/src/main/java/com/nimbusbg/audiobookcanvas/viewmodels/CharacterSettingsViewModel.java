package com.nimbusbg.audiobookcanvas.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nimbusbg.audiobookcanvas.data.listeners.TtsInitListener;
import com.nimbusbg.audiobookcanvas.data.local.entities.StoryCharacter;
import com.nimbusbg.audiobookcanvas.data.local.relations.TextBlockWithData;
import com.nimbusbg.audiobookcanvas.data.repository.AudiobookRepository;
import com.nimbusbg.audiobookcanvas.data.repository.TtsRepository;

import java.util.ArrayList;
import java.util.List;

public class CharacterSettingsViewModel extends AndroidViewModel
{
    private final AudiobookRepository databaseRepository;
    private final TtsRepository ttsRepository;
    private int projectId;
    private String voiceSampleText;
    
    private LiveData<List<StoryCharacter>> allCharacters;
    
    public CharacterSettingsViewModel(@NonNull Application application, int projectId)
    {
        super(application);
        this.databaseRepository = new AudiobookRepository(application);
        this.ttsRepository = new TtsRepository(application);
        this.projectId = projectId;
        this.voiceSampleText = "This is how the chosen voice sounds";
    
        this.allCharacters = databaseRepository.getAllCharactersByProjectId(this.projectId);
    }
    
    public String getVoiceSampleText()
    {
        return voiceSampleText;
    }
    
    public void playVoiceSampleForVoice(String voiceName)
    {
        ttsRepository.playVoiceSample(voiceName, voiceSampleText);
    }
    
    public void waitForTTS(TtsInitListener listener)
    {
        ttsRepository.initTTS(listener);
    }
    
    public LiveData<List<StoryCharacter>> getAllCharacters()
    {
        return allCharacters;
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
    
    public void updateCharacterVoice(String selectedCharacterName, String newVoice)
    {
        databaseRepository.updateCharacterVoice(selectedCharacterName, newVoice, projectId);
    }
    
    public ArrayList<String> getExtendedEnglishVoices()
    {
        return ttsRepository.getExtendedEnglishVoiceNames();
    }
    
    public void destroyTTS()
    {
        ttsRepository.destroyTTS();
    }
}
