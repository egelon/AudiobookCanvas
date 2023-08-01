package com.nimbusbg.audiobookcanvas.data.repository;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;

import com.nimbusbg.audiobookcanvas.data.listeners.TtsInitListener;
import com.nimbusbg.audiobookcanvas.data.listeners.TtsUtteranceListener;
import com.nimbusbg.audiobookcanvas.data.singletons.TtsSingleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TtsRepository
{
    Context context;
    List<Voice> currentLocaleVoices;
    
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
                currentLocaleVoices = TtsSingleton.getInstance(context).getHighQualityVoicesForCurrentLocale();
                listener.OnInitSuccess();
            }
        
            @Override
            public void OnInitFailure()
            {
                currentLocaleVoices = new ArrayList<>();
                listener.OnInitFailure();
            }
        });
    }
    
    public ArrayList<String> getVoiceNamesForCurrentLocale()
    {
        ArrayList<String> voiceNamesForLocale = new ArrayList<String>();
        for (Voice voice : currentLocaleVoices)
        {
            voiceNamesForLocale.add(voice.getName());
        }
        return voiceNamesForLocale;
    }
    
    public String getRandomVoiceName()
    {
        // Create a Random object
        Random rand = new Random();
    
        // Generate a random index within the bounds of the list
        int randomIndex = rand.nextInt(currentLocaleVoices.size());
    
        // Return the element at the random index
        return currentLocaleVoices.get(randomIndex).getName();
    }
    
    private Voice findVoiceByName(String desiredVoiceName)
    {
        for (Voice voice : currentLocaleVoices)
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
    
    private File getAudioFile(String fileName)
    {
        File exportFolder = new File(context.getExternalFilesDir(null), "tmp");
        if (!exportFolder.exists() && !exportFolder.mkdirs())
        {
            Log.v("TTS_REPOSITORY", "Couldn't find or create export folder " + exportFolder);
            return null;
        }
        return new File(exportFolder, fileName);
    }
    
    public void speakCharacterLine(String characterLine, String voiceName, String fileName, TtsUtteranceListener listener)
    {
        String utteranceId = "utterance_" + fileName;
        try
        {
            
            File file = getAudioFile(fileName);
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
            TtsSingleton.getInstance(context).getTts().synthesizeToFile(characterLine, params, file, utteranceId);
        }
        catch (NullPointerException ex)
        {
            Log.e("TTS_REPOSITORY", ex.getMessage());
            Log.e("TTS_REPOSITORY", "can't open audio file " + utteranceId);
        }
    }
    
    public void stitchWavFiles(int id, String outputFileName)
    {
        // Get the directory path of the app's default storage
        File appDirectory = context.getExternalFilesDir(null);
        
        // Create the temporary folder path
        String tmpFolderPath = appDirectory.getAbsolutePath() + File.separator + "tmp";
        
        // Create the temporary folder if it doesn't exist
        File tmpFolder = new File(tmpFolderPath);
        if (!tmpFolder.exists())
        {
            tmpFolder.mkdirs();
        }
        
        // List to store the matching WAV files
        List<File> matchingFiles = new ArrayList<>();
        
        // Find all WAV files that contain the ID in their names
        File[] files = tmpFolder.listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                if (file.isFile() && file.getName().contains(String.valueOf(id)) && file.getName().endsWith(".wav"))
                {
                    matchingFiles.add(file);
                }
            }
        }
        
        // Sort the matching files based on the number in their names
        Collections.sort(matchingFiles, new Comparator<File>()
        {
            @Override
            public int compare(File file1, File file2)
            {
                int number1 = extractNumber(file1.getName());
                int number2 = extractNumber(file2.getName());
                return Integer.compare(number1, number2);
            }
        });
        
        // Create a new output file for the stitched audio
        File outputFile = new File(tmpFolder, outputFileName);
    
        // Calculate the total audio length in bytes
        long totalAudioLength = 0;
        for (File file : matchingFiles)
        {
            totalAudioLength += file.length() - 44; // Exclude the header size
        }
    
        // Stitch the audio from the matching files
        try (FileOutputStream fos = new FileOutputStream(outputFile))
        {
            // Write the WAV file header for the output file
            writeWavFileHeader(fos, totalAudioLength);
        
            // Write the audio data from each file to the output file
            for (File file : matchingFiles)
            {
                try (FileInputStream fis = new FileInputStream(file))
                {
                    // Skip the header of each input file
                    fis.skip(44);
                
                    // Write the audio data to the output file
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1)
                    {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
            }
        
            // Update the file size information in the WAV file header
            updateWavFileSize(outputFile, totalAudioLength);
        
            // Stitching complete, the output file is saved
            fos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            // Error occurred during stitching or file operations
        }
    }
    
    // Helper method to extract the number from the file name
    private int extractNumber(String fileName)
    {
        String numberString = fileName.replaceAll("[^0-9]", "");
        return Integer.parseInt(numberString);
    }
    
    // Helper method to update the file size information in the WAV file header
    private void updateWavFileSize(File file, long totalAudioLength) throws IOException
    {
        RandomAccessFile wavFile = new RandomAccessFile(file, "rw");
        try
        {
            // Seek to the position where the file size is stored in the WAV file header
            wavFile.seek(4);
            
            // Update the file size value (ChunkSize - 8)
            int fileSize = (int) (totalAudioLength + 36);
            wavFile.write(intToBytesLittleEndian(fileSize));
            
            // Update the data size value (Subchunk2Size)
            int dataSize = (int) totalAudioLength;
            wavFile.seek(40);
            wavFile.write(intToBytesLittleEndian(dataSize));
        }
        finally
        {
            wavFile.close();
        }
    }
    
    // Helper method to write the WAV file header
    private void writeWavFileHeader(OutputStream outputStream, long totalAudioLength) throws IOException
    {
        short NumChannels = 1; // Mono
        int SampleRate = 24000; // SampleRate
        short BitsPerSample = 16; // BitsPerSample
    
        outputStream.write(new byte[] {'R', 'I', 'F', 'F'}); // ChunkID
        outputStream.write(intToBytesLittleEndian((int) (36 + totalAudioLength))); // ChunkSize
        outputStream.write(new byte[] {'W', 'A', 'V', 'E'}); // Format
    
        outputStream.write(new byte[] {'f', 'm', 't', ' '}); // Subchunk1ID
        outputStream.write(intToBytesLittleEndian(16)); // Subchunk1Size
        outputStream.write(shortToBytesLittleEndian((short) 1)); // AudioFormat
        outputStream.write(shortToBytesLittleEndian(NumChannels)); // NumChannels
        outputStream.write(intToBytesLittleEndian(SampleRate)); // SampleRate
        outputStream.write(intToBytesLittleEndian((SampleRate * NumChannels * BitsPerSample) / 8)); // ByteRate
        outputStream.write(shortToBytesLittleEndian((short) (NumChannels * BitsPerSample / 8))); // BlockAlign
        outputStream.write(shortToBytesLittleEndian(BitsPerSample)); // BitsPerSample
    
        outputStream.write(new byte[] {'d', 'a', 't', 'a'}); // Subchunk2ID
        outputStream.write(intToBytesLittleEndian((int) totalAudioLength)); // Subchunk2Size
    }
    
    // Helper method to convert a long to a little-endian byte array
    private byte[] longToBytesLittleEndian(long value)
    {
        return new byte[] {
                (byte) (value & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 24) & 0xFF),
                (byte) ((value >> 32) & 0xFF),
                (byte) ((value >> 40) & 0xFF),
                (byte) ((value >> 48) & 0xFF),
                (byte) ((value >> 56) & 0xFF)
        };
    }
    
    // Helper method to convert an integer to a little-endian byte array
    private byte[] intToBytesLittleEndian(int value)
    {
        return new byte[] {
                (byte) (value & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 24) & 0xFF)
        };
    }
    
    // Helper method to convert a short to a little-endian byte array
    private byte[] shortToBytesLittleEndian(short value)
    {
        return new byte[]{
                (byte) (value & 0xFF),
                (byte) ((value >> 8) & 0xFF)
        };
    }
    
}
