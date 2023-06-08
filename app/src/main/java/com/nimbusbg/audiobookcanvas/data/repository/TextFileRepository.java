package com.nimbusbg.audiobookcanvas.data.repository;

import android.app.Application;
import android.content.ContentResolver;

import android.net.Uri;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFileRepository
{
    
    ContentResolver contentResolver;
    ArrayList<String> textChunks;
    private int maxChunkSize;
    
    private char dialogueStartChar, dialogueEndChar;
    
    public static final ExecutorService fileOperationExecutor = Executors.newFixedThreadPool(1);
    
    public TextFileRepository(Application application)
    {
        this.contentResolver = application.getApplicationContext().getContentResolver();
        maxChunkSize = 1500;
    }
    
    public void setDialogueStartChar(char dialogueStartChar)
    {
        this.dialogueStartChar = dialogueStartChar;
    }
    
    public void setDialogueEndChar(char dialogueEndChar)
    {
        this.dialogueEndChar = dialogueEndChar;
    }
    
    public int getMaxChunkSize()
    {
        return maxChunkSize;
    }
    
    public void setMaxChunkSize(int maxChunkSize)
    {
        this.maxChunkSize = maxChunkSize;
    }
    
    private String readFileContent(Uri uri) throws IOException {
        InputStream inputStream = contentResolver.openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();
        inputStream.close();
        return stringBuilder.toString();
    }
    
    private ArrayList<String> chunkFileData(String fileData) {
        textChunks = new ArrayList<String>();
        StringBuilder text = new StringBuilder();
        Pattern sentenceBoundaryPattern = Pattern.compile("[.!?]+\\s*");
    
        for (int i = 0; i < fileData.length(); i++)
        {
            text.append(fileData.charAt(i));
            
            if (text.length() >= maxChunkSize)
            {
                Matcher matcher = sentenceBoundaryPattern.matcher(text);
                int lastIndex = 0;
                
                while (matcher.find())
                {
                    if (matcher.start() >= maxChunkSize)
                    {
                        break;
                    }
                    lastIndex = matcher.end();
                }
                
                if (lastIndex > 0)
                {
                    textChunks.add(text.substring(0, lastIndex));
                    text.delete(0, lastIndex);
                }
            }
        }
    
        if (text.length() > 0)
        {
            textChunks.add(text.toString());
        }
        
        for(int i = 0; i < textChunks.size(); i++)
        {
            textChunks.set(i, sanitiseChunk(textChunks.get(i)));
        }
    
        return textChunks;
    }
    
    private String sanitiseChunk(String chunk)
    {
        StringBuilder sanitisedChunk = new StringBuilder();
        boolean inDialogue = false;
    
        for (char c : chunk.toCharArray()) {
            sanitisedChunk.append(c);
            if (c == dialogueStartChar)
            {
                inDialogue = true;
            }
            else if (c == dialogueEndChar)
            {
                if (inDialogue)
                {
                    sanitisedChunk.append("\\n");
                    inDialogue = false;
                }
            }
        }
        
        return sanitisedChunk.toString();
    }
    
    public void GetSanitisedChunks(Uri uri, FIleOperationListener listener)
    {
        fileOperationExecutor.execute(() -> {
            String fileContents;
            try {
                fileContents = readFileContent(uri);
                listener.OnFileLoaded(fileContents);
                textChunks = chunkFileData(fileContents);
                listener.OnFileChunked(textChunks);
            } catch (IOException e) {
                Log.e("Error", e.getMessage());
            }
        });
    }
}
