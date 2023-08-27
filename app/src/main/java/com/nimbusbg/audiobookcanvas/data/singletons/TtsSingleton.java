package com.nimbusbg.audiobookcanvas.data.singletons;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import com.nimbusbg.audiobookcanvas.data.listeners.TtsInitListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TtsSingleton
{
    private static TtsSingleton instance;
    Context context;
    private static TextToSpeech tts;
    private boolean isTtsInitialised;
    
    private TtsSingleton(Context context)
    {
        isTtsInitialised = false;
        this.context = context;
    }
    
    public static synchronized TtsSingleton getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new TtsSingleton(context);
        }
        return instance;
    }
    
    public void initTTS(TtsInitListener listener)
    {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener()
        {
            @Override
            public void onInit(int status)
            {
                if (status == TextToSpeech.SUCCESS)
                {
                    isTtsInitialised = true;
                    listener.OnInitSuccess();
                    Log.d("TTS", "TTS initialised");
    
                    List<TextToSpeech.EngineInfo> engineInfo = tts.getEngines();
                    for (TextToSpeech.EngineInfo info : engineInfo) {
                        Log.d("TTS","info: "+info);
                    }
                    
                    
                }
                else
                {
                    isTtsInitialised = false;
                    listener.OnInitFailure();
                    Log.e("TTS", "TTS initialization failed");
                }
            }
        });
    }
    
    public TextToSpeech getTts()
    {
        if (tts == null || !isTtsInitialised)
        {
            throw new IllegalStateException("TextToSpeech not initialized yet");
        }
        return tts;
    }
    
    ArrayList<Voice> getVoicesForCurrentLocale()
    {
        ArrayList<Voice> voicesForLocale = new ArrayList<>();
        for (Voice voice : getTts().getVoices())
        {
            if (voice.getLocale().equals(Locale.getDefault()))
            {
                voicesForLocale.add(voice);
            }
        }
        return voicesForLocale;
    }
    
    public ArrayList<Voice> getVoicesForLocale(Locale locale, int quality, boolean isNetworkRequired)
    {
        ArrayList<Voice> voicesForLocale = new ArrayList<>();
        for (Voice voice : getTts().getVoices())
        {
            if (voice.getLocale().equals(locale) &&
                voice.getQuality() >= quality &&
                voice.isNetworkConnectionRequired() == isNetworkRequired)
            {
                voicesForLocale.add(voice);
            }
        }
        return voicesForLocale;
    }
    
    public ArrayList<Voice> getExtendedHQVoicesForEnglish(boolean isNetworkRequired)
    {
        ArrayList<Voice> highQualityVoices = new ArrayList<>();
        highQualityVoices.addAll(getVoicesForLocale(Locale.US, Voice.QUALITY_HIGH, isNetworkRequired));
        highQualityVoices.addAll(getVoicesForLocale(Locale.UK, Voice.QUALITY_HIGH, isNetworkRequired));
        highQualityVoices.addAll(getVoicesForLocale(Locale.CANADA, Voice.QUALITY_HIGH, isNetworkRequired));
        highQualityVoices.addAll(getVoicesForLocale(Locale.ENGLISH, Voice.QUALITY_HIGH, isNetworkRequired));
        return highQualityVoices;
    }
    
    public boolean isTtsInitialised()
    {
        return isTtsInitialised;
    }
}
