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
        maxChunkSize = 2000;
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
        Pattern sentenceBoundaryPattern = Pattern.compile("(" + dialogueStartChar +")([^" + dialogueEndChar + "]*)(" + dialogueEndChar + ")|[.!?]+\\s*");
        
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
                String finalChunk = text.substring(0, lastIndex).trim();
                String sanitisedFinalChunk = sanitiseChunk(finalChunk);
                textChunks.add(sanitisedFinalChunk);
                text.delete(0, lastIndex);
                lastIndex = 0;
            }
        }
        
        // If there's any leftover text, add it to the chunks.
        if (text.length() > 0)
        {
            String finalChunk = text.toString().trim();
            String sanitisedFinalChunk = sanitiseChunk(finalChunk);
            textChunks.add(sanitisedFinalChunk);
        }
        
        return textChunks;
    }
    
    private String sanitiseChunk2(String chunk) {
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
    
    private String sanitiseChunk(String chunk) {
        StringBuilder sanitisedChunk = new StringBuilder();
        boolean inDialogue = false;
        boolean justExitedDialogue = false; // Tracks if we have just exited a dialogue line
        
        for (int i = 0; i < chunk.length(); i++) {
            char c = chunk.charAt(i);
            
            if (c == dialogueStartChar) {
                // Check if the character following the dialogue start character is a capital letter
                if (i + 1 < chunk.length() && Character.isUpperCase(chunk.charAt(i + 1))) {
                    if (i > 0 && (!justExitedDialogue || (justExitedDialogue && chunk.charAt(i-1) != '\n'))) {
                        sanitisedChunk.append("\n");
                    }
                    sanitisedChunk.append(c);
                    inDialogue = true;
                    justExitedDialogue = false; // Reset this flag as we're in a dialogue line now
                } else {
                    sanitisedChunk.append(c); // Treat as narration line
                }
            } else if (c == dialogueEndChar) {
                sanitisedChunk.append(c);
                if (inDialogue) {
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
    
    private String sanitiseFileContents(String input)
    {
        // Replace each special character sequence or character with a space
        String output = input.replace("\r\n", " ");
        output = output.replace("\n", " ");
        output = output.replace("\r", " ");
        output = output.replace("\t", " ");
    
        // Replace a period followed by a non-space character with a period, a space, and the character
        output = output.replaceAll("(\\.)(\\S)", "$1 $2");
        
        return output;
    }
 
    
    
    public void GetSanitisedChunks(Uri uri, FileOperationListener listener)
    {
        fileOperationExecutor.execute(() -> {
            String fileContents;
            try {
                fileContents = readFileContent(uri);
                listener.OnFileLoaded(fileContents);
                textChunks = chunkFileData(sanitiseFileContents(fileContents));
                listener.OnFileChunked(textChunks);
            } catch (IOException e) {
                Log.e("Error", e.getMessage());
            }
        });
    }
    
    public char getDialogueStartChar()
    {
        return dialogueStartChar;
    }
    
    public char getDialogueEndChar()
    {
        return dialogueEndChar;
    }
}
