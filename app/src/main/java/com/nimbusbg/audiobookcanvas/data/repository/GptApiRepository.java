package com.nimbusbg.audiobookcanvas.data.repository;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.nimbusbg.audiobookcanvas.R;
import com.nimbusbg.audiobookcanvas.data.network.GptChatMessage;
import com.nimbusbg.audiobookcanvas.data.network.GptChatRequest;
import com.nimbusbg.audiobookcanvas.data.network.OkHttpSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GptApiRepository
{
    private String openaiCompletionsEndpoint;
    private String API_key;
    private int apiResponseTimeoutMs = 50000;
    private char dialogueStartChar, dialogueEndChar;
    
    Context appContext;
    
    public GptApiRepository(Application application)
    {
        this.appContext = application.getApplicationContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
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
        //requestBody.put("model", "text-davinci-003");
        requestBody.setModel("gpt-3.5-turbo");
    
        String namedEntityRecognitionPrompt = "Perform Named Entity Recognition on the following text fragment, following these rules:\n" +
                "This symbol always marks the start of a dialogue line: \"" + dialogueStartChar + "\".\n" +
                "This symbol always marks the end of a dialogue line: \"" + dialogueEndChar + "\".\n" +
                "Narration lines never start with the symbol for the start of a dialogue line. If a line does not start with the \"" + dialogueStartChar + "\" symbol, this means it is a narration line. A Narration line may start with a space. Narration lines are always read by the Narrator character. They are never read by any other character. Always mark narration lines with the Narrator character. \n" +
                "Dialogue lines are always read by some other character, different from the Narrator. If you see a dialogue line, but cannot infer the character's name from the context of the rest of the text, use Unknown as the name of the character.\n" +
                "Each dialogue or narration story line always starts on a new line.\n" +
                "Examples:\n" +
                "\"" + dialogueStartChar + "Hello!" + dialogueEndChar + "\" - this is a dialogue line, spoken by a character\n" +
                "\" he said.\" - this is a narration line, spoken by the Narrator.\n" +
                "\n" +
                "Your response needs to be well-formed JSON. \n" +
                "The first element is an array, called \"characterLines\". Each object from this array contains a \"line\" and a \"character\" attribute. \n" +
                "The \"line\" attribute contains a copy of the corresponding text line from the input - do not change this text in any way - copy it verbatim from the input! Do not split long input lines into multiple character lines! The \"character\" attribute contains the name of the character, inferred from the text. Mark the name of each character at the end of his or her dialogue line. There must be exactly as many character lines in your output, as there are lines in the input. Each object in the \"characterLines\" array contains exactly one \"line\" and one \"character\" attribute.\n" +
                "After the \"characterLines\" array, there needs to be a \"characters\" array. There must be exactly as many objects in this array, as the number of unique character names in the \"character\" attributes from the \"characterLines\" array.\n" +
                "Each object of the \"characters\" array contains 2 attributes - \"character\" and \"gender\". The \"character\" attribute contains the unique name of a character from the \"character\" attributes of the \"characterLines\" array, except the Narrator. The \"gender\" contains the inferred gender of that character from the text fragment's context.\n" +
                "Here's an example of a text fragment:\n" +
                "[Input]\n" +
                "His face was impassive, but more lined than Arthas remembered. His eyes, however, burned with righteous fury.\n" +
                "" + dialogueStartChar + "The dog returns to his vomit," + dialogueEndChar + "\n" +
                " said Uther, the words cracking like a whip.\n" +
                "" + dialogueStartChar + "I’d prayed you’d stay away." + dialogueEndChar + "\n" +
                "Arthas twitched slightly. His voice was rough as he replied,\n" +
                "" + dialogueStartChar + "I’m a bad copper—I just keep turning up. I see you still call yourself a paladin, even though I dissolved your order." + dialogueEndChar + "\n" +
                "Uther actually laughed, though it was bitter laughter.\n" +
                "" + dialogueStartChar + "As if you could dissolve it yourself. I answer to the Light, boy. So did you, once." + dialogueEndChar + "\n" +
                "\n" +
                "Here's an example of correct output:\n" +
                "[Output]\n" +
                "{\"characterLines\":[\n" +
                "{\"line\":\"His face was impassive, but more lined than Arthas remembered. His eyes, however, burned with righteous fury.\", \"character\":\"Narrator\"},\n" +
                "{\"line\":\"" + dialogueStartChar + "The dog returns to his vomit," + dialogueEndChar + "\", \"character\":\"Uther\"},\n" +
                "{\"line\":\" said Uther, the words cracking like a whip.\", \"character\":\"Narrator\"},\n" +
                "{\"line\":\"" + dialogueStartChar + "I’d prayed you’d stay away." + dialogueEndChar + "\", \"character\":\"Uther\"},\n" +
                "{\"line\":\"Arthas twitched slightly. His voice was rough as he replied, \", \"character\":\"Narrator\"},\n" +
                "{\"line\":\"" + dialogueStartChar + "I’m a bad copper—I just keep turning up. I see you still call yourself a paladin, even though I dissolved your order." + dialogueEndChar + "\", \"character\":\"Arthas\"},\n" +
                "{\"line\":\"Uther actually laughed, though it was bitter laughter.\", \"character\":\"Narrator\"},\n" +
                "{\"line\":\"" + dialogueStartChar + "As if you could dissolve it yourself. I answer to the Light, boy. So did you, once." + dialogueEndChar + "\", \"character\":\"Uther\"}],\n" +
                "\"characters\":[\n" +
                "{\"character\":\"Arthas\", \"gender\":\"male\"},\n" +
                "{\"character\":\"Uther\", \"gender\":\"male\"}\n" +
                "]}\n" +
                "\n";
    
    
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
    
    /*
    private Map<String, String> getCompletionRequestHeaders()
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Content-Type", " application/json");
        params.put("Authorization", "Bearer " + API_key);
        return params;
    }
     */
    
    /*
    private JsonObjectRequest createCompletionRequest(JSONObject reqBody, ApiResponseListener listener)
    {
        // Request a string response from the provided URL.
        return new JsonObjectRequest(Request.Method.POST,
                openaiCompletionsEndpoint,
                reqBody,
                response -> listener.OnResponse(response),
                error -> listener.OnError(error))
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                return getCompletionRequestHeaders();
            }
        };
    }
     */
    
    /*
    private void performCompletionRequest(JsonObjectRequest jsonRequest, String requestTag)
    {
        // Add the request to the RequestQueue.
        //jsonRequest.setTag(requestTag);
        //jsonRequest.setRetryPolicy(new DefaultRetryPolicy(apiResponseTimeoutMs, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    
        // Create the request body.
        RequestBody requestBody = RequestBody.create(jsonRequest.toString(), MediaType.parse("application/json; charset=utf-8"));
        OkHttpSingleton.getInstance(appContext).getClient().newCall(requestBody);
    
        Request request = new Request.Builder()
                .url("https://myapi.com/endpoint") // Replace with your URL.
                .post(requestBody)
                .tag(requestTag)
                .build();
    
        //RequestQueueSingleton.getInstance(appContext).addToRequestQueue(jsonRequest);
    }
     */
    
    public void getCompletion(String[] textLines, String tag, ApiResponseListener responseListener)
    {
        GptChatRequest requestBody =  createCompletionRequestBody(textLines);
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
    
    /*
    public void startQueuedRequests()
    {
        RequestQueueSingleton.getInstance(appContext).startQueue();
    }
    */
    
    public void stopQueuedRequests()
    {
        OkHttpSingleton.getInstance(appContext).cancelAllRequests();
    }
}
