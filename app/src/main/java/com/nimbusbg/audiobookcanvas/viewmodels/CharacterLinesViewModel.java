package com.nimbusbg.audiobookcanvas.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.listeners.ApiResponseListener;
import com.nimbusbg.audiobookcanvas.data.listeners.MixingProcessListener;
import com.nimbusbg.audiobookcanvas.data.listeners.TtsInitListener;
import com.nimbusbg.audiobookcanvas.data.listeners.TtsUtteranceListener;
import com.nimbusbg.audiobookcanvas.data.local.entities.BlockState;
import com.nimbusbg.audiobookcanvas.data.local.entities.CharacterLine;
import com.nimbusbg.audiobookcanvas.data.local.entities.StoryCharacter;
import com.nimbusbg.audiobookcanvas.data.local.entities.TextBlock;
import com.nimbusbg.audiobookcanvas.data.local.relations.ProjectWithMetadata;
import com.nimbusbg.audiobookcanvas.data.local.relations.TextBlockWithData;
import com.nimbusbg.audiobookcanvas.data.repository.AudioMixingRepository;
import com.nimbusbg.audiobookcanvas.data.repository.AudiobookRepository;
import com.nimbusbg.audiobookcanvas.data.repository.GptApiRepository;
import com.nimbusbg.audiobookcanvas.data.repository.MediaStorageRepository;
import com.nimbusbg.audiobookcanvas.data.repository.TtsRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Response;
import zeroonezero.android.audio_mixer.AudioMixer;

public class CharacterLinesViewModel extends AndroidViewModel
{
    private final AudiobookRepository databaseRepository;
    private final TtsRepository ttsRepository;
    private final MediaStorageRepository mediaStorageRepository;
    private final AudioMixingRepository audioMixingRepository;
    private GptApiRepository gptApiRepository;
    
    private int currentTextblockId;
    private LiveData<TextBlockWithData> currentTextBlockWithData;
    private LiveData<List<StoryCharacter>> allCharacters;
    private LiveData<ProjectWithMetadata> projectMetadata;
    
    private MutableLiveData<Boolean> ttsInitStatus = new MutableLiveData<>();
    private MutableLiveData<Boolean> wavFilesStitched = new MutableLiveData<>();
    
    private MutableLiveData<Integer> processedUtterances;
    
    private AudioMixer audioMixer;
    private int numCharacterLines;
    
    public CharacterLinesViewModel(@NonNull Application application, int textblockId, int projectId)
    {
        super(application);
        databaseRepository = new AudiobookRepository(application);
        ttsRepository = new TtsRepository(application);
        mediaStorageRepository = new MediaStorageRepository(application);
        audioMixingRepository = new AudioMixingRepository(application);
        gptApiRepository = new GptApiRepository(application);
        ttsInitStatus.postValue(false);
        wavFilesStitched.postValue(false);
        currentTextblockId = textblockId;
        currentTextBlockWithData = databaseRepository.getTextBlockWithDataByTextBlockId(currentTextblockId);
        allCharacters = databaseRepository.getAllCharactersByProjectId(projectId);
        projectMetadata = databaseRepository.getProjectWithMetadataById(projectId);
    
        processedUtterances = new MutableLiveData<Integer>(0);
        numCharacterLines = 0;
        
        
    }
    
    public void setNumCharacterLines(int numCharacterLines)
    {
        this.numCharacterLines = numCharacterLines;
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
    
    public MutableLiveData<Boolean> getWavFilesStitched()
    {
        Log.d("CharacterLinesViewModel", "wavFilesStitched");
        // Check if ttsInitStatus is null OR its value is false
        return wavFilesStitched;
    }
    
    private void waitForTTS()
    {
        String isoLanguageCode = projectMetadata.getValue().audiobookData.getLanguage();
        ttsRepository.initTTS(isoLanguageCode, new TtsInitListener()
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
    
    public static float mapIntToFloatRange(int currentVal, int maxVal) {
        if (currentVal < 0 || currentVal > maxVal) {
            throw new IllegalArgumentException("currentVal must be within the range of 0 to maxVal.");
        }
        if (maxVal == 0) {
            throw new IllegalArgumentException("maxVal must not be 0.");
        }
        
        return (float) currentVal / maxVal;
    }
    
    public MutableLiveData<Integer> getProcessedUtterances()
    {
        return processedUtterances;
    }
    
    public int getNumCharacterLines()
    {
        return numCharacterLines;
    }
    
    public void recordAllCharacterLines(MixingProcessListener processingListener)
    {
        wavFilesStitched.postValue(false);

        processedUtterances.postValue(0);
        
        List<CharacterLine> characterLines = currentTextBlockWithData.getValue().characterLines;
        String audiobookName = projectMetadata.getValue().project.getOutput_audiobook_path();
        
        for (CharacterLine line: characterLines)
        {
    
            recordNetworkVoiceCharacterLine(audiobookName, line, new ApiResponseListener()
            {
                @Override
                public void OnResponse(@NonNull Call call, @NonNull Response response) throws IOException
                {
                    processedUtterances.postValue(processedUtterances.getValue() + 1);
                }
    
                @Override
                public void OnError(@NonNull Call call, @NonNull IOException e)
                {
                    processedUtterances.postValue(processedUtterances.getValue() + 1);
                }
            });
            
            
            
            
            
            
            
            
            
            
            
            
            /*
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
                    processedUtterances.postValue(processedUtterances.getValue() + 1);
                    //processingListener.onProgress(finalProgress);
                    Log.d("CharacterLinesViewModel", "OnUtteranceDone: " + s);
                }
    
                @Override
                public void OnUtteranceError(String s)
                {
                    processedUtterances.postValue(processedUtterances.getValue() + 1);
                    //if(processedUtterances.get() >= characterLines.size())
                    //{
                    //    combineVoices(audiobookName, currentTextBlockId, generatedAudioPath, processingListener);
                    //}
                    Log.e("CharacterLinesViewModel", "OnUtteranceError: " + s);
                }
            });
            
             */
        }
    }
    
    public void combineVoices(MixingProcessListener processingListener)
    {
        String audiobookName = projectMetadata.getValue().project.getOutput_audiobook_path();
        int currentTextBlockId = currentTextBlockWithData.getValue().textBlock.getId();
        String generatedAudioPath = currentTextBlockWithData.getValue().textBlock.getGeneratedAudioPath();
    
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
                processingListener.onEnd();
                mediaStorageRepository.deleteFiles(matchingFiles);
                wavFilesStitched.postValue(true);
                audioMixingRepository.Release();
            }
        });
    }
    
    private static File appendToFileFilename(File originalFile, String stringToAppend)
    {
        if (originalFile == null || !originalFile.exists()) {
            throw new IllegalArgumentException("The file does not exist.");
        }
        
        String filePath = originalFile.getAbsolutePath();
        String parentDir = originalFile.getParent();
        String filename = originalFile.getName();
        
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) {
            // No extension found
            return new File(parentDir, filename + stringToAppend);
        }
        
        // Split the filename into base name and extension
        String baseName = filename.substring(0, dotIndex);
        String extension = filename.substring(dotIndex); // Includes the dot
        
        // Construct the new filename
        String newFilename = baseName + stringToAppend + extension;
        
        return new File(parentDir, newFilename);
    }
    
    public void addBackgroundMusic(MixingProcessListener processingListener)
    {
    
        int currentTextBlockId = currentTextBlockWithData.getValue().textBlock.getId();
        String generatedAudioPath = currentTextBlockWithData.getValue().textBlock.getGeneratedAudioPath();
        String audiobookName = projectMetadata.getValue().project.getOutput_audiobook_path();
    
        // Create the temporary folder if it doesn't exist
        File tmpFolder = mediaStorageRepository.makeFolderInAppStorage(audiobookName);
        File mergedAudioFile = mediaStorageRepository.getAudioFile(tmpFolder.getAbsolutePath(), generatedAudioPath);
        File pageWithAudio = null;
        try
        {
            pageWithAudio = appendToFileFilename(mergedAudioFile, "_bgAudio");
        }
        catch (IllegalArgumentException e)
        {
            Log.e("CharacterLinesViewModel", "finalPageWithAudio: " + e.getMessage());
        }
        File finalPageWithAudio = pageWithAudio;
        audioMixingRepository.addBackgroundMusic(pageWithAudio, mergedAudioFile, mediaStorageRepository.copyResourceToFile(R.raw.enchanted_duel, "enchanted_duel.wav"), 20, new AudioMixer.ProcessingListener()
        {
            @Override
            public void onProgress(double progress)
            {
                //processingListener.onProgress(progress);
            }
    
            @Override
            public void onEnd()
            {
                //processingListener.onEnd();
                
                try
                {
                    mediaStorageRepository.moveFileToMusicDirectory(finalPageWithAudio, "AudiobookCanvas" + File.separator + audiobookName);
                }
                catch (IOException ex)
                {
                    Log.e("CharacterLinesViewModel", "Problem moving file to Music directory: " + ex.getMessage());
                }
                //audioMixingRepository.Release();
            }
        });
    }
    
    
    
    public void recordCharacterLine(String folderName, CharacterLine characterLine, TtsUtteranceListener listener)
    {
        TextBlock currentTextBlock = currentTextBlockWithData.getValue().textBlock;
        String characterVoice = getVoiceByCharacterName(characterLine.getCharacterName());
        
        ttsRepository.speakCharacterLine(characterLine.getLine(), characterVoice, folderName, currentTextBlock.getLineAudioPath(characterLine.getStartIndex()), new TtsUtteranceListener()
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
    
    public void recordNetworkVoiceCharacterLine(String folderName, CharacterLine characterLine, ApiResponseListener listener)
    {
        TextBlock currentTextBlock = currentTextBlockWithData.getValue().textBlock;
        
        String fileName = currentTextBlock.getLineAudioPath(characterLine.getStartIndex());
    
        String[] voices = {
                "alloy", "echo", "fable", "onyx", "nova", "shimmer"
        };
    
        Random random = new Random();
        String randomVoice = voices[random.nextInt(voices.length)];
        
        gptApiRepository.getSpeech(characterLine.getLine(), randomVoice, fileName, new ApiResponseListener()
        {
            @Override
            public void OnResponse(@NonNull Call call, @NonNull Response response)
            {
                
                try
                {
                    listener.OnResponse(call, response);
                    
                    //String appDirectory = mediaStorageRepository.getAppDirectory().getAbsolutePath();
                    //File sampleAudioFile = mediaStorageRepository.getAudioFile(appDirectory + "/New Audiobook", fileName);
    
    
    
                    File exportFolder = new File(getApplication().getExternalFilesDir(null), folderName);
                    if (!exportFolder.exists() && !exportFolder.mkdirs())
                    {
                        Log.v("TTS_REPOSITORY", "Couldn't find or create export folder " + exportFolder);
                    }
                    File sampleAudioFile =  new File(exportFolder, fileName);
                
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
    
                    listener.OnResponse(call, response);
                    
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
    
    public void updateCharacter(String selectedCharacter, int itemIndex, int textblockId)
    {
        databaseRepository.updateCharacter(selectedCharacter, itemIndex, textblockId);
    }
    
    public void setCurrentTextblockDone()
    {
        databaseRepository.setTextBlockStateById(currentTextblockId, BlockState.REVIEWED);
    }
}
