package com.nimbusbg.audiobookcanvas.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nimbusbg.audiobookcanvas.data.listeners.ApiResponseListener;
import com.nimbusbg.audiobookcanvas.data.listeners.TtsInitListener;
import com.nimbusbg.audiobookcanvas.data.local.entities.StoryCharacter;
import com.nimbusbg.audiobookcanvas.data.local.relations.MetadataWithCharacters;
import com.nimbusbg.audiobookcanvas.data.repository.AudiobookRepository;
import com.nimbusbg.audiobookcanvas.data.repository.GptApiRepository;
import com.nimbusbg.audiobookcanvas.data.repository.MediaStorageRepository;
import com.nimbusbg.audiobookcanvas.data.repository.TtsRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Response;

public class CharacterSettingsViewModel extends AndroidViewModel
{
    private final AudiobookRepository databaseRepository;
    private final TtsRepository ttsRepository;
    private int projectId;
    private String voiceSampleText;
    private GptApiRepository gptApiRepository;
    private MediaStorageRepository mediaStorageRepository;
    
    private LiveData<MetadataWithCharacters> metadataWithCharacters;
    
    public CharacterSettingsViewModel(@NonNull Application application, int projectId)
    {
        super(application);
        this.databaseRepository = new AudiobookRepository(application);
        this.ttsRepository = new TtsRepository(application);
        this.gptApiRepository = new GptApiRepository(application);
        this.mediaStorageRepository = new MediaStorageRepository(application);
        this.projectId = projectId;
        this.voiceSampleText = "This is how the chosen voice sounds";
    
        this.metadataWithCharacters = databaseRepository.getMetadataWithAllCharactersByProjectId(this.projectId);
    }
    
    public String getVoiceSampleText()
    {
        return voiceSampleText;
    }
    
    public void playVoiceSampleForVoice(String voiceName)
    {
        ttsRepository.playVoiceSample(voiceName, voiceSampleText);
    }
    
    public void requestOnlineVoiceSampleText()
    {
        gptApiRepository.getSpeech(voiceSampleText, "alloy", "alloy_sample", new ApiResponseListener()
        {
            @Override
            public void OnResponse(@NonNull Call call, @NonNull Response response)
            {
                try
                {
                    String appDirectory = mediaStorageRepository.getAppDirectory().getAbsolutePath();
                    File sampleAudioFile = mediaStorageRepository.getAudioFile(appDirectory, "alloy_sample.wav");
                    
                    
                    InputStream is = response.body().byteStream();
                    FileOutputStream fos = new FileOutputStream(sampleAudioFile);
                    int read = 0;
                    byte[] buffer = new byte[32768];
                    while ((read = is.read(buffer)) > 0)
                    {
                        fos.write(buffer, 0, read);
                    }
    
                    fos.close();
                    is.close();
                }
                catch (IOException e)
                {
                    Log.e("Error", e.getMessage());
                }
            }
    
            @Override
            public void OnError(@NonNull Call call, @NonNull IOException e)
            {
                Log.e("Error", e.getMessage());
            }
        });
    }
    
    public void waitForTTS(TtsInitListener listener)
    {
        String isoLanguageCode = metadataWithCharacters.getValue().audiobookData.getLanguage();
        ttsRepository.initTTS(isoLanguageCode, listener);
    }
    
    public LiveData<MetadataWithCharacters> getMetadataWithCharacters()
    {
        return metadataWithCharacters;
    }
    
    private String getVoiceByCharacterName(String charName)
    {
        for(StoryCharacter character : metadataWithCharacters.getValue().storyCharacters)
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
        return ttsRepository.getExtendedLocaleVoiceNames();
    }
    
    public void destroyTTS()
    {
        ttsRepository.destroyTTS();
    }
}
