package com.nimbusbg.audiobookcanvas.data.repository;

import android.app.Application;
import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.nimbusbg.audiobookcanvas.data.listeners.FileOperationListener;
import com.nimbusbg.audiobookcanvas.data.listeners.FileSampleListener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextFileRepository
{
    private volatile boolean isCancelled = false;
    ContentResolver contentResolver;
    ArrayList<String> textChunks;
    private int maxChunkSize;
    
    private char dialogueStartChar, dialogueEndChar;
    
    public static final ExecutorService fileOperationExecutor = Executors.newFixedThreadPool(1);
    
    public TextFileRepository(Application application)
    {
        this.contentResolver = application.getApplicationContext().getContentResolver();
        maxChunkSize = 2500;
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
    
    private String readFileContent(Uri uri) throws IOException
    {
        InputStream inputStream = contentResolver.openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
        {
            stringBuilder.append(line);
        }
        reader.close();
        inputStream.close();
        return stringBuilder.toString();
    }
    
    
    private String readFile(Uri uri, Charset encoding) throws IOException
    {
        //byte[] encoded = Files.readAllBytes(Paths.get(uri.getPath()));
        //return new String(encoded, encoding);
    
        InputStream inputStream = contentResolver.openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int byteRead;
    
        // Read the file byte by byte
        while ((byteRead = inputStream.read()) != -1) {
            byteBuffer.write(byteRead);
        }
    
        inputStream.close();
    
        // Convert byte array to string using UTF-8 encoding
        byte[] byteArray = byteBuffer.toByteArray();
        return new String(byteArray, encoding); // Manually specify UTF-8 encoding
    }
    
    // Function to process text and extract direct speech and narration
    public ArrayList<String> processText(String text, char dialogueStartChar, char dialogueEndChar)
    {
        ArrayList<String> segments = new ArrayList<>();
        
        if(dialogueStartChar != dialogueEndChar)
        {
            //languages such as English, French, etc.
            // Build the regex using the provided start and end dialogue characters
            String speechRegex = Pattern.quote(String.valueOf(dialogueStartChar)) + "(.*?)" + Pattern.quote(String.valueOf(dialogueEndChar));
            Pattern speechPattern = Pattern.compile(speechRegex, Pattern.DOTALL);
            Matcher matcher = speechPattern.matcher(text);
            
            int lastIndex = 0;
            
            // Iterate through matches of direct speech
            while (matcher.find())
            {
                // Extract narration before direct speech
                if (matcher.start() > lastIndex)
                {
                    String narrationSegment = cleanText(text.substring(lastIndex, matcher.start()));
                    if (!narrationSegment.isEmpty())
                    {
                        segments.add(narrationSegment);
                    }
                }
                
                // Extract the direct speech
                String speechSegment = cleanText(matcher.group());
                if (!speechSegment.isEmpty())
                {
                    segments.add(speechSegment);
                }
                
                lastIndex = matcher.end();
            }
            
            // Extract any narration after the last speech segment
            if (lastIndex < text.length())
            {
                String narrationSegment = cleanText(text.substring(lastIndex));
                if (!narrationSegment.isEmpty())
                {
                    segments.add(narrationSegment);
                }
            }
        }
        else
        {
            //languages such as Bulgarian
            // Split the text into paragraphs based on newlines and excessive whitespace
            String[] paragraphs = text.split("\\r?\\n\\s*");
            
            for (String paragraph : paragraphs)
            {
                // Clean leading spaces, tabs, or other artifacts
                paragraph = cleanText(paragraph);
                
                // Check if the paragraph contains the dialogue marker at the start (which means it's speech)
                //we use dialogueStartChar as the dialogue and narration marker
                String dialogueMarker = String.valueOf(dialogueStartChar);
                if (paragraph.startsWith(dialogueMarker))
                {
                    processSpeechAndNarration(paragraph, dialogueMarker, segments);
                }
                else
                {
                    // If it doesn't start with the dialogue marker, it's narration
                    segments.add(cleanText(paragraph));
                }
            }
        }
        
        return segments;
    }
    
    // Function to process speech and narration within a paragraph
    private void processSpeechAndNarration(String paragraph, String dialogueMarker, ArrayList<String> segments)
    {
        // Pattern to find any occurrences of the dialogue marker (e.g., an em dash)
        String markerRegex = Pattern.quote(dialogueMarker);
        Pattern markerPattern = Pattern.compile(markerRegex);
        Matcher matcher = markerPattern.matcher(paragraph);
        
        int lastIndex = 0;
        boolean isSpeech = false;
        
        // Iterate through each occurrence of the dialogue marker
        while (matcher.find())
        {
            // Include everything between the last marker and the current marker in the correct segment
            String segment = paragraph.substring(lastIndex, matcher.start()).trim();
            if (!segment.isEmpty())
            {
                if (isSpeech)
                {
                    // Add the marker to speech segment
                    segments.add(dialogueMarker + " " + segment);
                }
                else
                {
                    // Add the narration segment without the marker
                    segments.add(segment);
                }
            }
            isSpeech = !isSpeech;  // Switch between speech and narration
            lastIndex = matcher.end();
        }
        
        // Process the last segment after the final marker
        if (lastIndex < paragraph.length())
        {
            String lastSegment = paragraph.substring(lastIndex).trim();
            if (!lastSegment.isEmpty())
            {
                if (isSpeech)
                {
                    segments.add(dialogueMarker + " " + lastSegment);  // Speech with marker
                }
                else
                {
                    segments.add(lastSegment);  // Narration without marker
                }
            }
        }
    }
    
    // Function to clean text from unwanted OCR artifacts such as extra spaces, tabs, etc.
    private String cleanText(String text)
    {
        // Replace multiple spaces or tabs with a single space
        return text.replaceAll("\\s+", " ").trim();
    }
    
    
    
    
    
    private String getTextStartSample(Uri uri, int sampleLength) throws IOException
    {
        InputStream inputStream = contentResolver.openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        int totalCharsRead = 0;
        char[] buffer = new char[500]; // Buffer size can be adjusted
        int charsRead;
    
        while ((charsRead = reader.read(buffer)) != -1)
        {
            if (totalCharsRead + charsRead > sampleLength)
            {
                int remainingChars = sampleLength - totalCharsRead;
                stringBuilder.append(buffer, 0, remainingChars);
                break;
            }
            else
            {
                stringBuilder.append(buffer, 0, charsRead);
                totalCharsRead += charsRead;
            }
        }
    
        reader.close();
        inputStream.close();
        return stringBuilder.toString();
    }
    
    public void getTextStartSample(Uri uri, FileSampleListener listener)
    {
        fileOperationExecutor.execute(() -> {
            String textSample;
            try {
                textSample = getTextStartSample(uri, maxChunkSize);
                listener.OnSampleLoaded(textSample);
            } catch (IOException e) {
                Log.e("Error", e.getMessage());
            }
        });
    }
    
    
    private ArrayList<String> chunkFileData(String fileData) {
        ArrayList<String> textChunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder();  // Holds the current chunk being built
        boolean inDialogue = false;  // Track whether we're inside a direct speech segment
        boolean dialogueBoundaryIsSame = dialogueStartChar == dialogueEndChar;  // Check if start and end markers are the same
        int lastSentenceBoundary = -1;  // Track the last sentence boundary in narration
        
        for (int i = 0; i < fileData.length(); i++) {
            char currentChar = fileData.charAt(i);
            
            if(currentChar == dialogueEndChar)
            {
                inDialogue = true;
            }
            
            if (inDialogue) {
                // We are inside a direct speech segment
                currentChunk.append(currentChar);
                
                // Check for the end of the dialogue segment
                if (currentChar == dialogueEndChar || (dialogueBoundaryIsSame && currentChar == '\n')) {
                    inDialogue = false;  // Exit dialogue segment
                }
                
                // If this chunk exceeds maxChunkSize, move the entire dialogue to the next chunk
                if (currentChunk.length() > maxChunkSize) {
                    moveToNextChunk(textChunks, currentChunk);
                }
            } else {
                // We are inside a narration segment
                currentChunk.append(currentChar);
                
                // Track sentence boundaries (e.g., ".", "!", "?")
                if (".!?".indexOf(currentChar) >= 0) {
                    lastSentenceBoundary = currentChunk.length();
                }
                
                // Check for the start of a dialogue segment
                if (currentChar == dialogueStartChar) {
                    // If we are about to enter a dialogue segment, we check the chunk size
                    if (currentChunk.length() > maxChunkSize) {
                        // If the current chunk exceeds the max size, split it at the last sentence boundary
                        if (lastSentenceBoundary > 0) {
                            textChunks.add(currentChunk.substring(0, lastSentenceBoundary).trim());
                            currentChunk.delete(0, lastSentenceBoundary);  // Remove the text that was added to the chunk
                        }
                    }
                    
                    // Now start the new dialogue segment
                    inDialogue = true;
                    currentChunk.append(currentChar);  // Add the dialogueStartChar if not already added
                }
                
                // If the chunk exceeds maxChunkSize and we are in narration, split at the last sentence boundary
                if (currentChunk.length() > maxChunkSize && lastSentenceBoundary > 0) {
                    textChunks.add(currentChunk.substring(0, lastSentenceBoundary).trim());
                    currentChunk.delete(0, lastSentenceBoundary);  // Remove the text that was added to the chunk
                    lastSentenceBoundary = -1;  // Reset the sentence boundary
                }
            }
        }
        
        // Add any remaining text in the buffer to the chunks
        if (currentChunk.length() > 0) {
            textChunks.add(currentChunk.toString().trim());
        }
        
        return textChunks;
    }
    
    private void moveToNextChunk(ArrayList<String> textChunks, StringBuilder currentChunk) {
        // Move the entire content of currentChunk to the next chunk
        String chunk = currentChunk.toString().trim();
        textChunks.add(sanitiseChunk(chunk));  // Add the entire dialogue as a separate chunk
        currentChunk.setLength(0);  // Clear the current chunk
    }
    
    public String sanitiseChunk(String chunk) {
        StringBuilder sanitisedChunk = new StringBuilder();
        boolean insideDialogue = false; // To track if we are inside a dialogue segment
        boolean enteredDialogue = false;
        boolean insideDialogueParagraph = false;
        for (int i = 0; i < chunk.length(); i++)
        {
            char currentChar = chunk.charAt(i);
            
    
            //We are entering a dialogue segment
            if (currentChar == dialogueStartChar && !insideDialogue)
            {
                // Check if the em dash is preceded by a new line (i.e., the start of dialogue)
                if (i > 1 && (chunk.charAt(i - 1) == '\n'))
                {
                    insideDialogue = true;
                    enteredDialogue = true;
                    insideDialogueParagraph = true;
                }
            }
    
            if (insideDialogue)
            {
                //we are inside a dialogue segment
                sanitisedChunk.append(currentChar);
    
                if (dialogueStartChar == dialogueEndChar)
                {
                    //DialogueStartChar and DialogueEndChar are the same (for certain languages, such as Bulgarian). This marks the end of this dialogue segment.
                    if(currentChar == '\n')
                    {
                        insideDialogue = false;
                    }
                    if (currentChar == dialogueEndChar)
                    {
                        if(enteredDialogue)
                        {
                            //DialogueStartChar and DialogueEndChar are the same, we are seeing one of them, and we just entered dialogue
                            enteredDialogue = false;
                        }
                        else
                        {
                            // We are exiting a dialogue segment
                            sanitisedChunk.append("\n");
                            insideDialogue = false;
                        }
                    }
                }
            }
            else
            {
                //We are inside a narration
                sanitisedChunk.append(currentChar);
        
                //And we've hit a sentence boundary (".", "!", "?")
                if (currentChar == '.' || currentChar == '!' || currentChar == '?' || currentChar == '…')
                {
                    // Check if the next character is a newline. If so, avoid adding an extra newline.
                    if (i + 2 < chunk.length() && chunk.charAt(i + 1) != '\r' && chunk.charAt(i + 2) != '\n')
                    {
                        sanitisedChunk.append("\n");
                    }
                }
            }
        }

        // Remove leading whitespace from each line and remove empty lines
        String[] lines = sanitisedChunk.toString().split("\n");
        for (int i = 0; i < lines.length; i++)
        {
            lines[i] = lines[i].stripLeading();
        }
    
        return Arrays.stream(lines)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.joining("\n"));
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
                fileContents = readFile(uri, Charset.forName("UTF-8"));
                listener.OnFileLoaded(fileContents);
    
    
                ArrayList<String> segments = processText(fileContents, dialogueStartChar, dialogueEndChar);
                StringBuilder stringBuilder = new StringBuilder();
                textChunks = new ArrayList<String>();
                int currentChunkSize = 0;
                for (String s : segments)
                {
                    if(currentChunkSize < maxChunkSize)
                    {
                        currentChunkSize += s.length() + 1;
                        stringBuilder.append(s).append("\n");
                    }
                    else
                    {
                        // Remove the last unnecessary newline character if needed
                        if (stringBuilder.length() > 0)
                        {
                            stringBuilder.setLength(stringBuilder.length() - 1); // Removes the last '\n'
                        }
                        textChunks.add(stringBuilder.toString());
                        stringBuilder.delete(0, stringBuilder.length());
                        currentChunkSize = 0;
                    }
                }
                textChunks.add(stringBuilder.toString());
                
                
                /*
                textChunks = chunkFileData(sanitiseFileContents(fileContents));
                for (int i=0; i < textChunks.size(); i++)
                {
                    ArrayList<String> segments = processText(textChunks.get(i), dialogueStartChar, dialogueEndChar);
    
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String s : segments) {
                        stringBuilder.append(s).append("\n");
                    }

                    // Remove the last unnecessary newline character if needed
                    if (stringBuilder.length() > 0) {
                        stringBuilder.setLength(stringBuilder.length() - 1); // Removes the last '\n'
                    }
    
                    String sanitisedChunk = stringBuilder.toString();
                    textChunks.set(i, sanitisedChunk);
                }
                */
                
                if(isCancelled)
                {
                    listener.OnChunkingStopped();
                }
                else
                {
                    listener.OnFileChunked(textChunks);
                }
                isCancelled = false;
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
    
    public void StopChunking()
    {
        isCancelled = true;
        //fileOperationExecutor.shutdown();
    }
}
