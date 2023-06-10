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
    
    private ArrayList<String> chunkFileData(String fileData)
    {
        ArrayList<String> textChunks = new ArrayList<>();
        StringBuilder text = new StringBuilder();
        // Regex pattern modified to consider your specific quotation marks
        Pattern sentenceBoundaryPattern = Pattern.compile("(“)([^”]*)(”)|[.!?]+\\s*");
        
        int lastIndex = 0;
        boolean insideQuotes = false;
        
        for (int i = 0; i < fileData.length(); i++)
        {
            char c = fileData.charAt(i);
            text.append(c);
            
            // Track if we're inside a dialogue line.
            if (c == dialogueStartChar)
            {
                insideQuotes = true;
            }
            else if (c == dialogueEndChar)
            {
                insideQuotes = false;
            }
            
            Matcher matcher = sentenceBoundaryPattern.matcher(text);
            
            while (matcher.find())
            {
                // If we're inside a dialogue line, ignore sentence boundaries.
                if (insideQuotes) continue;
                
                if (matcher.end() <= maxChunkSize)
                {
                    lastIndex = matcher.end();
                }
            }
            
            // If we have a valid sentence boundary and the current text length exceeds the maximum chunk size,
            // we can cut the chunk at the sentence boundary.
            if (lastIndex > 0 && text.length() > maxChunkSize)
            {
                textChunks.add(text.substring(0, lastIndex).trim());
                text.delete(0, lastIndex);
                lastIndex = 0;
            }
        }
        
        // If there's any leftover text, add it to the chunks.
        if (text.length() > 0)
        {
            textChunks.add(text.toString().trim());
        }
        
        // Sanitize each chunk
        for (int i = 0; i < textChunks.size(); i++)
        {
            textChunks.set(i, sanitiseChunk(textChunks.get(i)));
        }
        
        return textChunks;
    }
    
    private String sanitiseChunk(String chunk) {
        StringBuilder sanitisedChunk = new StringBuilder();
        boolean inDialogue = false;
        boolean justExitedDialogue = false; // Tracks if we have just exited a dialogue line
        
        for (int i = 0; i < chunk.length(); i++) {
            char c = chunk.charAt(i);
            
            if (c == dialogueStartChar) {
                if (i > 0 && (!justExitedDialogue || (justExitedDialogue && chunk.charAt(i-1) != '\n'))) {
                    sanitisedChunk.append("\n");
                }
                sanitisedChunk.append(c);
                inDialogue = true;
                justExitedDialogue = false; // Reset this flag as we're in a dialogue line now
            } else if (c == dialogueEndChar) {
                sanitisedChunk.append(c);
                inDialogue = false;
                justExitedDialogue = true; // We just exited a dialogue line
                
                if (i + 1 < chunk.length()) {
                    char nextChar = chunk.charAt(i + 1);
                    // Add newline if the next character isn't the start of another dialogue line
                    if (nextChar != ' ' && nextChar != dialogueStartChar) {
                        sanitisedChunk.append("\n");
                        justExitedDialogue = false; // We've handled the exit, so reset the flag
                    }
                }
            } else {
                // If we're not in a dialogue line but just exited one, we must be at the start of a narration line
                if (!inDialogue && justExitedDialogue) {
                    sanitisedChunk.append("\n");
                    justExitedDialogue = false; // We've handled the exit, so reset the flag
                }
                sanitisedChunk.append(c);
            }
        }
        
        // If the string ends with a dialogue line, append a newline
        if (justExitedDialogue) {
            sanitisedChunk.append("\n");
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
