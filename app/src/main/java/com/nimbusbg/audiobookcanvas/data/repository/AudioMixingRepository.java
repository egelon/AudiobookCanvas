package com.nimbusbg.audiobookcanvas.data.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

import zeroonezero.android.audio_mixer.AudioMixer;
import zeroonezero.android.audio_mixer.input.AudioInput;
import zeroonezero.android.audio_mixer.input.GeneralAudioInput;

public class AudioMixingRepository
{
    Context context;
    private AudioMixer audioMixer;
    
    public AudioMixingRepository(Application application)
    {
        this.context = application.getApplicationContext();
    }
    
    public void SetOutputPath(String path)
    {
        try
        {
            audioMixer = new AudioMixer(path);
        }
        catch (IOException e)
        {
            Log.e("AudioMixingRepository", "Exception while creating mixer: " + e.getMessage());
        }
    }
    
    public void SetupMixer(int bitrate, int samplerate, int channels, AudioMixer.MixingType mixingType)
    {
        audioMixer.setBitRate(bitrate);
        audioMixer.setSampleRate(samplerate);
        audioMixer.setChannelCount(channels);
    }
    
    public AudioInput CreateInputFromFile(String path)
    {
        AudioInput audioInput = null;
        try
        {
            audioInput = new GeneralAudioInput(path);
        }
        catch (IOException e)
        {
            Log.e("AudioMixingRepository", "Exception while creating audio input: " + e.getMessage());
        }
        return audioInput;
    }
    
    public void AppendAudioInput(AudioInput currentAudio)
    {
        try
        {
            audioMixer.addDataSource(currentAudio);
        }
        catch (IOException e)
        {
            Log.e("AudioMixingRepository", "Exception while appending audio: " + e.getMessage());
        }
    }
    
    public void Start(AudioMixer.ProcessingListener processingListener)
    {
        try
        {
            audioMixer.setProcessingListener(processingListener);
            
            //it is for setting up the all the things
            audioMixer.start();
        
            //starting real processing
            audioMixer.processAsync();
        }
        catch (IOException e)
        {
            Log.e("AudioMixingRepository", "Exception while starting audio mixer: " + e.getMessage());
        }
    }
    
    public void Release()
    {
        audioMixer.release();
    }
    
    
    
    
    public void stitchWavFiles(String audiobookName, List<File> matchingFiles, File outputFile, AudioMixer.ProcessingListener processingListener)
    {
        try
        {
            audioMixer = new AudioMixer(outputFile.getPath());
            
            int i = 0;
            for (File file : matchingFiles)
            {
                AudioInput currentAudio = new GeneralAudioInput(file.getPath());
                if (i == 0)
                {
                    //get the metadata of the first file and use that to stitch the others
                    audioMixer.setBitRate(currentAudio.getBitrate());
                    audioMixer.setSampleRate(currentAudio.getSampleRate());
                    audioMixer.setChannelCount(2); //stereo
                    audioMixer.setMixingType(AudioMixer.MixingType.SEQUENTIAL);
                }
                audioMixer.addDataSource(currentAudio);
                i++;
            }
            
            audioMixer.setProcessingListener(processingListener);
            
            //it is for setting up the all the things
            audioMixer.start();
            
            //starting real processing
            audioMixer.processAsync();
        } catch (IOException ex)
        {
            Log.e("AudioMixingRepository", "Exception while mixing audio: " + ex.getMessage());
        }
        
    }
}
