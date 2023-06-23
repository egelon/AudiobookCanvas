package com.nimbusbg.audiobookcanvas.data.repository;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TtsRepository
{
    Context context;
    private TextToSpeech tts;
    
    public static final ExecutorService ttsOperationExecutor = Executors.newFixedThreadPool(1);
    
    public TtsRepository(Application application)
    {
        this.context = application.getApplicationContext();
    }
    
    public void initTTS(TtsListener listener)
    {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener()
        {
            @Override
            public void onInit(int status)
            {
                if (status == TextToSpeech.SUCCESS)
                {
                    listener.OnInitSuccess();
                }
                else
                {
                    listener.OnInitFailure();
                }
            }
        });
    }
    
    public ArrayList<String> getVoicesForLocale(String language, String country)
    {
        Locale locale = new Locale(language, country);  // specify your locale
        Set<Voice> voices = tts.getVoices();
        ArrayList<String> voicesForLocale = new ArrayList<String>();
        for (Voice voice : voices)
        {
            if (voice.getLocale().equals(locale))
            {
                voicesForLocale.add(voice.getName());
            }
        }
        return voicesForLocale;
    }
    
    public void destroyTTS()
    {
        if (tts != null)
        {
            tts.stop();
            tts.shutdown();
        }
    }
    
    private File getAudioFile(String fileName)
    {
        File exportFolder = new File(context.getFilesDir(), "tmp");
        if (!exportFolder.exists() && !exportFolder.mkdirs()) {
            Log.v("TTS_REPOSITORY", "Couldn't find or create export folder " + exportFolder);
            return null;
        }
        return new File(exportFolder, fileName);
    }
    
    public void speakCharacterLine(String characterLine, String fileName, TtsListener listener)
    {
        String utteranceId = "utteranceId";
        File file = getAudioFile(fileName);
        Bundle params = new Bundle();
    
        // Set desired speech rate and pitch
        tts.setSpeechRate(1.0f); // Normal speed
        tts.setPitch(1.0f); // Normal pitch
        
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener()
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

        tts.synthesizeToFile(characterLine, params, file, utteranceId);
    }
    
}
