package com.nimbusbg.audiobookcanvas.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nimbusbg.audiobookcanvas.data.listeners.MixingProcessListener;
import com.nimbusbg.audiobookcanvas.data.listeners.TtsInitListener;
import com.nimbusbg.audiobookcanvas.data.listeners.TtsUtteranceListener;
import com.nimbusbg.audiobookcanvas.data.local.entities.CharacterLine;
import com.nimbusbg.audiobookcanvas.data.local.entities.StoryCharacter;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;
import com.nimbusbg.audiobookcanvas.data.local.relations.TextBlockWithData;
import com.nimbusbg.audiobookcanvas.data.repository.AudioMixingRepository;
import com.nimbusbg.audiobookcanvas.data.repository.AudiobookRepository;
import com.nimbusbg.audiobookcanvas.data.repository.MediaStorageRepository;
import com.nimbusbg.audiobookcanvas.data.repository.TtsRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import zeroonezero.android.audio_mixer.AudioMixer;

public class CharacterLinesViewModel extends AndroidViewModel
{
    private final AudiobookRepository databaseRepository;
    private final TtsRepository ttsRepository;
    private final MediaStorageRepository mediaStorageRepository;
    private final AudioMixingRepository audioMixingRepository;
    
    private LiveData<TextBlockWithData> currentTextBlockWithData;
    private LiveData<List<StoryCharacter>> allCharacters;
    private LiveData<ProjectWithMetadata> projectMetadata;

    private AtomicInteger processedUtterances;
    private MutableLiveData<Boolean> ttsInitStatus = new MutableLiveData<>();
    
    private AudioMixer audioMixer;
    
    public CharacterLinesViewModel(@NonNull Application application, int textblockId, int projectId)
    {
        super(application);
        databaseRepository = new AudiobookRepository(application);
        ttsRepository = new TtsRepository(application);
        mediaStorageRepository = new MediaStorageRepository(application);
        audioMixingRepository = new AudioMixingRepository(application);
        ttsInitStatus.postValue(false);
        currentTextBlockWithData = databaseRepository.getTextBlockWithDataByTextBlockId(textblockId);
        allCharacters = databaseRepository.getAllCharactersByProjectId(projectId);
        projectMetadata = databaseRepository.getProjectWithMetadataById(projectId);
        
    }
    
    public LiveData<Boolean> getTtsInitStatus()
    {
        Log.d("CharacterLinesViewModel", "getTtsInitStatus");
        // Check if ttsInitStatus is null OR its value is false
        if (ttsInitStatus.getValue() == null || !ttsInitStatus.getValue()) {
            Log.d("CharacterLinesViewModel", "Not initialised - waiting for TTS");
            waitForTTS();
        }
        return ttsInitStatus;
    }
    
    private void waitForTTS()
    {
        ttsRepository.initTTS(new TtsInitListener()
        {
            @Override
            public void OnInitSuccess()
            {
                Log.d("CharacterLinesViewModel", "Post value TTS initialised");
                ttsInitStatus.postValue(true); // Update LiveData
            }
            
            @Override
            public void OnInitFailure()
            {
                Log.d("CharacterLinesViewModel", "Failed to initialise TTS");
                ttsInitStatus.postValue(false); // Handle failure
            }
        });
    }
    
    
    
    public LiveData<List<StoryCharacter>> getAllCharacters()
    {
        return allCharacters;
    }
    
    public LiveData<TextBlockWithData> getTextBlockWithData()
    {
        return currentTextBlockWithData;
    }
    
    public LiveData<ProjectWithMetadata> getProjectMetadata()
    {
        return projectMetadata;
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
    
    public void recordAllCharacterLines(MixingProcessListener processingListener)
    {
        processedUtterances = new AtomicInteger(0);
        int currentTextBlockId = currentTextBlockWithData.getValue().textBlock.getId();
        String generatedAudioPath = currentTextBlockWithData.getValue().textBlock.getGeneratedAudioPath();
        
        List<CharacterLine> characterLines = currentTextBlockWithData.getValue().characterLines;
        String audiobookName = projectMetadata.getValue().project.getOutput_audiobook_path();

        for (CharacterLine line: characterLines)
        {
            recordCharacterLine(audiobookName, line, new TtsUtteranceListener()
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
                }
    
                @Override
                public void OnUtteranceError(String s)
                {
                    processedUtterances.getAndIncrement();
                    //if(processedUtterances.get() >= characterLines.size())
                    //{
                    //    combineVoices(audiobookName, currentTextBlockId, generatedAudioPath, processingListener);
                    //}
                    Log.e("CharacterLinesViewModel", "OnUtteranceError: " + s);
                }
            });
        }
        
        while (processedUtterances.get() < characterLines.size())
        {
        
        }
    
        if(processedUtterances.get() >= characterLines.size())
        {
            combineVoices(audiobookName, currentTextBlockId, generatedAudioPath, processingListener);
        }
    }
    
    private void combineVoices(String audiobookName, int currentTextBlockId, String generatedAudioPath, MixingProcessListener processingListener)
    {
        // Create the temporary folder if it doesn't exist
        File tmpFolder = mediaStorageRepository.makeFolderInAppStorage(audiobookName);
    
        // List to store the matching WAV files
        List<File> matchingFiles = mediaStorageRepository.getMatchingFilesForId(tmpFolder, currentTextBlockId, ".wav");
    
        //create the stitched output file
        File outputFile = mediaStorageRepository.createChild(tmpFolder.getAbsolutePath(), generatedAudioPath);
    
        Log.d("CharacterLinesViewModel", "stitchWavFiles");
        stitchWavFiles(audiobookName, matchingFiles, outputFile, processingListener);
        
    }
    
    private void stitchWavFiles(String audiobookName, List<File> matchingFiles, File outputFile, MixingProcessListener processingListener)
    {
        audioMixingRepository.stitchWavFiles(audiobookName, matchingFiles, outputFile, new AudioMixer.ProcessingListener()
        {
            @Override
            public void onProgress(double progress)
            {
                processingListener.onProgress(progress);
            }
    
            @Override
            public void onEnd()
            {
                
                try
                {
                    mediaStorageRepository.moveFileToMusicDirectory(outputFile, "AudiobookCanvas" + File.separator + audiobookName);
                    mediaStorageRepository.deleteFiles(matchingFiles);
                }
                catch (IOException ex)
                {
                    Log.e("CharacterLinesViewModel", "Problem moving file to Music directory: " + ex.getMessage());
                }
                processingListener.onEnd();
    
                audioMixingRepository.Release();
            }
        });
    }
    
    
    
    public void recordCharacterLine(String folderName, CharacterLine characterLine, TtsUtteranceListener listener)
    {
        TextBlock currentTextBlock = currentTextBlockWithData.getValue().textBlock;
        String characterLineStr = currentTextBlock.getLineByIndex(characterLine.getStartIndex());
        String characterVoice = getVoiceByCharacterName(characterLine.getCharacterName());
        
        ttsRepository.speakCharacterLine(characterLineStr, characterVoice, folderName, currentTextBlock.getLineAudioPath(characterLine.getStartIndex()), new TtsUtteranceListener()
        {
            @Override
            public void OnUtteranceStart(String s)
            {
                listener.OnUtteranceStart(s);
            }
    
            @Override
            public void OnUtteranceDone(String s)
            {
                listener.OnUtteranceDone(s);
            }
    
            @Override
            public void OnUtteranceError(String s)
            {
                listener.OnUtteranceError(s);
            }
        });
    }
    
    public void updateCharacter(String selectedCharacter, int itemIndex, int textblockId)
    {
        databaseRepository.updateCharacter(selectedCharacter, itemIndex, textblockId);
    }
}
