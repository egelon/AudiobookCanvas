package com.nimbusbg.audiobookcanvas.data.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.listeners.ApiResponseListener;
import com.nimbusbg.audiobookcanvas.data.network.GptChatMessage;
import com.nimbusbg.audiobookcanvas.data.network.GptChatRequest;
import com.nimbusbg.audiobookcanvas.data.singletons.OkHttpSingleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class GptApiRepository
{
    private String openaiCompletionsEndpoint;
    private String API_key;
    private char dialogueStartChar, dialogueEndChar;
    
    Context appContext;
    
    public GptApiRepository(Application application)
    {
        this.appContext = application.getApplicationContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        this.API_key = preferences.getString("openai_API_key", "");
        if(!API_key.isEmpty())
        {
        
        }
        this.openaiCompletionsEndpoint = application.getString(R.string.openai_completions_endpoint);
    }
    
    public void setDialogueStartChar(char dialogueStartChar) { this.dialogueStartChar = dialogueStartChar; }
    public void setDialogueEndChar(char dialogueEndChar) { this.dialogueEndChar = dialogueEndChar; }
    
    private GptChatRequest createCompletionRequestBody(String[] textLines)
    {
        GptChatRequest requestBody = new GptChatRequest();
        
        // adding params to json object.
        //requestBody.setModel("gpt-3.5-turbo");
    
        requestBody.setModel("ft:gpt-4o-2024-08-06:personal::A3PcsOfF");
        //requestBody.setModel("gpt-4");

        String namedEntityRecognitionPrompt = "You are a Named Entity Recognition web server. Perform Named Entity Recognition on the following text fragment, following these rules. " +
                "This symbol always marks the start of a dialogue line: \"" + dialogueStartChar + "\". " +
                "This symbol always marks the end of a dialogue line: \"" + dialogueEndChar + "\". " +
                "Narration lines never start with the symbol for the start of a dialogue line. " +
                "If a line does not start with the \"" + dialogueStartChar + "\" symbol, this means it is a narration line. " +
                "A Narration line may start with a space. " +
                "Narration lines are always read by the Narrator character. " +
                "They are never read by any other character. " +
                "Always mark narration lines with the Narrator character. " +
                "Dialogue lines are always read by some other character, different from the Narrator. " +
                "If you see a dialogue line, but cannot infer the character's name from the context of the rest of the text, use Unknown as the name of the character. " +
                "Each dialogue or narration story line always starts on a new line. " +
                "Examples: \n" +
                "\\u201CHello!\\u201D - this is a dialogue line, spoken by a character. \n" +
                " he said. - this is a narration line, spoken by the Narrator. \n" +
                "His face was impassive. - this is also a narration line, spoken by the Narrator. \n" +
                "Your response needs to be well-formed JSON. " +
                "The first element is an array, called 'characterLines'. " +
                "Each object from this array contains a 'line' and a 'character' attribute. " +
                "The 'line' attribute contains a copy of the corresponding text line from the input. " +
                "The end of this text line is always marked with the new line symbol. " +
                "Do not change this text line in any way - copy it verbatim from the input! " +
                "Do not split long input lines into multiple character lines! " +
                "The 'character' attribute contains the name of the character, inferred from the text. " +
                "Mark the name of each character at the end of his or her dialogue line. " +
                "There must be exactly as many character lines in your output, as there are lines in the input. " +
                "Each object in the 'characterLines' array contains exactly one 'line' and one 'character' attribute. " +
                "After the 'characterLines' array, there needs to be a 'characters' array. " +
                "There must be exactly as many objects in this array, as the number of unique character names in the 'character' attributes from the 'characterLines' array. " +
                "Each object of the 'characters' array contains 2 attributes - 'character' and 'gender'. " +
                "The 'character' attribute contains the unique name of a character from the 'character' attributes of the 'characterLines' array, except the Narrator. " +
                "The 'gender' contains the inferred gender of that character from the text fragment's context.\n";
    
    
        List<GptChatMessage> messages = new ArrayList<>();
        
        // Create the system message object
        messages.add(new GptChatMessage("system", namedEntityRecognitionPrompt));
        
        String prompt = "[Input]\n";;
        for(int i=0; i<textLines.length; i++)
        {
            prompt = prompt.concat(textLines[i]);
            prompt = prompt.concat("\n");
        }
        prompt = prompt.concat("\n[Output]");
        
        // Create the second message object
        messages.add(new GptChatMessage("user", prompt));

        requestBody.setMessages(messages);
        requestBody.setTemperature(0);
        requestBody.setMax_tokens(1700);
        requestBody.setTop_p(1);
        requestBody.setFrequency_penalty(0.0);
        requestBody.setPresence_penalty(0.0);
        return requestBody;
    }
    
    public void getCompletion(String[] textLines, String tag, ApiResponseListener responseListener)
    {
        GptChatRequest requestBody =  createCompletionRequestBody(textLines);
        Log.d("GptApiRepository", "tag: " + tag + "\nrequestBody:\n" + requestBody.toString());
        enqueueRequest(requestBody, responseListener);
    }
    
    private void enqueueRequest(GptChatRequest jsonRequestBody, ApiResponseListener responseListener)
    {
        Gson gson = new Gson();
        Request request = OkHttpSingleton.getInstance(appContext).createRequest(openaiCompletionsEndpoint, gson.toJson(jsonRequestBody), API_key);
        OkHttpSingleton.getInstance(appContext).getClient().newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e)
            {
                responseListener.OnError(call, e);
            }
    
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response)
            {
                responseListener.OnResponse(call, response);
            }
        });
    }

    public void stopQueuedRequests()
    {
        OkHttpSingleton.getInstance(appContext).cancelAllRequests();
    }
}
