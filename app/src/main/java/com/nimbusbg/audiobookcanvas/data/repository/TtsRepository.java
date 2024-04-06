package com.nimbusbg.audiobookcanvas.data.repository;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;

import com.nimbusbg.audiobookcanvas.data.listeners.TtsInitListener;
import com.nimbusbg.audiobookcanvas.data.listeners.TtsUtteranceListener;
import com.nimbusbg.audiobookcanvas.data.singletons.TtsSingleton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TtsRepository
{
    Context context;
    List<Voice> allEnglishVoices;
    
    public static final ExecutorService ttsOperationExecutor = Executors.newFixedThreadPool(1);
    
    public TtsRepository(Application application)
    {
        this.context = application.getApplicationContext();
    }
    
    public void initTTS(TtsInitListener listener)
    {
        TtsSingleton.getInstance(context).initTTS(new TtsInitListener()
        {
            @Override
            public void OnInitSuccess()
            {
                allEnglishVoices = TtsSingleton.getInstance(context).getExtendedHQVoicesForEnglish(false);
                listener.OnInitSuccess();
            }
        
            @Override
            public void OnInitFailure()
            {
                allEnglishVoices = new ArrayList<>();
                listener.OnInitFailure();
            }
        });
    }
    
    public ArrayList<String> getExtendedEnglishVoiceNames()
    {
        ArrayList<String> voiceNames = new ArrayList<String>();
        for (Voice voice : allEnglishVoices)
        {
            voiceNames.add(voice.getName());
        }
        return voiceNames;
    }
    
    public String getRandomVoiceName()
    {
        // Create a Random object
        Random rand = new Random();
    
        // Generate a random index within the bounds of the list
        int randomIndex = rand.nextInt(allEnglishVoices.size());
    
        // Return the element at the random index
        return allEnglishVoices.get(randomIndex).getName();
    }
    
    private Voice findVoiceByName(String desiredVoiceName)
    {
        for (Voice voice : allEnglishVoices)
        {
            if (desiredVoiceName.equals(voice.getName()))
            {
                return voice;
            }
        }
        return null; // Voice not found
    }
    
    public void destroyTTS()
    {
        if(TtsSingleton.getInstance(context).isTtsInitialised())
        {
            TtsSingleton.getInstance(context).getTts().stop();
            TtsSingleton.getInstance(context).getTts().shutdown();
        }
    }
    
    private File getAudioFile(String folderName, String fileName)
    {
        File exportFolder = new File(context.getExternalFilesDir(null), folderName);
        if (!exportFolder.exists() && !exportFolder.mkdirs())
        {
            Log.v("TTS_REPOSITORY", "Couldn't find or create export folder " + exportFolder);
            return null;
        }
        return new File(exportFolder, fileName);
    }
    
    public void speakCharacterLine(String characterLine, String voiceName, String folderName, String fileName, TtsUtteranceListener listener)
    {
        String utteranceId = "utterance_" + fileName;
        try
        {
            
            File utteranceFile = getAudioFile(folderName, fileName);
            Voice characterVoice = findVoiceByName(voiceName);
            Bundle params = new Bundle();
    
            // Set desired speech rate and pitch
            TtsSingleton.getInstance(context).getTts().setSpeechRate(1.0f); // Normal speed
            TtsSingleton.getInstance(context).getTts().setPitch(1.0f); // Normal pitch
    
            TtsSingleton.getInstance(context).getTts().setOnUtteranceProgressListener(new UtteranceProgressListener()
            {
                @Override
                public void onStart(String s)
                {
                    listener.OnUtteranceStart(s);
                }
        
                @Override
                public void onDone(String s)
                {
                    listener.OnUtteranceDone(s);
                }
        
                @Override
                public void onError(String s)
                {
                    listener.OnUtteranceError(s);
                }
            });
            TtsSingleton.getInstance(context).getTts().setVoice(characterVoice);
            TtsSingleton.getInstance(context).getTts().synthesizeToFile(characterLine, params, utteranceFile, utteranceId);
        }
        catch (NullPointerException ex)
        {
            Log.e("TTS_REPOSITORY", ex.getMessage());
            Log.e("TTS_REPOSITORY", "can't open audio file " + utteranceId);
        }
    }
    
    public void playVoiceSample(String voiceName, String voiceSampleText)
    {
        // Set desired speech rate and pitch
        TtsSingleton.getInstance(context).getTts().setSpeechRate(1.0f); // Normal speed
        TtsSingleton.getInstance(context).getTts().setPitch(1.0f); // Normal pitch
        TtsSingleton.getInstance(context).getTts().setVoice(findVoiceByName(voiceName));
        TtsSingleton.getInstance(context).getTts().setOnUtteranceProgressListener(new UtteranceProgressListener()
        {
            @Override
            public void onStart(String s)
            {
                Log.d("TTS_REPOSITORY", "playVoiceSample onStart: " + s);
            }
        
            @Override
            public void onDone(String s)
            {
                Log.d("TTS_REPOSITORY", "playVoiceSample onDone: " + s);
            }
        
            @Override
            public void onError(String s)
            {
                Log.d("TTS_REPOSITORY", "playVoiceSample onError: " + s);
            }
        });
        String utteranceId = "utterance_" + voiceName;
        TtsSingleton.getInstance(context).getTts().speak(voiceSampleText, TextToSpeech.QUEUE_FLUSH, new Bundle(), utteranceId);
    }
}
    